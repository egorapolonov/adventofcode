package com.adventofcode.year2024.day3;

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
        // 89912299 - wrong answer | 87163705 - correct one
        int total = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day3_1.txt")))) {
            String line = null;
            boolean enabled = true;
            while ((line = br.readLine()) != null) {
                Deque<Character> stack = new LinkedList<>();
                Deque<Character> instStack = new LinkedList<>();
                StringBuilder x = new StringBuilder();
                StringBuilder y = new StringBuilder();
                for (int index = 0; index < line.length(); index++) {
                    char ch = line.charAt(index);
                    Character stackChar = stack.peekLast();
                    Character instChar = instStack.peekLast();
                    System.out.printf("%s -> %s ---> %s ---> %s | %s | %s | %d%n", ch, instStack, stack, x, y, enabled,
                            total);
                    if (ch == 'm') {
                        fullReset(stack, instStack, x, y);
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
                        if (isEqualTo(stackChar, ',')) {
                            total += multiply(stack, instStack, x, y, enabled);
                        } else {
                            enabled = handleCloseBracket(ch, instChar, instStack, stack, x, y, enabled);
                        }
                    } else if (ch == 'd') {
                        fullReset(stack, instStack, x, y);
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
                        fullReset(stack, instStack, x, y);
                    }
                }
            }
        }
        System.out.println(total);
    }

    private static void fullReset(Deque<Character> stack, Deque<Character> instStack, StringBuilder x,
            StringBuilder y) {
        reset(stack, x, y);
        resetInst(instStack);
    }

    private static void handleInstruction(char ch, char pre, Character instChar, Deque<Character> instStack,
            Deque<Character> stack, StringBuilder x, StringBuilder y) {
        if (isEqualTo(instChar, pre)) {
            instStack.add(ch);
        } else {
            resetInst(instStack);
        }
        reset(stack, x, y);
    }

    private static void handleDigit(char ch, Character stackChar, Deque<Character> stack, Deque<Character> instStack,
            StringBuilder x, StringBuilder y) {
        if (isEqualTo(stackChar, '(')) {
            x.append(ch);
        } else if (isEqualTo(stackChar, ',')) {
            y.append(ch);
        } else {
            reset(stack, x, y);
        }
        resetInst(instStack);
    }

    private static void handleSymbol(char ch, char pre, Character stackChar, Deque<Character> stack,
            Deque<Character> instStack, StringBuilder x, StringBuilder y) {
        if (isEqualTo(stackChar, pre)) {
            stack.add(ch);
        } else {
            reset(stack, x, y);
        }
        resetInst(instStack);
    }

    private static boolean handleCloseBracket(char ch, Character instChar, Deque<Character> instStack,
            Deque<Character> stack, StringBuilder x, StringBuilder y, boolean enabled) {
        if (isEqualTo(instChar, '(')) {
            instStack.add(ch);
            if (instStack.size() == 4) {
                enabled = true;
                System.out.printf("enabled : %s", enabled);
            } else if (instStack.size() == 7) {
                enabled = false;
                System.out.printf("disabled : %s", enabled);
            }
        }
        fullReset(stack, instStack, x, y);
        return enabled;
    }

    private static void handleOpenBracket(char ch, Character stackChar, Deque<Character> stack, Character instChar,
            Deque<Character> instStack, StringBuilder x, StringBuilder y) {
        if (isEqualTo(stackChar, 'l')) {
            stack.add(ch);
            resetInst(instStack);
        } else {
            if (isEqualTo(instChar, 'o') || isEqualTo(instChar, 't')) {
                instStack.add(ch);
            } else {
                resetInst(instStack);
            }
            reset(stack, x, y);
        }
    }

    private static int multiply(Deque<Character> stack, Deque<Character> instStack, StringBuilder x, StringBuilder y,
            boolean enabled) {
        if (enabled && !x.isEmpty() && !y.isEmpty()) {
            int xVal = Integer.parseInt(x.toString());
            int yVal = Integer.parseInt(y.toString());
            fullReset(stack, instStack, x, y);
            return xVal * yVal;
        }
        fullReset(stack, instStack, x, y);
        return 0;
    }

    private static boolean isEqualTo(Character stackChar, char ch) {
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
