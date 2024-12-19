package com.adventofcode.day16;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day16_2_GraphApproachIntersection {

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
        new Day16_2_GraphApproachIntersection().count();
        // 668 on big set. The right answer if 670. But works with small examples
    }

    // The idea was to search forward and backward, and then map visited nodes and compare sum of scores to minScore
    protected void count() throws Exception {
        loadMap();
        printCharMap();
        BsfResult forward = bsfMinScore(start, target, false);
        System.out.println("forward = " + forward.score);
        System.out.println("forward endNodes = " + forward.endNodes);
        BsfResult backward = bsfBackward(forward);
        System.out.println("backward = " + backward.score);
        matchMaps(forward, backward);
    }

    private BsfResult bsfBackward(BsfResult forward) {
        BsfResult backward = null;
        for (var direction : forward.endNodes.stream().map(n -> n.value).toList()) {
            target.value = flip(direction);
            System.out.println("flip = " + target.value);
            backward = bsfMinScore(target, start, true);
            if (backward.score.equals(forward.score)) {
                System.out.println("backward direction found : " + direction);
                break;
            }
        }
        return backward;
    }

    private void matchMaps(BsfResult forward, BsfResult backward) {
        long minScore = forward.score;
        Set<Tile> tiles = new LinkedHashSet<>();
        for (var forwarEntry : forward.visited.entrySet()) {
            Node forwardNode = forwarEntry.getKey();
            char tmp = forwardNode.value;
            forwardNode.value = flip(forwardNode.value);
            Node backwarNode = backward.visited.get(forwardNode);
            if (backwarNode != null) {
                if ((forwardNode.score + backwarNode.score == minScore)) {
                    tiles.add(new Tile(forwardNode));
                }
            }
            forwardNode.value = tmp;
        }
        System.out.println("Tiles : " + (tiles.size()));
    }

    char flip(char value) {
        char turn = switch (value) {
            case UP -> DOWN;
            case RIGHT -> LEFT;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            default -> value;
        };
        return turn;
    }

    protected BsfResult bsfMinScore(Node start, Node target, boolean matchDirection) {
        //PriorityQueue<Node> queue = new PriorityQueue<>();
        LinkedList<Node> queue = new LinkedList<>();
        Map<Node, Node> visited = new HashMap<>();
        Set<Character> ends = new HashSet<>();
        Set<Node> endNodes = new HashSet<>();
        List<Long> result = new ArrayList<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.x == target.x && current.y == target.y) {
                if(matchDirection) {
                    if(current.value == target.value) {
                        ends.add(current.value);
                        endNodes.add(current);
                        result.add(current.score);
                    }
                } else {
                    //System.out.println("Found : " + current);
                    ends.add(current.value);
                    endNodes.add(current);
                    result.add(current.score);
                }
            }

            if (visited.containsKey(current)) {
                continue;
            }

            visited.put(current, current);

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
        Long score = result.stream().min(Comparator.comparingLong(Long::longValue)).orElse(null);
        endNodes.removeIf(n -> !score.equals(n.score));
        return new BsfResult(visited, score, ends, endNodes);
    }

    private class BsfResult {

        Map<Node, Node> visited;
        Long score;
        Set<Character> ends;
        Set<Node> endNodes;

        public BsfResult(Map<Node, Node> visited, Long score, Set<Character> ends, Set<Node> endNodes) {
            this.visited = visited;
            this.score = score;
            this.ends = ends;
            this.endNodes = endNodes;
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

        Node clockWise() {
            char clockWiseValue = turnClockWise(value);
            return new Node(y, x, clockWiseValue, this.score + 1000);
        }

        Node counterClockWise() {
            char counterClockWiseValue = turnCounterClockWise(value);
            return new Node(y, x, counterClockWiseValue, this.score + 1000);
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
                            start = node;
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
