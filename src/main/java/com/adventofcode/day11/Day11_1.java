package com.adventofcode.day11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adventofcode.utils.FileUtils;

public class Day11_1 {

    protected static final String DELIMITER = " ";

    protected List<Long> stones;

    public static void main(String[] args) throws Exception {
        new Day11_1().count(25);
    }

    protected void count(int blinks) throws Exception {
        loadStones();
        printStones();
        System.out.println("answer = " + countTransformations(blinks));
    }

    protected long countTransformations(int blinks) {
        for(int blinkIndex = 0;blinkIndex<blinks;blinkIndex++) {
            transform();
        }
        return stones.size();
    }

    protected void transform() {
        List<Long> newStones = new ArrayList<>();
        for(Long stone : stones) {
            List<Long> s = transform(stone);
            newStones.addAll(s);
        }
        this.stones = newStones;
    }

    protected List<Long> transform(long stone) {
        if(0L == stone) {
            return List.of(1L);
        }
        String strVal = Long.toString(stone);
        int len = strVal.length();

        if(len % 2 == 0) {
            long left = Long.parseLong(strVal.substring(0, len/2));
            long right = Long.parseLong(strVal.substring(len/2, len));
            return List.of(left, right);
        }
        return List.of(stone * 2024);
    }

    protected void printStones() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________STONES_________\n");
        for (Long stone : stones) {
            sb.append(stone);
            sb.append(DELIMITER);
        }
        System.out.println(sb);
    }

    protected void loadStones() throws Exception {
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day11_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day11_1.txt")))) {
            this.stones = Arrays.stream(br.readLine().split(DELIMITER)).map(Long::parseLong).toList();

        }
    }

}
