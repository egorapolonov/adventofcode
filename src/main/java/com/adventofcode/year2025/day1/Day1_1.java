package com.adventofcode.year2025.day1;

import static java.util.Objects.nonNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.adventofcode.utils.FileUtils;

public class Day1_1 {

    private static final String L = "L";
    private static final String R = "R";
    private static final int MAX = 99;
    private int val = 50;
    private int answer = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("Answer : " + new Day1_1().solve());
    }

    private int solve() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2025/day1_1.txt")))) {
            String line;
            while (nonNull(line = br.readLine())) {
                int pointer = rotate(line);
                System.out.println(val);
                if(pointer == 0) {
                    answer++;
                }
            }
        }
        return answer;
    }

    private int l(int number) {
        val -= number;
        if (val < 0) {
            val %= MAX+1;
            if(val != 0) {
                val += MAX+1;
            }
        }
        return val;
    }

    private int r(int number) {
        val += number;
        if (val > MAX) {
            val %= MAX+1;
        }
        return val;
    }

    private int rotate(String line) {
        if (nonNull(line)) {
            String prefix = line.substring(0, 1);
            int num = Integer.parseInt(line.substring(1));
            if (L.equals(prefix)) {
                return l(num);
            } else if (R.equals(prefix)) {
                return r(num);
            }
        }
        throw new IllegalArgumentException("Unable to parse line : " + line);
    }

}
