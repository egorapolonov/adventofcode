package com.adventofcode.day12;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import com.adventofcode.utils.FileUtils;

public class Day12_1 {

    protected List<List<Character>> rows;
    protected static LinkedHashSet<Node> visited = new LinkedHashSet<>();

    public static void main(String[] args) throws Exception {
        new Day12_1().count();
        // answer 1930 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println(rows);
        System.out.println("answer = unknown so far");
        long answer = countPrice();
        System.out.println("answer = " + answer);
    }

    protected long countPrice() {
        List<Node> nodes = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<Character> row = rows.get(rowIndex);
            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                Node node = creteRootNode(rowIndex, colIndex);
                if (node != null) {
                    System.out.printf("node : [%dx%d] = %d%n", node.row, node.col, node.area);
                    nodes.add(node);
                }
            }
        }
        nodes.forEach(System.out::println);
        long price = 0;
        for (Node node : nodes) {
            System.out.printf("%n[%s]: a=%d, pe=%d%n", node.val, node.area, node.perimeter);
            price += node.area * node.perimeter;
        }
        return price;
    }

    protected Node creteRootNode(int row, int col) {
        Node retVal = new Node(row, col);
        if (!visited.contains(retVal)) {
            visited.add(retVal);
            return retVal;
        }
        return null;
    }

    protected class Node {

        int row;
        int col;
        char val;
        long area;
        long perimeter;

        Node up;
        Node down;
        Node left;
        Node right;

        LinkedHashSet<Node> waypoints;

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
            return "Node{" + "row=" + row + ", col=" + col + ", val=" + val + ", area=" + area + ", perimeter="
                   + perimeter + ", up=" + up + ", down=" + down + ", left=" + left + ", right=" + right + '}';
        }

        Node(int row, int col, LinkedHashSet<Node> waypoints) {
            this.row = row;
            this.col = col;
            this.area = 1;
            this.perimeter = 4;
            this.waypoints = waypoints;
        }

        Node(int row, int col) {
            this.row = row;
            this.col = col;
            this.area = 1;
            this.perimeter = 4;
            this.val = rows.get(this.row).get(this.col);
            this.waypoints = new LinkedHashSet<>();
            this.waypoints.add(this);
            up();
            down();
            left();
            right();
            maintainPerimeter();
        }

        void up() {
            this.up = createNodeIfValid(row - 1, col, waypoints);
        }

        void down() {
            this.down = createNodeIfValid(row + 1, col, waypoints);
        }

        void left() {
            this.left = createNodeIfValid(row, col - 1, waypoints);
        }

        void right() {
            this.right = createNodeIfValid(row, col + 1, waypoints);
        }

        private Node createNodeIfValid(int row, int col, LinkedHashSet<Node> waypoints) {
            Node node = creteNewNode(row, col, waypoints);
            Node validNode = null;
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                validNode = getIfValid(node);
            }
            return validNode;
        }

        protected Node creteNewNode(int row, int col, LinkedHashSet<Node> visited) {
            return new Node(row, col, visited);
        }

        Node getIfValid(Node node) {
            //if (node.val != null && node.val.equals(val)) {
            if (isValid(node)) {
                initNodes(node);
                this.area += node.area;
                this.perimeter+=node.perimeter;
                /*node.perimeter--;
                this.perimeter--;
                this.perimeter+=node.perimeter;*/
                //this.perimeter+=node.perimeter;
            }
            return node;
        }

        private boolean isValid(Node node) {
            return node.val == val;
        }

        void initNodes(Node node) {
          /*  System.out.printf("%nnext to [%dx%d]=%s%n ---> [%dx%d]=%s%n", this.row, this.col, this.val, node.row,
                    node.col, node.val);*/
            waypoints.add(node);
            visited.add(node);
            node.up();
            node.down();
            node.left();
            node.right();
            //maintainPerimeter(node);
            //this.perimeter+=node.perimeter;
            node.maintainPerimeter();
            /*maintainPerimeter(node);
            this.perimeter=node.perimeter;*/
        }

        boolean isInRange(Node node) {
            return node.row >= 0 && node.row < rows.size() && node.col >= 0 && node.col < rows.getFirst().size()
                   && !waypoints.contains(node);
        }

        private void maintainPerimeter() {
            Node up = creteNewNode(this.row + 1, this.col, waypoints);
            if (isSimplyInRange(up)) {
                up.val = rows.get(up.row).get(up.col);
                if(isValid(up)) {
                this.perimeter--;
            }
                }
            Node down = creteNewNode(this.row - 1, this.col, waypoints);
            if (isSimplyInRange(down)) {
                down.val = rows.get(down.row).get(down.col);
                if (isValid(down)) {
                    this.perimeter--;
                }
            }
            Node left = creteNewNode(this.row , this.col-1, waypoints);
            if (isSimplyInRange(left)) {
                left.val = rows.get(left.row).get(left.col);
                if (isValid(left)) {
                    this.perimeter--;
                }
            }
            Node right = creteNewNode(this.row , this.col+1, waypoints);
            if (isSimplyInRange(right)) {
                right.val = rows.get(right.row).get(right.col);
                if (isValid(right)) {
                    this.perimeter--;
                }
            }
            //System.out.println("Perimeter of [%dx%d] = %d".formatted(this.row, this.col, this.perimeter));
        }

        private void maintainPerimeter(Node node) {
            Node up = creteNewNode(node.row + 1, node.col, waypoints);
            if (isSimplyInRange(up)) {
                up.val = rows.get(up.row).get(up.col);
                if(isValid(up)) {
                    node.perimeter--;
                }
            }
            Node down = creteNewNode(node.row - 1, node.col, waypoints);
            if (isSimplyInRange(down)) {
                down.val = rows.get(down.row).get(down.col);
                if (isValid(down)) {
                    node.perimeter--;
                }
            }
            Node left = creteNewNode(node.row , node.col-1, waypoints);
            if (isSimplyInRange(left)) {
                left.val = rows.get(left.row).get(left.col);
                if (isValid(left)) {
                    node.perimeter--;
                }
            }
            Node right = creteNewNode(node.row , node.col+1, waypoints);
            if (isSimplyInRange(right)) {
                right.val = rows.get(right.row).get(right.col);
                if (isValid(right)) {
                    node.perimeter--;
                }
            }
            //System.out.println("Perimeter of [%dx%d] = %d".formatted(node.row, node.col, node.perimeter));
        }

        boolean isSimplyInRange(Node node) {
            return node.row >= 0 && node.row < rows.size() && node.col >= 0 && node.col < rows.getFirst().size();
        }

    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (List<Character> row : rows) {
            for (Character pos : row) {
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
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_1.tmp.txt")))) {

                //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_2.tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_4.tmp.txt")))) {
            this.rows = new ArrayList<>();
            String line = null;
            while ((line = br.readLine()) != null) {
                List<Character> row = new ArrayList<>();
                for (int col = 0; col < line.length(); col++) {
                    char ch = line.charAt(col);
                    row.add(ch);
                }
                rows.add(row);
            }

        }
    }
}
