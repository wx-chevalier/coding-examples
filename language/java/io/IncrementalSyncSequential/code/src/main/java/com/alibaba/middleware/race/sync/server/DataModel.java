package com.alibaba.middleware.race.sync.server;

import com.alibaba.middleware.race.sync.utils.ByteArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.alibaba.middleware.race.sync.Constants.ASSERT;

/**
 * Stores column information except the PK
 * <p>
 * Created by yfu on 6/12/17.
 */
public class DataModel {

    private static int MAX_COLUMN_NAME_LENGTH = 4096;
    private static int MAX_COLUMNS = 4096;

    private final ByteArray[] columnNames;
    private final HashMap<ByteArray, Integer> columnMap;
    private final int[] columnLengthMap;
    private final int[] columnLength;
    private final boolean[] isText;
    private final int size;

    private DataModel(HashMap<ByteArray, Integer> columnMap, ByteArray[] columnNames, boolean[] isText, int[] columnLengthMap, int[] columnLength, int size) {
        this.columnMap = columnMap;
        this.columnNames = columnNames;
        this.isText = isText;
        this.columnLengthMap = columnLengthMap;
        this.columnLength = columnLength;
        this.size = size;
    }

    public int getIndex(ByteArray name) {
        return columnMap.get(name);
    }

    public int getIndexByLength(int len) {
        return columnLengthMap[len];
    }

    public boolean isText(int index) {
        return isText[index];
    }
    
    public int getColumnLength(int index) {
        return columnLength[index];
    }

    public int size() {
        return size;
    }

    public ByteArray getName(int index) {
        return columnNames[index];
    }

    public static class Builder {

        private final ArrayList<ByteArray> columnNames = new ArrayList<>();
        private final int[] columnLengthMap = new int[MAX_COLUMN_NAME_LENGTH];
        private final int[] columnLength = new int[MAX_COLUMNS];;
        private final HashMap<ByteArray, Integer> columnMap = new HashMap<>();
        private final boolean[] isText = new boolean[MAX_COLUMNS];
        private int size;

        {
            Arrays.fill(columnLengthMap, -2);
        }

        public void addColumn(ByteArray name, boolean isText) {
            columnMap.put(name, size);
            this.isText[size] = isText;
            columnNames.add(name);
            if (columnLengthMap[name.length()] == -2) {
                columnLengthMap[name.length()] = size;  // could use getIndexByLength()
            } else if (columnLengthMap[name.length()] != -1) {
                columnLengthMap[name.length()] = -1; // undetermined, should fall back to getIndex()
            }
            columnLength[size] = name.length();
            size += 1;
        }

        public DataModel build() {
            return new DataModel(columnMap, columnNames.toArray(new ByteArray[size]), isText, columnLengthMap, columnLength, size);
        }

    }

    public static DataModel columns;

    public static long startKey;
    public static long endKey;

    public static void setColumns(DataModel columns) {
        DataModel.columns = columns;
    }

    public static final byte[] keyBucketNo = new byte[10000000];
    public static final Result[] keyResults = new Result[10000000];
    
    public static void setKeyBelongsTo(long key, int bucketNo) {
        keyBucketNo[(int)(key - DataModel.startKey)] = (byte) bucketNo;
    }

    public static void setResult(long key, Result result) {
        keyResults[(int)(key - DataModel.startKey)] = result;
    }
    
    public static int getKeyBelongsTo(long key) {
        return keyBucketNo[(int)(key - DataModel.startKey)];
    }

    public static Result getResult(long key) {
        return keyResults[(int)(key - DataModel.startKey)];
    }

    public static void setKeyRange(long start, long end) {
        if (ASSERT) assert startKey == 0 && endKey == 0;
        if (ASSERT) assert endKey - startKey <= 10000000;
        startKey = start;
        endKey = end;
        Arrays.fill(keyBucketNo, 0, (int) (endKey - startKey), (byte) -1);
    }

    public static boolean inRange(long key) {
        if (ASSERT) assert startKey != 0 || endKey != 0;
        return startKey < key && key < endKey;
    }
}
