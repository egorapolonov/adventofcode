package com.adventofcode.day17;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;

import com.adventofcode.utils.FileUtils;

public class Day17_1 {

    protected static final char SPACE = '.';
    protected ProgramInfo map;

    public static void main(String[] args) throws Exception {
        new Day17_1().count();
        // answer 7,1,2,3,2,6,7,2,5 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println("answer = " + map.run());
        printMap();
    }

    private static class ProgramInfo {
        long regA;
        long regB;
        long regC;
        int[] prog;
        StringBuilder out = new StringBuilder(0);
        int instructionPointer = 0;

        String run() {
            out.setLength(0);
            instructionPointer = 0;
            while (instructionPointer < prog.length) {
                calc();
                System.out.println(this);
            }
            return out.toString();
        }

        void calc() {
            int comboOperand = prog[instructionPointer];
            int instruction = prog[instructionPointer+1];
            System.out.println("calc : %d, %d".formatted(comboOperand, instruction));
            switch (comboOperand) {
                case 0: {
                    adv(instruction);
                    break;
                }
                case 1: {
                    bxl(instruction);
                    break;
                }
                case 2 : {
                    bst(instruction);
                    break;
                }
                case 3 : {
                    jnz(instruction);
                    break;
                }
                case 4 : {
                    bxc(instruction);
                    break;
                }
                case 5: {
                    out(instruction);
                    break;
                }
                case 6: {
                    bdv(instruction);
                    break;
                }
                case 7: {
                    cdv(instruction);
                    break;
                }
                default: {
                    // nothing so far
                    break;
                }
            }
        }

        void movePointer() {
            this.instructionPointer+=2;
        }

        // division
        void adv(int instruction) {
            System.out.println("adv : " + instruction);
            long value = instructionValue(instruction);
            this.regA /= pow2(value); //pow of 2
            movePointer();
        }

        private static long pow2(long value) {
            if(value == 0) {
                return 1;
            } else {
                return 2 << (value - 1);
            }
            //return Double.valueOf(Math.pow(2, value)).longValue();
        }

        // bitwise XOR
        void bxl(int instruction) {
            System.out.println("bxl : " + instruction);
            this.regB ^= instruction;
            movePointer();
        }

        // combo modulo 8
        void bst(int instruction) {
            System.out.println("bst : " + instruction);
            this.regB = instructionValue(instruction) % 8;
            movePointer();
        }

        // move instruction pointer to actual value of literal operand
        void jnz(int instruction) {
            System.out.println("jnz : " + instruction);
            if(regA != 0) {
                this.instructionPointer = instruction;
            } else {
                movePointer();
            }
        }

        // bitwise XOR between B and C
        void bxc(int instruction) {
            System.out.println("bxc : " + instruction);
            this.regB = this.regB ^ this.regC;
            movePointer();
        }

        void out(int instruction) {
            long instructionValue = instructionValue(instruction);
            long value = instructionValue % 8;
            if(this.out.length() > 0) {
                this.out.append(",");
            }
            this.out.append(value);
            System.out.println(value);
            movePointer();
        }

        // division
        void bdv(int instruction) {
            System.out.println("bdv : " + instruction);
            this.regB = this.regA / pow2(instructionValue(instruction)); //pow of 2
            movePointer();
        }

        // division
        void cdv(int instruction) {
            System.out.println("cdv : " + instruction);
            this.regC = this.regA / pow2(instructionValue(instruction)); //pow of 2
            movePointer();
        }

        // actual value resolver
        long instructionValue(int instruction) {
            return switch (instruction) {
                case 0, 1, 2, 3 -> {
                    yield instruction;
                }
                case 4 -> regA;
                case 5 -> regB;
                case 6 -> regC;
                case 7 -> throw new IllegalArgumentException(
                        "Combo operand 7 is reserved and will not appear in valid programs.");
                default -> throw new IllegalArgumentException("Unsuported instruction : " + instruction);
            };
        }

        @Override
        public String toString() {
            return "ProgramInfo{" + "regA=" + regA + ", regB=" + regB + ", regC=" + regC + ", prog=" + Arrays.toString(
                    prog) + ", out=" + out + ", instructionPointer=" + instructionPointer + '}';
        }
    }

    protected void printMap() {
        System.out.println(map);
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day17_1_1_tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day17_1_2_tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day17_1_3_tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day17_1_4_tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day17_1_5_tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day17_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day17_1.txt")))) {
            String line = null;
            ProgramInfo programInfo = new ProgramInfo();
            String REG_A = "Register A: ";
            String REG_B = "Register B: ";
            String REG_C = "Register C: ";
            String PROG = "Program: ";
            while ((line = br.readLine()) != null) {
                if (line.startsWith(REG_A)) {
                    programInfo.regA = Long.parseLong(line.substring(line.indexOf(REG_A) + REG_A.length()));
                } else if (line.startsWith(REG_B)) {
                    programInfo.regB = Long.parseLong(line.substring(line.indexOf(REG_B) + REG_B.length()));
                } else if (line.startsWith(REG_C)) {
                    programInfo.regC = Long.parseLong(line.substring(line.indexOf(REG_C) + REG_C.length()));
                } else if (line.startsWith(PROG)) {
                    programInfo.prog = Arrays.stream(line.substring(line.indexOf(PROG) + PROG.length()).split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray();
                }
            }
            this.map = programInfo;
        }
    }
}
