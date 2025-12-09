package com.adventofcode.year2024.day7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adventofcode.utils.FileUtils;

public class Day7_1 {

    private final boolean countSum;
    private final boolean countMul;
    private final boolean countConcat;

    private Day7_1() {
        this(true, true, false);
    }

    public Day7_1(boolean countSum, boolean countMul, boolean countConcat) {
        this.countSum = countSum;
        this.countMul = countMul;
        this.countConcat = countConcat;
    }

    public static void main(String[] args) throws Exception {
        new Day7_1().count();
        // answer is 4998764814652
    }

    public void count() throws Exception {
        List<Calibration> calibrations = loadCalibrations();
        long count = 0;
        for (Calibration calibration : calibrations) {
            Node node = new Node().calculate(calibration.expected, calibration.values);
            if (node.calibrated) {
                count += node.expected;
            }
        }
        System.out.println("answer = " + count);
    }

    // set calibrated back to the calling node
    private void markCalibratedIfDetected(Node node) {

        if (countSum && node.sum != null && node.sum.calibrated) {
            node.calibrated = true;
        }
        if (countMul && node.mul != null && node.mul.calibrated) {
            node.calibrated = true;
        }
        if (countConcat && node.concat != null && node.concat.calibrated) {
            node.calibrated = true;
        }
    }

    protected class Node {

        int index;
        long expected;
        String exp;
        long val;
        Node mul;
        Node sum;
        Node concat;
        boolean calibrated;

        @Override
        public String toString() {
            return "Node{" + "index=" + index + ", expected=" + expected + ", exp='" + exp + '\'' + ", val=" + val
                   + ", calibrated=" + calibrated + '}';
        }

        Node() {
        }

        protected Node calculate(long expected, List<Long> values) {
            Node root = new Node();
            root.index = 0;
            root.expected = expected;
            root.val = values.get(root.index);
            root.exp = String.valueOf(root.val);
            initNextNodes(values, root);
            markCalibratedIfDetected(root);
            return root;
        }

        Node sum(Node prev, List<Long> values) {
            if (isLast(prev, values)) {
                return null;
            }
            Node retVal = initNode(prev);
            long val = values.get(retVal.index);
            retVal.exp = prev.exp + "+" + val;
            retVal.val = prev.val + val;
            initNextNodes(values, retVal);
            markIfCalibrated(values, retVal);
            return retVal;
        }

        Node mul(Node prev, List<Long> values) {
            if (isLast(prev, values)) {
                return null;
            }
            Node retVal = initNode(prev);
            long val = values.get(retVal.index);
            retVal.exp = prev.exp + "*" + val;
            retVal.val = prev.val * val;
            initNextNodes(values, retVal);
            markIfCalibrated(values, retVal);
            return retVal;
        }

        Node concat(Node prev, List<Long> values) {
            if (isLast(prev, values)) {
                return null;
            }
            Node retVal = initNode(prev);
            long prevVal = prev.val;
            long currVal = values.get(retVal.index);
            long val = Long.parseLong("%d%d".formatted(prevVal, currVal));
            retVal.exp = prev.exp + "||" + currVal;
            retVal.val = val;
            initNextNodes(values, retVal);
            markIfCalibrated(values, retVal);
            return retVal;
        }

        private Node initNode(Node prev) {
            Node retVal = new Node();
            retVal.index = prev.index + 1;
            retVal.expected = prev.expected;
            return retVal;
        }

        private void initNextNodes(List<Long> values, Node retVal) {
            retVal.sum = sum(retVal, values);
            retVal.mul = mul(retVal, values);
            retVal.concat = concat(retVal, values);
        }

        private void markIfCalibrated(List<Long> values, Node retVal) {
            if (retVal.isAnswer() && isLast(retVal, values)) {
                retVal.calibrated = true; // mark this one as calibrated
            }
            markCalibratedIfDetected(retVal);
        }

        boolean isAnswer() {
            return val == expected;
        }

        private boolean isLast(Node prev, List<Long> values) {
            return prev.index >= values.size() - 1;
        }

    }

    protected class Calibration {
        long expected;
        List<Long> values;

        public Calibration(long expected, List<Long> values) {
            this.expected = expected;
            this.values = values;
        }

        @Override
        public String toString() {
            return "Calibration{" + "expected=" + expected + ", values=" + values + '}';
        }
    }

    protected List<Calibration> loadCalibrations() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day7_1.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day7_1_tmp.txt")))) {
            List<Calibration> retVal = new ArrayList<>();
            String calibrationLine = null;
            while ((calibrationLine = br.readLine()) != null && calibrationLine.length() != 0) {
                String[] blocks = calibrationLine.split(":");
                long expected = Long.parseLong(blocks[0]);
                List<Long> values = Arrays.stream(blocks[1].trim().split(" "))
                        .mapToLong(Long::parseLong)
                        .boxed()
                        .toList();
                Calibration calibration = new Calibration(expected, values);
                retVal.add(calibration);
            }
            return retVal;
        }
    }
}
