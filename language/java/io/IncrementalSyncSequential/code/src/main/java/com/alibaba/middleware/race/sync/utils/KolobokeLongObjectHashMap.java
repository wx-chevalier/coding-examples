

package com.alibaba.middleware.race.sync.utils;

import com.koloboke.collect.impl.AbstractEntry;
import com.koloboke.collect.impl.AbstractLongKeyView;
import com.koloboke.collect.impl.AbstractObjValueView;
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
import com.koloboke.collect.impl.InternalLongCollectionOps;
import com.koloboke.collect.impl.InternalLongObjMapOps;
import com.koloboke.collect.impl.InternalObjCollectionOps;
import com.koloboke.collect.impl.hash.LHash;
import com.koloboke.collect.impl.hash.LHashCapacities;
import com.koloboke.collect.impl.LongArrays;
import com.koloboke.collect.LongCollection;
import com.koloboke.function.LongConsumer;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.LongIterator;
import com.koloboke.function.LongObjConsumer;
import com.koloboke.collect.map.LongObjMap;
import com.koloboke.function.LongObjPredicate;
import com.koloboke.function.LongPredicate;
import com.koloboke.collect.set.LongSet;
import java.util.Map;
import com.koloboke.collect.impl.Maths;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
final class KolobokeLongObjectHashMap<V>  extends LongObjectHashMap<V> {
    KolobokeLongObjectHashMap(int expectedSize) {
        this.init(DEFAULT_CONFIG_WRAPPER, expectedSize);
    }

    static void verifyConfig(HashConfig config) {
        if ((config.getGrowthFactor()) != 2.0) {
            throw new IllegalArgumentException(((((((config + " passed, HashConfig for a hashtable\n") + "implementation with linear probing must have growthFactor of 2.0.\n") + "A Koloboke Compile-generated hashtable implementation could have\n") + "a different growth factor, if the implemented type is annotated with\n") + "@com.koloboke.compile.hash.algo.openaddressing.QuadraticProbing or\n") + "@com.koloboke.compile.hash.algo.openaddressing.DoubleHashing"));
        } 
    }

    long freeValue;

    @Nonnull
    public final HashConfig hashConfig() {
        return configWrapper().config();
    }

    long[] set;

    public long sizeAsLong() {
        return ((long) (size()));
    }

    V[] values;

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

    int size;

    public final int removedSlots() {
        return 0;
    }

    private int maxSize;

    private int modCount = 0;

    public int capacity() {
        return set.length;
    }

    @Nonnull
    public HashLongSet keySet() {
        return new KolobokeLongObjectHashMap.KeyView();
    }

    public final double currentLoad() {
        return ((double) (size())) / ((double) (capacity()));
    }

    final void init(HashConfigWrapper configWrapper, int size, long freeValue) {
        KolobokeLongObjectHashMap.this.freeValue = freeValue;
        init(configWrapper, size);
    }

    public void forEach(Consumer<? super Long> action) {
        if (action == null)
            throw new NullPointerException();
        
        if (KolobokeLongObjectHashMap.this.isEmpty())
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

    boolean nullableValueEquals(@Nullable
    V a, @Nullable
    V b) {
        return (a == b) || ((a != null) && (valueEquals(a, b)));
    }

    public final HashConfigWrapper configWrapper() {
        return configWrapper;
    }

    class KeyView extends AbstractLongKeyView implements HashLongSet , InternalLongCollectionOps , KolobokeLongObjectHashMap.Support.SeparateKVLongLHash {
        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return KolobokeLongObjectHashMap.this.hashConfig();
        }

        @Override
        public HashConfigWrapper configWrapper() {
            return KolobokeLongObjectHashMap.this.configWrapper();
        }

        @Override
        public int size() {
            return KolobokeLongObjectHashMap.this.size();
        }

        @Override
        public double currentLoad() {
            return KolobokeLongObjectHashMap.this.currentLoad();
        }

        @Override
        public long freeValue() {
            return KolobokeLongObjectHashMap.this.freeValue();
        }

        @Override
        public boolean supportRemoved() {
            return KolobokeLongObjectHashMap.this.supportRemoved();
        }

        @Override
        public long removedValue() {
            return KolobokeLongObjectHashMap.this.removedValue();
        }

        @Nonnull
        @Override
        public long[] keys() {
            return KolobokeLongObjectHashMap.this.keys();
        }

        @Override
        public int capacity() {
            return KolobokeLongObjectHashMap.this.capacity();
        }

        @Override
        public int freeSlots() {
            return KolobokeLongObjectHashMap.this.freeSlots();
        }

        @Override
        public boolean noRemoved() {
            return KolobokeLongObjectHashMap.this.noRemoved();
        }

        @Override
        public int removedSlots() {
            return KolobokeLongObjectHashMap.this.removedSlots();
        }

        @Override
        public int modCount() {
            return KolobokeLongObjectHashMap.this.modCount();
        }

        @Override
        public final boolean contains(Object o) {
            return KolobokeLongObjectHashMap.this.contains(o);
        }

        @Override
        public boolean contains(long key) {
            return KolobokeLongObjectHashMap.this.contains(key);
        }

        public void forEach(Consumer<? super Long> action) {
            KolobokeLongObjectHashMap.this.forEach(action);
        }

        @Override
        public void forEach(LongConsumer action) {
            KolobokeLongObjectHashMap.this.forEach(action);
        }

        @Override
        public boolean forEachWhile(LongPredicate predicate) {
            return KolobokeLongObjectHashMap.this.forEachWhile(predicate);
        }

        @Override
        public boolean allContainingIn(LongCollection c) {
            return KolobokeLongObjectHashMap.this.allContainingIn(c);
        }

        @Override
        public boolean reverseAddAllTo(LongCollection c) {
            return KolobokeLongObjectHashMap.this.reverseAddAllTo(c);
        }

        @Override
        public boolean reverseRemoveAllFrom(LongSet s) {
            return KolobokeLongObjectHashMap.this.reverseRemoveAllFrom(s);
        }

        @Override
        @Nonnull
        public LongIterator iterator() {
            return KolobokeLongObjectHashMap.this.iterator();
        }

        @Override
        @Nonnull
        public LongCursor cursor() {
            return setCursor();
        }

        @Override
        @Nonnull
        public Object[] toArray() {
            return KolobokeLongObjectHashMap.this.toArray();
        }

        @Override
        @Nonnull
        public <T>  T[] toArray(@Nonnull
        T[] a) {
            return KolobokeLongObjectHashMap.this.toArray(a);
        }

        @Override
        public long[] toLongArray() {
            return KolobokeLongObjectHashMap.this.toLongArray();
        }

        @Override
        public long[] toArray(long[] a) {
            return KolobokeLongObjectHashMap.this.toArray(a);
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
            return KolobokeLongObjectHashMap.this.shrink();
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
            return KolobokeLongObjectHashMap.this.removeIf(filter);
        }

        @Override
        public boolean removeIf(LongPredicate filter) {
            return KolobokeLongObjectHashMap.this.removeIf(filter);
        }

        @Override
        public boolean removeAll(@Nonnull
        Collection<?> c) {
            if (c instanceof LongCollection) {
                if (c instanceof InternalLongCollectionOps) {
                    InternalLongCollectionOps c2 = ((InternalLongCollectionOps) (c));
                    if ((c2.size()) < (KolobokeLongObjectHashMap.KeyView.this.size())) {
                        return c2.reverseRemoveAllFrom(KolobokeLongObjectHashMap.KeyView.this);
                    } 
                } 
                return KolobokeLongObjectHashMap.this.removeAll(KolobokeLongObjectHashMap.KeyView.this, ((LongCollection) (c)));
            } 
            return KolobokeLongObjectHashMap.this.removeAll(KolobokeLongObjectHashMap.KeyView.this, c);
        }

        @Override
        public boolean retainAll(@Nonnull
        Collection<?> c) {
            return KolobokeLongObjectHashMap.this.retainAll(KolobokeLongObjectHashMap.KeyView.this, c);
        }

        @Override
        public void clear() {
            KolobokeLongObjectHashMap.this.clear();
        }
    }

