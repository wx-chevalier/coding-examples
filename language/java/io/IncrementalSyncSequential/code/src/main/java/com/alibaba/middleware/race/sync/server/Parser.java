package com.alibaba.middleware.race.sync.server;

import com.alibaba.middleware.race.sync.Constants;
import com.alibaba.middleware.race.sync.utils.ByteArray;
import com.alibaba.middleware.race.sync.utils.Logger;
import com.alibaba.middleware.race.sync.utils.LongArrayPool;
import com.alibaba.middleware.race.sync.utils.PromisePool;
import com.lmax.disruptor.WorkHandler;

import java.nio.MappedByteBuffer;

import static com.alibaba.middleware.race.sync.Constants.ASSERT;
import static com.alibaba.middleware.race.sync.server.DataModel.columns;
import static com.alibaba.middleware.race.sync.utils.ReadUtils.skipNext;

/**
 * Read binlog file and dispatch tasks
 * <p>
 * Created by yfu on 6/17/17.
 */
public class Parser implements WorkHandler<Segment> {

    private static final Logger logger = Logger.SERVER_LOGGER;

    private final PromisePool promisePool = new PromisePool();
    
    @Override
    public void onEvent(Segment segment) throws Exception {
        if (segment.size == -1) {
            return;
        }

        segment.ensureAllocated();
        
        int writePos = 0;
        for (MappedByteBuffer mappedBuffer: segment.mappedBuffers) {
            mappedBuffer.get(segment.data, writePos, mappedBuffer.limit());
            writePos += mappedBuffer.limit();
        }
        assert writePos == segment.size;
        segment.mappedBuffers.clear();

        int pos = 0;
        if (!segment.isBegin) {
            while (pos < segment.size && segment.data[pos++] != '\n') ;
            if (pos == segment.size) return;
        }

        parseSegment(segment.data, pos, segment.size, segment);
    }

    private static volatile boolean firstPass = true;
    private static volatile int pkInfoLen = 0;
    private static volatile int fullColInfoLen = 0;

    // Predict
    private int remainLineLength1 = 42;
    private int remainLineLength2 = 42;

    // Predict
    private int opcodeOffset = 42;

    private void parseSegment(byte[] buffer, int pos, int limit, Segment segment) throws InterruptedException {

        segment.clear();

        while (pos < limit && pos <= Constants.SEGMENT_SIZE) {
            
            ////////////////////////////////////////////////////////////////////////////////
            // Skip header
            ////////////////////////////////////////////////////////////////////////////////
            
            // Example:
            // |mysql-bin.000018490963009|1496828295000|middleware5|student|I|id:1:1|NULL|888918|...
            // ^ start                                                      ^ end
            if (buffer[pos + opcodeOffset - 1] == (byte) '|' && buffer[pos + opcodeOffset + 1] == (byte) '|') {
                // Fast pass
                pos += opcodeOffset;
            } else {
                int lineBegin = pos;
                pos++;  // Skip '|'
                pos = skipNext(buffer, pos, '|');  // Skip binlog ID
                pos = skipNext(buffer, pos, '|');  // Skip timestamp
                pos = skipNext(buffer, pos, '|');  // Skip database name
                pos = skipNext(buffer, pos, '|');  // Skip table name
                opcodeOffset = pos - lineBegin;
            }

            byte operation = buffer[pos++];

            if (firstPass) {
                if (operation == 'I') {
                    synchronized (Parser.class) {
                        if (firstPass) {
                            doFirstPass(buffer, pos);
                            firstPass = false;
                        }
                    }
                } else {
                    do {
                        Thread.yield(); // Expecting other parsers could do extracting
                    } while (firstPass);
                }
            }
            
            pos += pkInfoLen;

            ////////////////////////////////////////////////////////////////////////////////
            // Parse events
            ////////////////////////////////////////////////////////////////////////////////
            
            byte ch;
            long key = 0;

            switch (operation) {
            case 'U':
                //////////////////////////////////////////////////////////////////////////
                // Parse Update
                //////////////////////////////////////////////////////////////////////////
                
                long srcKey = 0;
                while ((ch = buffer[pos++]) != '|') {
                    srcKey = srcKey * 10 + (ch - '0');
                }

                long dstKey = 0;
                while ((ch = buffer[pos++]) != '|') {
                    dstKey = dstKey * 10 + (ch - '0');
                }

                if (srcKey != dstKey) {
                    int promise = segment.addUpdateKeySrc(srcKey, promisePool);
                    segment.addUpdateKeyDst(pos, dstKey, promise);
                } else {
                    segment.addUpdate(pos, srcKey);
                }
                break;
                
            case 'I':
                //////////////////////////////////////////////////////////////////////////
                // Parse Insert
                //////////////////////////////////////////////////////////////////////////
                
                pos += 5; // "NULL|".length()

                while ((ch = buffer[pos++]) != '|') {
                    key = key * 10 + (ch - '0');
                }

                segment.addInsert(pos, key);
                
                pos += fullColInfoLen;
                break;
                
            case 'D':
                //////////////////////////////////////////////////////////////////////////
                // Parse Delete
                //////////////////////////////////////////////////////////////////////////
                
                while ((ch = buffer[pos++]) != '|') {
                    key = key * 10 + (ch - '0');
                }

                pos += 5; // "NULL|".length()

                segment.addDelete(key);
                
                pos += fullColInfoLen;
                break;
                
            default:
                throw new RuntimeException("Unknown operation " + (char) operation);
            }

            //////////////////////////////////////////////////////////////////////////
            // Skip to next line
            //////////////////////////////////////////////////////////////////////////

            // Example:
            // ...|U|id:1:1|3349535|3349535|first_name:2:0|XX|YY|.|mysql....
            //                             ^ start            end ^
            if (buffer[pos + remainLineLength1 - 1] == '\n') {
                // Fast pass
                pos += remainLineLength1;
            } else if (buffer[pos + remainLineLength2 - 1] == '\n') {
                // Fast pass
                pos += remainLineLength2;
            } else {
                int mark = pos;
                pos = skipNext(buffer, pos, '\n');
                remainLineLength2 = remainLineLength1;
                remainLineLength1 = pos - mark;
            }
        }
    }

    private DataModel extractDataModel(byte[] buffer, int pos) {
        // Example:
        // ...|id:1:1|NULL|888918|first_name:2:0|NULL|侯|last_name:2:0|NULL|骏兲|sex:2:0|NULL|女|score:1:0|NULL|68|
        //     ^ start here

        DataModel.Builder builder = new DataModel.Builder();
        boolean isPK = true;
        while (buffer[pos] != (byte) '\n') {
            int mark = pos;
            int n = 0;
            while (buffer[pos++] != ':') n++;

            boolean isText = buffer[pos++] == '2';
            pos += 8; // "|1|NULL|".length()
            pos = skipNext(buffer, pos, '|');

            if (!isPK) { // Exclude PK column
                builder.addColumn(ByteArray.copyOf(buffer, mark, n), isText);
            }
            isPK = false;
        }
        return builder.build();
    }
    
    private void doFirstPass(byte[] buffer, int pos) {
        
        DataModel.setColumns(extractDataModel(buffer, pos));

        LongArrayPool.setArrayLength(columns.size());

        for (int i = 0; i < columns.size(); i++) {
            fullColInfoLen += columns.getName(i).length() + 12; // ":2:0|NULL|_|"
        }

        if (ASSERT) assert buffer[pos] == '|';
        pos++;  // Skip '|'
        while (buffer[pos++] != '|') pkInfoLen++;
        pkInfoLen += 2;
    }
}
