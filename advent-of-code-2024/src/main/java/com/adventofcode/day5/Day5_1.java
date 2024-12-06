package com.adventofcode.day5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.adventofcode.utils.FileUtils;

public class Day5_1 {

    public static void main(String[] args) throws Exception {
        new Day5_1().count();
    }

    public void count() throws Exception {
        // 143 correct answer for tmp
        // 143 correct answer
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day5_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day5_1.txt")))) {
            List<Rule> rules = loadRules(br);
            Map<Integer, Set<Integer>> rulesMap = loadRulesMap(rules);
            List<Update> updates = loadUpdates(br);
            List<Integer> middles = new ArrayList<>();
            for (Update update : updates) {
                boolean correct = true;
                for (int index = update.pages.size() - 1; index > 0; index--) {
                    Integer last = update.pages.get(index);
                    Integer previous = update.pages.get(index - 1);
                    Set<Integer> befores = rulesMap.get(previous);
                    if (befores == null || !befores.contains(last)) {
                        System.out.println("Wrong update!");
                        correct = false;
                        break;
                    }
                }
                if (correct) {
                    System.out.println("Correct updates : " + update.pages);
                    int middleIndex = update.pages.size() / 2;
                    middles.add(update.pages.get(middleIndex));
                }
            }
            System.out.println(rules);
            System.out.println("----------");
            System.out.println(updates);
            System.out.printf(
                    "\nanswer : %d, of %s".formatted(middles.stream().mapToInt(Integer::intValue).sum(), middles));
        }
    }

    protected static class Rule {
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

    protected static Map<Integer, Set<Integer>> loadRulesMap(List<Rule> rules) {
        Map<Integer, Set<Integer>> rulesMap = new HashMap<>();
        for (Rule rule : rules) {
            rulesMap.computeIfAbsent(rule.page, k -> new HashSet<>()).add(rule.before);
        }
        return rulesMap;
    }

    protected static class Update {

        List<Integer> pages;

        public Update(List<Integer> pages) {
            this.pages = pages;
        }

        @Override
        public String toString() {
            return pages.toString();
        }
    }

    protected List<Rule> loadRules(BufferedReader br) throws Exception {
        List<Rule> retVal = new ArrayList<>();
        String ruleLine = null;
        while ((ruleLine = br.readLine()) != null && ruleLine.length() != 0) {
            String[] pages = ruleLine.split("\\|");
            Rule rule = new Rule(pages[0], pages[1]);
            retVal.add(rule);
        }
        return retVal;
    }

    protected List<Update> loadUpdates(BufferedReader br) throws Exception {
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
