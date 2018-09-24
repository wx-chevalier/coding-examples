package com.alibaba.middleware.race.sync.utils;

import java.util.Arrays;

/**
 * Primitive int array
 * 
 * Created by yfu on 6/11/17.
 */
public class IntArray {

    private int[] array;
    private int size;

    public IntArray() {
        this(16);
    }

    public IntArray(int capacity) {
        array = new int[capacity];
    }
    
    public void add(int value) {
        checkCapacity();
        array[size++] = value;
    }
    
    public int get(int index) {
        return array[index];
    }
    
    public int size() {
        return size;
    }
    
    public int capacity() {
        return array.length;
    }
    
    private void checkCapacity() {
        if (array.length == size) {
            array = Arrays.copyOf(array, array.length * 2);
        }
    }
}
