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

public class Day5_2 {

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
                    if(fixed) {
                        System.out.printf("\npair = [%d,%d]\n", previous, last);
                    }
                    Set<Integer> befores = rulesMap.get(previous);
                    if (befores == null || !befores.contains(last)) {
                        Set<Integer> potentialBefores = reverseRulesMap.get(last);
                        Integer substitution = update.pages.stream().filter(potentialBefores::contains).findFirst().orElse(null);
                        if(substitution != null) {
                            System.out.println("Initial : " + update.pages);
                            System.out.printf("Substitution %d by %d\n", last, substitution);
                            update.pages.remove(substitution); // remove original position of substitution. O(n)
                            update.pages.add(index, substitution); // add substitution right here in the middle. O(n) because of copying
                            System.out.printf("Update at %d : %s\n", index, update.pages);
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

    private static class Rule {
        int page;
        int before;

        public Rule(int page, int before) {
            this.page = page;
            this.before = before;
        }

        public Rule(String page, String before) {
            this.page = Integer.parseInt(page);
            this.before = Integer.parseInt(before);
        }

        @Override
        public String toString() {
            return "%d|%d".formatted(page, before);
        }
    }

    private static Map<Integer, Set<Integer>> loadRulesMap(List<Rule> rules) {
        Map<Integer, Set<Integer>> rulesMap = new HashMap<>();
        for (Rule rule : rules) {
            rulesMap.computeIfAbsent(rule.page, k -> new HashSet<>()).add(rule.before);
        }
        return rulesMap;
    }

    private static Map<Integer, Set<Integer>> loadReverseRulesMap(List<Rule> rules) {
        Map<Integer, Set<Integer>> rulesMap = new HashMap<>();
        for (Rule rule : rules) {
            rulesMap.computeIfAbsent(rule.before, k -> new HashSet<>()).add(rule.page);
        }
        return rulesMap;
    }

    private static class Update {

        List<Integer> pages;

        public Update(List<Integer> pages) {
            this.pages = pages;
        }

        @Override
        public String toString() {
            return pages.toString();
        }
    }

    private List<Rule> loadRules(BufferedReader br) throws Exception {
        List<Rule> retVal = new ArrayList<>();
        String ruleLine = null;
        while ((ruleLine = br.readLine()) != null && ruleLine.length() != 0) {
            String[] pages = ruleLine.split("\\|");
            Rule rule = new Rule(pages[0], pages[1]);
            retVal.add(rule);
        }
        return retVal;
    }

    private List<Update> loadUpdates(BufferedReader br) throws Exception {
        List<Update> retVal = new ArrayList<>();
        String updateLine = null;
        while ((updateLine = br.readLine()) != null) {
            Update update = new Update(
                    Arrays.stream(updateLine.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
            retVal.add(update);
        }
        return retVal;
    }

}
