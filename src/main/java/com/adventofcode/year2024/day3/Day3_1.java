package com.adventofcode.year2024.day3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.LinkedList;

import com.adventofcode.utils.FileUtils;

public class Day3_1 {

    public static void main(String[] args) throws Exception {
        new Day3_1().mullItOver();
    }

    protected void mullItOver() throws Exception {
        int total = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day3_1.txt")))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                Deque<Character> stack = new LinkedList<>();
                StringBuilder x = new StringBuilder();
                StringBuilder y = new StringBuilder();
                for (int index = 0; index < line.length(); index++) {
                    char ch = line.charAt(index);
                    Character stackChar = stack.peekLast();
                    if (ch == 'm') {
                        reset(stack, x, y);
                        stack.add(ch);
                    } else if (ch == 'u') {
                        if (isSequelTo(stackChar, 'm')) {
                            stack.add(ch);
                        } else {
                            reset(stack, x, y);
                        }
                    } else if (ch == 'l') {
                        if (isSequelTo(stackChar, 'u')) {
                            stack.add(ch);
                        } else {
                            reset(stack, x, y);
                        }
                    } else if (ch == '(') {
                        if (isSequelTo(stackChar, 'l')) {
                            stack.add(ch);
                        } else {
                            reset(stack, x, y);
                        }
                    } else if (ch == ',') {
                        if (isSequelTo(stackChar, '(')) {
                            stack.add(ch);
                        } else {
                            reset(stack, x, y);
                        }
                    } else if (Character.isDigit(ch)) {
                        if (isSequelTo(stackChar, '(')) {
                            x.append(ch);
                        } else if (isSequelTo(stackChar, ',')) {
                            y.append(ch);
                        } else {
                            reset(stack, x, y);
                        }
                    } else if (ch == ')') {
                        if (isSequelTo(stackChar, ',')) {
                            total += multiply(x, y);
                        }
                        reset(stack, x, y);
                    } else {
                        reset(stack, x, y);
                    }
                }
            }
        }
        System.out.println(total);
    }

    private static int multiply(StringBuilder x, StringBuilder y) {
        if (!x.isEmpty() && !y.isEmpty()) {
            int xVal = Integer.parseInt(x.toString());
            int yVal = Integer.parseInt(y.toString());
            return xVal * yVal;
        }
        return 0;
    }

    private static boolean isSequelTo(Character stackChar, char ch) {
        return stackChar != null && stackChar.equals(ch);
    }

    private static void reset(Deque<Character> stack, StringBuilder x, StringBuilder y) {
        stack.clear();
        x.setLength(0);
        y.setLength(0);
    }

}
