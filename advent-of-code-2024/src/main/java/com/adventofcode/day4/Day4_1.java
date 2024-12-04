package com.adventofcode.day4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;

import com.adventofcode.utils.FileUtils;

public class Day4_1 {

    private static final int SIDE = 4;
    private static final int HASH = 4;
    private static final String XMAS = "XMAS";
    private static final String SAMX = "SAMX";

    private LinkedList<String> linesCache = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        new Day4_1().count();
    }

    private void count() throws Exception {
        // 2578 correct answer
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day4_1_tmp_short.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day4_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day4_1.txt")))) {
            String line;
            int counter = 0;
            int linesCounter = 0;
            while ((line = br.readLine()) != null) {
                if (linesCache.size() == SIDE) {
                    linesCache.removeFirst();
                    System.out.println("__________________________");
                }
                linesCache.add(line);
                linesCounter++;
                if (linesCache.size() == SIDE) {
                    for (int xOffset = 0; xOffset <= linesCache.peek().length() - SIDE; xOffset++) {
                        Character[][] frame = loadFrame(xOffset);
                        printFrame(frame);
                        //System.out.printf("frame : %s\n", Arrays.deepToString(frame));
                        counter += countFrame(frame, linesCounter > SIDE, xOffset > 0);
                        //System.out.println(counter);
                    }
                }
            }
            System.out.println(counter);
        }
    }

    private void printFrame(Character[][] frame) {
        StringBuilder sb = new StringBuilder();
        StringBuilder rsb = new StringBuilder();
        rsb.append("(");
        for (int x = 0; x < frame.length; x++) {
            for (int y = 0; y < frame[x].length; y++) {
                sb.append(frame[x][y]);
                rsb.append(frame[x][y]);
            }
            if (x != frame.length - 1) {
                sb.append("\n");
                rsb.append("|");
            }
        }
        rsb.append(")");
        System.out.println(sb);
        System.out.println(rsb); // regex for search frame for debug in txt document. For example, (SMMS|MXMA|XAAM|SXSX)
    }

    private Character[][] loadFrame(int xOffset) {
        Character[][] frame = new Character[SIDE][SIDE];
        int lineIndex = 0;
        for (String line : linesCache) {
            for (int x = 0; x < SIDE; x++) {
                frame[lineIndex][x] = line.charAt(x + xOffset);
            }
            lineIndex++;
        }
        return frame;
    }

    private int countFrame(Character[][] frame, boolean lastLine, boolean lastBar) {
        System.out.printf("ll : %s, leftDiagonal : %s\n", lastLine, lastBar);
        int counter = 0;
        StringBuilder leftDiagonal = new StringBuilder();
        StringBuilder rightDiagonal = new StringBuilder();
        for (int x = 0; x < SIDE; x++) {
            StringBuilder horizontal = new StringBuilder();
            StringBuilder vertical = new StringBuilder();
            for (int y = 0; y < SIDE; y++) {
                if (lastLine) {
                    if (isLast(x)) {
                        horizontal.append(frame[x][y]);
                    }
                } else {
                    horizontal.append(frame[x][y]);
                }
                if (lastBar) {
                    if (isLast(x)) {
                        vertical.append(frame[y][x]); //reverse XY
                    }
                } else {
                    vertical.append(frame[y][x]); //reverse XY
                }
                if (isLeftDiagonalPoint(x, y)) {
                    leftDiagonal.append(frame[x][y]);
                }
                if (isRightDiagonalPoint(x, y)) {
                    rightDiagonal.append(frame[x][y]);
                }
            }
            if (isValid(horizontal)) {
                counter++;
                System.out.printf("h : %s\n", horizontal);
            }
            horizontal.setLength(0);
            if (isValid(vertical)) {
                counter++;
                System.out.printf("v : %s\n", vertical);
            }
            vertical.setLength(0);
        }
        if (isValid(leftDiagonal)) {
            System.out.printf("l : %s\n", leftDiagonal);
            counter++;
        }
        if (isValid(rightDiagonal)) {
            System.out.printf("r : %s\n", rightDiagonal);
            counter++;
        }
        return counter;
    }

    private static boolean isRightDiagonalPoint(int x, int y) {
        return x == SIDE - y - 1;
    }

    private static boolean isLeftDiagonalPoint(int x, int y) {
        return x == y;
    }

    private static boolean isLast(int x) {
        return x == SIDE - 1;
    }

    private boolean isValid(StringBuilder sb) {
        String value = sb.toString();
        return XMAS.equals(value) || SAMX.equals(value);
    }
}
