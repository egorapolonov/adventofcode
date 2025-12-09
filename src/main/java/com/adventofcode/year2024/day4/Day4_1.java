package com.adventofcode.year2024.day4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day4_1 {

    protected final int SIDE;
    protected final boolean countHorizontal;
    protected final boolean countVertical;
    protected final boolean countDiagonals;
    protected final Set<String> WORDS;
    protected final LinkedList<String> linesCache;
    protected boolean lastLineOnly = false;
    protected boolean lastBarOnly = false;

    public Day4_1() {
        this(4, true, true, true, "XMAS", "SAMX");
    }

    public Day4_1(int side, boolean countHorizontal, boolean countVertical, boolean countDiagonals, String... words) {
        this.SIDE = side;
        this.countHorizontal = countHorizontal;
        this.countVertical = countVertical;
        this.countDiagonals = countDiagonals;
        this.WORDS = Set.of(words);
        this.linesCache = new LinkedList<>();
    }

    public static void main(String[] args) throws Exception {
        new Day4_1().count();
    }

    public void count() throws Exception {
        // 2578 correct answer
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day4_1_tmp_short.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day4_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day4_1.txt")))) {
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
                        lastLineOnly = countLastLineOnly(linesCounter);
                        lastBarOnly = countLastBarOnly(xOffset);
                        //System.out.printf("frame : %s\n", Arrays.deepToString(frame));
                        counter += countFrame(frame);
                        //System.out.println(counter);
                    }
                }
            }
            System.out.println(counter);
        }
    }

    protected boolean countLastBarOnly(int xOffset) {
        return xOffset > 0;
    }

    protected boolean countLastLineOnly(int linesCounter) {
        return linesCounter > SIDE;
    }

    protected void printFrame(Character[][] frame) {
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

    protected Character[][] loadFrame(int xOffset) {
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

    protected int countFrame(Character[][] frame) {
        System.out.printf("ll : %s, leftDiagonal : %s\n", lastLineOnly, lastBarOnly);
        int counter = 0;
        StringBuilder leftDiagonal = new StringBuilder();
        StringBuilder rightDiagonal = new StringBuilder();
        for (int x = 0; x < SIDE; x++) {
            StringBuilder horizontal = new StringBuilder();
            StringBuilder vertical = new StringBuilder();
            for (int y = 0; y < SIDE; y++) {
                if(countHorizontal) {
                    if (lastLineOnly) {
                        if (isLast(x)) {
                            horizontal.append(frame[x][y]);
                        }
                    } else {
                        horizontal.append(frame[x][y]);
                    }
                }
                if(countVertical) {
                    if (lastBarOnly) {
                        if (isLast(x)) {
                            vertical.append(frame[y][x]); //reverse XY
                        }
                    } else {
                        vertical.append(frame[y][x]); //reverse XY
                    }
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

    protected boolean isRightDiagonalPoint(int x, int y) {
        return x == SIDE - y - 1;
    }

    protected boolean isLeftDiagonalPoint(int x, int y) {
        return x == y;
    }

    protected boolean isLast(int x) {
        return x == SIDE - 1;
    }

    protected boolean isValid(StringBuilder sb) {
        String value = sb.toString();
        return WORDS.contains(value);
    }
}
