package com.adventofcode.day16;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day16_1_3_graph {

    protected static final char UP = '^';
    protected static final char RIGHT = '>';
    protected static final char DOWN = 'V';
    protected static final char LEFT = '<';
    protected static final Set<Character> DIRECTION = Set.of(UP, RIGHT, DOWN, LEFT);
    protected static final char OBSTACLE = '#';
    protected static final char DOT = '.';
    protected static final char S = 'S';
    protected static final char E = 'E';
    protected Map<Integer, Map<Integer, Node>> map;
    protected Node cursor;
    protected int MAP_WIDTH;
    protected int MAP_HEIGHT;

    public static void main(String[] args) throws Exception {
        new Day16_1_3_graph().count();
        // answer 364280 is too high
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println("answer = " + searchBSF('E', cursor).get());
    }

    protected Optional<Node> searchBSF(char value, Node start) {
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(start);
        Node currentNode;
        Set<Node> alreadyVisited = new HashSet<>();
        List<Optional<Node>> retVal = new ArrayList<>();
        while (!queue.isEmpty()) {
            currentNode = queue.remove();
            System.out.println("Visited node with value: %s".formatted(currentNode));
            if (currentNode.value == value) {
                System.out.println("queue size : " + queue.size());
                retVal.add(Optional.of(currentNode));
            } else {
                alreadyVisited.add(currentNode);
                queue.addAll(currentNode.nodes);
                queue.removeAll(alreadyVisited);
            }
        }
        System.out.println("variant : " + retVal.size());
        //return Optional.empty();
        return retVal.size()> 0 ? retVal.get(0) : Optional.empty();
    }

    protected class Node {

        int y;
        int x;
        char value;
        boolean marked;

        List<Node> nodes;

        @Override
        public String toString() {
            String nodesStr = nodes.size() > 0 ? ", nodes = " + Optional.ofNullable(nodes)
                    .stream()
                    .flatMap(List::stream)
                    .map(n -> "%dx%d:%d".formatted(n.y,n.x, n.nodes.size())).toList() : "";
            return "Node{" + "y=" + y + ", x=" + x + nodesStr + '}';
        }

        public Node(int y, int x, char value) {
            this.y = y;
            this.x = x;
            this.value = value;
            this.nodes = new ArrayList<>();
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        boolean isSimplyInRange(Node node) {
            return node.y >= 0 && node.y < MAP_HEIGHT && node.x >= 0 && node.x < MAP_WIDTH;
        }

        void linkUp() {
            Node node = creteNewNode(y - 1, x, value);
            if (isSimplyInRange(node)) {
                if(map.get(node.y) != null && (node = map.get(node.y).get(node.x)) != null) {
                    if(node.nodes == null) {
                        node.nodes = new ArrayList<>();
                    }
                    node.nodes.add(this);
                    if(this.nodes == null) {
                        this.nodes = new ArrayList<>();
                    }
                    this.nodes.add(node);
                }
            }
        }

        void linkDown() {
            Node node = creteNewNode(y + 1, x, value);
            if (isSimplyInRange(node)) {
                if(map.get(node.y) != null && (node = map.get(node.y).get(node.x)) != null) {
                    if(node.nodes == null) {
                        node.nodes = new ArrayList<>();
                    }
                    node.nodes.add(this);
                    if(this.nodes == null) {
                        this.nodes = new ArrayList<>();
                    }
                    this.nodes.add(node);
                }
            }
        }

        void linkLeft() {
            Node node = creteNewNode(y, x-1, value);
            if (isSimplyInRange(node)) {
                if(map.get(node.y) != null && (node = map.get(node.y).get(node.x)) != null) {
                    if(node.nodes == null) {
                        node.nodes = new ArrayList<>();
                    }
                    node.nodes.add(this);
                    if(this.nodes == null) {
                        this.nodes = new ArrayList<>();
                    }
                    this.nodes.add(node);
                }
            }
        }

        void linkRight() {
            Node node = creteNewNode(y, x+1, value);
            if (isSimplyInRange(node)) {
                if(map.get(node.y) != null && (node = map.get(node.y).get(node.x)) != null) {
                    if(node.nodes == null) {
                        node.nodes = new ArrayList<>();
                    }
                    node.nodes.add(this);
                    if(this.nodes == null) {
                        this.nodes = new ArrayList<>();
                    }
                    this.nodes.add(node);
                }
            }
        }

        protected Node creteNewNode(int y, int x, char value) {
            return new Node(y, x, value);
        }
        /*
        void linkDown() {
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
        }*/

    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for(var entry : map.entrySet()) {
            System.out.println(entry.getValue());
        }
        /*for(int y = 0;y<MAP_HEIGHT;y++) {
            for(int x =0;x<MAP_WIDTH;x++) {
            }
        }*/
        /*for (List<Character> row : map) {
            for (Character pos : row) {
                if (pos != null) {
                    sb.append(pos);
                } else {
                    sb.append('.');
                }
            }
            System.out.println(sb);
            sb.setLength(0);
        }*/
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.tmp_1.txt")))) {
            this.map = new HashMap<>();
            String line = null;
            int y = 0;
            while ((line = br.readLine()) != null) {
                this.MAP_HEIGHT = y+1;
                map.computeIfAbsent(y, HashMap::new);
                Map<Integer, Node> xRow = map.get(y);
                this.MAP_WIDTH = line.length();
                for (int x = 0; x < line.length(); x++) {
                    char ch = line.charAt(x);
                    if((ch == DOT) || (ch == S ) || ( ch == E)) {
                        Node node = new Node(y, x, ch);
                        node.linkUp();
                        node.linkDown();
                        node.linkLeft();
                        node.linkRight();
                        if ('S' == ch) {
                            cursor = node;
                        }
                        xRow.put(x, node);
                    }
                }
                y++;
            }
        }
    }
}
