package org.oscim.utils;

/*
 * Copyright 2014 Hannes Janetzek
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Stripped down HashMap making HashItem entries public - So you have your custom
 * 'Entry' holding key and value. HashItem must implement equals() and hashCode()
 * only for the 'key' part. Items may only be in one KeyMap at a time!
 * <p/>
 * KeyMap.put(HashItem, boolean replace) allows to get or add an item in one invocation.
 * <p/>
 * TODO add to NOTICE file
 * The VTM library includes software developed as part of the Apache
 * Harmony project which is copyright 2006, The Apache Software Foundation and
 * released under the Apache License 2.0. http://harmony.apache.org
 */
/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements. See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.oscim.utils.KeyMap.HashItem;
import org.oscim.utils.pool.Inlist;

import java.util.Arrays;

/**
 * <p/>
 * Note: the implementation of {@code KeyMap} is not synchronized. If one thread
 * of several threads accessing an instance modifies the map structurally,
 * access to the map needs to be synchronized. A structural modification is an
 * operation that adds or removes an entry. Changes in the value of an entry are
 * not structural changes.
 *
 * @param <K> the type of keys maintained by this map
 */
public class KeyMap<K extends HashItem> extends Inlist<KeyMap<K>> {
    /**
     * Min capacity (other than zero) for a HashMap. Must be a power of two
     * greater than 1 (and less than 1 << 30).
     */
    private static final int MINIMUM_CAPACITY = 4;

    /**
     * Max capacity for a HashMap. Must be a power of two >= MINIMUM_CAPACITY.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * An empty table shared by all zero-capacity maps (typically from default
     * constructor). It is never written to, and replaced on first put. Its size
     * is set to half the minimum, so that the first resize will create a
     * minimum-sized table.
     */
    private static final HashItem[] EMPTY_TABLE = new HashItem[MINIMUM_CAPACITY >>> 1];

    /**
     * The default load factor. Note that this implementation ignores the
     * load factor, but cannot do away with it entirely because it's
     * mentioned in the API.
     * <p/>
     * <p/>
     * Note that this constant has no impact on the behavior of the program, but
     * it is emitted as part of the serialized form. The load factor of .75 is
     * hardwired into the program, which uses cheap shifts in place of expensive
     * division.
     */
    static final float DEFAULT_LOAD_FACTOR = .75F;

    /**
     * The hash table. If this hash map contains a mapping for null, it is
     * not represented this hash table.
     */
    HashItem[] table;

    /**
     * The number of mappings in this hash map.
     */
    int size;

    /**
     * The table is rehashed when its size exceeds this threshold.
     * The value of this field is generally .75 * capacity, except when
     * the capacity is zero, as described in the EMPTY_TABLE declaration
     * above.
     */
    private int threshold;

    /**
     * Constructs a new empty {@code HashMap} instance.
     */
    public KeyMap() {
        table = (HashItem[]) EMPTY_TABLE;
        threshold = -1; // Forces first put invocation to replace EMPTY_TABLE
    }

    /**
     * Constructs a new {@code HashMap} instance with the specified capacity.
     *
     * @param capacity the initial capacity of this hash map.
     * @throws IllegalArgumentException when the capacity is less than zero.
     */
    public KeyMap(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity: " + capacity);
        }

        if (capacity == 0) {
            HashItem[] tab = (HashItem[]) EMPTY_TABLE;
            table = tab;
            threshold = -1; // Forces first put() to replace EMPTY_TABLE
            return;
        }

