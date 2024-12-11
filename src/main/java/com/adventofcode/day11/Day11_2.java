package com.adventofcode.day11;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adventofcode.utils.FileUtils;

public class Day11_2 {

    protected static final String DELIMITER = " ";

    protected List<Long> stones;

    public static void main(String[] args) throws Exception {
        new Day11_2().count();
    }

    protected void count() throws Exception {
        loadStones();
        printStones();
        //System.out.println("answer = unknown so far");
        blink(75);
        printStones();
        System.out.println("answer = " + stones.size());
    }

   /* protected long transform(List<Long> stones, int index) {
        long retVal = 0;
        if(index < stones.size()) {
            List<Long> newStones = new ArrayList<>();
            Long stone = stones.get(index);
            List<Long> s = transform(stone);

        return newStones.size();
    }*/


    protected long readZeros() {
        return stones.stream().filter(s -> s.equals(0L)).count();
    }

    protected long readDoublesLess5() {
        return stones.stream().mapToInt(s -> {
            String strVal = s.toString();
            int len = strVal.length();
            int counter = 0;
            if(len == 2) {
                long left = Long.parseLong(strVal.substring(0, len/2));
                if(left < 5) {
                    counter++;
                }
                long right = Long.parseLong(strVal.substring(len/2, len));
                if(right < 5) {
                    counter++;
                }
            }
            return counter;
        }).sum();
    }

    protected long countNextZerosByZeros(long zeros, long n) {
        return zeros * ((n+1) / 5);
    }

    protected long countNextZerosByLess3(long less3, long n) {
        return less3;
    }

    protected long countNextDoublesGt3Lt5(long gt3lt5, long n) {
        return 0;
    }




    protected long countNextZerosByLess5(long less5, long n) {
        return less5 * ((n+1) / 4);
    }

    protected void blink(int n) {
        for(int i = 0;i<n;i++) {
            long zeros = readZeros();
            //System.out.println("zeros : " + readZeros());
            //System.out.println("less5 : " + readDoublesLess5());
            long less5 = readDoublesLess5();
            transform();
            //System.out.println("next zeros by zeros : " + countNextZerosByZeros(zeros, i));
            //System.out.println("next zeros by less5 : " + countNextZerosByLess5(zeros, i));
            printStones();
        }
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
        sb.append("_________STONES of %s_________\n".formatted(stones.size()));
        for (Long stone : stones) {
            sb.append(stone);
            sb.append(DELIMITER);
        }
        //System.out.println(sb);
    }

    protected void loadStones() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day11_1_tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day11_1.txt")))) {
            this.stones = Arrays.stream(br.readLine().split(DELIMITER)).map(Long::parseLong).toList();

        }
    }

}
