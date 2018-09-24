

package com.alibaba.middleware.race.sync.utils;

import com.koloboke.collect.impl.AbstractEntry;
import com.koloboke.collect.impl.AbstractIntValueView;
import com.koloboke.collect.impl.AbstractLongKeyView;
import com.koloboke.collect.impl.AbstractSetView;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import com.koloboke.collect.impl.CommonObjCollectionOps;
import java.util.ConcurrentModificationException;
import com.koloboke.function.Consumer;
import com.koloboke.collect.impl.Containers;
import com.koloboke.collect.Equivalence;
import javax.annotation.Generated;
import com.koloboke.collect.impl.hash.Hash;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.impl.hash.HashConfigWrapper;
import com.koloboke.collect.set.hash.HashLongSet;
import com.koloboke.collect.set.hash.HashObjSet;
import com.koloboke.function.IntBinaryOperator;
import com.koloboke.collect.IntCollection;
import com.koloboke.function.IntConsumer;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.IntIterator;
import com.koloboke.function.IntPredicate;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.impl.InternalLongCollectionOps;
import com.koloboke.collect.impl.InternalLongIntMapOps;
import com.koloboke.collect.impl.InternalObjCollectionOps;
import com.koloboke.collect.impl.hash.LHash;
import com.koloboke.collect.impl.hash.LHashCapacities;
import com.koloboke.collect.impl.LongArrays;
import com.koloboke.collect.LongCollection;
import com.koloboke.function.LongConsumer;
import com.koloboke.collect.LongCursor;
import com.koloboke.function.LongIntConsumer;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.function.LongIntPredicate;
import com.koloboke.function.LongIntToIntFunction;
import com.koloboke.collect.LongIterator;
import com.koloboke.function.LongPredicate;
import com.koloboke.collect.set.LongSet;
import com.koloboke.function.LongToIntFunction;
import java.util.Map;
import com.koloboke.collect.impl.Maths;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import com.koloboke.collect.ObjCollection;
import com.koloboke.collect.ObjCursor;
import com.koloboke.collect.ObjIterator;
import com.koloboke.collect.set.ObjSet;
import com.koloboke.function.Predicate;
import com.koloboke.collect.impl.Primitives;
import java.util.Random;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import com.koloboke.collect.impl.ThreadLocalRandom;

@Generated(value = "com.koloboke.compile.processor.KolobokeCollectionProcessor")
@SuppressFBWarnings(value = { "IA_AMBIGUOUS_INVOCATION_OF_INHERITED_OR_OUTER_METHOD" })
@SuppressWarnings(value = { "all" , "unsafe" , "deprecation" , "overloads" , "rawtypes" })
final class KolobokeLongIntHashMap extends LongIntHashMap {
    KolobokeLongIntHashMap(int expectedSize) {
        this.init(DEFAULT_CONFIG_WRAPPER, expectedSize);
    }

    static void verifyConfig(HashConfig config) {
        if ((config.getGrowthFactor()) != 2.0) {
            throw new IllegalArgumentException(((((((config + " passed, HashConfig for a hashtable\n") + "implementation with linear probing must have growthFactor of 2.0.\n") + "A Koloboke Compile-generated hashtable implementation could have\n") + "a different growth factor, if the implemented type is annotated with\n") + "@com.koloboke.compile.hash.algo.openaddressing.QuadraticProbing or\n") + "@com.koloboke.compile.hash.algo.openaddressing.DoubleHashing"));
        } 
    }

    int[] values;

    long freeValue;

    @Nonnull
    public final HashConfig hashConfig() {
        return configWrapper().config();
    }

    long[] set;

    @Override
    public long sizeAsLong() {
        return ((long) (size()));
    }

    public final boolean noRemoved() {
        return true;
    }

    public final boolean isEmpty() {
        return (size()) == 0;
    }

    public final boolean containsKey(Object key) {
        return contains(key);
    }

    public final int freeSlots() {
        return (capacity()) - (size());
    }

    private HashConfigWrapper configWrapper;

    @Nonnull
    public long[] keys() {
        return set;
    }

    public boolean containsKey(long key) {
        return contains(key);
    }

    int size;

    public final int removedSlots() {
        return 0;
    }

    private int maxSize;

    private int modCount = 0;

    public int capacity() {
        return set.length;
    }

