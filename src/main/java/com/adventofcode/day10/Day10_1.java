package com.adventofcode.day10;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import com.adventofcode.utils.FileUtils;

public class Day10_1 {

    protected static final char SPACE = '.';
    protected List<List<Integer>> rows;
    protected static List<Node> trailHeads = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new Day10_1().count();
        // answer 694 is correct
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
                    Node node = creteRootNode(rowIndex, colIndex);
                    //System.out.println(node);
                    counter += node.trailheads;
                    System.out.printf("node trailheads : [%dx%d] = %d%n", node.row, node.col, node.trailheads);
                }
            }
        }
        return counter;
    }

    protected Node creteRootNode(int row, int col) {
        return new Node(row, col);
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

        LinkedHashSet<Node> visited;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Node node = (Node) o;
            return row == node.row && col == node.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public String toString() {
            return "Node{" + "row=" + row + ", col=" + col + ", val=" + val + ", sum=" + sum + ", trailheads="
                   + trailheads + ", up=" + up + ", down=" + down + ", left=" + left + ", right=" + right + '}';
        }

        Node(int row, int col, LinkedHashSet<Node> visited) {
            this.row = row;
            this.col = col;
            this.visited = visited;
        }

        Node(int row, int col) {
            this.row = row;
            this.col = col;
            this.sum = 0;
            this.val = 0;
            this.trailheads = 0;
            this.visited = new LinkedHashSet<>();
            up();
            down();
            left();
            right();
        }

        void up() {
            Node node = creteNewNode(row - 1, col, visited);
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                this.up = getIfValid(node);
            }
        }

        protected Node creteNewNode(int row, int col) {
            return new Node(row, col);
        }

        protected Node creteNewNode(int row, int col, LinkedHashSet<Node> visited) {
            return new Node(row, col, visited);
        }

        void down() {
            Node node = creteNewNode(row + 1, col, visited);
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                this.down = getIfValid(node);
            }
        }

        void left() {
            Node node = creteNewNode(row, col - 1, visited);
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                this.left = getIfValid(node);
            }
        }

        void right() {
            Node node = creteNewNode(row, col + 1, visited);
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                this.right = getIfValid(node);
            }
        }

        Node getIfValid(Node node) {
            if (node.val != null && node.val - val == 1) {
                node.sum = node.val + sum;
                initNodes(node);
                this.trailheads += node.trailheads;
            }
            return node;
        }

        void initNodes(Node node) {
            node.up();
            node.down();
            node.left();
            node.right();
            this.visited.add(node);
            if (node.sum == 45) {
                trailHeads.add(node);
                node.trailheads = 1;
                System.out.println("Found : " + node);
            }
        }

        boolean isInRange(Node node) {
            return node.row >= 0 && node.row < rows.size() && node.col >= 0 && node.col < rows.getFirst().size()
                   && !visited.contains(node);
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
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day10_1_tmp_13.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day10_1_tmp_2.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day10_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day10_1.txt")))) {
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
