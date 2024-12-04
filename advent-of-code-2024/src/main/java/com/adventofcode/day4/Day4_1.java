package com.adventofcode.day4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day4_1_tmp_short.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day4_1_tmp.txt")))) {
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
        System.out.println(rsb);
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
        System.out.printf("ll : %s, lb : %s\n", lastLine, lastBar);
        int counter = 0;
        Deque<Character> leftDiagonalValidator = new LinkedList<>();
        Deque<Character> rightDiagonalValidator = new LinkedList<>();
        StringBuilder lb = new StringBuilder();
        StringBuilder rb = new StringBuilder();
        for (int x = 0; x < SIDE; x++) {
            Deque<Character> horizontalValidator = new LinkedList<>();
            Deque<Character> verticalValidator = new LinkedList<>();
            StringBuilder hb = new StringBuilder();
            StringBuilder yb = new StringBuilder();
            for (int y = 0; y < SIDE; y++) {
                /*if(lastLine) {
                     if(x == SIDE - 1) {
                         pushIfValid(horizontalValidator, frame[x][y]);
                         hb.append(frame[x][y]);
                     }
                } else {
                    pushIfValid(horizontalValidator, frame[x][y]);
                    hb.append(frame[x][y]);
                }
                if(lastBar) {
                    if( x == SIDE - 1){
                        pushIfValid(verticalValidator, frame[y][x]);
                        yb.append(frame[y][x]);
                        yb.append("\n");
                    }
                } else {
                    pushIfValid(verticalValidator, frame[y][x]);
                    yb.append(frame[y][x]);
                    yb.append("\n");
                }*/
                if (x == y) {
                    pushIfValid(leftDiagonalValidator, frame[x][y]);
                    lb.append(frame[x][y]);
                }
                if (x == SIDE - y - 1) {
                    pushIfValid(rightDiagonalValidator, frame[x][y]);
                    rb.append(frame[x][y]);
                }
            }
            if (horizontalValidator.size() == HASH) {
                counter++;
                System.out.printf("h : %s\n", hb);
            }
            hb.setLength(0);
            horizontalValidator.clear();
            if (verticalValidator.size() == HASH) {
                counter++;
                System.out.printf("v : %s\n", yb);
            }
            System.out.printf("v : %s\n", yb);
            verticalValidator.clear();
            yb.setLength(0);
        }
        if (leftDiagonalValidator.size() == HASH) {
            System.out.printf("l++ : %s\n", lb);
            counter++;
        }
        System.out.printf("l : %s\n", lb);
        if (rightDiagonalValidator.size() == HASH) {
            System.out.printf("r++ : %s\n", rb);
            counter++;
        }
        System.out.printf("r : %s\n", rb);
        return counter;
    }

    private void pushIfValid(Deque<Character> validator, Character ch) {
        switch (ch) {
            case 'X': {
                if(validator.isEmpty() || 'M' == validator.peekLast()) {
                    validator.add(ch);
                }
            }
            case 'M' : {
                if(!validator.isEmpty() && ('A' == validator.peekLast() || 'X' == validator.peekLast())) {
                    validator.add(ch);
                }
            }
            case 'A' : {
                if(!validator.isEmpty() && ('S' == validator.peekLast() || 'M' ==validator.peekLast())) {
                    validator.add(ch);
                }
            }
            case 'S' : {
                if(validator.isEmpty() || 'A' ==validator.peekLast()) {
                    validator.add(ch);
                }
            }
        }
    }

    private boolean isValid(StringBuilder sb) {
        String value = sb.toString();
        return XMAS.equals(value) ||  SAMX.equals(value);
    }
}