    int valueIndex(int value) {
        if (KolobokeLongIntHashMap.this.isEmpty())
            return -1;
        
        int index = -1;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            if ((keys[i]) != free) {
                if (value == (vals[i])) {
                    index = i;
                    break;
                } 
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return index;
    }

    @Nonnull
    public HashLongSet keySet() {
        return new KolobokeLongIntHashMap.KeyView();
    }

    public final double currentLoad() {
        return ((double) (size())) / ((double) (capacity()));
    }

    final void init(HashConfigWrapper configWrapper, int size, long freeValue) {
        KolobokeLongIntHashMap.this.freeValue = freeValue;
        init(configWrapper, size);
    }

    public void forEach(Consumer<? super Long> action) {
        if (action == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return ;
        
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                action.accept(key);
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
    }

    public final HashConfigWrapper configWrapper() {
        return configWrapper;
    }

    class KeyView extends AbstractLongKeyView implements HashLongSet , InternalLongCollectionOps , KolobokeLongIntHashMap.Support.SeparateKVLongLHash {
        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return KolobokeLongIntHashMap.this.hashConfig();
        }

        @Override
        public HashConfigWrapper configWrapper() {
            return KolobokeLongIntHashMap.this.configWrapper();
        }

        @Override
        public int size() {
            return KolobokeLongIntHashMap.this.size();
        }

        @Override
        public double currentLoad() {
            return KolobokeLongIntHashMap.this.currentLoad();
        }

        @Override
        public long freeValue() {
            return KolobokeLongIntHashMap.this.freeValue();
        }

        @Override
        public boolean supportRemoved() {
            return KolobokeLongIntHashMap.this.supportRemoved();
        }

        @Override
        public long removedValue() {
            return KolobokeLongIntHashMap.this.removedValue();
        }

        @Nonnull
        @Override
        public long[] keys() {
            return KolobokeLongIntHashMap.this.keys();
        }

        @Override
        public int capacity() {
            return KolobokeLongIntHashMap.this.capacity();
        }

        @Override
        public int freeSlots() {
            return KolobokeLongIntHashMap.this.freeSlots();
        }

        @Override
        public boolean noRemoved() {
            return KolobokeLongIntHashMap.this.noRemoved();
        }

        @Override
        public int removedSlots() {
            return KolobokeLongIntHashMap.this.removedSlots();
        }

        @Override
        public int modCount() {
            return KolobokeLongIntHashMap.this.modCount();
        }

        @Override
        public final boolean contains(Object o) {
            return KolobokeLongIntHashMap.this.contains(o);
        }

        @Override
        public boolean contains(long key) {
            return KolobokeLongIntHashMap.this.contains(key);
        }

        public void forEach(Consumer<? super Long> action) {
            KolobokeLongIntHashMap.this.forEach(action);
        }

        @Override
        public void forEach(LongConsumer action) {
            KolobokeLongIntHashMap.this.forEach(action);
        }

        @Override
        public boolean forEachWhile(LongPredicate predicate) {
            return KolobokeLongIntHashMap.this.forEachWhile(predicate);
        }

        @Override
        public boolean allContainingIn(LongCollection c) {
            return KolobokeLongIntHashMap.this.allContainingIn(c);
        }

        @Override
        public boolean reverseAddAllTo(LongCollection c) {
            return KolobokeLongIntHashMap.this.reverseAddAllTo(c);
        }

        @Override
        public boolean reverseRemoveAllFrom(LongSet s) {
            return KolobokeLongIntHashMap.this.reverseRemoveAllFrom(s);
        }

        @Override
        @Nonnull
        public LongIterator iterator() {
            return KolobokeLongIntHashMap.this.iterator();
        }

        @Override
        @Nonnull
        public LongCursor cursor() {
            return setCursor();
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            return KolobokeLongIntHashMap.this.toArray();
        }

        @Override
        @Nonnull
        public <T>  T[] toArray(@Nonnull
        T[] a) {
            return KolobokeLongIntHashMap.this.toArray(a);
        }

        @Override
        public long[] toLongArray() {
            return KolobokeLongIntHashMap.this.toLongArray();
        }

        @Override
        public long[] toArray(long[] a) {
            return KolobokeLongIntHashMap.this.toArray(a);
        }

        @Override
        public int hashCode() {
            return setHashCode();
        }

        @Override
        public String toString() {
            return setToString();
        }

        @Override
        public boolean shrink() {
            return KolobokeLongIntHashMap.this.shrink();
        }

        @Override
        public final boolean remove(Object o) {
            return justRemove(((Long) (o)));
        }

        @Override
        public boolean removeLong(long v) {
            return justRemove(v);
        }

        public boolean removeIf(Predicate<? super Long> filter) {
            return KolobokeLongIntHashMap.this.removeIf(filter);
        }

        @Override
        public boolean removeIf(LongPredicate filter) {
            return KolobokeLongIntHashMap.this.removeIf(filter);
        }

        @Override
        public boolean removeAll(@Nonnull
        Collection<?> c) {
            if (c instanceof LongCollection) {
                if (c instanceof InternalLongCollectionOps) {
                    InternalLongCollectionOps c2 = ((InternalLongCollectionOps) (c));
                    if ((c2.size()) < (KolobokeLongIntHashMap.KeyView.this.size())) {
                        return c2.reverseRemoveAllFrom(KolobokeLongIntHashMap.KeyView.this);
                    } 
                } 
                return KolobokeLongIntHashMap.this.removeAll(KolobokeLongIntHashMap.KeyView.this, ((LongCollection) (c)));
            } 
            return KolobokeLongIntHashMap.this.removeAll(KolobokeLongIntHashMap.KeyView.this, c);
        }

        @Override
        public boolean retainAll(@Nonnull
        Collection<?> c) {
            return KolobokeLongIntHashMap.this.retainAll(KolobokeLongIntHashMap.KeyView.this, c);
        }

        @Override
        public void clear() {
            KolobokeLongIntHashMap.this.clear();
        }
    }

    public long freeValue() {
        return freeValue;
    }

    @Override
    public final int size() {
        return size;
    }

    public boolean supportRemoved() {
        return false;
    }

    public final int modCount() {
        return modCount;
    }

    final void incrementModCount() {
        (modCount)++;
    }

    public long removedValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(int value) {
        return (valueIndex(value)) >= 0;
    }

    public void forEach(LongConsumer action) {
        if (action == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return ;
        
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                action.accept(key);
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
    }

    public boolean contains(Object key) {
        return contains(((Long) (key)).longValue());
    }

    boolean removeValue(int value) {
        int index = valueIndex(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        } else {
            return false;
        }
    }

    public boolean containsEntry(long key, int value) {
        int index = index(key);
        if (index >= 0) {
            return (values[index]) == value;
        } else {
            return false;
        }
    }

    public boolean contains(long key) {
        return (index(key)) >= 0;
    }

    int index(long key) {
        long free;
        if (key != (free = freeValue)) {
            long[] keys = set;
            int capacityMask;
            int index;
            long cur;
            if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & (capacityMask = (keys.length) - 1))]) == key) {
                return index;
            } else {
                if (cur == free) {
                    return -1;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            return index;
                        } else if (cur == free) {
                            return -1;
                        } 
                    }
                }
            }
        } else {
            return -1;
        }
    }

    final void init(HashConfigWrapper configWrapper, int size) {
        KolobokeLongIntHashMap.verifyConfig(configWrapper.config());
        KolobokeLongIntHashMap.this.configWrapper = configWrapper;
        KolobokeLongIntHashMap.this.size = 0;
        internalInit(targetCapacity(size));
    }

    @Override
    public boolean containsValue(Object value) {
        return containsValue(((Integer) (value)).intValue());
    }

    public boolean forEachWhile(LongPredicate predicate) {
        if (predicate == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return true;
        
        boolean terminated = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (!(predicate.test(key))) {
                    terminated = true;
                    break;
                } 
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return !terminated;
    }

    @Override
    public Integer get(Object key) {
        int index = index(((Long) (key)));
        if (index >= 0) {
            return values[index];
        } else {
            return null;
        }
    }

    int insert(long key, int value) {
        long free;
        if (key == (free = freeValue)) {
            free = changeFree();
        } 
        long[] keys = set;
        int capacityMask;
        int index;
        long cur;
        keyAbsent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & (capacityMask = (keys.length) - 1))]) != free) {
            if (cur == key) {
                return index;
            } else {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == free) {
                        break keyAbsent;
                    } else if (cur == key) {
                        return index;
                    } 
                }
            }
        } 
        incrementModCount();
        keys[index] = key;
        values[index] = value;
        postInsertHook();
        return -1;
    }

    private void internalInit(int capacity) {
        assert Maths.isPowerOf2(capacity);
        maxSize = maxSize(capacity);
        allocateArrays(capacity);
    }

    private int maxSize(int capacity) {
        return !(isMaxCapacity(capacity)) ? configWrapper.maxSize(capacity) : capacity - 1;
    }

    @Override
    public int get(long key) {
        int index = index(key);
        if (index >= 0) {
            return values[index];
        } else {
            return defaultValue();
        }
    }

    private long findNewFreeOrRemoved() {
        long free = KolobokeLongIntHashMap.this.freeValue;
        Random random = ThreadLocalRandom.current();
        long newFree;
        {
            do {
                newFree = ((long) (random.nextLong()));
            } while ((newFree == free) || ((index(newFree)) >= 0) );
        }
        return newFree;
    }

    public boolean allContainingIn(LongCollection c) {
        if (KolobokeLongIntHashMap.this.isEmpty())
            return true;
        
        boolean containsAll = true;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (!(c.contains(key))) {
                    containsAll = false;
                    break;
                } 
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return containsAll;
    }

    @Override
    public Integer getOrDefault(Object key, Integer defaultValue) {
        int index = index(((Long) (key)));
        if (index >= 0) {
            return values[index];
        } else {
            return defaultValue;
        }
    }

    void allocateArrays(int capacity) {
        _MutableSeparateKVLongLHashSO_allocateArrays(capacity);
        values = new int[capacity];
    }

    @Override
    public int getOrDefault(long key, int defaultValue) {
        int index = index(key);
        if (index >= 0) {
            return values[index];
        } else {
            return defaultValue;
        }
    }

    long changeFree() {
        int mc = modCount();
        long newFree = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        LongArrays.replaceAll(set, freeValue, newFree);
        KolobokeLongIntHashMap.this.freeValue = newFree;
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return newFree;
    }

    public boolean reverseAddAllTo(LongCollection c) {
        if (KolobokeLongIntHashMap.this.isEmpty())
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                changed |= c.add(key);
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    final void initForRehash(int newCapacity) {
        (modCount)++;
        internalInit(newCapacity);
    }

    private void _MutableSeparateKVLongLHashSO_allocateArrays(int capacity) {
        set = new long[capacity];
        if ((freeValue) != 0)
            Arrays.fill(set, freeValue);
        
    }

    private void _MutableLHash_clear() {
        (modCount)++;
        size = 0;
    }

    private void _MutableSeparateKVLongLHashSO_clear() {
        _MutableLHash_clear();
        Arrays.fill(set, freeValue);
    }

    public boolean reverseRemoveAllFrom(LongSet s) {
        if ((KolobokeLongIntHashMap.this.isEmpty()) || (s.isEmpty()))
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                changed |= s.removeLong(key);
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    @Override
    public void forEach(LongIntConsumer action) {
        if (action == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return ;
        
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                action.accept(key, vals[i]);
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
    }

    @Override
    public boolean shrink() {
        int newCapacity = targetCapacity(size);
        if (newCapacity < (capacity())) {
            rehash(newCapacity);
            return true;
        } else {
            return false;
        }
    }

    private boolean tryRehashForExpansion(int newCapacity) {
        if (newCapacity > (capacity())) {
            rehash(newCapacity);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean forEachWhile(LongIntPredicate predicate) {
        if (predicate == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return true;
        
        boolean terminated = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (!(predicate.test(key, vals[i]))) {
                    terminated = true;
                    break;
                } 
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return !terminated;
    }

    @Override
    public final boolean ensureCapacity(long minSize) {
        int intMinSize = ((int) (Math.min(minSize, ((long) (Integer.MAX_VALUE)))));
        if (minSize < 0L)
            throw new IllegalArgumentException((("Min size should be positive, " + minSize) + " given."));
        
        return (intMinSize > (maxSize)) && (tryRehashForExpansion(targetCapacity(intMinSize)));
    }

    @Nonnull
    public Object[] toArray() {
        int size = size();
        Object[] result = new Object[size];
        if (size == 0)
            return result;
        
        int resultIndex = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                result[(resultIndex++)] = key;
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return result;
    }

    final void postRemoveHook() {
        (size)--;
    }

    final void postInsertHook() {
        if ((++(size)) > (maxSize)) {
            int capacity = capacity();
            if (!(isMaxCapacity(capacity))) {
                rehash((capacity << 1));
            } 
        } 
    }

    @Nonnull
    @Override
    public LongIntCursor cursor() {
        int mc = modCount();
        return new KolobokeLongIntHashMap.NoRemovedMapCursor(mc);
    }

    @SuppressWarnings(value = "unchecked")
    @Nonnull
    public <T>  T[] toArray(@Nonnull
    T[] a) {
        int size = size();
        if ((a.length) < size) {
            Class<?> elementType = a.getClass().getComponentType();
            a = ((T[]) (Array.newInstance(elementType, size)));
        } 
        if (size == 0) {
            if ((a.length) > 0)
                a[0] = null;
            
            return a;
        } 
        int resultIndex = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                a[(resultIndex++)] = ((T) (Long.valueOf(key)));
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        if ((a.length) > resultIndex)
            a[resultIndex] = null;
        
        return a;
    }

    private static boolean identical(Object a, Object b) {
        return a == b;
    }

    boolean doubleSizedArrays() {
        return false;
    }

    private int targetCapacity(int size) {
        return LHashCapacities.capacity(configWrapper, size, doubleSizedArrays());
    }

    @SuppressFBWarnings(value = "BC_IMPOSSIBLE_CAST")
    @SuppressWarnings(value = "unchecked")
    public boolean containsAllEntries(Map<?, ?> m) {
        if (KolobokeLongIntHashMap.identical(KolobokeLongIntHashMap.this, m))
            throw new IllegalArgumentException();
        
        if (m instanceof LongIntMap) {
            LongIntMap m2 = ((LongIntMap) (m));
            if ((KolobokeLongIntHashMap.this.size()) < (m2.size()))
                return false;
            
            if ((InternalLongIntMapOps.class.isAssignableFrom(getClass())) && (m2 instanceof InternalLongIntMapOps)) {
                return ((InternalLongIntMapOps) (m2)).allEntriesContainingIn(((InternalLongIntMapOps) (InternalLongIntMapOps.class.cast(KolobokeLongIntHashMap.this))));
            } 
            return m2.forEachWhile(new LongIntPredicate() {
                @Override
                public boolean test(long a, int b) {
                    return containsEntry(a, b);
                }
            });
        } 
        for (Map.Entry<?, ?> e : m.entrySet()) {
            if (!(containsEntry(((Long) (e.getKey())), ((Integer) (e.getValue())))))
                return false;
            
        }
        return true;
    }

    private boolean isMaxCapacity(int capacity) {
        return LHashCapacities.isMaxCapacity(capacity, doubleSizedArrays());
    }

    @Nonnull
    public long[] toLongArray() {
        int size = size();
        long[] result = new long[size];
        if (size == 0)
            return result;
        
        int resultIndex = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                result[(resultIndex++)] = key;
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return result;
    }

    @Nonnull
    public long[] toArray(long[] a) {
        int size = size();
        if ((a.length) < size)
            a = new long[size];
        
        if (size == 0) {
            if ((a.length) > 0)
                a[0] = 0L;
            
            return a;
        } 
        int resultIndex = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                a[(resultIndex++)] = key;
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        if ((a.length) > resultIndex)
            a[resultIndex] = 0L;
        
        return a;
    }

    public int setHashCode() {
        int hashCode = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                hashCode += ((int) (key ^ (key >>> 32)));
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return hashCode;
    }

    @Override
    @Nonnull
    public HashObjSet<Map.Entry<Long, Integer>> entrySet() {
        return new KolobokeLongIntHashMap.EntryView();
    }

    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
    public String setToString() {
        if (KolobokeLongIntHashMap.this.isEmpty())
            return "[]";
        
        StringBuilder sb = new StringBuilder();
        int elementCount = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                sb.append(' ').append(key).append(',');
                if ((++elementCount) == 8) {
                    int expectedLength = (sb.length()) * ((size()) / 8);
                    sb.ensureCapacity((expectedLength + (expectedLength / 2)));
                } 
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        sb.setCharAt(0, '[');
        sb.setCharAt(((sb.length()) - 1), ']');
        return sb.toString();
    }

    @Override
    @Nonnull
    public IntCollection values() {
        return new KolobokeLongIntHashMap.ValueView();
    }

    @Override
    public boolean equals(Object o) {
        if ((KolobokeLongIntHashMap.this) == o) {
            return true;
        } 
        if (!(o instanceof Map)) {
            return false;
        } 
        Map<?, ?> that = ((Map<?, ?>) (o));
        if ((that.size()) != (KolobokeLongIntHashMap.this.size())) {
            return false;
        } 
        try {
            return KolobokeLongIntHashMap.this.containsAllEntries(that);
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                hashCode += ((int) ((key ^ (key >>> 32)))) ^ (vals[i]);
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return hashCode;
    }

    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
    @Override
    public String toString() {
        if (KolobokeLongIntHashMap.this.isEmpty())
            return "{}";
        
        StringBuilder sb = new StringBuilder();
        int elementCount = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                sb.append(' ');
                sb.append(key);
                sb.append('=');
                sb.append(vals[i]);
                sb.append(',');
                if ((++elementCount) == 8) {
                    int expectedLength = (sb.length()) * ((size()) / 8);
                    sb.ensureCapacity((expectedLength + (expectedLength / 2)));
                } 
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        sb.setCharAt(0, '{');
        sb.setCharAt(((sb.length()) - 1), '}');
        return sb.toString();
    }

    private final static Logger logger = Logger.SERVER_LOGGER;
    
    void rehash(int newCapacity) {
        logger.warn("Rehash! current size = %d", size);
        
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        initForRehash(newCapacity);
        mc++;
        long[] newKeys = set;
        int capacityMask = (newKeys.length) - 1;
        int[] newVals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                int index;
                if ((newKeys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & capacityMask)]) != free) {
                    while (true) {
                        if ((newKeys[(index = (index - 1) & capacityMask)]) == free) {
                            break;
                        } 
                    }
                } 
                newKeys[index] = key;
                newVals[index] = vals[i];
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
    }

    @Override
    public Integer put(Long key, Integer value) {
        int index = insert(key, value);
        if (index < 0) {
            return null;
        } else {
            int[] vals = values;
            int prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public int put(long key, int value) {
        int index = insert(key, value);
        if (index < 0) {
            return defaultValue();
        } else {
            int[] vals = values;
            int prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public Integer putIfAbsent(Long key, Integer value) {
        int index = insert(key, value);
        if (index < 0) {
            return null;
        } else {
            return values[index];
        }
    }

    @Override
    public int putIfAbsent(long key, int value) {
        int index = insert(key, value);
        if (index < 0) {
            return defaultValue();
        } else {
            return values[index];
        }
    }

    public void justPut(long key, int value) {
        int index = insert(key, value);
        if (index < 0) {
            return ;
        } else {
            values[index] = value;
            return ;
        }
    }

    @Override
    public int compute(long key, LongIntToIntFunction remappingFunction) {
        if (remappingFunction == null)
            throw new NullPointerException();
        
        long free;
        if (key == (free = freeValue)) {
            free = changeFree();
        } 
        long[] keys = set;
        int[] vals = values;
        int capacityMask;
        int index;
        long cur;
        keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & (capacityMask = (keys.length) - 1))]) != key) {
            keyAbsent : if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    } 
                }
            } 
            int newValue = remappingFunction.applyAsInt(key, defaultValue());
            incrementModCount();
            keys[index] = key;
            vals[index] = newValue;
            postInsertHook();
            return newValue;
        } 
        int newValue = remappingFunction.applyAsInt(key, vals[index]);
        vals[index] = newValue;
        return newValue;
    }

    @Override
    public int computeIfAbsent(long key, LongToIntFunction mappingFunction) {
        if (mappingFunction == null)
            throw new NullPointerException();
        
        long free;
        if (key == (free = freeValue)) {
            free = changeFree();
        } 
        long[] keys = set;
        int[] vals = values;
        int capacityMask;
        int index;
        long cur;
        if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & (capacityMask = (keys.length) - 1))]) == key) {
            return vals[index];
        } else {
            keyAbsent : if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        return vals[index];
                    } else if (cur == free) {
                        break keyAbsent;
                    } 
                }
            } 
            int value = mappingFunction.applyAsInt(key);
            incrementModCount();
            keys[index] = key;
            vals[index] = value;
            postInsertHook();
            return value;
        }
    }

    @Override
    public int computeIfPresent(long key, LongIntToIntFunction remappingFunction) {
        if (remappingFunction == null)
            throw new NullPointerException();
        
        int index = index(key);
        if (index >= 0) {
            int[] vals = values;
            int newValue = remappingFunction.applyAsInt(key, vals[index]);
            vals[index] = newValue;
            return newValue;
        } else {
            return defaultValue();
        }
    }

    @Override
    public int merge(long key, int value, IntBinaryOperator remappingFunction) {
        if (remappingFunction == null)
            throw new NullPointerException();
        
        long free;
        if (key == (free = freeValue)) {
            free = changeFree();
        } 
        long[] keys = set;
        int[] vals = values;
        int capacityMask;
        int index;
        long cur;
        keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & (capacityMask = (keys.length) - 1))]) != key) {
            keyAbsent : if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    } 
                }
            } 
            incrementModCount();
            keys[index] = key;
            vals[index] = value;
            postInsertHook();
            return value;
        } 
        int newValue = remappingFunction.applyAsInt(vals[index], value);
        vals[index] = newValue;
        return newValue;
    }

    @Override
    public int addValue(long key, int value) {
        long free;
        if (key == (free = freeValue)) {
            free = changeFree();
        } 
        long[] keys = set;
        int[] vals = values;
        int capacityMask;
        int index;
        long cur;
        keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & (capacityMask = (keys.length) - 1))]) != key) {
            keyAbsent : if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    } 
                }
            } 
            int newValue = (defaultValue()) + value;
            incrementModCount();
            keys[index] = key;
            vals[index] = newValue;
            postInsertHook();
            return newValue;
        } 
        int newValue = (vals[index]) + value;
        vals[index] = newValue;
        return newValue;
    }

    class NoRemovedIterator implements LongIterator {
        long[] keys;

        final long free;

        final int capacityMask;

        int expectedModCount;

        int index = -1;

        int nextIndex;

        long next;

        NoRemovedIterator(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongIntHashMap.NoRemovedIterator.this.keys = set;
            capacityMask = (keys.length) - 1;
            long free = this.free = freeValue;
            int nextI = keys.length;
            while ((--nextI) >= 0) {
                long key;
                if ((key = keys[nextI]) != free) {
                    next = key;
                    break;
                } 
            }
            nextIndex = nextI;
        }

        @Override
        public long nextLong() {
            if ((expectedModCount) == (modCount())) {
                int nextI;
                if ((nextI = nextIndex) >= 0) {
                    index = nextI;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedIterator.this.keys;
                    long free = KolobokeLongIntHashMap.NoRemovedIterator.this.free;
                    long prev = next;
                    while ((--nextI) >= 0) {
                        long key;
                        if ((key = keys[nextI]) != free) {
                            next = key;
                            break;
                        } 
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new NoSuchElementException();
                }
            } else {
                throw new ConcurrentModificationException();
            }
        }

        public void forEachRemaining(Consumer<? super Long> action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedIterator.this.keys;
            long free = KolobokeLongIntHashMap.NoRemovedIterator.this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(key);
                } 
            }
            if ((nextI != (nextIndex)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(LongConsumer action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedIterator.this.keys;
            long free = KolobokeLongIntHashMap.NoRemovedIterator.this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(key);
                } 
            }
            if ((nextI != (nextIndex)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return (nextIndex) >= 0;
        }

        @Override
        public Long next() {
            return nextLong();
        }

        @Override
        public void remove() {
            int index;
            if ((index = KolobokeLongIntHashMap.NoRemovedIterator.this.index) >= 0) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedIterator.this.index = -1;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedIterator.this.keys;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedIterator.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == (free)) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedIterator.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = (nextIndex) + 1) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedIterator.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedIterator.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedIterator.this.nextIndex = index;
                                        if (indexToShift < (index - 1)) {
                                            KolobokeLongIntHashMap.NoRemovedIterator.this.next = keyToShift;
                                        } 
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public int addValue(long key, int addition, int initialValue) {
        long free;
        if (key == (free = freeValue)) {
            free = changeFree();
        } 
        long[] keys = set;
        int[] vals = values;
        int capacityMask;
        int index;
        long cur;
        keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & (capacityMask = (keys.length) - 1))]) != key) {
            keyAbsent : if (cur != free) {
                while (true) {
                    if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                        break keyPresent;
                    } else if (cur == free) {
                        break keyAbsent;
                    } 
                }
            } 
            int newValue = initialValue + addition;
            incrementModCount();
            keys[index] = key;
            vals[index] = newValue;
            postInsertHook();
            return newValue;
        } 
        int newValue = (vals[index]) + addition;
        vals[index] = newValue;
        return newValue;
    }

    @SuppressFBWarnings(value = "BC_IMPOSSIBLE_CAST")
    @Override
    public void putAll(@Nonnull
    Map<? extends Long, ? extends Integer> m) {
        if (KolobokeLongIntHashMap.identical(KolobokeLongIntHashMap.this, m))
            throw new IllegalArgumentException();
        
        long maxPossibleSize = (sizeAsLong()) + (Containers.sizeAsLong(m));
        ensureCapacity(maxPossibleSize);
        if (m instanceof LongIntMap) {
            if ((InternalLongIntMapOps.class.isAssignableFrom(getClass())) && (m instanceof InternalLongIntMapOps)) {
                ((InternalLongIntMapOps) (m)).reversePutAllTo(((InternalLongIntMapOps) (InternalLongIntMapOps.class.cast(KolobokeLongIntHashMap.this))));
            } else {
                ((LongIntMap) (m)).forEach(new LongIntConsumer() {
                    @Override
                    public void accept(long key, int value) {
                        justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Long, ? extends Integer> e : m.entrySet()) {
                justPut(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public Integer replace(Long key, Integer value) {
        int index = index(key);
        if (index >= 0) {
            int[] vals = values;
            int oldValue = vals[index];
            vals[index] = value;
            return oldValue;
        } else {
            return null;
        }
    }

    @Override
    public int replace(long key, int value) {
        int index = index(key);
        if (index >= 0) {
            int[] vals = values;
            int oldValue = vals[index];
            vals[index] = value;
            return oldValue;
        } else {
            return defaultValue();
        }
    }

    @Override
    public boolean replace(Long key, Integer oldValue, Integer newValue) {
        return replace(key.longValue(), oldValue.intValue(), newValue.intValue());
    }

    @Override
    public boolean replace(long key, int oldValue, int newValue) {
        int index = index(key);
        if (index >= 0) {
            int[] vals = values;
            if ((vals[index]) == oldValue) {
                vals[index] = newValue;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    class NoRemovedCursor implements LongCursor {
        long[] keys;

        final long free;

        final int capacityMask;

        int expectedModCount;

        int index;

        long curKey;

        NoRemovedCursor(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongIntHashMap.NoRemovedCursor.this.keys = set;
            capacityMask = (keys.length) - 1;
            index = keys.length;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(LongConsumer action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedCursor.this.keys;
            long free = KolobokeLongIntHashMap.NoRemovedCursor.this.free;
            int index = KolobokeLongIntHashMap.NoRemovedCursor.this.index;
            for (int i = index - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(key);
                } 
            }
            if ((index != (KolobokeLongIntHashMap.NoRemovedCursor.this.index)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            KolobokeLongIntHashMap.NoRemovedCursor.this.index = -1;
            curKey = free;
        }

        @Override
        public long elem() {
            long curKey;
            if ((curKey = KolobokeLongIntHashMap.NoRemovedCursor.this.curKey) != (free)) {
                return curKey;
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if ((expectedModCount) == (modCount())) {
                long[] keys = KolobokeLongIntHashMap.NoRemovedCursor.this.keys;
                long free = KolobokeLongIntHashMap.NoRemovedCursor.this.free;
                for (int i = (index) - 1; i >= 0; i--) {
                    long key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        return true;
                    } 
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long curKey;
            long free;
            if ((curKey = KolobokeLongIntHashMap.NoRemovedCursor.this.curKey) != (free = KolobokeLongIntHashMap.NoRemovedCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedCursor.this.curKey = free;
                    int index = KolobokeLongIntHashMap.NoRemovedCursor.this.index;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedCursor.this.keys;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedCursor.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedCursor.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedCursor.this.index = ++index;
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public void replaceAll(LongIntToIntFunction function) {
        if (function == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return ;
        
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                vals[i] = function.applyAsInt(key, vals[i]);
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
    }

    @Override
    public void clear() {
        doClear();
    }

    private void doClear() {
        int mc = (modCount()) + 1;
        _MutableSeparateKVLongLHashSO_clear();
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
    }

    void removeAt(int index) {
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        int capacityMask = (keys.length) - 1;
        incrementModCount();
        int indexToRemove = index;
        int indexToShift = indexToRemove;
        int shiftDistance = 1;
        while (true) {
            indexToShift = (indexToShift - 1) & capacityMask;
            long keyToShift;
            if ((keyToShift = keys[indexToShift]) == free) {
                break;
            } 
            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                keys[indexToRemove] = keyToShift;
                vals[indexToRemove] = vals[indexToShift];
                indexToRemove = indexToShift;
                shiftDistance = 1;
            } else {
                shiftDistance++;
                if (indexToShift == (1 + index)) {
                    throw new ConcurrentModificationException();
                } 
            }
        }
        keys[indexToRemove] = free;
        postRemoveHook();
    }

    @Override
    public Integer remove(Object key) {
        long k = ((Long) (key));
        long free;
        if (k != (free = freeValue)) {
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int index;
            long cur;
            keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(k)) & capacityMask)]) != k) {
                if (cur == free) {
                    return null;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == k) {
                            break keyPresent;
                        } else if (cur == free) {
                            return null;
                        } 
                    }
                }
            } 
            int[] vals = values;
            int val = vals[index];
            incrementModCount();
            int indexToRemove = index;
            int indexToShift = indexToRemove;
            int shiftDistance = 1;
            while (true) {
                indexToShift = (indexToShift - 1) & capacityMask;
                long keyToShift;
                if ((keyToShift = keys[indexToShift]) == free) {
                    break;
                } 
                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                    keys[indexToRemove] = keyToShift;
                    vals[indexToRemove] = vals[indexToShift];
                    indexToRemove = indexToShift;
                    shiftDistance = 1;
                } else {
                    shiftDistance++;
                    if (indexToShift == (1 + index)) {
                        throw new ConcurrentModificationException();
                    } 
                }
            }
            keys[indexToRemove] = free;
            postRemoveHook();
            return val;
        } else {
            return null;
        }
    }

    public boolean justRemove(long key) {
        long free;
        if (key != (free = freeValue)) {
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int index;
            long cur;
            keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & capacityMask)]) != key) {
                if (cur == free) {
                    return false;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            return false;
                        } 
                    }
                }
            } 
            int[] vals = values;
            incrementModCount();
            int indexToRemove = index;
            int indexToShift = indexToRemove;
            int shiftDistance = 1;
            while (true) {
                indexToShift = (indexToShift - 1) & capacityMask;
                long keyToShift;
                if ((keyToShift = keys[indexToShift]) == free) {
                    break;
                } 
                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                    keys[indexToRemove] = keyToShift;
                    vals[indexToRemove] = vals[indexToShift];
                    indexToRemove = indexToShift;
                    shiftDistance = 1;
                } else {
                    shiftDistance++;
                    if (indexToShift == (1 + index)) {
                        throw new ConcurrentModificationException();
                    } 
                }
            }
            keys[indexToRemove] = free;
            postRemoveHook();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int remove(long key) {
        long free;
        if (key != (free = freeValue)) {
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int index;
            long cur;
            keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & capacityMask)]) != key) {
                if (cur == free) {
                    return defaultValue();
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            return defaultValue();
                        } 
                    }
                }
            } 
            int[] vals = values;
            int val = vals[index];
            incrementModCount();
            int indexToRemove = index;
            int indexToShift = indexToRemove;
            int shiftDistance = 1;
            while (true) {
                indexToShift = (indexToShift - 1) & capacityMask;
                long keyToShift;
                if ((keyToShift = keys[indexToShift]) == free) {
                    break;
                } 
                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                    keys[indexToRemove] = keyToShift;
                    vals[indexToRemove] = vals[indexToShift];
                    indexToRemove = indexToShift;
                    shiftDistance = 1;
                } else {
                    shiftDistance++;
                    if (indexToShift == (1 + index)) {
                        throw new ConcurrentModificationException();
                    } 
                }
            }
            keys[indexToRemove] = free;
            postRemoveHook();
            return val;
        } else {
            return defaultValue();
        }
    }

    @Override
    public boolean remove(Object key, Object value) {
        return remove(((Long) (key)).longValue(), ((Integer) (value)).intValue());
    }

    @Override
    public boolean remove(long key, int value) {
        long free;
        if (key != (free = freeValue)) {
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int index;
            long cur;
            keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & capacityMask)]) != key) {
                if (cur == free) {
                    return false;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            return false;
                        } 
                    }
                }
            } 
            int[] vals = values;
            if ((vals[index]) == value) {
                incrementModCount();
                int indexToRemove = index;
                int indexToShift = indexToRemove;
                int shiftDistance = 1;
                while (true) {
                    indexToShift = (indexToShift - 1) & capacityMask;
                    long keyToShift;
                    if ((keyToShift = keys[indexToShift]) == free) {
                        break;
                    } 
                    if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                        keys[indexToRemove] = keyToShift;
                        vals[indexToRemove] = vals[indexToShift];
                        indexToRemove = indexToShift;
                        shiftDistance = 1;
                    } else {
                        shiftDistance++;
                        if (indexToShift == (1 + index)) {
                            throw new ConcurrentModificationException();
                        } 
                    }
                }
                keys[indexToRemove] = free;
                postRemoveHook();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean removeIf(LongIntPredicate filter) {
        if (filter == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (filter.test(key, vals[i])) {
                    incrementModCount();
                    mc++;
                    closeDeletion : if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                } 
                                if (indexToRemove == i) {
                                    i++;
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + i)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                } 
            } 
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        } 
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    public boolean removeIf(Predicate<? super Long> filter) {
        if (filter == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (filter.test(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion : if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                } 
                                if (indexToRemove == i) {
                                    i++;
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + i)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                } 
            } 
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        } 
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    public boolean removeIf(LongPredicate filter) {
        if (filter == null)
            throw new NullPointerException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (filter.test(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion : if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                } 
                                if (indexToRemove == i) {
                                    i++;
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + i)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                } 
            } 
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        } 
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
    public boolean removeAll(@Nonnull
    HashLongSet thisC, @Nonnull
    Collection<?> c) {
        if (thisC == ((Object) (c)))
            throw new IllegalArgumentException();
        
        if ((KolobokeLongIntHashMap.this.isEmpty()) || (c.isEmpty()))
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (c.contains(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion : if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                } 
                                if (indexToRemove == i) {
                                    i++;
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + i)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                } 
            } 
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        } 
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    boolean removeAll(@Nonnull
    HashLongSet thisC, @Nonnull
    LongCollection c) {
        if (thisC == ((Object) (c)))
            throw new IllegalArgumentException();
        
        if ((KolobokeLongIntHashMap.this.isEmpty()) || (c.isEmpty()))
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (c.contains(key)) {
                    incrementModCount();
                    mc++;
                    closeDeletion : if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                } 
                                if (indexToRemove == i) {
                                    i++;
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + i)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                } 
            } 
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        } 
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
    public boolean retainAll(@Nonnull
    HashLongSet thisC, @Nonnull
    Collection<?> c) {
        if (c instanceof LongCollection)
            return retainAll(thisC, ((LongCollection) (c)));
        
        if (thisC == ((Object) (c)))
            throw new IllegalArgumentException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return false;
        
        if (c.isEmpty()) {
            clear();
            return true;
        } 
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (!(c.contains(key))) {
                    incrementModCount();
                    mc++;
                    closeDeletion : if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                } 
                                if (indexToRemove == i) {
                                    i++;
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + i)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                } 
            } 
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        } 
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    private boolean retainAll(@Nonnull
    HashLongSet thisC, @Nonnull
    LongCollection c) {
        if (thisC == ((Object) (c)))
            throw new IllegalArgumentException();
        
        if (KolobokeLongIntHashMap.this.isEmpty())
            return false;
        
        if (c.isEmpty()) {
            clear();
            return true;
        } 
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        int[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                if (!(c.contains(key))) {
                    incrementModCount();
                    mc++;
                    closeDeletion : if (firstDelayedRemoved < 0) {
                        int indexToRemove = i;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if (indexToShift > indexToRemove) {
                                    firstDelayedRemoved = i;
                                    delayedRemoved = key;
                                    keys[indexToRemove] = key;
                                    break closeDeletion;
                                } 
                                if (indexToRemove == i) {
                                    i++;
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + i)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        keys[i] = delayedRemoved;
                    }
                    changed = true;
                } 
            } 
        }
        if (firstDelayedRemoved >= 0) {
            closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
        } 
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return changed;
    }

    void closeDelayedRemoved(int firstDelayedRemoved, long delayedRemoved) {
        long free = freeValue;
        long[] keys = set;
        int[] vals = values;
        int capacityMask = (keys.length) - 1;
        for (int i = firstDelayedRemoved; i >= 0; i--) {
            if ((keys[i]) == delayedRemoved) {
                int indexToRemove = i;
                int indexToShift = indexToRemove;
                int shiftDistance = 1;
                while (true) {
                    indexToShift = (indexToShift - 1) & capacityMask;
                    long keyToShift;
                    if ((keyToShift = keys[indexToShift]) == free) {
                        break;
                    } 
                    if ((keyToShift != delayedRemoved) && ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance)) {
                        keys[indexToRemove] = keyToShift;
                        vals[indexToRemove] = vals[indexToShift];
                        indexToRemove = indexToShift;
                        shiftDistance = 1;
                    } else {
                        shiftDistance++;
                        if (indexToShift == (1 + i)) {
                            throw new ConcurrentModificationException();
                        } 
                    }
                }
                keys[indexToRemove] = free;
                postRemoveHook();
            } 
        }
    }

    public LongIterator iterator() {
        int mc = modCount();
        return new KolobokeLongIntHashMap.NoRemovedKeyIterator(mc);
    }

    public LongCursor setCursor() {
        int mc = modCount();
        return new KolobokeLongIntHashMap.NoRemovedKeyCursor(mc);
    }

    class NoRemovedKeyIterator extends KolobokeLongIntHashMap.NoRemovedIterator {
        int[] vals;

        private NoRemovedKeyIterator(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            int index;
            if ((index = KolobokeLongIntHashMap.NoRemovedKeyIterator.this.index) >= 0) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedKeyIterator.this.index = -1;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedKeyIterator.this.keys;
                    int[] vals = KolobokeLongIntHashMap.NoRemovedKeyIterator.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedKeyIterator.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == (free)) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedKeyIterator.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = (nextIndex) + 1) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedKeyIterator.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongIntHashMap.NoRemovedKeyIterator.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedKeyIterator.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedKeyIterator.this.nextIndex = index;
                                        if (indexToShift < (index - 1)) {
                                            KolobokeLongIntHashMap.NoRemovedKeyIterator.this.next = keyToShift;
                                        } 
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedKeyCursor extends KolobokeLongIntHashMap.NoRemovedCursor {
        int[] vals;

        private NoRemovedKeyCursor(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            long curKey;
            long free;
            if ((curKey = KolobokeLongIntHashMap.NoRemovedKeyCursor.this.curKey) != (free = KolobokeLongIntHashMap.NoRemovedKeyCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedKeyCursor.this.curKey = free;
                    int index = KolobokeLongIntHashMap.NoRemovedKeyCursor.this.index;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedKeyCursor.this.keys;
                    int[] vals = KolobokeLongIntHashMap.NoRemovedKeyCursor.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedKeyCursor.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedKeyCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedKeyCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongIntHashMap.NoRemovedKeyCursor.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedKeyCursor.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedKeyCursor.this.index = ++index;
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class EntryView extends AbstractSetView<Map.Entry<Long, Integer>> implements HashObjSet<Map.Entry<Long, Integer>> , InternalObjCollectionOps<Map.Entry<Long, Integer>> {
        @Nonnull
        @Override
        public Equivalence<Map.Entry<Long, Integer>> equivalence() {
            return Equivalence.entryEquivalence(Equivalence.<Long>defaultEquality(), Equivalence.<Integer>defaultEquality());
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return KolobokeLongIntHashMap.this.hashConfig();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double currentLoad() {
            return KolobokeLongIntHashMap.this.currentLoad();
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Long, Integer> e = ((Map.Entry<Long, Integer>) (o));
                return containsEntry(e.getKey(), e.getValue());
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        @Nonnull
        public final Object[] toArray() {
            int size = size();
            Object[] result = new Object[size];
            if (size == 0)
                return result;
            
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    result[(resultIndex++)] = new KolobokeLongIntHashMap.MutableEntry(mc, i, key, vals[i]);
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return result;
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        @Nonnull
        public final <T>  T[] toArray(@Nonnull
        T[] a) {
            int size = size();
            if ((a.length) < size) {
                Class<?> elementType = a.getClass().getComponentType();
                a = ((T[]) (Array.newInstance(elementType, size)));
            } 
            if (size == 0) {
                if ((a.length) > 0)
                    a[0] = null;
                
                return a;
            } 
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    a[(resultIndex++)] = ((T) (new KolobokeLongIntHashMap.MutableEntry(mc, i, key, vals[i])));
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            if ((a.length) > resultIndex)
                a[resultIndex] = null;
            
            return a;
        }

        @Override
        public final void forEach(@Nonnull
        Consumer<? super Map.Entry<Long, Integer>> action) {
            if (action == null)
                throw new NullPointerException();
            
            if (KolobokeLongIntHashMap.EntryView.this.isEmpty())
                return ;
            
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(new KolobokeLongIntHashMap.MutableEntry(mc, i, key, vals[i]));
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
        }

        @Override
        public boolean forEachWhile(@Nonnull
        Predicate<? super Map.Entry<Long, Integer>> predicate) {
            if (predicate == null)
                throw new NullPointerException();
            
            if (KolobokeLongIntHashMap.EntryView.this.isEmpty())
                return true;
            
            boolean terminated = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (!(predicate.test(new KolobokeLongIntHashMap.MutableEntry(mc, i, key, vals[i])))) {
                        terminated = true;
                        break;
                    } 
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return !terminated;
        }

        @Override
        @Nonnull
        public ObjIterator<Map.Entry<Long, Integer>> iterator() {
            int mc = modCount();
            return new KolobokeLongIntHashMap.NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Long, Integer>> cursor() {
            int mc = modCount();
            return new KolobokeLongIntHashMap.NoRemovedEntryCursor(mc);
        }

        @Override
        public final boolean containsAll(@Nonnull
        Collection<?> c) {
            return CommonObjCollectionOps.containsAll(KolobokeLongIntHashMap.EntryView.this, c);
        }

        @Override
        public final boolean allContainingIn(ObjCollection<?> c) {
            if (KolobokeLongIntHashMap.EntryView.this.isEmpty())
                return true;
            
            boolean containsAll = true;
            KolobokeLongIntHashMap.ReusableEntry e = new KolobokeLongIntHashMap.ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (!(c.contains(e.with(key, vals[i])))) {
                        containsAll = false;
                        break;
                    } 
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return containsAll;
        }

        @Override
        public boolean reverseRemoveAllFrom(ObjSet<?> s) {
            if ((KolobokeLongIntHashMap.EntryView.this.isEmpty()) || (s.isEmpty()))
                return false;
            
            boolean changed = false;
            KolobokeLongIntHashMap.ReusableEntry e = new KolobokeLongIntHashMap.ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    changed |= s.remove(e.with(key, vals[i]));
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Long, Integer>> c) {
            if (KolobokeLongIntHashMap.EntryView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    changed |= c.add(new KolobokeLongIntHashMap.MutableEntry(mc, i, key, vals[i]));
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        public int hashCode() {
            return KolobokeLongIntHashMap.this.hashCode();
        }

        @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
        @Override
        public String toString() {
            if (KolobokeLongIntHashMap.EntryView.this.isEmpty())
                return "[]";
            
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    sb.append(vals[i]);
                    sb.append(',');
                    if ((++elementCount) == 8) {
                        int expectedLength = (sb.length()) * ((size()) / 8);
                        sb.ensureCapacity((expectedLength + (expectedLength / 2)));
                    } 
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            sb.setCharAt(0, '[');
            sb.setCharAt(((sb.length()) - 1), ']');
            return sb.toString();
        }

        @Override
        public boolean shrink() {
            return KolobokeLongIntHashMap.this.shrink();
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Long, Integer> e = ((Map.Entry<Long, Integer>) (o));
                long key = e.getKey();
                int value = e.getValue();
                return KolobokeLongIntHashMap.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        public final boolean removeIf(@Nonnull
        Predicate<? super Map.Entry<Long, Integer>> filter) {
            if (filter == null)
                throw new NullPointerException();
            
            if (KolobokeLongIntHashMap.EntryView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (filter.test(new KolobokeLongIntHashMap.MutableEntry(mc, i, key, vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @SuppressWarnings(value = "unchecked")
        @Override
        public final boolean removeAll(@Nonnull
        Collection<?> c) {
            if (c instanceof InternalObjCollectionOps) {
                InternalObjCollectionOps c2 = ((InternalObjCollectionOps) (c));
                if ((equivalence().equals(c2.equivalence())) && ((c2.size()) < (KolobokeLongIntHashMap.EntryView.this.size()))) {
                    c2.reverseRemoveAllFrom(KolobokeLongIntHashMap.EntryView.this);
                } 
            } 
            if ((KolobokeLongIntHashMap.EntryView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if ((KolobokeLongIntHashMap.EntryView.this.isEmpty()) || (c.isEmpty()))
                return false;
            
            boolean changed = false;
            KolobokeLongIntHashMap.ReusableEntry e = new KolobokeLongIntHashMap.ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (c.contains(e.with(key, vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        public final boolean retainAll(@Nonnull
        Collection<?> c) {
            if ((KolobokeLongIntHashMap.EntryView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if (KolobokeLongIntHashMap.EntryView.this.isEmpty())
                return false;
            
            if (c.isEmpty()) {
                clear();
                return true;
            } 
            boolean changed = false;
            KolobokeLongIntHashMap.ReusableEntry e = new KolobokeLongIntHashMap.ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (!(c.contains(e.with(key, vals[i])))) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        public void clear() {
            KolobokeLongIntHashMap.this.doClear();
        }
    }

    abstract class LongIntEntry extends AbstractEntry<Long, Integer> {
        abstract long key();

        @Override
        public final Long getKey() {
            return key();
        }

        abstract int value();

        @Override
        public final Integer getValue() {
            return value();
        }

        @SuppressWarnings(value = "unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            long k2;
            int v2;
            try {
                e2 = ((Map.Entry) (o));
                k2 = ((Long) (e2.getKey()));
                v2 = ((Integer) (e2.getValue()));
                return ((key()) == k2) && ((value()) == v2);
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return (Primitives.hashCode(key())) ^ (Primitives.hashCode(value()));
        }
    }

    class MutableEntry extends KolobokeLongIntHashMap.LongIntEntry {
        final int modCount;

        private final int index;

        final long key;

        private int value;

        MutableEntry(int modCount, int index, long key, int value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            KolobokeLongIntHashMap.MutableEntry.this.value = value;
        }

        @Override
        public long key() {
            return key;
        }

        @Override
        public int value() {
            return value;
        }

        @Override
        public Integer setValue(Integer newValue) {
            if ((modCount) != (modCount()))
                throw new IllegalStateException();
            
            int oldValue = value;
            int unwrappedNewValue = newValue;
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(int newValue) {
            values[index] = newValue;
        }
    }

    class ReusableEntry extends KolobokeLongIntHashMap.LongIntEntry {
        private long key;

        private int value;

        KolobokeLongIntHashMap.ReusableEntry with(long key, int value) {
            KolobokeLongIntHashMap.ReusableEntry.this.key = key;
            KolobokeLongIntHashMap.ReusableEntry.this.value = value;
            return KolobokeLongIntHashMap.ReusableEntry.this;
        }

        @Override
        public long key() {
            return key;
        }

        @Override
        public int value() {
            return value;
        }
    }

    class ValueView extends AbstractIntValueView {
        @Override
        public int size() {
            return KolobokeLongIntHashMap.this.size();
        }

        @Override
        public boolean shrink() {
            return KolobokeLongIntHashMap.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return KolobokeLongIntHashMap.this.containsValue(o);
        }

        @Override
        public boolean contains(int v) {
            return KolobokeLongIntHashMap.this.containsValue(v);
        }

        public void forEach(Consumer<? super Integer> action) {
            if (action == null)
                throw new NullPointerException();
            
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return ;
            
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    action.accept(vals[i]);
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
        }

        @Override
        public void forEach(IntConsumer action) {
            if (action == null)
                throw new NullPointerException();
            
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return ;
            
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    action.accept(vals[i]);
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
        }

        @Override
        public boolean forEachWhile(IntPredicate predicate) {
            if (predicate == null)
                throw new NullPointerException();
            
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return true;
            
            boolean terminated = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    if (!(predicate.test(vals[i]))) {
                        terminated = true;
                        break;
                    } 
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return !terminated;
        }

        @Override
        public boolean allContainingIn(IntCollection c) {
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return true;
            
            boolean containsAll = true;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    if (!(c.contains(vals[i]))) {
                        containsAll = false;
                        break;
                    } 
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return containsAll;
        }

        @Override
        public boolean reverseAddAllTo(IntCollection c) {
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    changed |= c.add(vals[i]);
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        public boolean reverseRemoveAllFrom(IntSet s) {
            if ((KolobokeLongIntHashMap.ValueView.this.isEmpty()) || (s.isEmpty()))
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    changed |= s.removeInt(vals[i]);
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        @Nonnull
        public IntIterator iterator() {
            int mc = modCount();
            return new KolobokeLongIntHashMap.NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public IntCursor cursor() {
            int mc = modCount();
            return new KolobokeLongIntHashMap.NoRemovedValueCursor(mc);
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            int size = size();
            Object[] result = new Object[size];
            if (size == 0)
                return result;
            
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    result[(resultIndex++)] = vals[i];
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return result;
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        @Nonnull
        public <T>  T[] toArray(@Nonnull
        T[] a) {
            int size = size();
            if ((a.length) < size) {
                Class<?> elementType = a.getClass().getComponentType();
                a = ((T[]) (Array.newInstance(elementType, size)));
            } 
            if (size == 0) {
                if ((a.length) > 0)
                    a[0] = null;
                
                return a;
            } 
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    a[(resultIndex++)] = ((T) (Integer.valueOf(vals[i])));
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            if ((a.length) > resultIndex)
                a[resultIndex] = null;
            
            return a;
        }

        @Override
        public int[] toIntArray() {
            int size = size();
            int[] result = new int[size];
            if (size == 0)
                return result;
            
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    result[(resultIndex++)] = vals[i];
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return result;
        }

        @Override
        public int[] toArray(int[] a) {
            int size = size();
            if ((a.length) < size)
                a = new int[size];
            
            if (size == 0) {
                if ((a.length) > 0)
                    a[0] = 0;
                
                return a;
            } 
            int resultIndex = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    a[(resultIndex++)] = vals[i];
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            if ((a.length) > resultIndex)
                a[resultIndex] = 0;
            
            return a;
        }

        @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
        @Override
        public String toString() {
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return "[]";
            
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    sb.append(' ').append(vals[i]).append(',');
                    if ((++elementCount) == 8) {
                        int expectedLength = (sb.length()) * ((size()) / 8);
                        sb.ensureCapacity((expectedLength + (expectedLength / 2)));
                    } 
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            sb.setCharAt(0, '[');
            sb.setCharAt(((sb.length()) - 1), ']');
            return sb.toString();
        }

        @Override
        public boolean remove(Object o) {
            return removeInt(((Integer) (o)));
        }

        @Override
        public boolean removeInt(int v) {
            return removeValue(v);
        }

        @Override
        public void clear() {
            KolobokeLongIntHashMap.this.clear();
        }

        public boolean removeIf(Predicate<? super Integer> filter) {
            if (filter == null)
                throw new NullPointerException();
            
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (filter.test(vals[i])) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        public boolean removeIf(IntPredicate filter) {
            if (filter == null)
                throw new NullPointerException();
            
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (filter.test(vals[i])) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        public boolean removeAll(@Nonnull
        Collection<?> c) {
            if (c instanceof IntCollection)
                return removeAll(((IntCollection) (c)));
            
            if ((KolobokeLongIntHashMap.ValueView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if ((KolobokeLongIntHashMap.ValueView.this.isEmpty()) || (c.isEmpty()))
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (c.contains(vals[i])) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        private boolean removeAll(IntCollection c) {
            if ((KolobokeLongIntHashMap.ValueView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if ((KolobokeLongIntHashMap.ValueView.this.isEmpty()) || (c.isEmpty()))
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (c.contains(vals[i])) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        public boolean retainAll(@Nonnull
        Collection<?> c) {
            if (c instanceof IntCollection)
                return retainAll(((IntCollection) (c)));
            
            if ((KolobokeLongIntHashMap.ValueView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return false;
            
            if (c.isEmpty()) {
                clear();
                return true;
            } 
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (!(c.contains(vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        private boolean retainAll(IntCollection c) {
            if ((KolobokeLongIntHashMap.ValueView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if (KolobokeLongIntHashMap.ValueView.this.isEmpty())
                return false;
            
            if (c.isEmpty()) {
                clear();
                return true;
            } 
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            int[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (!(c.contains(vals[i]))) {
                        incrementModCount();
                        mc++;
                        closeDeletion : if (firstDelayedRemoved < 0) {
                            int indexToRemove = i;
                            int indexToShift = indexToRemove;
                            int shiftDistance = 1;
                            while (true) {
                                indexToShift = (indexToShift - 1) & capacityMask;
                                long keyToShift;
                                if ((keyToShift = keys[indexToShift]) == free) {
                                    break;
                                } 
                                if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                    if (indexToShift > indexToRemove) {
                                        firstDelayedRemoved = i;
                                        delayedRemoved = key;
                                        keys[indexToRemove] = key;
                                        break closeDeletion;
                                    } 
                                    if (indexToRemove == i) {
                                        i++;
                                    } 
                                    keys[indexToRemove] = keyToShift;
                                    vals[indexToRemove] = vals[indexToShift];
                                    indexToRemove = indexToShift;
                                    shiftDistance = 1;
                                } else {
                                    shiftDistance++;
                                    if (indexToShift == (1 + i)) {
                                        throw new ConcurrentModificationException();
                                    } 
                                }
                            }
                            keys[indexToRemove] = free;
                            postRemoveHook();
                        } else {
                            keys[i] = delayedRemoved;
                        }
                        changed = true;
                    } 
                } 
            }
            if (firstDelayedRemoved >= 0) {
                closeDelayedRemoved(firstDelayedRemoved, delayedRemoved);
            } 
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }
    }

    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Long, Integer>> {
        long[] keys;

        int[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        class MutableEntry2 extends KolobokeLongIntHashMap.MutableEntry {
            MutableEntry2(int modCount, int index, long key, int value) {
                super(modCount, index, key, value);
            }

            @Override
            void updateValueInTable(int newValue) {
                if ((vals) == (values)) {
                    vals[index] = newValue;
                } else {
                    justPut(key, newValue);
                    if ((KolobokeLongIntHashMap.NoRemovedEntryIterator.MutableEntry2.this.modCount) != (modCount())) {
                        throw new IllegalStateException();
                    } 
                }
            }
        }

        int index = -1;

        int nextIndex;

        KolobokeLongIntHashMap.MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.keys = set;
            capacityMask = (keys.length) - 1;
            int[] vals = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.vals = values;
            long free = this.free = freeValue;
            int nextI = keys.length;
            while ((--nextI) >= 0) {
                long key;
                if ((key = keys[nextI]) != free) {
                    next = new KolobokeLongIntHashMap.NoRemovedEntryIterator.MutableEntry2(mc, nextI, key, vals[nextI]);
                    break;
                } 
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull
        Consumer<? super Map.Entry<Long, Integer>> action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.keys;
            int[] vals = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.vals;
            long free = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(new KolobokeLongIntHashMap.NoRemovedEntryIterator.MutableEntry2(mc, i, key, vals[i]));
                } 
            }
            if ((nextI != (nextIndex)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return (nextIndex) >= 0;
        }

        @Override
        public Map.Entry<Long, Integer> next() {
            int mc;
            if ((mc = expectedModCount) == (modCount())) {
                int nextI;
                if ((nextI = nextIndex) >= 0) {
                    index = nextI;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.keys;
                    long free = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.free;
                    KolobokeLongIntHashMap.MutableEntry prev = next;
                    while ((--nextI) >= 0) {
                        long key;
                        if ((key = keys[nextI]) != free) {
                            next = new KolobokeLongIntHashMap.NoRemovedEntryIterator.MutableEntry2(mc, nextI, key, vals[nextI]);
                            break;
                        } 
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new NoSuchElementException();
                }
            } else {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            int index;
            if ((index = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.index) >= 0) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedEntryIterator.this.index = -1;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.keys;
                    int[] vals = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedEntryIterator.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == (free)) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedEntryIterator.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = (nextIndex) + 1) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedEntryIterator.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongIntHashMap.NoRemovedEntryIterator.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedEntryIterator.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedEntryIterator.this.nextIndex = index;
                                        if (indexToShift < (index - 1)) {
                                            KolobokeLongIntHashMap.NoRemovedEntryIterator.this.next = new KolobokeLongIntHashMap.NoRemovedEntryIterator.MutableEntry2(modCount(), indexToShift, keyToShift, vals[indexToShift]);
                                        } 
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Long, Integer>> {
        long[] keys;

        int[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        class MutableEntry2 extends KolobokeLongIntHashMap.MutableEntry {
            MutableEntry2(int modCount, int index, long key, int value) {
                super(modCount, index, key, value);
            }

            @Override
            void updateValueInTable(int newValue) {
                if ((vals) == (values)) {
                    vals[index] = newValue;
                } else {
                    justPut(key, newValue);
                    if ((KolobokeLongIntHashMap.NoRemovedEntryCursor.MutableEntry2.this.modCount) != (modCount())) {
                        throw new IllegalStateException();
                    } 
                }
            }
        }

        int index;

        long curKey;

        int curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.keys = set;
            capacityMask = (keys.length) - 1;
            index = keys.length;
            vals = values;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Long, Integer>> action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.keys;
            int[] vals = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.vals;
            long free = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.free;
            int index = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.index;
            for (int i = index - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(new KolobokeLongIntHashMap.NoRemovedEntryCursor.MutableEntry2(mc, i, key, vals[i]));
                } 
            }
            if ((index != (KolobokeLongIntHashMap.NoRemovedEntryCursor.this.index)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            KolobokeLongIntHashMap.NoRemovedEntryCursor.this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Long, Integer> elem() {
            long curKey;
            if ((curKey = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.curKey) != (free)) {
                return new KolobokeLongIntHashMap.NoRemovedEntryCursor.MutableEntry2(expectedModCount, index, curKey, curValue);
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if ((expectedModCount) == (modCount())) {
                long[] keys = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.keys;
                long free = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.free;
                for (int i = (index) - 1; i >= 0; i--) {
                    long key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    } 
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long curKey;
            long free;
            if ((curKey = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.curKey) != (free = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedEntryCursor.this.curKey = free;
                    int index = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.index;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.keys;
                    int[] vals = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedEntryCursor.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedEntryCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedEntryCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongIntHashMap.NoRemovedEntryCursor.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedEntryCursor.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedEntryCursor.this.index = ++index;
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedValueIterator implements IntIterator {
        long[] keys;

        int[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        int index = -1;

        int nextIndex;

        int next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongIntHashMap.NoRemovedValueIterator.this.keys = set;
            capacityMask = (keys.length) - 1;
            int[] vals = KolobokeLongIntHashMap.NoRemovedValueIterator.this.vals = values;
            long free = this.free = freeValue;
            int nextI = keys.length;
            while ((--nextI) >= 0) {
                if ((keys[nextI]) != free) {
                    next = vals[nextI];
                    break;
                } 
            }
            nextIndex = nextI;
        }

        @Override
        public int nextInt() {
            if ((expectedModCount) == (modCount())) {
                int nextI;
                if ((nextI = nextIndex) >= 0) {
                    index = nextI;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedValueIterator.this.keys;
                    long free = KolobokeLongIntHashMap.NoRemovedValueIterator.this.free;
                    int prev = next;
                    while ((--nextI) >= 0) {
                        if ((keys[nextI]) != free) {
                            next = vals[nextI];
                            break;
                        } 
                    }
                    nextIndex = nextI;
                    return prev;
                } else {
                    throw new NoSuchElementException();
                }
            } else {
                throw new ConcurrentModificationException();
            }
        }

        public void forEachRemaining(Consumer<? super Integer> action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedValueIterator.this.keys;
            int[] vals = KolobokeLongIntHashMap.NoRemovedValueIterator.this.vals;
            long free = KolobokeLongIntHashMap.NoRemovedValueIterator.this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if ((keys[i]) != free) {
                    action.accept(vals[i]);
                } 
            }
            if ((nextI != (nextIndex)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            index = nextIndex = -1;
        }

        @Override
        public void forEachRemaining(IntConsumer action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedValueIterator.this.keys;
            int[] vals = KolobokeLongIntHashMap.NoRemovedValueIterator.this.vals;
            long free = KolobokeLongIntHashMap.NoRemovedValueIterator.this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                if ((keys[i]) != free) {
                    action.accept(vals[i]);
                } 
            }
            if ((nextI != (nextIndex)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            index = nextIndex = -1;
        }

        @Override
        public boolean hasNext() {
            return (nextIndex) >= 0;
        }

        @Override
        public Integer next() {
            return nextInt();
        }

        @Override
        public void remove() {
            int index;
            if ((index = KolobokeLongIntHashMap.NoRemovedValueIterator.this.index) >= 0) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedValueIterator.this.index = -1;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedValueIterator.this.keys;
                    int[] vals = KolobokeLongIntHashMap.NoRemovedValueIterator.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedValueIterator.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == (free)) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedValueIterator.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = (nextIndex) + 1) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedValueIterator.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongIntHashMap.NoRemovedValueIterator.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedValueIterator.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedValueIterator.this.nextIndex = index;
                                        if (indexToShift < (index - 1)) {
                                            KolobokeLongIntHashMap.NoRemovedValueIterator.this.next = vals[indexToShift];
                                        } 
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedValueCursor implements IntCursor {
        long[] keys;

        int[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        int index;

        long curKey;

        int curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongIntHashMap.NoRemovedValueCursor.this.keys = set;
            capacityMask = (keys.length) - 1;
            index = keys.length;
            vals = values;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(IntConsumer action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedValueCursor.this.keys;
            int[] vals = KolobokeLongIntHashMap.NoRemovedValueCursor.this.vals;
            long free = KolobokeLongIntHashMap.NoRemovedValueCursor.this.free;
            int index = KolobokeLongIntHashMap.NoRemovedValueCursor.this.index;
            for (int i = index - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    action.accept(vals[i]);
                } 
            }
            if ((index != (KolobokeLongIntHashMap.NoRemovedValueCursor.this.index)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            KolobokeLongIntHashMap.NoRemovedValueCursor.this.index = -1;
            curKey = free;
        }

        @Override
        public int elem() {
            if ((curKey) != (free)) {
                return curValue;
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if ((expectedModCount) == (modCount())) {
                long[] keys = KolobokeLongIntHashMap.NoRemovedValueCursor.this.keys;
                long free = KolobokeLongIntHashMap.NoRemovedValueCursor.this.free;
                for (int i = (index) - 1; i >= 0; i--) {
                    long key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    } 
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long curKey;
            long free;
            if ((curKey = KolobokeLongIntHashMap.NoRemovedValueCursor.this.curKey) != (free = KolobokeLongIntHashMap.NoRemovedValueCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedValueCursor.this.curKey = free;
                    int index = KolobokeLongIntHashMap.NoRemovedValueCursor.this.index;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedValueCursor.this.keys;
                    int[] vals = KolobokeLongIntHashMap.NoRemovedValueCursor.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedValueCursor.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedValueCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedValueCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongIntHashMap.NoRemovedValueCursor.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedValueCursor.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedValueCursor.this.index = ++index;
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedMapCursor implements LongIntCursor {
        long[] keys;

        int[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        int index;

        long curKey;

        int curValue;

        NoRemovedMapCursor(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongIntHashMap.NoRemovedMapCursor.this.keys = set;
            capacityMask = (keys.length) - 1;
            index = keys.length;
            vals = values;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(LongIntConsumer action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongIntHashMap.NoRemovedMapCursor.this.keys;
            int[] vals = KolobokeLongIntHashMap.NoRemovedMapCursor.this.vals;
            long free = KolobokeLongIntHashMap.NoRemovedMapCursor.this.free;
            int index = KolobokeLongIntHashMap.NoRemovedMapCursor.this.index;
            for (int i = index - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(key, vals[i]);
                } 
            }
            if ((index != (KolobokeLongIntHashMap.NoRemovedMapCursor.this.index)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            KolobokeLongIntHashMap.NoRemovedMapCursor.this.index = -1;
            curKey = free;
        }

        @Override
        public long key() {
            long curKey;
            if ((curKey = KolobokeLongIntHashMap.NoRemovedMapCursor.this.curKey) != (free)) {
                return curKey;
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public int value() {
            if ((curKey) != (free)) {
                return curValue;
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public void setValue(int value) {
            if ((curKey) != (free)) {
                if ((expectedModCount) == (modCount())) {
                    vals[index] = value;
                    if ((vals) != (values)) {
                        values[index] = value;
                    } 
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if ((expectedModCount) == (modCount())) {
                long[] keys = KolobokeLongIntHashMap.NoRemovedMapCursor.this.keys;
                long free = KolobokeLongIntHashMap.NoRemovedMapCursor.this.free;
                for (int i = (index) - 1; i >= 0; i--) {
                    long key;
                    if ((key = keys[i]) != free) {
                        index = i;
                        curKey = key;
                        curValue = vals[i];
                        return true;
                    } 
                }
                curKey = free;
                index = -1;
                return false;
            } else {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void remove() {
            long curKey;
            long free;
            if ((curKey = KolobokeLongIntHashMap.NoRemovedMapCursor.this.curKey) != (free = KolobokeLongIntHashMap.NoRemovedMapCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongIntHashMap.NoRemovedMapCursor.this.curKey = free;
                    int index = KolobokeLongIntHashMap.NoRemovedMapCursor.this.index;
                    long[] keys = KolobokeLongIntHashMap.NoRemovedMapCursor.this.keys;
                    int[] vals = KolobokeLongIntHashMap.NoRemovedMapCursor.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongIntHashMap.NoRemovedMapCursor.this.capacityMask;
                        incrementModCount();
                        int indexToRemove = index;
                        int indexToShift = indexToRemove;
                        int shiftDistance = 1;
                        while (true) {
                            indexToShift = (indexToShift - 1) & capacityMask;
                            long keyToShift;
                            if ((keyToShift = keys[indexToShift]) == free) {
                                break;
                            } 
                            if ((((LHash.SeparateKVLongKeyMixing.mix(keyToShift)) - indexToShift) & capacityMask) >= shiftDistance) {
                                if ((KolobokeLongIntHashMap.NoRemovedMapCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongIntHashMap.NoRemovedMapCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongIntHashMap.NoRemovedMapCursor.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongIntHashMap.NoRemovedMapCursor.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongIntHashMap.NoRemovedMapCursor.this.index = ++index;
                                    } 
                                } 
                                keys[indexToRemove] = keyToShift;
                                vals[indexToRemove] = vals[indexToShift];
                                indexToRemove = indexToShift;
                                shiftDistance = 1;
                            } else {
                                shiftDistance++;
                                if (indexToShift == (1 + index)) {
                                    throw new ConcurrentModificationException();
                                } 
                            }
                        }
                        keys[indexToRemove] = free;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    KolobokeLongIntHashMap(HashConfig hashConfig, int expectedSize) {
        this.init(new HashConfigWrapper(hashConfig), expectedSize);
    }

    static class Support {
        static interface LongHash extends Hash {
            long freeValue();

            boolean supportRemoved();

            long removedValue();
        }

        static interface SeparateKVLongLHash extends LHash , KolobokeLongIntHashMap.Support.SeparateKVLongHash {        }

        static interface SeparateKVLongHash extends KolobokeLongIntHashMap.Support.LongHash {
            @Nonnull
            long[] keys();
        }
    }

    static final HashConfigWrapper DEFAULT_CONFIG_WRAPPER = new HashConfigWrapper(HashConfig.getDefault());
}