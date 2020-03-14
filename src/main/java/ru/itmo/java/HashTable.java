package ru.itmo.java;

import java.util.Map;

public class HashTable {
    private HashTableElement[] data;
    private final float loadFactor;
    private int threshold;
    private int size;

    private static final int MINIMAL_DATA_SIZE = 7;
    private static final float MINIMAL_LOAD_FACTOR = 0.2f;
    private static final float MAXIMUM_LOAD_FACTOR = 1.0f;
    private static final int DEFAULT_DATA_SIZE = 127;
    private static final float DEFAULT_LOAD_FACTOR = 0.5f;

    public HashTable() {
        this(DEFAULT_DATA_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int size) {
        this(size, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(float loadFactor) {
        this(DEFAULT_DATA_SIZE, loadFactor);
    }

    public HashTable(int size, float loadFactor) {
        if (size < MINIMAL_DATA_SIZE || loadFactor <= MINIMAL_LOAD_FACTOR || loadFactor >= MAXIMUM_LOAD_FACTOR)
            throw new IllegalArgumentException();
        this.loadFactor = loadFactor;
        data = new HashTableElement[size];
        threshold = (int) (data.length * loadFactor);
    }

    private int getHashCode(Object key) {
        return Math.abs(key.hashCode() % data.length);
    }

    private Integer getIndex(Object key) {
        if (key == null)
            return null;
        int start_index = getHashCode(key);
        int index = start_index;

        while (data[index] != null) {
            int elementKeyHash = data[index].getKey().hashCode();
            int keyHash = key.hashCode();
            if (data[index].isNotDeleted() && elementKeyHash == keyHash && data[index].getKey().equals(key)) {
                return index;
            }
            index++;
            index %= data.length;
            if (index == start_index) {
                return null;
            }
        }
        return null;
    }

    private void resize() {
        int newLength = data.length * (int) Math.ceil(1.0 / loadFactor + 0.1); // newSize = data.length * 2 or bigger
        HashTableElement[] buffer = data;
        data = new HashTableElement[newLength];
        threshold = (int) (data.length * loadFactor);
        size = 0;
        for (HashTableElement element : buffer) {
            if (element != null && element.isNotDeleted()) {
                put(element.getKey(), element.getValue());
            }
        }
    }

    Object put(Object key, Object value) {
        if (key == null || value == null)
            throw new NullPointerException();

        //find index of position to put
        Integer index = getIndex(key);
        if (index == null) {            //if HashTable does not contain this key
            if (size >= threshold) {
                resize();
            }
            size++;
            index = getHashCode(key);
            while (data[index] != null && data[index].isNotDeleted()) {        // find an empty position
                index++;
                index %= data.length;
            }
        }

        Object result = null;
        if (data[index] != null && data[index].isNotDeleted()) {
            result = data[index].getValue();
        }
        data[index] = new HashTableElement(key, value);
        return result;
    }

    Object get(Object key) {
        Integer index = getIndex(key);
        if (index == null) {
            return null;
        }
        return data[index].getValue();
    }

    Object remove(Object key) {
        Integer index = getIndex(key);
        if (index == null) {
            return null;
        }
        size--;
        data[index].Delete();
        return data[index].getValue();
    }

    int size() {
        return size;
    }

    private static class HashTableElement {
        public HashTableElement(Object key, Object value) {
            if (key == null || value == null)
                throw new IllegalArgumentException();
            this.key = key;
            this.value = value;
            isDeleted = false;
        }

        private boolean isDeleted;
        private Object key;
        private Object value;

        public void Delete() {
            isDeleted = true;
        }

        public boolean isNotDeleted() {
            return !isDeleted;
        }

        public Object getValue() {
            return value;
        }

        public Object getKey() {
            return key;
        }
    }

}
