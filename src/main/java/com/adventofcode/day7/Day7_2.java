package com.adventofcode.day7;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.adventofcode.utils.FileUtils;

public class Day7_2 {

    public static void main(String[] args) throws Exception {
        new Day7_2().count();
    }

    public void count() throws Exception {
        // 292: 11 6 16 20
       /* long expected = 292;
        List<Long> values = List.of(11, 6, 16, 20);
        Node node = new Node().calculate(expected, values);
        //System.out.println("calibrated = " + node.calibrated);*/

        List<Calibration> calibrations = loadCalibrations();
        long count = 0;
        for (Calibration calibration : calibrations) {
            //if (calibration.expected != 192) continue;;
            Node node = new Node().calculate(calibration.expected, calibration.values);
            if (node.calibrated) {
                count += node.expected;
            }
        }
        System.out.println("final answer = " + count);
        // answer for tmp 11387
        // answer 37599040735984 is too high
    }

    protected static class Node {

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
            root.sum = sum(root, values);
            root.mul = mul(root, values);
            root.concat = concat(root, values);
            if(root.sum != null && root.sum.calibrated) {
                root.calibrated=true;
            }
            if(root.mul != null && root.mul.calibrated) {
                root.calibrated=true;
            }
            if(root.concat != null && root.concat.calibrated) {
                root.calibrated=true;
            }
            return root;
        }

        Node sum(Node prev, List<Long> values) {
            //System.out.println("SUM prev " + prev);
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
            retVal.concat = concat(retVal, values);
            if (retVal.isAnswer() && isLastIndex(values, retVal)) {
                retVal.calibrated=true;
                //System.out.println("answer SUM = " + retVal.exp + ", calibrated = " + retVal);
            }
            if(retVal.sum != null && retVal.sum.calibrated) {
                prev.calibrated=true;
            }
            if(retVal.mul != null && retVal.mul.calibrated) {
                prev.calibrated=true;
            }
            if(retVal.concat != null && retVal.concat.calibrated) {
                prev.calibrated=true;
            }
            return retVal;
        }

        Node mul(Node prev, List<Long> values) {
            //System.out.println("MUL prev " + prev);
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
            retVal.concat = concat(retVal, values);
            if (retVal.isAnswer() && isLastIndex(values, retVal)) {
                retVal.calibrated=true;
                //System.out.println("answer SUM = " + retVal.exp + ", calibrated = " + retVal);
            }
            if(retVal.sum != null && retVal.sum.calibrated) {
                prev.calibrated=true;
            }
            if(retVal.mul != null && retVal.mul.calibrated) {
                prev.calibrated=true;
            }
            if(retVal.concat != null && retVal.concat.calibrated) {
                prev.calibrated=true;
            }
            return retVal;
        }

        Node concat(Node prev, List<Long> values) {
            //System.out.println("MUL prev " + prev);
            if (prev.index >= values.size() - 1) {
                return null;
            }
            Node retVal = new Node();
            retVal.index = prev.index + 1;
            retVal.expected = prev.expected;
            long prevVal = prev.val;
            long currVal = values.get(retVal.index);
            long val = Long.parseLong("%d%d".formatted(prevVal, currVal));
            //System.out.println("concat = %s, %s, %s".formatted(prevVal, currVal, val));
            retVal.exp = prev.exp + "||" + currVal;
            retVal.val = val;
            retVal.sum = sum(retVal, values);
            retVal.mul = mul(retVal, values);
            retVal.concat = concat(retVal, values);
            if (retVal.isAnswer() && isLastIndex(values, retVal)) {
                retVal.calibrated=true;
                //System.out.println("answer CONCAT = " + retVal.exp + ", calibrated = " + retVal);
            }
            if(retVal.sum != null && retVal.sum.calibrated) {
                prev.calibrated=true;
            }
            if(retVal.mul != null && retVal.mul.calibrated) {
                prev.calibrated=true;
            }
            if(retVal.concat != null && retVal.concat.calibrated) {
                prev.calibrated=true;
            }
            return retVal;
        }

        private boolean isLastIndex(List<Long> values, Node retVal) {
            return retVal.index == values.size() - 1;
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
