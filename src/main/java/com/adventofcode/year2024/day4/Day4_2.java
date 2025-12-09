package com.adventofcode.year2024.day4;

public class Day4_2 extends Day4_1 {

    private Day4_2() {
        super(3, false, false, true, "MAS", "SAM");
    }

    public static void main(String[] args) throws Exception {
        // 1972 is the correct answer
        new Day4_2().count();
    }

    @Override
    protected int countFrame(Character[][] frame) {
        int counter = super.countFrame(frame);
        return counter == 2 ? 1 : 0;
    }

}
