package com.adventofcode.day12;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day12_2_1 {

    protected List<List<Character>> rows;
    protected static LinkedHashSet<Node> visited = new LinkedHashSet<>();

    public static void main(String[] args) throws Exception {
        new Day12_2_1().count();
        // answer 805986 is too low
        // answer 841078 is
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
                    System.out.printf("node [%s]: [%dx%d] : a=%d, ans=%d%n", node.val, node.row, node.col, node.area, node.answer);
                    nodes.add(node);
                }
            }
        }
        nodes.forEach(System.out::println);
        long price = 0;
        for (Node node : nodes) {
            System.out.printf("%n[%s]: a=%d, ans=%d%n", node.val, node.area, node.answer);
            price += node.area * node.answer;
        }
        return price;
    }

    protected Node creteRootNode(int row, int col) {
        Node retVal = new Node();
        retVal.row=row;
        retVal.col = col;
        if (!visited.contains(retVal)) {
            retVal = new Node(row, col);
            visited.add(retVal);
            return retVal;
        }
        return null;
    }

    protected class SideKey {

        int row;
        int col;
        boolean visited;

        public SideKey(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            SideKey sideKey = (SideKey) o;
            return row == sideKey.row && col == sideKey.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public String toString() {
            return "SideKey{" + "row=" + row + ", col=" + col + '}';
        }
    }

    protected class Node {

        int row;
        int col;
        char val;
        long area;
        long perimeter;
        long side;
        long answer;

        Node up;
        Node down;
        Node left;
        Node right;

        LinkedHashSet<SideKey> ups;
        LinkedHashSet<SideKey> downs;
        LinkedHashSet<SideKey> lefts;
        LinkedHashSet<SideKey> rights;
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
                   + perimeter + ", side=" + side + '}';
        }

        Node(){}

        Node(int row, int col, LinkedHashSet<Node> waypoints) {
            this.row = row;
            this.col = col;
            this.area = 1;
            this.perimeter = 4;
            this.side = 0;
            this.waypoints = waypoints;
        }

        Node(int row, int col) {
            this.row = row;
            this.col = col;
            this.area = 1;
            this.perimeter = 4;
            this.side = 4;
            this.answer = 0;
            this.val = rows.get(this.row).get(this.col);
            this.waypoints = new LinkedHashSet<>();
            this.waypoints.add(this);
            this.ups = new LinkedHashSet<>();
            this.downs = new LinkedHashSet<>();
            this.lefts = new LinkedHashSet<>();
            this.rights = new LinkedHashSet<>();
            up();
            down();
            left();
            right();
            maintainPerimeter();
            calculateUps();
            calculateDowns();
            calculateLefts();
            calculateRights();
            //calculateUpSides();
            //calculateDownSides();
            //calculateLeftSides();
            //calculateRightSides();
        }

        long calculateUps() {
            Map<Integer, Set<SideKey>> groupsByRow = new HashMap<>();
            for (var side : ups) {
                groupsByRow.computeIfAbsent(side.row, HashSet::new);
                for(var side1 : ups) {
                    if(side.row == side1.row) {
                        groupsByRow.get(side.row).add(side1);
                    }
                }
            }
            List<Set<SideKey>> cols = new ArrayList<>();

            for(var group : groupsByRow.entrySet()) {
                List<SideKey> keys = new ArrayList<>(group.getValue());
                Collections.sort(keys, Comparator.comparingInt(SideKey::getCol));
                for(SideKey sk : keys) {
                    if(!sk.visited) {
                        System.out.println("sk : " + sk);
                        Set<SideKey> gr = new HashSet<>();
                        sk.visited= true;
                        gr.add(sk);
                        int prevCol = sk.col;
                        for (SideKey sk1 : keys) {
                            if (!sk1.visited) {
                                System.out.println("[%dx%d] vs [%dx%d]".formatted(prevCol, sk.col, sk1.row, sk1.col));
                                if (sk1.col == prevCol + 1) {
                                    sk1.visited = true;
                                    gr.add(sk1);
                                    prevCol = sk1.col;
                                }
                            }
                        }
                        if (!gr.isEmpty()) {
                            System.out.println("gr = " + gr);
                            cols.add(gr);
                        }
                    }
                }
                for(SideKey sk : keys) {
                    if(!sk.visited) {
                        sk.visited = true;
                        cols.add(Set.of(sk));
                    }
                }
            }

            /*for(var group : groupsByRow.entrySet()) {
                for(SideKey key : group.getValue()) {
                    if(!key.visited) {
                        Set<SideKey> coco = new HashSet<>();
                        //coco.add(key);
                        int increment = 1;
                        boolean found = false;
                        for (SideKey key1 : group.getValue()) {
                            if(!key1.visited) {
                                if (key1.col == key.col || key1.col == key.col - increment || key1.col == key.col + increment) {
                                    coco.add(key1);
                                    key1.visited = true;
                                    found = true;
                                }
                            }
                            *//*if (found) {
                                increment++;
                                found = false;
                            }*//*
                        }
                        //key.visited=true;
                        cols.add(coco);
                    }
                }
            }*/
            System.out.println("calculateUps = " + groupsByRow);
            System.out.println("calculateUps cosl = " + cols);
            this.answer+=cols.size();
            return cols.size();
        }

        long calculateLefts() {
            Map<Integer, Set<SideKey>> groupsByRow = new HashMap<>();
            for (var side : lefts) {
                groupsByRow.computeIfAbsent(side.col, HashSet::new);
                for(var side1 : lefts) {
                    if(side.col == side1.col) {
                        groupsByRow.get(side.col).add(side1);
                    }
                }
            }
            List<Set<SideKey>> cols = new ArrayList<>();
            /*for(var group : groupsByRow.entrySet()) {
                for(SideKey key : group.getValue()) {
                    if(!key.visited) {
                        Set<SideKey> coco = new HashSet<>();
                        //coco.add(key);
                        int increment = 1;
                        boolean found = false;
                        for (SideKey key1 : group.getValue()) {
                            if(!key1.visited) {
                                if (key1.row == key.row || key1.row == key.row - increment || key1.row == key.row + increment) {
                                    coco.add(key1);
                                    key1.visited = true;
                                    found = true;
                                }
                            }
                            *//*if (found) {
                                increment++;
                                found = false;
                            }*//*
                        }
                        //key.visited=true;
                        cols.add(coco);
                    }
                }
            }*/
            for(var group : groupsByRow.entrySet()) {
                List<SideKey> keys = new ArrayList<>(group.getValue());
                Collections.sort(keys, Comparator.comparingInt(SideKey::getRow));
                for(SideKey sk : keys) {
                    if(!sk.visited) {
                        System.out.println("sk : " + sk);
                        Set<SideKey> gr = new HashSet<>();
                        sk.visited= true;
                        gr.add(sk);
                        int prevRow = sk.row;
                        for (SideKey sk1 : keys) {
                            if (!sk1.visited) {
                                System.out.println("[%dx%d] vs [%dx%d]".formatted(prevRow, sk.col, sk1.row, sk1.col));
                                if (sk1.row == prevRow + 1) {
                                    sk1.visited = true;
                                    gr.add(sk1);
                                    prevRow = sk1.row;
                                }
                            }
                        }
                        if (!gr.isEmpty()) {
                            System.out.println("gr = " + gr);
                            cols.add(gr);
                        }
                    }
                }
                for(SideKey sk : keys) {
                    if(!sk.visited) {
                        sk.visited = true;
                        cols.add(Set.of(sk));
                    }
                }
            }
            System.out.println("calculateLefts = " + groupsByRow);
            System.out.println("calculateLefts cols = " + cols);
            this.answer+=cols.size();
            return cols.size();
        }

        long calculateRights() {
            Map<Integer, Set<SideKey>> groupsByRow = new HashMap<>();
            for (var side : rights) {
                groupsByRow.computeIfAbsent(side.col, HashSet::new);
                for(var side1 : rights) {
                    if(side.col == side1.col) {
                        groupsByRow.get(side.col).add(side1);
                    }
                }
            }
            List<Set<SideKey>> cols = new ArrayList<>();
            for(var group : groupsByRow.entrySet()) {
                List<SideKey> keys = new ArrayList<>(group.getValue());
                Collections.sort(keys, Comparator.comparingInt(SideKey::getRow));
                for(SideKey sk : keys) {
                    if(!sk.visited) {
                        System.out.println("sk : " + sk);
                        Set<SideKey> gr = new HashSet<>();
                        sk.visited= true;
                        gr.add(sk);
                        int prevRow = sk.row;
                        for (SideKey sk1 : keys) {
                            if (!sk1.visited) {
                                System.out.println("[%dx%d] vs [%dx%d]".formatted(prevRow, sk.col, sk1.row, sk1.col));
                                if (sk1.row == prevRow + 1) {
                                    sk1.visited = true;
                                    gr.add(sk1);
                                    prevRow = sk1.row;
                                }
                            }
                        }
                        if (!gr.isEmpty()) {
                            System.out.println("gr = " + gr);
                            cols.add(gr);
                        }
                    }
                }
                for(SideKey sk : keys) {
                    if(!sk.visited) {
                        sk.visited = true;
                        cols.add(Set.of(sk));
                    }
                }
            }
            /*for(var group : groupsByRow.entrySet()) {
                for(SideKey key : group.getValue()) {
                    if(!key.visited) {
                        Set<SideKey> coco = new HashSet<>();
                        //coco.add(key);
                        int increment = 1;
                        boolean found = false;
                        for (SideKey key1 : group.getValue()) {
                            if(!key1.visited) {
                                if (key1.row == key.row || key1.row == key.row - increment || key1.row == key.row + increment) {
                                    coco.add(key1);
                                    key1.visited = true;
                                    found = true;
                                }
                            }
                            *//*if (found) {
                                increment++;
                                found = false;
                            }*//*
                        }
                        //key.visited=true;
                        cols.add(coco);
                    }
                }
            }*/
            System.out.println("calculateRights = " + groupsByRow);
            System.out.println("calculateRights cols = " + cols);
            this.answer+=cols.size();
            return cols.size();
        }

        long calculateDowns() {
            Map<Integer, Set<SideKey>> groupsByRow = new HashMap<>();
            for (var side : downs) {
                groupsByRow.computeIfAbsent(side.row, HashSet::new);
                for(var side1 : downs) {
                    if(side.row == side1.row) {
                        groupsByRow.get(side.row).add(side1);
                    }
                }
            }
            List<Set<SideKey>> cols = new ArrayList<>();
            for(var group : groupsByRow.entrySet()) {
                List<SideKey> keys = new ArrayList<>(group.getValue());
                Collections.sort(keys, Comparator.comparingInt(SideKey::getCol));
                for(SideKey sk : keys) {
                    if(!sk.visited) {
                        System.out.println("sk : " + sk);
                        Set<SideKey> gr = new HashSet<>();
                        sk.visited= true;
                        gr.add(sk);
                        int prevCol = sk.col;
                        for (SideKey sk1 : keys) {
                            if (!sk1.visited) {
                                System.out.println("[%dx%d] vs [%dx%d]".formatted(prevCol, sk.col, sk1.row, sk1.col));
                                if (sk1.col == prevCol + 1) {
                                    sk1.visited = true;
                                    gr.add(sk1);
                                    prevCol = sk1.col;
                                }
                            }
                        }
                        if (!gr.isEmpty()) {
                            System.out.println("gr = " + gr);
                            cols.add(gr);
                        }
                    }
                }
                for(SideKey sk : keys) {
                    if(!sk.visited) {
                        sk.visited = true;
                        cols.add(Set.of(sk));
                    }
                }
            }
            /*for(var group : groupsByRow.entrySet()) {
                for(SideKey key : group.getValue()) {
                    if(!key.visited) {
                        Set<SideKey> coco = new HashSet<>();
                        coco.add(key);
                        int increment = 1;
                        boolean found = false;
                        for (SideKey key1 : group.getValue()) {
                            if(!key1.visited) {
                                if (key1.col == key.col || key1.col == key.col - increment || key1.col == key.col + increment) {
                                    coco.add(key1);
                                    key1.visited = true;
                                    found = true;
                                }
                            }
                            *//*if (found) {
                                increment++;
                                found = false;
                            }*//*
                        }
                        //key.visited=true;
                        cols.add(coco);
                    }
                }
            }*/
            System.out.println("calculateDowns = " + groupsByRow);
            System.out.println("calculateDowns cols = " + cols);
            this.answer+=cols.size();
            return cols.size();
        }

        void calculateUpSides() {
            Map<Integer, Set<SideKey>> rows = new HashMap<>();
            Set<SideKey> singles = new HashSet<>();
            for (var side : ups) {
                //if (!side.visited) {
                    side.visited = true;
                    int leftCol = side.col - 1;
                    boolean single = true;
                    SideKey leftKey = new SideKey(side.row, leftCol);
                    if (ups.contains(leftKey)) {
                        leftKey.visited = true;
                        ups.add(leftKey);
                        rows.computeIfAbsent(side.row, HashSet::new);
                        rows.get(side.row).add(leftKey);
                        rows.get(side.row).add(side);
                        single = false;
                    }
                    int rightCol = side.col + 1;
                    SideKey rightKey = new SideKey(side.row, rightCol);
                    if (ups.contains(rightKey)) {
                        rightKey.visited = true;
                        ups.add(rightKey);
                        rows.computeIfAbsent(side.row, HashSet::new);
                        rows.get(side.row).add(rightKey);
                        rows.get(side.row).add(side);
                        single = false;
                    }
                    if(single) {
                       singles.add(side);
                    }
                //}
            }
            this.answer+=rows.size()+singles.size();
            System.out.println("up answer = " + (rows.size()+singles.size()));
            System.out.println("ups = " + rows + ", singles = " + singles);
            System.out.println("grouped ups = %s, total = %d".formatted(rows, rows.size()));
        }

        void calculateDownSides() {
            Map<Integer, Set<SideKey>> rows = new HashMap<>();
            Set<SideKey> singles = new HashSet<>();
            for (var side : downs) {
               // if (!side.visited) {
                    side.visited = true;
                    int leftCol = side.col - 1;
                    SideKey leftKey = new SideKey(side.row, leftCol);
                     boolean single = true;
                    if (downs.contains(leftKey)) {
                        leftKey.visited = true;
                        downs.add(leftKey);
                        rows.computeIfAbsent(side.row, HashSet::new);
                        rows.get(side.row).add(leftKey);
                        rows.get(side.row).add(side);
                        single = false;
                    }
                    int rightCol = side.col + 1;
                    SideKey rightKey = new SideKey(side.row, rightCol);
                    if (downs.contains(rightKey)) {
                        rightKey.visited = true;
                        downs.add(rightKey);
                        rows.computeIfAbsent(side.row, HashSet::new);
                        rows.get(side.row).add(rightKey);
                        rows.get(side.row).add(side);
                        single = false;
                    }
                    if(single) {
                        singles.add(side);
                    }
                }
            //}
            this.answer+=rows.size()+singles.size();
            System.out.println("down answer = " + (rows.size()+singles.size()));
            System.out.println("down = " + rows + ", singles = " + singles);
            System.out.println("grouped lefts = %s, total = %d".formatted(rows, rows.size()));
        }

        void calculateLeftSides() {
            Map<Integer, Set<SideKey>> rows = new HashMap<>();
            Set<SideKey> singles = new HashSet<>();
            for (var side : lefts) {
                //  if (!side.visited) {
                side.visited = true;
                int upRow = side.row - 1;
                SideKey upKey = new SideKey(upRow, side.col);
                boolean single = true;
                if (lefts.contains(upKey)) {
                    System.out.println("contains up : %s, %s".formatted(side, upKey));
                    upKey.visited = true;
                    lefts.add(upKey);
                    rows.computeIfAbsent(side.col, HashSet::new);
                    rows.get(side.col).add(upKey);
                    rows.get(side.col).add(side);
                    single = false;
                }
                int downRow = side.row + 1;
                SideKey downKey = new SideKey(downRow, side.col);
                if (lefts.contains(downKey)) {
                    System.out.println("contains down : %s, %s".formatted(side, downKey));
                    downKey.visited = true;
                    lefts.add(downKey);
                    rows.computeIfAbsent(side.col, HashSet::new);
                    rows.get(side.col).add(downKey);
                    rows.get(side.col).add(side);
                    single = false;
                }
                if(single) {
                    singles.add(side);
                }
                //   }
            }
            this.answer+=rows.size()+singles.size();
            System.out.println("left answer = " + (rows.size()+singles.size()));
            System.out.println("lefts = " + rows + ", singles = " + singles);
            System.out.println("grouped lefts = %s, total = %d".formatted(rows, rows.size()));
        }

        void calculateRightSides() {
            List<Map<Integer, Set<SideKey>>> groups = new ArrayList<>();
            Map<Integer, Set<SideKey>> rows = new HashMap<>();
            Set<SideKey> singles = new HashSet<>();
            for(var side : rights) {
                Map<Integer, Set<SideKey>> group = new HashMap<>();
                int groupId = 0;
                for(int row = side.row + 1; row <= 140;row++) {
                    group.computeIfAbsent(groupId, HashSet::new);
                    SideKey key = new SideKey(row, side.col);
                    if(rights.contains(key)) {
                        group.get(groupId).add(key);
                    }
                }
                for(int row = 0; row < side.row;row++) {
                    group.computeIfAbsent(groupId, HashSet::new);
                    SideKey key = new SideKey(row, side.col);
                    if(rights.contains(key)) {
                        group.get(groupId).add(key);
                    }
                }
                groups.add(group);
            }
            System.out.println("GROUPS : " + groups);
            for (var side : rights) {
              //  if (!side.visited) {
                    side.visited = true;
                    int upRow = side.row - 1;
                    SideKey upKey = new SideKey(upRow, side.col);
                    boolean single = true;
                    if (rights.contains(upKey)) {
                        System.out.println("contains up : %s, %s".formatted(side, upKey));
                        upKey.visited = true;
                        rights.add(upKey);
                        rows.computeIfAbsent(side.col, HashSet::new);
                        rows.get(side.col).add(upKey);
                        rows.get(side.col).add(side);
                        single = false;
                    }
                    int downRow = side.row + 1;
                    SideKey downKey = new SideKey(downRow, side.col);
                    if (rights.contains(downKey)) {
                        System.out.println("contains down : %s, %s".formatted(side, downKey));
                        downKey.visited = true;
                        rights.add(downKey);
                        rows.computeIfAbsent(side.col, HashSet::new);
                        rows.get(side.col).add(downKey);
                        rows.get(side.col).add(side);
                        single = false;
                    }
                    if(single) {
                        singles.add(side);
                    }
             //   }
            }
            this.answer+=rows.size()+singles.size();
            System.out.println("right answer = " + (rows.size()+singles.size()));
            System.out.println("rights = " + rows + ", singles = " + singles);
            System.out.println("grouped rights = %s, total = %d".formatted(rows, rows.size()));
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
                node.ups = this.ups;
                node.downs = this.downs;
                node.lefts = this.lefts;
                node.rights = this.rights;
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
            node.maintainPerimeter();
            /*maintainPerimeter(node);
            this.perimeter=node.perimeter;*/
        }

        boolean isInRange(Node node) {
            return node.row >= 0 && node.row < rows.size() && node.col >= 0 && node.col < rows.getFirst().size()
                   && !waypoints.contains(node);
        }

        private void maintainPerimeter() {
            Node up = creteNewNode(this.row - 1, this.col, waypoints);
            if (isSimplyInRange(up)) {
                up.val = rows.get(up.row).get(up.col);
                if (isValid(up)) {
                    this.perimeter--;
                } else {
                    ups.add(new SideKey(this.row, this.col));
                }
            } else {
                ups.add(new SideKey(this.row, this.col));
            }
            Node down = creteNewNode(this.row + 1, this.col, waypoints);
            if (isSimplyInRange(down)) {
                down.val = rows.get(down.row).get(down.col);
                if (isValid(down)) {
                    this.perimeter--;
                } else {
                    downs.add(new SideKey(this.row, this.col));
                }
            } else {
                downs.add(new SideKey(this.row, this.col));
            }
            Node left = creteNewNode(this.row, this.col - 1, waypoints);
            if (isSimplyInRange(left)) {
                left.val = rows.get(left.row).get(left.col);
                if (isValid(left)) {
                    this.perimeter--;
                } else {
                    lefts.add(new SideKey(this.row, this.col));
                }
            } else {
                lefts.add(new SideKey(this.row, this.col));
            }
            Node right = creteNewNode(this.row, this.col + 1, waypoints);
            if (isSimplyInRange(right)) {
                right.val = rows.get(right.row).get(right.col);
                if (isValid(right)) {
                    this.perimeter--;
                } else {
                    rights.add(new SideKey(this.row, this.col));
                }
            } else {
                rights.add(new SideKey(this.row, this.col));
            }
            /*System.out.println("UPS of [%s] [%dx%d] = %s".formatted(this.val, this.row, this.col, this.ups));
            System.out.println("DOWNS of [%s] [%dx%d] = %s".formatted(this.val, this.row, this.col, this.downs));
            System.out.println("LEFTS of [%s] [%dx%d] = %s".formatted(this.val, this.row, this.col, this.lefts));
            System.out.println("RIGTHS of [%s] [%dx%d] = %s".formatted(this.val, this.row, this.col, this.rights));*/
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
                new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_1.tmp.txt")))) {

                //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_2.tmp.txt")))) {
               // new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_3.tmp.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_4.tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day12_1_5.tmp.txt")))) {
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
