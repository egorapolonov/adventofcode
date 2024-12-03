package com.adventofcode.day2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import com.adventofcode.utils.FileUtils;

public class Day2_1 {

    private static final String DELIMITER = " ";
    private static final int MIN_DIFF = 1;
    private static final int MAX_DIFF = 3;

    public static void main(String[] args) throws Exception {
        new Day2_1().validateReports();
    }

    protected void validateReports() throws Exception {
        int totalValidCounter = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day2_1.txt")))) {
            String report = null;
            while ((report = br.readLine()) != null) {
                boolean valid = isSafeReport(report);
                if (valid) {
                    totalValidCounter++;
                }
            }
        }
        System.out.println(totalValidCounter);
    }

    protected boolean isSafeReport(String report) {
        List<Integer> levels = readLevels(report);
        Boolean increasing = null;
        Integer prevLevel = null;
        Integer currLevel = null;
        boolean valid = true;
        for (int index = 0; index < levels.size() && valid; index++) {
            prevLevel = currLevel;
            currLevel = levels.get(index);
            if (index == 1) {
                increasing = currLevel > prevLevel;
            }
            if (index > 0) {
                valid &= isSafeLevelPair(increasing, prevLevel, currLevel);
            }
        }
        return valid;
    }

    protected List<Integer> readLevels(String report) {
        return Arrays.stream(report.split(DELIMITER)).map(Integer::parseInt).toList();
    }

    protected boolean isSafeLevelPair(Boolean increasing, Integer prevLevel, Integer currLevel) {
        if (increasing) {
            int diff = currLevel - prevLevel;
            return diff >= MIN_DIFF && diff <= MAX_DIFF;
        }
        int diff = prevLevel - currLevel;
        return diff >= MIN_DIFF && diff <= MAX_DIFF;
    }

}
