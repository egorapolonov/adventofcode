package com.adventofcode.day3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Deque;
import java.util.LinkedList;

import com.adventofcode.utils.FileUtils;

public class Day3_2 {

    public static void main(String[] args) throws Exception {
        new Day3_2().mullItOver();
    }

    protected void mullItOver() throws Exception {
        int total = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day3_2.txt")))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                Deque<Character> stack = new LinkedList<>();
                Deque<Character> instStack = new LinkedList<>();
                boolean enabled = true;
                StringBuilder x = new StringBuilder();
                StringBuilder y = new StringBuilder();
                for (int index = 0; index < line.length(); index++) {
                    char ch = line.charAt(index);
                    Character stackChar = stack.peekLast();
                    Character instChar = instStack.peekLast();
                    System.out.println(
                            "%s -> %s ---> %s ---> %s | %s | %s | %d".formatted(Character.toString(ch), instStack,
                                    stack, x, y, enabled, total));
                    if (ch == 'm') {
                        reset(stack, x, y);
                        resetInst(instStack);
                        stack.add(ch);
                    } else if (ch == 'u') {
                        handleSymbol(ch, 'm', stackChar, stack, instStack, x, y);
                    } else if (ch == 'l') {
                        handleSymbol(ch, 'u', stackChar, stack, instStack, x, y);
                    } else if (ch == '(') {
                        handleOpenBracket(ch, stackChar, stack, instChar, instStack, x, y);
                    } else if (ch == ',') {
                        handleSymbol(ch, '(', stackChar, stack, instStack, x, y);
                    } else if (Character.isDigit(ch)) {
                        handleDigit(ch, stackChar, stack, instStack, x, y);
                    } else if (ch == ')') {
                        if (isSequelTo(stackChar, ',')) {
                            total += multiply(x, y, enabled);
                        } else {
                            enabled = handleCloseBracket(ch, instChar, instStack, enabled);
                        }
                        resetInst(instStack);
                        reset(stack, x, y);
                    } else if (ch == 'd') {
                        resetInst(instStack);
                        reset(stack, x, y);
                        instStack.add(ch);
                    } else if (ch == 'o') {
                        handleInstruction(ch, 'd', instChar, instStack, stack, x, y);
                    } else if (ch == 'n') {
                        handleInstruction(ch, 'o', instChar, instStack, stack, x, y);
                    } else if (ch == '\'') {
                        handleInstruction(ch, 'n', instChar, instStack, stack, x, y);
                    } else if (ch == 't') {
                        handleInstruction(ch, '\'', instChar, instStack, stack, x, y);
                    } else {
                        reset(stack, x, y);
                    }
                }
            }
        }
        System.out.println(total);
    }

    private static void handleInstruction(char ch, char pre, Character instChar, Deque<Character> instStack,
            Deque<Character> stack, StringBuilder x, StringBuilder y) {
        if (isSequelTo(instChar, pre)) {
            instStack.add(ch);
        } else {
            resetInst(instStack);
            reset(stack, x, y);
        }
    }

    private static void handleDigit(char ch, Character stackChar, Deque<Character> stack, Deque<Character> instStack,
            StringBuilder x, StringBuilder y) {
        if (isSequelTo(stackChar, '(')) {
            x.append(ch);
        } else if (isSequelTo(stackChar, ',')) {
            y.append(ch);
        } else {
            reset(stack, x, y);
        }
        resetInst(instStack);
    }

    private static void handleSymbol(char ch, char pre, Character stackChar, Deque<Character> stack,
            Deque<Character> instStack, StringBuilder x, StringBuilder y) {
        if (isSequelTo(stackChar, pre)) {
            stack.add(ch);
        } else {
            reset(stack, x, y);
        }
        resetInst(instStack);
    }

    private static boolean handleCloseBracket(char ch, Character instChar, Deque<Character> instStack, boolean enabled) {
        if (isSequelTo(instChar, '(')) {
            instStack.add(ch);
            if (instStack.size() == 4) {
                enabled = true;
            } else if (instStack.size() == 7) {
                enabled = false;
            }
            resetInst(instStack);
        } else {
            resetInst(instStack);
        }
        return enabled;
    }

    private static void handleOpenBracket(char ch, Character stackChar, Deque<Character> stack, Character instChar,
            Deque<Character> instStack, StringBuilder x, StringBuilder y) {
        if (isSequelTo(stackChar, 'l')) {
            stack.add(ch);
            resetInst(instStack);
        } else {
            if (isSequelTo(instChar, 'o') || isSequelTo(instChar, 't')) {
                instStack.add(ch);
            } else {
                resetInst(instStack);
            }
            reset(stack, x, y);
        }
    }

    private static int multiply(StringBuilder x, StringBuilder y, boolean enabled) {
        if (enabled && !x.isEmpty() && !y.isEmpty()) {
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

    private static void resetInst(Deque<Character> instStack) {
        instStack.clear();
    }

}
