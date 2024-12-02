package com.adventofcode.day1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day1_2 {

    private static final String DELIMITER = "   ";

    public static void main(String[] args) throws Exception {
        List<Integer> left = initArray(0);
        List<Integer> right = initArray(1);
        Map<Integer, Integer> rightStats = new HashMap<>();
        for (Integer r : right) {
            rightStats.compute(r, (k, v) -> v == null ? 1 : v + 1);
        }
        int diff = 0;
        for (int index = 0; index < left.size(); index++) {
            Integer lv = left.get(index);
            Integer rs = rightStats.get(lv);
            if (rs != null) {
                diff += lv * rs;
            }
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
        InputStream ioStream = Day1_2.class.getClassLoader().getResourceAsStream(fileName);
        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }
}
