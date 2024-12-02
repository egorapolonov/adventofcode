package com.adventofcode.day1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day1_1 {

    private static final String DELIMITER = "   ";

    public static void main(String[] args) throws Exception {
        List<Integer> left = initArray(0);
        List<Integer> right = initArray(1);
        Collections.sort(left);
        Collections.sort(right);
        int diff = 0;
        for (int index = 0; index < left.size(); index++) {
            int lv = left.get(index);
            int rv = right.get(index);
            diff += Math.abs(lv - rv);
        }
        System.out.println(diff);
    }

    private static List<Integer> initArray(int columnIndex) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getFileAsIOStream("day1_1.txt")))) {
            return new ArrayList<>(
                    br.lines().map(line -> Integer.parseInt(line.split(DELIMITER)[columnIndex])).toList());
        }
    }

    private static InputStream getFileAsIOStream(final String fileName) {
        InputStream ioStream = Day1_1.class.getClassLoader().getResourceAsStream(fileName);
        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }
}
