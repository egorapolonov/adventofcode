package com.adventofcode.day16;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day16_1_3_GraphApproach {

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
    protected Node target;
    protected int MAP_WIDTH;
    protected int MAP_HEIGHT;
    protected Map<Integer, Map<Integer, Character>> charMap;
    protected Map<Character, List<Integer>> deltas = Map.of(UP, List.of(-1, 0), DOWN, List.of(1, 0), LEFT,
            List.of(0, -1), RIGHT, List.of(0, 1));

    public static void main(String[] args) throws Exception {
        new Day16_1_3_GraphApproach().count();
        // 148628 - is not correct
        // 147628 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printCharMap();
        System.out.println("answer = " + bfsMinScore(cursor, target));
    }

    protected long bfsMinScore(Node start, Node target) {
        //PriorityQueue<Node> queue = new PriorityQueue<>();
        LinkedList<Node> queue = new LinkedList<>();
        LinkedHashSet<Node> visited = new LinkedHashSet<>();
        List<Long> result = new ArrayList<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.x == target.x && current.y == target.y) {
                System.out.println("Found : " + current);
                result.add(current.score);
            }

            if (visited.contains(current)) {
                continue;
            }

            visited.add(current);

            Node forward = current.forward();
            if (forward != null) {
                // queue.add(forward); | This approach you can use if you operate with PriorityQueue<Node> queue = new PriorityQueue<>();
                queue.addFirst(forward); // this element will have the samllest score
            }
            Node clockWise = current.clockWise();
            if (clockWise != null) {
                queue.add(clockWise);
            }
            Node counterClockWise = current.counterClockWise();
            if (counterClockWise != null) {
                queue.add(counterClockWise);
            }

        }
        return result.stream().mapToLong(Long::longValue).min().orElseThrow();
    }

    // doesn't work well
    public static List<LinkedHashSet<Node>> findAllPaths(Node start, Node target) {
        List<LinkedHashSet<Node>> allPaths = new ArrayList<>(); // To store all possible paths
        Queue<LinkedHashSet<Node>> queue = new LinkedList<>();  // BFS Queue to track paths

        // Initialize BFS with the start node
        LinkedHashSet<Node> initialPath = new LinkedHashSet<>();
        initialPath.add(start);
        queue.add(initialPath);

        while (!queue.isEmpty()) {
            // Get the current path from the queue
            LinkedHashSet<Node> currentPath = queue.poll();
            Node lastNode = currentPath.getLast();

            // If we reached the target node, add this path to the results
            if (lastNode.equals(target)) {
                allPaths.add(new LinkedHashSet<>(currentPath));
                continue;
            }

            // Explore the neighbors of the last node
            for (Node neighbor : lastNode.nodes) {
                if (!currentPath.contains(neighbor)) { // Avoid cycles
                    LinkedHashSet<Node> newPath = new LinkedHashSet<>(currentPath);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
        }

        return allPaths;
    }

    protected Optional<Node> searchBFS(char value, Node start) {
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(start);
        Node currentNode;
        LinkedHashSet<Node> alreadyVisited = new LinkedHashSet<>();
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
        printPath(alreadyVisited);
        //return Optional.empty();
        return retVal.size() > 0 ? retVal.get(0) : Optional.empty();
    }

    protected class Node implements Comparable<Node> {

        int y;
        int x;
        char value;
        boolean marked;
        long score;

        List<Node> nodes;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Node node = (Node) o;
            return y == node.y && x == node.x && value == node.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(y, x, value);
        }

        @Override
        public int compareTo(Node other) {
            return Long.compare(this.score, other.score); // for PriorityQueue
        }

        @Override
        public String toString() {
            String nodesStr = nodes != null && nodes.size() > 0 ? ", nodes = " + Optional.ofNullable(nodes)
                    .stream()
                    .flatMap(List::stream)
                    .map(n -> "%dx%d".formatted(n.y, n.x))
                    .toList() : "";
            return "Node{" + "y=" + y + ", x=" + x + ",value=" + value + ", score=" + score + nodesStr + '}';
        }

        public Node(Node node, long score, char value) {
            this.y = node.y;
            this.x = node.x;
            this.value = value;
            this.score = score;
            this.nodes = node.nodes;
        }

        public Node(int y, int x, char value) {
            this.y = y;
            this.x = x;
            this.value = value;
            this.nodes = new ArrayList<>();
        }

        public Node(int y, int x, char value, long score) {
            this.y = y;
            this.x = x;
            this.value = value;
            this.score = score;
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

        boolean thereIsTheWay(Node node) {
            return node.y >= 0 && node.y <= MAP_HEIGHT && node.x >= 0 && node.x <= MAP_WIDTH && (
                    charMap.get(node.y).get(node.x) != OBSTACLE);
        }

        void linkUp() {
            Node node = creteNewNode(y - 1, x, value);
            if (isSimplyInRange(node)) {
                if (map.get(node.y) != null && (node = map.get(node.y).get(node.x)) != null) {
                    if (node.nodes == null) {
                        node.nodes = new ArrayList<>();
                    }
                    node.nodes.add(this);
                    if (this.nodes == null) {
                        this.nodes = new ArrayList<>();
                    }
                    this.nodes.add(node);
                }
            }
        }

        void linkDown() {
            Node node = creteNewNode(y + 1, x, value);
            if (isSimplyInRange(node)) {
                if (map.get(node.y) != null && (node = map.get(node.y).get(node.x)) != null) {
                    if (node.nodes == null) {
                        node.nodes = new ArrayList<>();
                    }
                    node.nodes.add(this);
                    if (this.nodes == null) {
                        this.nodes = new ArrayList<>();
                    }
                    this.nodes.add(node);
                }
            }
        }

        void linkLeft() {
            Node node = creteNewNode(y, x - 1, value);
            if (isSimplyInRange(node)) {
                if (map.get(node.y) != null && (node = map.get(node.y).get(node.x)) != null) {
                    if (node.nodes == null) {
                        node.nodes = new ArrayList<>();
                    }
                    node.nodes.add(this);
                    if (this.nodes == null) {
                        this.nodes = new ArrayList<>();
                    }
                    this.nodes.add(node);
                }
            }
        }

        void linkRight() {
            Node node = creteNewNode(y, x + 1, value);
            if (isSimplyInRange(node)) {
                if (map.get(node.y) != null && (node = map.get(node.y).get(node.x)) != null) {
                    if (node.nodes == null) {
                        node.nodes = new ArrayList<>();
                    }
                    node.nodes.add(this);
                    if (this.nodes == null) {
                        this.nodes = new ArrayList<>();
                    }
                    this.nodes.add(node);
                }
            }
        }

        protected Node creteNewNode(int y, int x, char value) {
            return new Node(y, x, value);
        }

        Node forward() {
            List<Integer> dydx = deltas.get(value);
            Node node = new Node(y + dydx.get(0), x + dydx.get(1), value, this.score + 1);
            if (thereIsTheWay(node)) {
                return node;
            }
            return null;
        }

        Node forward(char value) {
            return switch (this.value) {
                case UP -> nodes.stream()
                        .filter(n -> n.y == y - 1 && n.x == x)
                        .findFirst()
                        .map(n -> new Node(n, this.score + 1, value))
                        .orElse(null);
                case DOWN -> nodes.stream()
                        .filter(n -> n.y == y + 1 && n.x == x)
                        .findFirst()
                        .map(n -> new Node(n, this.score + 1, value))
                        .orElse(null);
                case RIGHT -> nodes.stream()
                        .filter(n -> n.y == y && n.x == x + 1)
                        .findFirst()
                        .map(n -> new Node(n, this.score + 1, value))
                        .orElse(null);
                case LEFT -> nodes.stream()
                        .filter(n -> n.y == y && n.x == x - 1)
                        .findFirst()
                        .map(n -> new Node(n, this.score + 1, value))
                        .orElse(null);
                default -> null;
            };
        }

        Node clockWise() {
            char clockWiseValue = turnClockWise(value);
            return new Node(y, x, clockWiseValue, this.score + 1000);
        }

        Node clockWise(char value) {
            char clockWiseValue = turnClockWise(value);
            Node clockWise = new Node(this, this.score + 1000, clockWiseValue);
            return clockWise;
        }

        Node counterClockWise() {
            char counterClockWiseValue = turnCounterClockWise(value);
            return new Node(y, x, counterClockWiseValue, this.score + 1000);
        }

        Node counterClockWise(char value) {
            char counterClockWiseValue = turnCounterClockWise(value);
            Node counterClockWise = new Node(this, this.score + 1000, counterClockWiseValue);
            return counterClockWise;
        }

        Node flip() {
            char flip = turnCounterClockWise(turnCounterClockWise(value));
            return new Node(y, x, flip, this.score + 2000);
        }

        Node flip(char value) {
            char flipValue = turnCounterClockWise(turnCounterClockWise(value));
            Node flipNode = new Node(this, this.score + 2000, flipValue);
            return flipNode;
        }

        char turnClockWise(char value) {
            char turn = switch (value) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
                default -> value;
            };
            return turn;
        }

        char turnCounterClockWise(char value) {
            char turn = switch (value) {
                case UP -> LEFT;
                case LEFT -> DOWN;
                case DOWN -> RIGHT;
                case RIGHT -> UP;
                default -> value;
            };
            return turn;
        }

    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (var entry : map.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    private void printPath(Collection<Node> nodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("HERE WE GO : ");
        for (Node node : nodes) {
            sb.append(node);
            sb.append("--->");
        }
        System.out.println(sb);
    }

    protected void printCharMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                sb.append(charMap.get(y).get(x));
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.tmp.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.tmp_1.txt")))) {
            this.map = new HashMap<>();
            this.charMap = new HashMap<>();
            String line = null;
            int y = 0;
            while ((line = br.readLine()) != null) {
                this.MAP_HEIGHT = y + 1;
                map.computeIfAbsent(y, HashMap::new);
                charMap.computeIfAbsent(y, HashMap::new);
                Map<Integer, Node> xRow = map.get(y);
                Map<Integer, Character> xCharRow = charMap.get(y);
                this.MAP_WIDTH = line.length();
                for (int x = 0; x < line.length(); x++) {
                    char ch = line.charAt(x);
                    if ((ch == DOT) || (ch == S) || (ch == E)) {
                        Node node = new Node(y, x, ch);
                        node.linkUp();
                        node.linkDown();
                        node.linkLeft();
                        node.linkRight();
                        if ('S' == ch) {
                            node.value = RIGHT;
                            cursor = node;
                        }
                        if ('E' == ch) {
                            target = node;
                        }
                        xRow.put(x, node);
                    }
                    xCharRow.put(x, ch);
                }
                y++;
            }
        }
    }
}