        if (capacity < MINIMUM_CAPACITY) {
            capacity = MINIMUM_CAPACITY;
        } else if (capacity > MAXIMUM_CAPACITY) {
            capacity = MAXIMUM_CAPACITY;
        } else {
            capacity = roundUpToPowerOfTwo(capacity);
        }
        makeTable(capacity);
    }

    /**
     * Constructs a new {@code HashMap} instance with the specified capacity and
     * load factor.
     *
     * @param capacity   the initial capacity of this hash map.
     * @param loadFactor the initial load factor.
     * @throws IllegalArgumentException when the capacity is less than zero or the load factor is
     *                                  less or equal to zero or NaN.
     */
    public KeyMap(int capacity, float loadFactor) {
        this(capacity);

        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor: " + loadFactor);
        }

        /* Note that this implementation ignores loadFactor; it always uses
         * a load factor of 3/4. This simplifies the code and generally
         * improves performance. */
    }

    /**
     * Returns an appropriate capacity for the specified initial size. Does
     * not round the result up to a power of two; the caller must do this!
     * The returned value will be between 0 and MAXIMUM_CAPACITY (inclusive).
     */
    static int capacityForInitSize(int size) {
        int result = (size >> 1) + size; // Multiply by 3/2 to allow for growth

        // boolean expr is equivalent to result >= 0 && result<MAXIMUM_CAPACITY
        return (result & ~(MAXIMUM_CAPACITY - 1)) == 0 ? result : MAXIMUM_CAPACITY;
    }

    /**
     * This method is called from the pseudo-constructors (clone and readObject)
     * prior to invoking constructorPut/constructorPutAll, which invoke the
     * overridden constructorNewEntry method. Normally it is a VERY bad idea to
     * invoke an overridden method from a pseudo-constructor (Effective Java
     * Item 17). In this case it is unavoidable, and the init method provides a
     * workaround.
     */
    void init() {
    }

    /**
     * Returns whether this map is empty.
     *
     * @return {@code true} if this map has no elements, {@code false}
     * otherwise.
     * @see #size()
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of elements in this map.
     *
     * @return the number of elements in this map.
     */
    public int size() {
        return size;
    }

    /**
     * Returns the value of the mapping with the specified key.
     *
     * @param key the key.
     * @return the value of the mapping with the specified key, or {@code null}
     * if no mapping for the specified key is found.
     */
    @SuppressWarnings("unchecked")
    public K get(HashItem key) {

        // Doug Lea's supplemental secondaryHash function (inlined)
        int hash = key.hashCode();
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        hash ^= (hash >>> 7) ^ (hash >>> 4);

        HashItem[] tab = table;
        for (HashItem e = tab[hash & (tab.length - 1)]; e != null; e = e.next) {
            HashItem eKey = e;
            if (eKey == key || (e.hash == hash && key.equals(eKey))) {
                return (K) e;
            }
        }
        return null;
    }

    /**
     * Maps the specified key to the specified value.
     *
     * @param key   the key.
     * @return the value of any previous mapping with the specified key or
     * {@code null} if there was no such mapping.
     */
    public K put(K key) {
        return put(key, true);
    }

    @SuppressWarnings("unchecked")
    public K put(K key, boolean replace) {
        if (key.next != null)
            throw new IllegalStateException("item not unhooked");

        int hash = secondaryHash(key.hashCode());
        HashItem[] tab = table;
        int index = hash & (tab.length - 1);
        for (HashItem e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && key.equals(e)) {
                if (replace) {
                    tab[index] = Inlist.remove(tab[index], e);
                    tab[index] = Inlist.push(tab[index], key);
                }
                //V oldValue = e.value;
                //e.value = value;
                return (K) e; //oldValue;
            }
        }

        // No entry key is present; create one
        if (size++ > threshold) {
            tab = doubleCapacity();
            index = hash & (tab.length - 1);
        }
        addNewEntry(key, hash, index);
        return null;
    }

    /**
     * Removes the mapping with the specified key from this map.
     *
     * @param key the key of the mapping to remove.
     * @return the value of the removed mapping or {@code null} if no mapping
     * for the specified key was found.
     */
    @SuppressWarnings("unchecked")
    public K remove(K key) {

        int hash = secondaryHash(key.hashCode());
        HashItem[] tab = table;
        int index = hash & (tab.length - 1);
        for (HashItem e = tab[index], prev = null; e != null; prev = e, e = e.next) {
            if (e.hash == hash && key.equals(e)) {
                if (prev == null) {
                    tab[index] = e.next;
                } else {
                    prev.next = e.next;
                }
                e.next = null;
                //modCount++;
                size--;
                //postRemove(e);
                return (K) e;
            }
        }
        return null;
    }

    /**
     * Creates a new entry for the given key, value, hash, and index and
     * inserts it into the hash table. This method is called by put
     * (and indirectly, putAll), and overridden by LinkedHashMap. The hash
     * must incorporate the secondary hash function.
     */
    void addNewEntry(K key, int hash, int index) {
        key.setIndex(hash, table[index]);
        table[index] = key;
    }

    /**
     * Allocate a table of the given capacity and set the threshold accordingly.
     *
     * @param newCapacity must be a power of two
     */
    private HashItem[] makeTable(int newCapacity) {
        HashItem[] newTable = (HashItem[]) new HashItem[newCapacity];
        table = newTable;
        threshold = (newCapacity >> 1) + (newCapacity >> 2); // 3/4 capacity
        return newTable;
    }

    /**
     * Doubles the capacity of the hash table. Existing entries are placed in
     * the correct bucket on the enlarged table. If the current capacity is,
     * MAXIMUM_CAPACITY, this method is a no-op. Returns the table, which
     * will be new unless we were already at MAXIMUM_CAPACITY.
     */
    private HashItem[] doubleCapacity() {
        HashItem[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            return oldTable;
        }
        int newCapacity = oldCapacity * 2;
        HashItem[] newTable = makeTable(newCapacity);
        if (size == 0) {
            return newTable;
        }

        for (int j = 0; j < oldCapacity; j++) {
            /* Rehash the bucket using the minimum number of field writes.
             * This is the most subtle and delicate code in the class. */
            HashItem e = oldTable[j];
            if (e == null) {
                continue;
            }
            int highBit = e.hash & oldCapacity;
            HashItem broken = null;
            newTable[j | highBit] = e;
            for (HashItem n = e.next; n != null; e = n, n = n.next) {
                int nextHighBit = n.hash & oldCapacity;
                if (nextHighBit != highBit) {
                    if (broken == null)
                        newTable[j | nextHighBit] = n;
                    else
                        broken.next = n;
                    broken = e;
                    highBit = nextHighBit;
                }
            }
            if (broken != null)
                broken.next = null;
        }
        return newTable;
    }

    /**
     * Subclass overrides this method to unlink entry.
     */
    void postRemove(HashItem e) {
    }

    /**
     * Removes all mappings from this hash map, leaving it empty.
     *
     * @see #isEmpty
     * @see #size
     */
    public void clear() {
        if (size != 0) {
            Arrays.fill(table, null);
            size = 0;
        }
    }

    static final boolean STATS = false;

    @SuppressWarnings("unchecked")
    public K releaseItems() {
        if (size == 0)
            return null;

        int collisions = 0;
        int max = 0;
        int sum = 0;

        HashItem items = null;
        HashItem last;
        for (int i = 0, n = table.length; i < n; i++) {
            HashItem item = table[i];
            if (item == null)
                continue;
            table[i] = null;
            if (STATS) {
                sum = 0;
                last = item;
                while (last != null) {
                    if (last.next == null)
                        break;

                    sum++;
                    last = last.next;
                }
                max = Math.max(max, sum);
                collisions += sum;
            } else {
                last = Inlist.last(item);
            }
            last.next = items;
            items = item;
        }
        if (STATS)
            System.out.println("collisions: " + collisions + " " + max + " " + size);

        Arrays.fill(table, null);
        size = 0;

        return (K) items;
    }

    public static class HashItem extends Inlist<HashItem> {
        int hash;

        public void setIndex(int hash, HashItem next) {
            this.hash = hash;
            this.next = next;
        }
    }

    /**
     * Applies a supplemental hash function to a given hashCode, which defends
     * against poor quality hash functions. This is critical because HashMap
     * uses power-of-two length hash tables, that otherwise encounter collisions
     * for hashCodes that do not differ in lower or upper bits.
     */
    private static int secondaryHash(int h) {
        // Doug Lea's supplemental hash function
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns the smallest power of two >= its argument, with several caveats:
     * If the argument is negative but not Integer.MIN_VALUE, the method returns
     * zero. If the argument is > 2^30 or equal to Integer.MIN_VALUE, the method
     * returns Integer.MIN_VALUE. If the argument is zero, the method returns
     * zero.
     */
    private static int roundUpToPowerOfTwo(int i) {
        i--; // If input is a power of two, shift its high-order bit right

        // "Smear" the high-order bit all the way to the right
        i |= i >>> 1;
        i |= i >>> 2;
        i |= i >>> 4;
        i |= i >>> 8;
        i |= i >>> 16;

        return i + 1;
    }

    //    public K put(K key) {
    //        //        if (key == null) {
    //        //            return putValueForNullKey(value);
    //        //        }
    //
    //        int hash = secondaryHash(key.hashCode());
    //        HashItem[] tab = table;
    //        int index = hash & (tab.length - 1);
    //        for (HashItem e = tab[index]; e != null; e = e.next) {
    //            if (e.hash == hash && key.equals(e.key)) {
    //                preModify(e);
    //                //V oldValue = e.value;
    //                //e.value = value;
    //                return e.key; //oldValue;
    //            }
    //        }
    //
    //        // No entry for (non-null) key is present; create one
    //        modCount++;
    //        if (size++ > threshold) {
    //            tab = doubleCapacity();
    //            index = hash & (tab.length - 1);
    //        }
    //        addNewEntry(key, hash, index);
    //        return null;
    //    }

    //    private V putValueForNullKey(V value) {
    //        HashMapEntry<K> entry = entryForNullKey;
    //        if (entry == null) {
    //            addNewEntryForNullKey(value);
    //            size++;
    //            modCount++;
    //            return null;
    //        } else {
    //            preModify(entry);
    //            V oldValue = entry.value;
    //            entry.value = value;
    //            return oldValue;
    //        }
    //    }

    //    /**
    //     * Returns whether this map contains the specified key.
    //     *
    //     * @param key
    //     *            the key to search for.
    //     * @return {@code true} if this map contains the specified key,
    //     *         {@code false} otherwise.
    //     */
    //    //@Override
    //    public boolean containsKey(Object key) {
    //        if (key == null) {
    //            return entryForNullKey != null;
    //        }
    //
    //        // Doug Lea's supplemental secondaryHash function (inlined)
    //        int hash = key.hashCode();
    //        hash ^= (hash >>> 20) ^ (hash >>> 12);
    //        hash ^= (hash >>> 7) ^ (hash >>> 4);
    //
    //        HashItem[] tab = table;
    //        for (HashItem e = tab[hash & (tab.length - 1)]; e != null; e = e.next) {
    //            K eKey = e.key;
    //            if (eKey == key || (e.hash == hash && key.equals(eKey))) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }

    //    /**
    //     * Returns whether this map contains the specified value.
    //     *
    //     * @param value
    //     *            the value to search for.
    //     * @return {@code true} if this map contains the specified value,
    //     *         {@code false} otherwise.
    //     */
    //    @Override
    //    public boolean containsValue(Object value) {
    //        HashMapEntry[] tab = table;
    //        int len = tab.length;
    //        if (value == null) {
    //            for (int i = 0; i < len; i++) {
    //                for (HashMapEntry e = tab[i]; e != null; e = e.next) {
    //                    if (e.value == null) {
    //                        return true;
    //                    }
    //                }
    //            }
    //            return entryForNullKey != null && entryForNullKey.value == null;
    //        }
    //
    //        // value is non-null
    //        for (int i = 0; i < len; i++) {
    //            for (HashMapEntry e = tab[i]; e != null; e = e.next) {
    //                if (value.equals(e.value)) {
    //                    return true;
    //                }
    //            }
    //        }
    //        return entryForNullKey != null && value.equals(entryForNullKey.value);
    //    }

    ///**
    // * Ensures that the hash table has sufficient capacity to store the
    // * specified number of mappings, with room to grow. If not, it increases the
    // * capacity as appropriate. Like doubleCapacity, this method moves existing
    // * entries to new buckets as appropriate. Unlike doubleCapacity, this method
    // * can grow the table by factors of 2^n for n > 1. Hopefully, a single call
    // * to this method will be faster than multiple calls to doubleCapacity.
    // *
    // * <p>
    // * This method is called only by putAll.
    // */
    //private void ensureCapacity(int numMappings) {
    //    int newCapacity = roundUpToPowerOfTwo(capacityForInitSize(numMappings));
    //    HashItem[] oldTable = table;
    //    int oldCapacity = oldTable.length;
    //    if (newCapacity <= oldCapacity) {
    //        return;
    //    }
    //    if (newCapacity == oldCapacity * 2) {
    //        doubleCapacity();
    //        return;
    //    }
    //
    //    // We're growing by at least 4x, rehash in the obvious way
    //    HashItem[] newTable = makeTable(newCapacity);
    //    if (size != 0) {
    //        int newMask = newCapacity - 1;
    //        for (int i = 0; i < oldCapacity; i++) {
    //            for (HashItem e = oldTable[i]; e != null;) {
    //                HashItem oldNext = e.next;
    //                int newIndex = e.hash & newMask;
    //                HashItem newNext = newTable[newIndex];
    //                newTable[newIndex] = e;
    //                e.next = newNext;
    //                e = oldNext;
    //            }
    //        }
    //    }
    //}

}
