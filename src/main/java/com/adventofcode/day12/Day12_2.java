package com.adventofcode.day12;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day12_2 {

    protected List<List<Character>> rows;
    protected static LinkedHashSet<Node> visited = new LinkedHashSet<>();

    public static void main(String[] args) throws Exception {
        new Day12_2().count();
        // answer 694 is correct
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
       /* for (Node node : nodes) {
            price += node.area * node.perimeter;
        }*/
        for (Node node : nodes) {
            long sides = countSides(node);
            long plantsPrice = node.area * sides;
            System.out.printf("%n[%s]: u=%d, d=%d, l=%d, r=%d, ,a=%d, s=%d, p=%d%n", node.val, node.upSides.size(),
                    node.downSides.size(), node.leftSides.size(), node.rightSides.size(), node.area, sides,
                    plantsPrice);
            price += plantsPrice;
        }
        return price;
    }

    private static long countSides(Node node) {
        return countSides(node.upSides)
               + countSides(node.downSides)
               + countSides(node.leftSides)
               + countSides(node.rightSides);
    }

    private static long countSides(Map<Integer, Set<Integer>> sides) {
        long retVal = 0;
        if(sides != null && sides.size() > 0) {
            for (var dimension : sides.entrySet()) {
                retVal += dimension.getValue().size();
            }
        }
        return retVal;
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
        long sides;

        Node up;
        Node down;
        Node left;
        Node right;

        Set<Node> waypoints;
        Map<Integer, Set<Integer>> upSides; //<ROW, COL>
        Map<Integer, Set<Integer>> downSides; //<ROW, CO>
        Map<Integer, Set<Integer>> leftSides; // <COL,ROW>
        Map<Integer, Set<Integer>> rightSides; // <COL,ROW>

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
            return "Node{" + "row=" + row + ", col=" + col + ", val=" + val + ", area=" + area + ", upSides=" + upSides
                   + ", downSides=" + downSides + ", leftSides=" + leftSides + ", rightSides=" + rightSides + '}';
        }

        Node(int row, int col, Set<Node> waypoints) {
            this.row = row;
            this.col = col;
            this.area = 1;
            this.perimeter = 4;
            this.sides = 4;
            this.waypoints = waypoints;
        }

        Node(int row, int col) {
            this.row = row;
            this.col = col;
            this.area = 1;
            this.perimeter = 4;
            this.sides = 4;
            this.val = rows.get(this.row).get(this.col);
            this.waypoints = new LinkedHashSet<>();
            this.waypoints.add(this);
            this.upSides = new LinkedHashMap<>();
            this.downSides = new LinkedHashMap<>();
            this.leftSides = new LinkedHashMap<>();
            this.rightSides = new LinkedHashMap<>();
            up();
            down();
            left();
            right();
            maintainNodeNeighborhood();
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

        private Node createNodeIfValid(int row, int col, Set<Node> waypoints) {
            Node node = creteNewNode(row, col, waypoints);
            Node validNode = null;
            if (isInRange(node)) {
                node.val = rows.get(node.row).get(node.col);
                validNode = getIfValid(node);
            }
            return validNode;
        }

        protected Node creteNewNode(int row, int col, Set<Node> visited) {
            Node retVal = new Node(row, col, visited);
            retVal.upSides = this.upSides;
            retVal.downSides = this.downSides;
            retVal.leftSides = this.leftSides;
            retVal.rightSides = this.rightSides;
            return retVal;
        }

        Node getIfValid(Node node) {
            //if (node.val != null && node.val.equals(val)) {
            if (isValid(node)) {
                initNodes(node);
                this.area += node.area;
                this.perimeter += node.perimeter;
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
            node.maintainNodeNeighborhood();
            /*maintainPerimeter(node);
            this.perimeter=node.perimeter;*/
        }

        boolean isInRange(Node node) {
            return node.row >= 0 && node.row < rows.size() && node.col >= 0 && node.col < rows.getFirst().size()
                   && !waypoints.contains(node);
        }

        private void maintainNodeNeighborhood() {
            Node up = creteNewNode(this.row + 1, this.col, waypoints);
            boolean missingUp = true;
            if (isSimplyInRange(up)) {
                up.val = rows.get(up.row).get(up.col);
                if (isValid(up)) {
                    this.perimeter--;
                    missingUp = false;
                } else {
                    //upSides.add(this.row);
                }
            } else {
                //upSides.add(this.row);
            }
            Node down = creteNewNode(this.row - 1, this.col, waypoints);
            boolean missingDown = true;
            if (isSimplyInRange(down)) {
                down.val = rows.get(down.row).get(down.col);
                if (isValid(down)) {
                    this.perimeter--;
                    missingDown = false;
                } else {
                    //downSides.add(this.row);
                }
            } else {
                //downSides.add(this.row);
            }
            Node left = creteNewNode(this.row, this.col - 1, waypoints);
            boolean missingLeft = true;
            if (isSimplyInRange(left)) {
                left.val = rows.get(left.row).get(left.col);
                if (isValid(left)) {
                    this.perimeter--;
                    missingLeft = false;
                } else {
                    //leftSides.add(this.col);
                }
            } else {
                //leftSides.add(this.col);
            }
            Node right = creteNewNode(this.row, this.col + 1, waypoints);
            boolean missingRight = true;
            if (isSimplyInRange(right)) {
                right.val = rows.get(right.row).get(right.col);
                if (isValid(right)) {
                    this.perimeter--;
                    missingRight = false;
                } else {
                    //rightSides.add(this.col);
                }
            } else {
                //rightSides.add(this.col);
            }
            if (missingUp) {
                if (!upSides.containsKey(this.row)) {
                    Set<Integer> cols = new HashSet<>();
                    cols.add(this.col);
                    upSides.put(this.row, cols);
                } else if (missingLeft || missingRight) {
                        if(missingLeft && missingRight) {
                            Set<Integer> cols = upSides.get(this.row);
                            cols.add(this.col);
                            upSides.put(this.row, cols);
                        } else if (missingLeft) {
                            Set<Integer> cols = upSides.get(this.row);
                            if(!cols.contains(right.col)) {
                                cols.add(this.col);
                                upSides.put(this.row, cols);
                            }
                        } else if(missingRight) {
                            Set<Integer> cols = upSides.get(this.row);
                            if(!cols.contains(left.col)) {
                                cols.add(this.col);
                                upSides.put(this.row, cols);
                            }
                        }
                    }
                }
            if (missingDown) {
                if (!downSides.containsKey(this.row)) {
                    Set<Integer> cols = new HashSet<>();
                    cols.add(this.col);
                    downSides.put(this.row, cols);
                } else {
                    if (missingLeft || missingRight) {
                        Set<Integer> cols = downSides.get(this.row);
                        cols.add(this.col);
                        downSides.put(this.row, cols);
                    }
                }
            }
            if (missingLeft) {
                if (!leftSides.containsKey(this.col)) {
                    Set<Integer> rows = new HashSet<>();
                    rows.add(this.row);
                    leftSides.put(this.col, rows);
                } else {
                    if (missingUp || missingDown) {
                        Set<Integer> rows = leftSides.get(this.col);
                        rows.add(this.row);
                        leftSides.put(this.col, rows);
                    }
                }
            }
            if (missingRight) {
                if(this.row == 2 && this.col == 4) {
                    System.out.println("Here");
                }
                if (!rightSides.containsKey(this.col)) {
                    Set<Integer> rows = new HashSet<>();
                    rows.add(this.row);
                    rightSides.put(this.col, rows);
                } else {
                    if (missingUp || missingDown) {
                        Set<Integer> rows = rightSides.get(this.col);
                        rows.add(this.row);
                        rightSides.put(this.col, rows);
                    }
                }
            }
            System.out.println(
                    "Sides of [%dx%d] : mu=%s,md=%s,ml=%s,mr=%s".formatted(this.row, this.col, missingUp, missingDown, missingLeft, missingRight));
            //System.out.println("Perimeter of [%dx%d] = %d".formatted(this.row, this.col, this.perimeter));
            //System.out.println("Sides of [%dx%d] = %d".formatted(this.row, this.col, this.sides));
            System.out.println(
                    "Sides of [%dx%d] : u=%d,d=%d,l=%d,r=%d".formatted(this.row, this.col, this.upSides.size(),
                            this.downSides.size(), this.leftSides.size(), this.rightSides.size()));
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
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_3.tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_4.tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_5.tmp.txt")))) {
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
