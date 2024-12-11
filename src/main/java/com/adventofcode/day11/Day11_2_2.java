package com.adventofcode.day11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.adventofcode.utils.FileUtils;

public class Day11_2_2 {

    protected static final String DELIMITER = " ";

    protected List<Long> stones;

    public static void main(String[] args) throws Exception {
        new Day11_2_2().count();
    }

    protected void count() throws Exception {
        loadStones();
        printStones();
        System.out.println("answer = unknown so far");
        blink(15);
        printStones();
        System.out.println("answer = " + stones.size());
    }

    protected void blink(int n) {
        for(long s = 0; s<10;s++) {
            this.stones = List.of(s);
            System.out.println("Analysis of [%d]".formatted(s) );
            boolean printed = false;
            for(int i = 0;i<n;i++) {
                transform();
                if(stones.contains(s) && !printed) {
                    System.out.println("REPETITION OF [%d] = [%d]".formatted(s,i));
                    printed = true;
                }
                printStones();
            }
        }

    }

    private long countNumber(List<Long> stones, int number, int blink) {
        long retVal = stones.stream().filter(Objects::nonNull).filter(l -> l == 0).count();
        System.out.println("zeros of [%d] in [%d] blinks = %d".formatted(number, blink, retVal));
        return retVal;
    }

    /*private long countEvens(List<Long> stones, int number, int blink) {
        long retVal = stones.stream().filter(Objects::nonNull).filter(l -> l % 2 == 0).count();
        System.out.println("evens of [%d] in [%d] blinks = %d".formatted(number, blink, retVal));
        return retVal;
    }

    private long countEvens(List<Long> stones, int number, int blink) {
        long retVal = stones.stream().filter(Objects::nonNull).filter(l -> l % 2 == 0).count();
        System.out.println("evens of [%d] in [%d] blinks = %d".formatted(number, blink, retVal));
        return retVal;
    }*/

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
        //sb.append("_________STONES_________\n");
        for (Long stone : stones) {
            sb.append(stone);
            sb.append(DELIMITER);
        }
        System.out.println(sb);
    }

    protected void loadStones() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day11_1_tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day11_1.txt")))) {
            this.stones = Arrays.stream(br.readLine().split(DELIMITER)).map(Long::parseLong).toList();

        }
    }

}
