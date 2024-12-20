package com.adventofcode.day16;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.adventofcode.utils.FileUtils;

public class Day16_2_GraphBackTrack {

    protected static final char UP = '^';
    protected static final char RIGHT = '>';
    protected static final char DOWN = 'V';
    protected static final char LEFT = '<';
    protected static final Set<Character> DIRECTIONS = Set.of(UP, RIGHT, DOWN, LEFT);
    protected static final char OBSTACLE = '#';
    protected static final char DOT = '.';
    protected static final char S = 'S';
    protected static final char E = 'E';
    protected Map<Integer, Map<Integer, Node>> map;
    protected Node start;
    protected Node target;
    protected int MAP_WIDTH;
    protected int MAP_HEIGHT;
    protected Map<Integer, Map<Integer, Character>> charMap;
    protected Map<Character, List<Integer>> deltas = Map.of(UP, List.of(-1, 0), DOWN, List.of(1, 0), LEFT,
            List.of(0, -1), RIGHT, List.of(0, 1), DOT, List.of(0, 0), E, List.of(0, 0));

    public static void main(String[] args) throws Exception {
        new Day16_2_GraphBackTrack().count();
        // 668 on big set. The right answer if 670. But works with small examples
    }

    // The idea was to search forward and backward, and then map visited nodes and compare sum of scores to minScore
    protected void count() throws Exception {
        loadMap();
        printCharMap();
        BfsResult forward = bfsMinScore(new Node(start), new Node(target));
        System.out.println("score = " + forward.score);
        System.out.println("tiles = " + forward.tiles.size());
    }

    protected BfsResult bfsMinScore(Node start, Node target) {
        LinkedList<Node> queue = new LinkedList<>();
        Map<Node, Node> visited = new HashMap<>();
        Set<Node> tileNodes = new HashSet<>();
        Node minScoreNode = null;
        queue.add(start);
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (current.x == target.x && current.y == target.y) {
                if (visited.get(target) == null || visited.get(target).score > current.score) {
                    visited.put(target, current);
                    tileNodes.clear();
                    tileNodes.add(current);
                    minScoreNode = current;
                }
                if (current.score == visited.get(target).score) {
                    tileNodes.addAll(current.visited.keySet());
                    tileNodes.add(current);
                }
            }

            if (visited.containsKey(current) && visited.get(current).score < current.score) {
                continue;
            }

            visited.put(current, current);
            current.visited.put(current, current.score);

            Node forward = current.forward();
            if (forward != null) {
                // queue.add(forward); | This approach you can use if you operate with PriorityQueue<Node> queue = new PriorityQueue<>();
                queue.addFirst(forward); // this element will have the smallest score
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
        BfsResult retVal = new BfsResult(minScoreNode.score, tileNodes.stream().map(Tile::new).collect(Collectors.toSet()));
        return retVal;
    }

    private class BfsResult {

        Long score;
        Set<Tile> tiles;

        public BfsResult(Long score, Set<Tile> tiles) {
            this.score = score;
            this.tiles = tiles;
        }
    }

    protected class Tile {

        int y;
        int x;

        Tile(Node node) {
            this.y = node.y;
            this.x = node.x;
        }

        public Tile(int y, int x) {
            this.y = y;
            this.x = x;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Tile tile = (Tile) o;
            return y == tile.y && x == tile.x;
        }

        @Override
        public int hashCode() {
            return Objects.hash(y, x);
        }
    }

    protected class Node implements Comparable<Node> {

        int y;
        int x;
        char value;
        boolean marked;
        long score;

        List<Node> nodes;
        Map<Node, Long> visited;

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

        // just clone
        public Node(Node node) {
            this.y = node.y;
            this.x = node.x;
            this.value = node.value;
            this.nodes = new ArrayList<>();
            this.visited = new HashMap<>();
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

        public Node(int y, int x, char value, long score, Map<Node, Long> visited) {
            this.y = y;
            this.x = x;
            this.value = value;
            this.score = score;
            this.visited = new HashMap<>(visited);
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

        protected Node creteNewNode(int y, int x, char value) {
            return new Node(y, x, value);
        }

        Node forward() {
            List<Integer> dydx = deltas.get(value);
            Node node = new Node(y + dydx.get(0), x + dydx.get(1), value, this.score + 1, visited);
            if (thereIsTheWay(node)) {
                return node;
            }
            return null;
        }

        Node clockWise() {
            char clockWiseValue = turnClockWise(value);
            return new Node(y, x, clockWiseValue, this.score + 1000, visited);
        }

        Node counterClockWise() {
            char counterClockWiseValue = turnCounterClockWise(value);
            return new Node(y, x, counterClockWiseValue, this.score + 1000, visited);
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

    protected void printTilesMap(Set<Tile> tiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = new Tile(y, x);
                if (tiles.contains(tile)) {
                    sb.append("O");
                } else {
                    sb.append(charMap.get(y).get(x));
                }
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    protected void printVisited(Set<Node> visited) {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Tile tile = new Tile(y, x);
                int finalY = y;
                int finalX = x;
                if (visited.stream().anyMatch(v -> v.y == finalY && v.x == finalX)) {
                    sb.append("O");
                } else {
                    sb.append(charMap.get(y).get(x));
                }
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
                        if ('S' == ch) {
                            node.value = RIGHT;
                            start = new Node(node);
                        }
                        if ('E' == ch) {
                            target = new Node(node);
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