    boolean valueEquals(@Nonnull
    V a, @Nullable
    V b) {
        return a.equals(b);
    }

    public long freeValue() {
        return freeValue;
    }

    @Override
    public final int size() {
        return size;
    }

    int nullableValueHashCode(@Nullable
    V value) {
        return value != null ? valueHashCode(value) : 0;
    }

    public boolean supportRemoved() {
        return false;
    }

    public final int modCount() {
        return modCount;
    }

    int valueHashCode(@Nonnull
    V value) {
        return value.hashCode();
    }

    final void incrementModCount() {
        (modCount)++;
    }

    public long removedValue() {
        throw new UnsupportedOperationException();
    }

    public void forEach(LongConsumer action) {
        if (action == null)
            throw new NullPointerException();
        
        if (KolobokeLongObjectHashMap.this.isEmpty())
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

    int valueIndex(@Nullable
    Object value) {
        if (value == null)
            return nullValueIndex();
        
        if (KolobokeLongObjectHashMap.this.isEmpty())
            return -1;
        
        V val = ((V) (value));
        int index = -1;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        V[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            if ((keys[i]) != free) {
                if (valueEquals(val, vals[i])) {
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
    public Equivalence<V> valueEquivalence() {
        return Equivalence.defaultEquality();
    }

    public boolean contains(Object key) {
        return contains(((Long) (key)).longValue());
    }

    public boolean contains(long key) {
        return (index(key)) >= 0;
    }

    public boolean containsEntry(long key, Object value) {
        int index = index(key);
        if (index >= 0) {
            return nullableValueEquals(values[index], ((V) (value)));
        } else {
            return false;
        }
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
        KolobokeLongObjectHashMap.verifyConfig(configWrapper.config());
        KolobokeLongObjectHashMap.this.configWrapper = configWrapper;
        KolobokeLongObjectHashMap.this.size = 0;
        internalInit(targetCapacity(size));
    }

    public boolean forEachWhile(LongPredicate predicate) {
        if (predicate == null)
            throw new NullPointerException();
        
        if (KolobokeLongObjectHashMap.this.isEmpty())
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
    public V get(Object key) {
        int index = index(((Long) (key)));
        if (index >= 0) {
            return values[index];
        } else {
            return null;
        }
    }

    private void internalInit(int capacity) {
        assert Maths.isPowerOf2(capacity);
        maxSize = maxSize(capacity);
        allocateArrays(capacity);
    }

    private int nullValueIndex() {
        if (KolobokeLongObjectHashMap.this.isEmpty())
            return -1;
        
        int index = -1;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        V[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            if ((keys[i]) != free) {
                if ((vals[i]) == null) {
                    index = i;
                    break;
                } 
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return index;
    }

    private int maxSize(int capacity) {
        return !(isMaxCapacity(capacity)) ? configWrapper.maxSize(capacity) : capacity - 1;
    }

    @Override
    public V get(long key) {
        int index = index(key);
        if (index >= 0) {
            return values[index];
        } else {
            return null;
        }
    }

    private long findNewFreeOrRemoved() {
        long free = KolobokeLongObjectHashMap.this.freeValue;
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
        if (KolobokeLongObjectHashMap.this.isEmpty())
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
    public boolean containsValue(Object value) {
        return (valueIndex(value)) >= 0;
    }

    boolean removeValue(@Nullable
    Object value) {
        int index = valueIndex(value);
        if (index >= 0) {
            removeAt(index);
            return true;
        } else {
            return false;
        }
    }

    long changeFree() {
        int mc = modCount();
        long newFree = findNewFreeOrRemoved();
        incrementModCount();
        mc++;
        LongArrays.replaceAll(set, freeValue, newFree);
        KolobokeLongObjectHashMap.this.freeValue = newFree;
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return newFree;
    }

    int insert(long key, V value) {
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

    public boolean reverseAddAllTo(LongCollection c) {
        if (KolobokeLongObjectHashMap.this.isEmpty())
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
        if ((KolobokeLongObjectHashMap.this.isEmpty()) || (s.isEmpty()))
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

    public boolean shrink() {
        int newCapacity = targetCapacity(size);
        if (newCapacity < (capacity())) {
            rehash(newCapacity);
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings(value = "unchecked")
    void allocateArrays(int capacity) {
        _MutableSeparateKVLongLHashSO_allocateArrays(capacity);
        values = ((V[]) (new Object[capacity]));
    }

    private boolean tryRehashForExpansion(int newCapacity) {
        if (newCapacity > (capacity())) {
            rehash(newCapacity);
            return true;
        } else {
            return false;
        }
    }

    private void _MutableLHashSeparateKVLongObjMapSO_clear() {
        _MutableSeparateKVLongLHashSO_clear();
        Arrays.fill(values, null);
    }

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
        if (KolobokeLongObjectHashMap.identical(KolobokeLongObjectHashMap.this, m))
            throw new IllegalArgumentException();
        
        if (m instanceof LongObjMap) {
            LongObjMap m2 = ((LongObjMap) (m));
            if (m2.valueEquivalence().equals(KolobokeLongObjectHashMap.this.valueEquivalence())) {
                if ((KolobokeLongObjectHashMap.this.size()) < (m2.size()))
                    return false;
                
                if ((InternalLongObjMapOps.class.isAssignableFrom(getClass())) && (m2 instanceof InternalLongObjMapOps)) {
                    return ((InternalLongObjMapOps) (m2)).allEntriesContainingIn(((InternalLongObjMapOps<?>) (InternalLongObjMapOps.class.cast(KolobokeLongObjectHashMap.this))));
                } 
            } 
            return m2.forEachWhile(new LongObjPredicate() {
                @Override
                public boolean test(long a, Object b) {
                    return containsEntry(a, b);
                }
            });
        } 
        for (Map.Entry<?, ?> e : m.entrySet()) {
            if (!(containsEntry(((Long) (e.getKey())), e.getValue())))
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
    public HashObjSet<Map.Entry<Long, V>> entrySet() {
        return new EntryView();
    }

    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
    public String setToString() {
        if (KolobokeLongObjectHashMap.this.isEmpty())
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
    public ObjCollection<V> values() {
        return new ValueView();
    }

    @Override
    public boolean equals(Object o) {
        if ((KolobokeLongObjectHashMap.this) == o) {
            return true;
        } 
        if (!(o instanceof Map)) {
            return false;
        } 
        Map<?, ?> that = ((Map<?, ?>) (o));
        if ((that.size()) != (KolobokeLongObjectHashMap.this.size())) {
            return false;
        } 
        try {
            return KolobokeLongObjectHashMap.this.containsAllEntries(that);
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
        V[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                hashCode += ((int) ((key ^ (key >>> 32)))) ^ (nullableValueHashCode(vals[i]));
            } 
        }
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
        return hashCode;
    }

    @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
    @Override
    public String toString() {
        if (KolobokeLongObjectHashMap.this.isEmpty())
            return "{}";
        
        StringBuilder sb = new StringBuilder();
        int elementCount = 0;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        V[] vals = values;
        for (int i = (keys.length) - 1; i >= 0; i--) {
            long key;
            if ((key = keys[i]) != free) {
                sb.append(' ');
                sb.append(key);
                sb.append('=');
                Object val = vals[i];
                sb.append((val != ((Object) (KolobokeLongObjectHashMap.this)) ? val : "(this Map)"));
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
        V[] vals = values;
        initForRehash(newCapacity);
        mc++;
        long[] newKeys = set;
        int capacityMask = (newKeys.length) - 1;
        V[] newVals = values;
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
    public V put(Long key, V value) {
        int index = insert(key, value);
        if (index < 0) {
            return null;
        } else {
            V[] vals = values;
            V prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    @Override
    public V put(long key, V value) {
        int index = insert(key, value);
        if (index < 0) {
            return null;
        } else {
            V[] vals = values;
            V prevValue = vals[index];
            vals[index] = value;
            return prevValue;
        }
    }

    public void justPut(long key, V value) {
        int index = insert(key, value);
        if (index < 0) {
            return ;
        } else {
            values[index] = value;
            return ;
        }
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
            long[] keys = KolobokeLongObjectHashMap.NoRemovedIterator.this.keys = set;
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
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedIterator.this.keys;
                    long free = KolobokeLongObjectHashMap.NoRemovedIterator.this.free;
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
            long[] keys = KolobokeLongObjectHashMap.NoRemovedIterator.this.keys;
            long free = KolobokeLongObjectHashMap.NoRemovedIterator.this.free;
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
            long[] keys = KolobokeLongObjectHashMap.NoRemovedIterator.this.keys;
            long free = KolobokeLongObjectHashMap.NoRemovedIterator.this.free;
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
            if ((index = KolobokeLongObjectHashMap.NoRemovedIterator.this.index) >= 0) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongObjectHashMap.NoRemovedIterator.this.index = -1;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedIterator.this.keys;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongObjectHashMap.NoRemovedIterator.this.capacityMask;
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
                                if ((KolobokeLongObjectHashMap.NoRemovedIterator.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = (nextIndex) + 1) > 0) {
                                            KolobokeLongObjectHashMap.NoRemovedIterator.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongObjectHashMap.NoRemovedIterator.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongObjectHashMap.NoRemovedIterator.this.nextIndex = index;
                                        if (indexToShift < (index - 1)) {
                                            KolobokeLongObjectHashMap.NoRemovedIterator.this.next = keyToShift;
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

    @SuppressFBWarnings(value = "BC_IMPOSSIBLE_CAST")
    @Override
    public void putAll(@Nonnull
    Map<? extends Long, ? extends V> m) {
        if (KolobokeLongObjectHashMap.identical(KolobokeLongObjectHashMap.this, m))
            throw new IllegalArgumentException();
        
        long maxPossibleSize = (sizeAsLong()) + (Containers.sizeAsLong(m));
        ensureCapacity(maxPossibleSize);
        if (m instanceof LongObjMap) {
            if ((InternalLongObjMapOps.class.isAssignableFrom(getClass())) && (m instanceof InternalLongObjMapOps)) {
                ((InternalLongObjMapOps) (m)).reversePutAllTo(((InternalLongObjMapOps<? super V>) (InternalLongObjMapOps.class.cast(KolobokeLongObjectHashMap.this))));
            } else {
                ((LongObjMap) (m)).forEach(new LongObjConsumer<V>() {
                    @Override
                    public void accept(long key, V value) {
                        justPut(key, value);
                    }
                });
            }
        } else {
            for (Map.Entry<? extends Long, ? extends V> e : m.entrySet()) {
                justPut(e.getKey(), e.getValue());
            }
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
            long[] keys = KolobokeLongObjectHashMap.NoRemovedCursor.this.keys = set;
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
            long[] keys = KolobokeLongObjectHashMap.NoRemovedCursor.this.keys;
            long free = KolobokeLongObjectHashMap.NoRemovedCursor.this.free;
            int index = KolobokeLongObjectHashMap.NoRemovedCursor.this.index;
            for (int i = index - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(key);
                } 
            }
            if ((index != (KolobokeLongObjectHashMap.NoRemovedCursor.this.index)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            KolobokeLongObjectHashMap.NoRemovedCursor.this.index = -1;
            curKey = free;
        }

        @Override
        public long elem() {
            long curKey;
            if ((curKey = KolobokeLongObjectHashMap.NoRemovedCursor.this.curKey) != (free)) {
                return curKey;
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if ((expectedModCount) == (modCount())) {
                long[] keys = KolobokeLongObjectHashMap.NoRemovedCursor.this.keys;
                long free = KolobokeLongObjectHashMap.NoRemovedCursor.this.free;
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
            if ((curKey = KolobokeLongObjectHashMap.NoRemovedCursor.this.curKey) != (free = KolobokeLongObjectHashMap.NoRemovedCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongObjectHashMap.NoRemovedCursor.this.curKey = free;
                    int index = KolobokeLongObjectHashMap.NoRemovedCursor.this.index;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedCursor.this.keys;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongObjectHashMap.NoRemovedCursor.this.capacityMask;
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
                                if ((KolobokeLongObjectHashMap.NoRemovedCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongObjectHashMap.NoRemovedCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongObjectHashMap.NoRemovedCursor.this.keys[indexToRemove] = free;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongObjectHashMap.NoRemovedCursor.this.index = ++index;
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
    public void clear() {
        doClear();
    }

    private void doClear() {
        int mc = (modCount()) + 1;
        _MutableLHashSeparateKVLongObjMapSO_clear();
        if (mc != (modCount()))
            throw new ConcurrentModificationException();
        
    }

    void removeAt(int index) {
        long free = freeValue;
        long[] keys = set;
        V[] vals = values;
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
        vals[indexToRemove] = null;
        postRemoveHook();
    }

    @Override
    public V remove(Object key) {
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
            V[] vals = values;
            V val = vals[index];
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
            vals[indexToRemove] = null;
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
            V[] vals = values;
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
            vals[indexToRemove] = null;
            postRemoveHook();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public V remove(long key) {
        long free;
        if (key != (free = freeValue)) {
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int index;
            long cur;
            keyPresent : if ((cur = keys[(index = (LHash.SeparateKVLongKeyMixing.mix(key)) & capacityMask)]) != key) {
                if (cur == free) {
                    return null;
                } else {
                    while (true) {
                        if ((cur = keys[(index = (index - 1) & capacityMask)]) == key) {
                            break keyPresent;
                        } else if (cur == free) {
                            return null;
                        } 
                    }
                }
            } 
            V[] vals = values;
            V val = vals[index];
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
            vals[indexToRemove] = null;
            postRemoveHook();
            return val;
        } else {
            return null;
        }
    }

    public boolean remove(long key, Object value) {
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
            V[] vals = values;
            if (nullableValueEquals(vals[index], ((V) (value)))) {
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
                vals[indexToRemove] = null;
                postRemoveHook();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean removeIf(Predicate<? super Long> filter) {
        if (filter == null)
            throw new NullPointerException();
        
        if (KolobokeLongObjectHashMap.this.isEmpty())
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        V[] vals = values;
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
                        vals[indexToRemove] = null;
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
        
        if (KolobokeLongObjectHashMap.this.isEmpty())
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        V[] vals = values;
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
                        vals[indexToRemove] = null;
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
        
        if ((KolobokeLongObjectHashMap.this.isEmpty()) || (c.isEmpty()))
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        V[] vals = values;
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
                        vals[indexToRemove] = null;
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
        
        if ((KolobokeLongObjectHashMap.this.isEmpty()) || (c.isEmpty()))
            return false;
        
        boolean changed = false;
        int mc = modCount();
        long free = freeValue;
        long[] keys = set;
        int capacityMask = (keys.length) - 1;
        int firstDelayedRemoved = -1;
        long delayedRemoved = 0L;
        V[] vals = values;
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
                        vals[indexToRemove] = null;
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
        
        if (KolobokeLongObjectHashMap.this.isEmpty())
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
        V[] vals = values;
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
                        vals[indexToRemove] = null;
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
        
        if (KolobokeLongObjectHashMap.this.isEmpty())
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
        V[] vals = values;
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
                        vals[indexToRemove] = null;
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
        V[] vals = values;
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
                vals[indexToRemove] = null;
                postRemoveHook();
            } 
        }
    }

    public LongIterator iterator() {
        int mc = modCount();
        return new NoRemovedKeyIterator(mc);
    }

    public LongCursor setCursor() {
        int mc = modCount();
        return new NoRemovedKeyCursor(mc);
    }

    class NoRemovedKeyIterator extends KolobokeLongObjectHashMap.NoRemovedIterator {
        V[] vals;

        private NoRemovedKeyIterator(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            int index;
            if ((index = KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.index) >= 0) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.index = -1;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.keys;
                    V[] vals = KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.capacityMask;
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
                                if ((KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = (nextIndex) + 1) > 0) {
                                            KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.keys[indexToRemove] = free;
                                                KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.vals[indexToRemove] = null;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.nextIndex = index;
                                        if (indexToShift < (index - 1)) {
                                            KolobokeLongObjectHashMap.NoRemovedKeyIterator.this.next = keyToShift;
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
                        vals[indexToRemove] = null;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                        vals[index] = null;
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedKeyCursor extends KolobokeLongObjectHashMap.NoRemovedCursor {
        V[] vals;

        private NoRemovedKeyCursor(int mc) {
            super(mc);
            vals = values;
        }

        @Override
        public void remove() {
            long curKey;
            long free;
            if ((curKey = KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.curKey) != (free = KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.curKey = free;
                    int index = KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.index;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.keys;
                    V[] vals = KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.capacityMask;
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
                                if ((KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.keys[indexToRemove] = free;
                                                KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.vals[indexToRemove] = null;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongObjectHashMap.NoRemovedKeyCursor.this.index = ++index;
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
                        vals[indexToRemove] = null;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                        vals[index] = null;
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class EntryView extends AbstractSetView<Map.Entry<Long, V>> implements HashObjSet<Map.Entry<Long, V>> , InternalObjCollectionOps<Map.Entry<Long, V>> {
        @Nonnull
        @Override
        public Equivalence<Map.Entry<Long, V>> equivalence() {
            return Equivalence.entryEquivalence(Equivalence.<Long>defaultEquality(), valueEquivalence());
        }

        @Nonnull
        @Override
        public HashConfig hashConfig() {
            return KolobokeLongObjectHashMap.this.hashConfig();
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double currentLoad() {
            return KolobokeLongObjectHashMap.this.currentLoad();
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public boolean contains(Object o) {
            try {
                Map.Entry<Long, V> e = ((Map.Entry<Long, V>) (o));
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
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    result[(resultIndex++)] = new MutableEntry(mc, i, key, vals[i]);
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
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    a[(resultIndex++)] = ((T) (new MutableEntry(mc, i, key, vals[i])));
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
        Consumer<? super Map.Entry<Long, V>> action) {
            if (action == null)
                throw new NullPointerException();
            
            if (KolobokeLongObjectHashMap.EntryView.this.isEmpty())
                return ;
            
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry(mc, i, key, vals[i]));
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
        }

        @Override
        public boolean forEachWhile(@Nonnull
        Predicate<? super Map.Entry<Long, V>> predicate) {
            if (predicate == null)
                throw new NullPointerException();
            
            if (KolobokeLongObjectHashMap.EntryView.this.isEmpty())
                return true;
            
            boolean terminated = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (!(predicate.test(new MutableEntry(mc, i, key, vals[i])))) {
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
        public ObjIterator<Map.Entry<Long, V>> iterator() {
            int mc = modCount();
            return new NoRemovedEntryIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<Map.Entry<Long, V>> cursor() {
            int mc = modCount();
            return new NoRemovedEntryCursor(mc);
        }

        @Override
        public final boolean containsAll(@Nonnull
        Collection<?> c) {
            return CommonObjCollectionOps.containsAll(KolobokeLongObjectHashMap.EntryView.this, c);
        }

        @Override
        public final boolean allContainingIn(ObjCollection<?> c) {
            if (KolobokeLongObjectHashMap.EntryView.this.isEmpty())
                return true;
            
            boolean containsAll = true;
            KolobokeLongObjectHashMap<V>.ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
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
            if ((KolobokeLongObjectHashMap.EntryView.this.isEmpty()) || (s.isEmpty()))
                return false;
            
            boolean changed = false;
            KolobokeLongObjectHashMap<V>.ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
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
        public final boolean reverseAddAllTo(ObjCollection<? super Map.Entry<Long, V>> c) {
            if (KolobokeLongObjectHashMap.EntryView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    changed |= c.add(new MutableEntry(mc, i, key, vals[i]));
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        public int hashCode() {
            return KolobokeLongObjectHashMap.this.hashCode();
        }

        @SuppressFBWarnings(value = "EC_UNRELATED_TYPES_USING_POINTER_EQUALITY")
        @Override
        public String toString() {
            if (KolobokeLongObjectHashMap.EntryView.this.isEmpty())
                return "[]";
            
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    sb.append(' ');
                    sb.append(key);
                    sb.append('=');
                    Object val = vals[i];
                    sb.append((val != ((Object) (KolobokeLongObjectHashMap.EntryView.this)) ? val : "(this Collection)"));
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
            return KolobokeLongObjectHashMap.this.shrink();
        }

        @Override
        @SuppressWarnings(value = "unchecked")
        public boolean remove(Object o) {
            try {
                Map.Entry<Long, V> e = ((Map.Entry<Long, V>) (o));
                long key = e.getKey();
                V value = e.getValue();
                return KolobokeLongObjectHashMap.this.remove(key, value);
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }

        @Override
        public final boolean removeIf(@Nonnull
        Predicate<? super Map.Entry<Long, V>> filter) {
            if (filter == null)
                throw new NullPointerException();
            
            if (KolobokeLongObjectHashMap.EntryView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    if (filter.test(new MutableEntry(mc, i, key, vals[i]))) {
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
                            vals[indexToRemove] = null;
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
                if ((equivalence().equals(c2.equivalence())) && ((c2.size()) < (KolobokeLongObjectHashMap.EntryView.this.size()))) {
                    c2.reverseRemoveAllFrom(KolobokeLongObjectHashMap.EntryView.this);
                } 
            } 
            if ((KolobokeLongObjectHashMap.EntryView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if ((KolobokeLongObjectHashMap.EntryView.this.isEmpty()) || (c.isEmpty()))
                return false;
            
            boolean changed = false;
            KolobokeLongObjectHashMap<V>.ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            V[] vals = values;
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
                            vals[indexToRemove] = null;
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
            if ((KolobokeLongObjectHashMap.EntryView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if (KolobokeLongObjectHashMap.EntryView.this.isEmpty())
                return false;
            
            if (c.isEmpty()) {
                clear();
                return true;
            } 
            boolean changed = false;
            KolobokeLongObjectHashMap<V>.ReusableEntry e = new ReusableEntry();
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            V[] vals = values;
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
                            vals[indexToRemove] = null;
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
            KolobokeLongObjectHashMap.this.doClear();
        }
    }

    abstract class LongObjEntry extends AbstractEntry<Long, V> {
        abstract long key();

        @Override
        public final Long getKey() {
            return key();
        }

        abstract V value();

        @Override
        public final V getValue() {
            return value();
        }

        @SuppressWarnings(value = "unchecked")
        @Override
        public boolean equals(Object o) {
            Map.Entry e2;
            long k2;
            V v2;
            try {
                e2 = ((Map.Entry) (o));
                k2 = ((Long) (e2.getKey()));
                v2 = ((V) (e2.getValue()));
                return ((key()) == k2) && (nullableValueEquals(v2, value()));
            } catch (ClassCastException e) {
                return false;
            } catch (NullPointerException e) {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return (Primitives.hashCode(key())) ^ (nullableValueHashCode(value()));
        }
    }

    class MutableEntry extends KolobokeLongObjectHashMap<V>.LongObjEntry {
        final int modCount;

        private final int index;

        final long key;

        private V value;

        MutableEntry(int modCount, int index, long key, V value) {
            this.modCount = modCount;
            this.index = index;
            this.key = key;
            KolobokeLongObjectHashMap.MutableEntry.this.value = value;
        }

        @Override
        public long key() {
            return key;
        }

        @Override
        public V value() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            if ((modCount) != (modCount()))
                throw new IllegalStateException();
            
            V oldValue = value;
            V unwrappedNewValue = newValue;
            value = unwrappedNewValue;
            updateValueInTable(unwrappedNewValue);
            return oldValue;
        }

        void updateValueInTable(V newValue) {
            values[index] = newValue;
        }
    }

    class ReusableEntry extends KolobokeLongObjectHashMap<V>.LongObjEntry {
        private long key;

        private V value;

        KolobokeLongObjectHashMap<V>.ReusableEntry with(long key, V value) {
            KolobokeLongObjectHashMap.ReusableEntry.this.key = key;
            KolobokeLongObjectHashMap.ReusableEntry.this.value = value;
            return KolobokeLongObjectHashMap.ReusableEntry.this;
        }

        @Override
        public long key() {
            return key;
        }

        @Override
        public V value() {
            return value;
        }
    }

    class ValueView extends AbstractObjValueView<V> {
        @Override
        public Equivalence<V> equivalence() {
            return valueEquivalence();
        }

        @Override
        public int size() {
            return KolobokeLongObjectHashMap.this.size();
        }

        @Override
        public boolean shrink() {
            return KolobokeLongObjectHashMap.this.shrink();
        }

        @Override
        public boolean contains(Object o) {
            return KolobokeLongObjectHashMap.this.containsValue(o);
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            
            if (KolobokeLongObjectHashMap.ValueView.this.isEmpty())
                return ;
            
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    action.accept(vals[i]);
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
        }

        @Override
        public boolean forEachWhile(Predicate<? super V> predicate) {
            if (predicate == null)
                throw new NullPointerException();
            
            if (KolobokeLongObjectHashMap.ValueView.this.isEmpty())
                return true;
            
            boolean terminated = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
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
        public boolean allContainingIn(ObjCollection<?> c) {
            if (KolobokeLongObjectHashMap.ValueView.this.isEmpty())
                return true;
            
            boolean containsAll = true;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
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
        public boolean reverseAddAllTo(ObjCollection<? super V> c) {
            if (KolobokeLongObjectHashMap.ValueView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
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
        public boolean reverseRemoveAllFrom(ObjSet<?> s) {
            if ((KolobokeLongObjectHashMap.ValueView.this.isEmpty()) || (s.isEmpty()))
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    changed |= s.remove(vals[i]);
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            return changed;
        }

        @Override
        @Nonnull
        public ObjIterator<V> iterator() {
            int mc = modCount();
            return new NoRemovedValueIterator(mc);
        }

        @Nonnull
        @Override
        public ObjCursor<V> cursor() {
            int mc = modCount();
            return new NoRemovedValueCursor(mc);
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
            V[] vals = values;
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
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    a[(resultIndex++)] = ((T) (vals[i]));
                } 
            }
            if (mc != (modCount()))
                throw new ConcurrentModificationException();
            
            if ((a.length) > resultIndex)
                a[resultIndex] = null;
            
            return a;
        }

        @Override
        public String toString() {
            if (KolobokeLongObjectHashMap.ValueView.this.isEmpty())
                return "[]";
            
            StringBuilder sb = new StringBuilder();
            int elementCount = 0;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            V[] vals = values;
            for (int i = (keys.length) - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    V val;
                    sb.append(' ').append(((val = vals[i]) != ((Object) (KolobokeLongObjectHashMap.ValueView.this)) ? val : "(this Collection)")).append(',');
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
            return removeValue(o);
        }

        @Override
        public void clear() {
            KolobokeLongObjectHashMap.this.clear();
        }

        @Override
        public boolean removeIf(Predicate<? super V> filter) {
            if (filter == null)
                throw new NullPointerException();
            
            if (KolobokeLongObjectHashMap.ValueView.this.isEmpty())
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            V[] vals = values;
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
                            vals[indexToRemove] = null;
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
            if ((KolobokeLongObjectHashMap.ValueView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if ((KolobokeLongObjectHashMap.ValueView.this.isEmpty()) || (c.isEmpty()))
                return false;
            
            boolean changed = false;
            int mc = modCount();
            long free = freeValue;
            long[] keys = set;
            int capacityMask = (keys.length) - 1;
            int firstDelayedRemoved = -1;
            long delayedRemoved = 0L;
            V[] vals = values;
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
                            vals[indexToRemove] = null;
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
            if ((KolobokeLongObjectHashMap.ValueView.this) == ((Object) (c)))
                throw new IllegalArgumentException();
            
            if (KolobokeLongObjectHashMap.ValueView.this.isEmpty())
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
            V[] vals = values;
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
                            vals[indexToRemove] = null;
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

    class NoRemovedEntryIterator implements ObjIterator<Map.Entry<Long, V>> {
        long[] keys;

        V[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        class MutableEntry2 extends KolobokeLongObjectHashMap<V>.MutableEntry {
            MutableEntry2(int modCount, int index, long key, V value) {
                super(modCount, index, key, value);
            }

            @Override
            void updateValueInTable(V newValue) {
                if ((vals) == (values)) {
                    vals[index] = newValue;
                } else {
                    justPut(key, newValue);
                    if ((KolobokeLongObjectHashMap.NoRemovedEntryIterator.MutableEntry2.this.modCount) != (modCount())) {
                        throw new IllegalStateException();
                    } 
                }
            }
        }

        int index = -1;

        int nextIndex;

        KolobokeLongObjectHashMap<V>.MutableEntry next;

        NoRemovedEntryIterator(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.keys = set;
            capacityMask = (keys.length) - 1;
            V[] vals = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.vals = values;
            long free = this.free = freeValue;
            int nextI = keys.length;
            while ((--nextI) >= 0) {
                long key;
                if ((key = keys[nextI]) != free) {
                    next = new MutableEntry2(mc, nextI, key, vals[nextI]);
                    break;
                } 
            }
            nextIndex = nextI;
        }

        @Override
        public void forEachRemaining(@Nonnull
        Consumer<? super Map.Entry<Long, V>> action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.keys;
            V[] vals = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.vals;
            long free = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.free;
            int nextI = nextIndex;
            for (int i = nextI; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry2(mc, i, key, vals[i]));
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
        public Map.Entry<Long, V> next() {
            int mc;
            if ((mc = expectedModCount) == (modCount())) {
                int nextI;
                if ((nextI = nextIndex) >= 0) {
                    index = nextI;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.keys;
                    long free = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.free;
                    KolobokeLongObjectHashMap<V>.MutableEntry prev = next;
                    while ((--nextI) >= 0) {
                        long key;
                        if ((key = keys[nextI]) != free) {
                            next = new MutableEntry2(mc, nextI, key, vals[nextI]);
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
            if ((index = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.index) >= 0) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.index = -1;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.keys;
                    V[] vals = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.capacityMask;
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
                                if ((KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = (nextIndex) + 1) > 0) {
                                            KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.keys[indexToRemove] = free;
                                                KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.vals[indexToRemove] = null;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.nextIndex = index;
                                        if (indexToShift < (index - 1)) {
                                            KolobokeLongObjectHashMap.NoRemovedEntryIterator.this.next = new MutableEntry2(modCount(), indexToShift, keyToShift, vals[indexToShift]);
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
                        vals[indexToRemove] = null;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                        vals[index] = null;
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedEntryCursor implements ObjCursor<Map.Entry<Long, V>> {
        long[] keys;

        V[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        class MutableEntry2 extends KolobokeLongObjectHashMap<V>.MutableEntry {
            MutableEntry2(int modCount, int index, long key, V value) {
                super(modCount, index, key, value);
            }

            @Override
            void updateValueInTable(V newValue) {
                if ((vals) == (values)) {
                    vals[index] = newValue;
                } else {
                    justPut(key, newValue);
                    if ((KolobokeLongObjectHashMap.NoRemovedEntryCursor.MutableEntry2.this.modCount) != (modCount())) {
                        throw new IllegalStateException();
                    } 
                }
            }
        }

        int index;

        long curKey;

        V curValue;

        NoRemovedEntryCursor(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.keys = set;
            capacityMask = (keys.length) - 1;
            index = keys.length;
            vals = values;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super Map.Entry<Long, V>> action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.keys;
            V[] vals = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.vals;
            long free = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.free;
            int index = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.index;
            for (int i = index - 1; i >= 0; i--) {
                long key;
                if ((key = keys[i]) != free) {
                    action.accept(new MutableEntry2(mc, i, key, vals[i]));
                } 
            }
            if ((index != (KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.index)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.index = -1;
            curKey = free;
        }

        @Override
        public Map.Entry<Long, V> elem() {
            long curKey;
            if ((curKey = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.curKey) != (free)) {
                return new MutableEntry2(expectedModCount, index, curKey, curValue);
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if ((expectedModCount) == (modCount())) {
                long[] keys = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.keys;
                long free = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.free;
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
            if ((curKey = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.curKey) != (free = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.curKey = free;
                    int index = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.index;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.keys;
                    V[] vals = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.capacityMask;
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
                                if ((KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.keys[indexToRemove] = free;
                                                KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.vals[indexToRemove] = null;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongObjectHashMap.NoRemovedEntryCursor.this.index = ++index;
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
                        vals[indexToRemove] = null;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                        vals[index] = null;
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedValueIterator implements ObjIterator<V> {
        long[] keys;

        V[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        int index = -1;

        int nextIndex;

        V next;

        NoRemovedValueIterator(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.keys = set;
            capacityMask = (keys.length) - 1;
            V[] vals = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.vals = values;
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
        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.keys;
            V[] vals = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.vals;
            long free = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.free;
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
        public V next() {
            if ((expectedModCount) == (modCount())) {
                int nextI;
                if ((nextI = nextIndex) >= 0) {
                    index = nextI;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.keys;
                    long free = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.free;
                    V prev = next;
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

        @Override
        public void remove() {
            int index;
            if ((index = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.index) >= 0) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongObjectHashMap.NoRemovedValueIterator.this.index = -1;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.keys;
                    V[] vals = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongObjectHashMap.NoRemovedValueIterator.this.capacityMask;
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
                                if ((KolobokeLongObjectHashMap.NoRemovedValueIterator.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = (nextIndex) + 1) > 0) {
                                            KolobokeLongObjectHashMap.NoRemovedValueIterator.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongObjectHashMap.NoRemovedValueIterator.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongObjectHashMap.NoRemovedValueIterator.this.keys[indexToRemove] = free;
                                                KolobokeLongObjectHashMap.NoRemovedValueIterator.this.vals[indexToRemove] = null;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongObjectHashMap.NoRemovedValueIterator.this.nextIndex = index;
                                        if (indexToShift < (index - 1)) {
                                            KolobokeLongObjectHashMap.NoRemovedValueIterator.this.next = vals[indexToShift];
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
                        vals[indexToRemove] = null;
                        postRemoveHook();
                    } else {
                        justRemove(keys[index]);
                        vals[index] = null;
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    class NoRemovedValueCursor implements ObjCursor<V> {
        long[] keys;

        V[] vals;

        final long free;

        final int capacityMask;

        int expectedModCount;

        int index;

        long curKey;

        V curValue;

        NoRemovedValueCursor(int mc) {
            expectedModCount = mc;
            long[] keys = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.keys = set;
            capacityMask = (keys.length) - 1;
            index = keys.length;
            vals = values;
            long free = this.free = freeValue;
            curKey = free;
        }

        @Override
        public void forEachForward(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            
            int mc = expectedModCount;
            long[] keys = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.keys;
            V[] vals = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.vals;
            long free = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.free;
            int index = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.index;
            for (int i = index - 1; i >= 0; i--) {
                if ((keys[i]) != free) {
                    action.accept(vals[i]);
                } 
            }
            if ((index != (KolobokeLongObjectHashMap.NoRemovedValueCursor.this.index)) || (mc != (modCount()))) {
                throw new ConcurrentModificationException();
            } 
            KolobokeLongObjectHashMap.NoRemovedValueCursor.this.index = -1;
            curKey = free;
        }

        @Override
        public V elem() {
            if ((curKey) != (free)) {
                return curValue;
            } else {
                throw new IllegalStateException();
            }
        }

        @Override
        public boolean moveNext() {
            if ((expectedModCount) == (modCount())) {
                long[] keys = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.keys;
                long free = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.free;
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
            if ((curKey = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.curKey) != (free = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.free)) {
                if (((expectedModCount)++) == (modCount())) {
                    KolobokeLongObjectHashMap.NoRemovedValueCursor.this.curKey = free;
                    int index = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.index;
                    long[] keys = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.keys;
                    V[] vals = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.vals;
                    if (keys == (set)) {
                        int capacityMask = KolobokeLongObjectHashMap.NoRemovedValueCursor.this.capacityMask;
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
                                if ((KolobokeLongObjectHashMap.NoRemovedValueCursor.this.keys) == keys) {
                                    if (indexToShift > indexToRemove) {
                                        int slotsToCopy;
                                        if ((slotsToCopy = index) > 0) {
                                            KolobokeLongObjectHashMap.NoRemovedValueCursor.this.keys = Arrays.copyOf(keys, slotsToCopy);
                                            KolobokeLongObjectHashMap.NoRemovedValueCursor.this.vals = Arrays.copyOf(vals, slotsToCopy);
                                            if (indexToRemove < slotsToCopy) {
                                                KolobokeLongObjectHashMap.NoRemovedValueCursor.this.keys[indexToRemove] = free;
                                                KolobokeLongObjectHashMap.NoRemovedValueCursor.this.vals[indexToRemove] = null;
                                            } 
                                        } 
                                    } else if (indexToRemove == index) {
                                        KolobokeLongObjectHashMap.NoRemovedValueCursor.this.index = ++index;
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
                        vals[indexToRemove] = null;
                        postRemoveHook();
                    } else {
                        justRemove(curKey);
                        vals[index] = null;
                    }
                } else {
                    throw new ConcurrentModificationException();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    KolobokeLongObjectHashMap(HashConfig hashConfig, int expectedSize) {
        this.init(new HashConfigWrapper(hashConfig), expectedSize);
    }

    static class Support {
        static interface LongHash extends Hash {
            long freeValue();

            boolean supportRemoved();

            long removedValue();
        }

        static interface SeparateKVLongLHash extends LHash , KolobokeLongObjectHashMap.Support.SeparateKVLongHash {        }

        static interface SeparateKVLongHash extends KolobokeLongObjectHashMap.Support.LongHash {
            @Nonnull
            long[] keys();
        }
    }

    static final HashConfigWrapper DEFAULT_CONFIG_WRAPPER = new HashConfigWrapper(HashConfig.getDefault());
}