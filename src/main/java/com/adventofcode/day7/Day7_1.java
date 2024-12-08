package com.adventofcode.day7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adventofcode.utils.FileUtils;

public class Day7_1 {

    public static void main(String[] args) throws Exception {
        new Day7_1().count();
    }

    public void count() throws Exception {
        // 292: 11 6 16 20
       /* long expected = 292;
        List<Long> values = List.of(11, 6, 16, 20);
        Node node = new Node().calculate(expected, values);
        System.out.println("calibrated = " + node.calibrated);*/

        List<Calibration> calibrations = loadCalibrations();
        long count = 0;
        for (Calibration calibration : calibrations) {
            Node node = new Node().calculate(calibration.expected, calibration.values);
            if (node.calibrated) {
                count += node.expected;
            }
        }
        System.out.println("final answer = " + count);
        // answer is 4998764814652
    }

    protected static class Node {

        int index;
        long expected;
        String exp;
        long val;
        Node mul;
        Node sum;
        boolean calibrated;

        @Override
        public String toString() {
            return "Node{" + "index=" + index + ", expected=" + expected + ", exp='" + exp + '\'' + ", val=" + val
                   + ", calibrated=" + calibrated + '}';
        }

        Node() {

        }

        Node calculate(long expected, List<Long> values) {
            Node root = new Node();
            root.index = 0;
            root.expected = expected;
            root.val = values.get(index);
            root.exp = String.valueOf(val);
            root.sum = sum(root, values);
            root.mul = mul(root, values);
            if (root.sum != null && root.sum.calibrated || root.mul != null && root.mul.calibrated) {
                root.calibrated = true;
            }
            return root;
        }

        Node sum(Node prev, List<Long> values) {
            System.out.println("prev " + prev);
            if (prev.index >= values.size() - 1) {
                return null;
            }
            Node retVal = new Node();
            retVal.index = prev.index + 1;
            long val = values.get(retVal.index);
            retVal.expected = prev.expected;
            retVal.exp = prev.exp + "+" + val;
            retVal.val = prev.val + val;
            retVal.sum = sum(retVal, values);
            retVal.mul = mul(retVal, values);
            if (retVal.isAnswer()) {
                retVal.calibrated = true;
                System.out.println("answer = " + retVal.exp + ", calibrated = " + prev);
            }
            if (retVal.sum != null && retVal.sum.calibrated || retVal.mul != null && retVal.mul.calibrated) {
                prev.calibrated = true;
            }
            return retVal;
        }

        Node mul(Node prev, List<Long> values) {
            if (prev.index >= values.size() - 1) {
                return null;
            }
            Node retVal = new Node();
            retVal.index = prev.index + 1;
            retVal.expected = prev.expected;
            long val = values.get(retVal.index);
            retVal.exp = prev.exp + "*" + val;
            retVal.val = prev.val * val;
            retVal.sum = sum(retVal, values);
            retVal.mul = mul(retVal, values);
            if (retVal.isAnswer()) {
                prev.calibrated = true;
                System.out.println("answer = " + retVal.exp + ", calibrated = " + prev);
            }
            if (retVal.sum != null && retVal.sum.calibrated || retVal.mul != null && retVal.mul.calibrated) {
                prev.calibrated = true;
            }
            return retVal;
        }

        boolean isAnswer() {
            return val == expected;
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
                new InputStreamReader(FileUtils.resourceFileToInputStream("day7_1.txt")))) {
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
