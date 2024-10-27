package com.archgeek.data.meta.utils;

import java.util.UUID;

/**
 * @author pizhihui
 * @date 2024-10-27
 */
public class RandomIdGenerator {

    public static final long MAX_ID = 0x7fffffffffffffffL;


    public long nextId() {
        // Make sure this is a positive number.
        return UUID.randomUUID().getLeastSignificantBits() & MAX_ID;
    }

    public static void main(String[] args) {

        RandomIdGenerator randomIdGenerator = new RandomIdGenerator();
        System.out.println(randomIdGenerator.nextId());

    }
}
