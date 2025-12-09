package com.adventofcode.year2024.day2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day2_2 extends Day2_1 {

    public static void main(String[] args) throws Exception {
        new Day2_2().validateReports();
    }

    @Override
    protected boolean isSafeReport(String report) {
        List<Integer> levels = readLevels(report);
        boolean valid = isValidLevels(levels);
        System.out.printf("%s : %s\n", levels, valid);
        if(!valid) {
            levels = new ArrayList<>(levels);
            Collections.reverse(levels); // quick workaround if the problem at the beginning of levels array
            return isValidLevels(levels);
        }
        return valid;
    }

    private boolean isValidLevels(List<Integer> levels) {
        Boolean increasing = null;
        Integer prevLevel = null;
        Integer currLevel = null;
        boolean valid = true;
        int unsafeCounter = 0;
        for (int index = 0; index < levels.size() && valid; index++) {
            prevLevel = currLevel;
            currLevel = levels.get(index);
            System.out.printf("%d vs %d\n", prevLevel, currLevel);
            if (index == 1) {
                increasing = currLevel > prevLevel;
            }
            if (index > 0) {
                boolean safe = isSafeLevelPair(increasing, prevLevel, currLevel);
                if(!safe) {
                    if(unsafeCounter == 0) {
                        unsafeCounter++;
                        safe = true;
                        currLevel = prevLevel;
                    }
                }
                valid &= safe;
            }
        }
        return valid;
    }
}
