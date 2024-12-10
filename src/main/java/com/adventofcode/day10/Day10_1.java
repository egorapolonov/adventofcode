package com.adventofcode.day10;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.adventofcode.utils.FileUtils;

public class Day10_1 {

    protected static final char SPACE = '.';
    protected List<List<Integer>> rows;
    protected static List<Node> trailHeads = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new Day10_1().count();
        // answer 6331212425418 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println(rows);
        System.out.println("answer = unknown so far");
        int answer = sumTrailHeads();
        System.out.println(trailHeads);
        System.out.println("answer = " + answer);
        System.out.println("visited = " + trailHeads.size());
    }

    protected int sumTrailHeads() {
        int counter = 0;
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<Integer> row = rows.get(rowIndex);
            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                if (row.get(colIndex) != null && row.get(colIndex) == 0) {
                    Node node = new Node(rowIndex, colIndex);
                    //System.out.println(node);
                    counter += node.trailheads;
                    System.out.printf("node trailheads : [%dx%d] = %d%n", node.row, node.col, node.trailheads);
                }
            }
        }
        return counter;
    }

    protected class Node {

        int row;
        int col;
        Integer val;
        Integer sum;

        int trailheads;

        Node up;
        Node down;
        Node left;
        Node right;

        @Override
        public String toString() {
            return "Node{" + "row=" + row + ", col=" + col + ", val=" + val + ", sum=" + sum + ", trailheads="
                   + trailheads + ", up=" + up + ", down=" + down + ", left=" + left + ", right=" + right + '}';
        }

        Node(int row, int col) {
            this.row = row;
            this.col = col;
            this.sum = 0;
            this.val = 0;
            this.trailheads = 0;
            up();
            down();
            left();
            right();/*
            countTrailHeads(up);
            countTrailHeads(down);
            countTrailHeads(left);
            countTrailHeads(right);*/
        }

        private void countTrailHeads(Node node) {
            if (node != null) {
                this.trailheads += node.trailheads;
            }
        }

        Node() {
        }

        void up() {
            Node node = new Node();
            node.row = row - 1;
            node.col = col;
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                if (node.val != null && node.val - val == 1) {
                    node.sum = node.val + sum;
                    node.up();
                    node.left();
                    node.right();
                    initNodes(node);
                    this.up = node;
                    this.trailheads += node.trailheads;
                }
            }
        }

        void down() {
            Node node = new Node();
            node.row = row + 1;
            node.col = col;
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                if (node.val != null && node.val - val == 1) {
                    node.sum = node.val + sum;
                    node.down();
                    node.left();
                    node.right();
                    initNodes(node);
                    this.down = node;
                    this.trailheads += node.trailheads;
                }
            }
        }

        void left() {
            Node node = new Node();
            node.row = row;
            node.col = col - 1;
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                if (node.val != null && node.val - val == 1) {
                    node.sum = node.val + sum;
                    node.up();
                    node.down();
                    node.left();
                    initNodes(node);
                    this.left = node;
                    this.trailheads += node.trailheads;
                }
            }
        }

        void right() {
            Node node = new Node();
            node.row = row;
            node.col = col + 1;
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                if (node.val != null && node.val - val == 1) {
                    node.sum = node.val + sum;
                    node.up();
                    node.down();
                    node.right();
                    initNodes(node);
                    this.right = node;
                    this.trailheads += node.trailheads;
                }
            }
        }

        private void initNodes(Node node) {
            if (node.sum == 45) {
                trailHeads.add(node);
                node.trailheads = 1;
                System.out.println("Found : " + node + " at :  " + this);
            }
        }

        private boolean isInRange(Node node) {
            return node.row >= 0 && node.row < rows.size() && node.col >= 0 && node.col < rows.getFirst().size();
        }

    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (List<Integer> row : rows) {
            for (Integer pos : row) {
                if (pos != null) {
                    sb.append(pos);
                } else {
                    sb.append('.');
                }
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day10_1_tmp_2.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day10_1_tmp.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day10_1.txt")))) {
            this.rows = new ArrayList<>();
            String line = null;
            while ((line = br.readLine()) != null) {
                List<Integer> row = new ArrayList<>();
                for (int col = 0; col < line.length(); col++) {
                    char ch = line.charAt(col);
                    if (Character.isDigit(ch)) {
                        Integer height = Integer.parseInt(String.valueOf(ch));
                        row.add(height);
                    } else {
                        row.add(null);
                    }
                }
                rows.add(row);
            }

        }
    }
}
