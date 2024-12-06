package com.adventofcode.day5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.adventofcode.utils.FileUtils;

public class Day5_2 extends Day5_1 {

    public static void main(String[] args) throws Exception {
        new Day5_2().count();
    }

    public void count() throws Exception {
        // 4797 - too high
        // 4299 - too high
        // 123 correct answer for tmp
        // 4151 correct answer
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day5_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day5_1.txt")))) {
            List<Rule> rules = loadRules(br);
            Map<Integer, Set<Integer>> rulesMap = loadRulesMap(rules);
            Map<Integer, Set<Integer>> reverseRulesMap = loadRulesMap(rules);
            List<Update> updates = loadUpdates(br);
            List<Integer> middles = new ArrayList<>();
            for (Update update : updates) {
                boolean fixed = false;
                for (int index = update.pages.size() - 1; index > 0; index--) {
                    Integer last = update.pages.get(index);
                    Integer previous = update.pages.get(index - 1);
                    Set<Integer> befores = rulesMap.get(previous);
                    if (befores == null || !befores.contains(last)) {
                        Set<Integer> potentialBefores = reverseRulesMap.get(last);
                        Integer substitution = update.pages.stream().filter(potentialBefores::contains).findFirst().orElse(null);
                        if(substitution != null) {
                            System.out.printf("Substitution %d by %d\n", last, substitution);
                            update.pages.remove(substitution); // remove original position of substitution. O(n)
                            update.pages.add(index, substitution); // add substitution right here in the middle. O(n) because of copying
                            index = update.pages.size(); // reset index, because replacement could be at the end of the array
                            fixed = true;
                        } else {
                            System.out.println("Wrong update!");
                            break;
                        }
                    }
                }
                if (fixed) {
                    System.out.println("Correct updates : " + update.pages);
                    int middleIndex = update.pages.size() / 2;
                    middles.add(update.pages.get(middleIndex));
                    //return; // tmp for debug
                }
            }
            System.out.println(rules);
            System.out.println("----------");
            System.out.println(updates);
            System.out.printf(
                    "\nanswer : %d, of %s".formatted(middles.stream().mapToInt(Integer::intValue).sum(), middles));
        }
    }

    private static Map<Integer, Set<Integer>> loadReverseRulesMap(List<Rule> rules) {
        Map<Integer, Set<Integer>> rulesMap = new HashMap<>();
        for (Rule rule : rules) {
            rulesMap.computeIfAbsent(rule.before, k -> new HashSet<>()).add(rule.page);
        }
        return rulesMap;
    }

}
