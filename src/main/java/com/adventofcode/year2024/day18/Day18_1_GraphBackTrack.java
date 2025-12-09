package com.adventofcode.year2024.day18;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.adventofcode.utils.FileUtils;

public class Day18_1_GraphBackTrack {

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
    protected int MAP_X;
    protected int MAP_Y;
    protected Map<Integer, Map<Integer, Character>> charMap;
    protected Map<Character, List<Integer>> deltas = Map.of(UP, List.of(-1, 0), DOWN, List.of(1, 0), LEFT,
            List.of(0, -1), RIGHT, List.of(0, 1), DOT, List.of(0, 0), E, List.of(0, 0));

    public static void main(String[] args) throws Exception {
        new Day18_1_GraphBackTrack().count();
        // 668 on big set. The right answer if 670. But works with small examples
    }

    // The idea was to search forward and backward, and then map visited nodes and compare sum of scores to minScore
    protected void count() throws Exception {
        loadMap();
        printCharMap();
        BfsResult forward = bfsMinScore(new Node(start), new Node(target));
        System.out.println("score = " + forward.score);
        System.out.println("tiles = " + forward.tiles.size());
       /* int iterations = 12;
        MAP_X = 7;
        MAP_Y = 7;
        loadAndCount(iterations);*/
        /*int iterations = 1024;
        MAP_X = 71;
        MAP_Y = 71;
        loadAndCount(iterations);*/
        // 140 is too low
    }

    protected BfsResult bfsMinScore(Node start, Node target) {
        LinkedList<Node> queue = new LinkedList<>();
        //Map<Node, Node> visited = new HashMap<>();
        visited.clear();
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
                /*if (current.score == visited.get(target).score) {
                    tileNodes.addAll(current.visited.keySet());
                    tileNodes.add(current);
                }*/
            }

            if (visited.containsKey(current) && visited.get(current).score < current.score) {
                continue;
            }

            visited.put(current, current);
            //current.visited.put(current, current.score);

            Node up = current.up();
            if (up != null) {
                // queue.add(forward); | This approach you can use if you operate with PriorityQueue<Node> queue = new PriorityQueue<>();
                queue.add(up); // this element will have the smallest score
            }
            Node down = current.down();
            if (down != null) {
                queue.addFirst(down);
            }
            Node left = current.left();
            if (left != null) {
                queue.add(left);
            }
            Node right = current.right();
            if (right != null) {
                queue.addFirst(right);
            }
        }
        System.out.println(visited);
        printVisited(visited.keySet());
        BfsResult retVal = new BfsResult(minScoreNode.score,
                tileNodes.stream().map(Tile::new).collect(Collectors.toSet()));
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

        LinkedHashSet<Node> nodes;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Node node = (Node) o;
            return y == node.y && x == node.x;// && value == node.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(y, x);//, value);
        }

        @Override
        public int compareTo(Node other) {
            return Long.compare(this.score, other.score); // for PriorityQueue
        }

        @Override
        public String toString() {
            String nodesStr = nodes != null && nodes.size() > 0 ? ", nodes = " + Optional.ofNullable(nodes)
                    .stream()
                    .flatMap(Set::stream)
                    .map(n -> "%dx%d".formatted(n.y, n.x))
                    .toList() : "";
            return "Node{" + "y=" + y + ", x=" + x + ",value=" + value + ", score=" + score + nodesStr + '}';
        }

        public Node(Node node) {
            this.y = node.y;
            this.x = node.x;
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
            this.nodes = new LinkedHashSet<>();
        }

        public Node(int y, int x, char value, long score, LinkedHashSet<Node> nodes) {
            this.y = y;
            this.x = x;
            this.value = value;
            this.score = score;
            this.nodes = nodes;
        }

        public LinkedHashSet<Node> getNodes() {
            return nodes;
        }

        public void setNodes(LinkedHashSet<Node> nodes) {
            this.nodes = nodes;
        }

        boolean isSimplyInRange(Node node) {
            return node.y >= 0 && node.y < MAP_Y && node.x >= 0 && node.x < MAP_X;
        }

        boolean thereIsTheWay(Node node) {
            return node.y >= 0 && node.y < MAP_Y && node.x >= 0 && node.x < MAP_X && (charMap.get(node.y).get(node.x)
                                                                                      != OBSTACLE
            && !visited.containsKey(node));
        }

        protected Node creteNewNode(int y, int x, char value) {
            return new Node(y, x, value);
        }

        Node up() {
            List<Integer> dydx = deltas.get(UP);
            Node node = new Node(y + dydx.get(0), x + dydx.get(1), value, this.score + 1, nodes);
            if (thereIsTheWay(node)) {
                return node;
            }
            return null;
        }

        Node down() {
            List<Integer> dydx = deltas.get(DOWN);
            Node node = new Node(y + dydx.get(0), x + dydx.get(1), value, this.score + 1, nodes);
            if (thereIsTheWay(node)) {
                return node;
            }
            //System.out.println("Down : " + node);
            return null;
        }

        Node left() {
            List<Integer> dydx = deltas.get(LEFT);
            Node node = new Node(y + dydx.get(0), x + dydx.get(1), value, this.score + 1, nodes);
            if (thereIsTheWay(node)) {
                return node;
            }
            return null;
        }

        Node right() {
            List<Integer> dydx = deltas.get(RIGHT);
            Node node = new Node(y + dydx.get(0), x + dydx.get(1), value, this.score + 1, nodes);
            if (thereIsTheWay(node)) {
                return node;
            }
            return null;
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
        for (int y = 0; y < MAP_Y; y++) {
            for (int x = 0; x < MAP_X; x++) {
                sb.append(charMap.get(y).get(x));
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    protected void printTilesMap(Set<Tile> tiles) {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (int y = 0; y < MAP_Y; y++) {
            for (int x = 0; x < MAP_X; x++) {
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
        for (int y = 0; y < MAP_Y; y++) {
            for (int x = 0; x < MAP_X; x++) {
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
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day18_1_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day18_1.txt")))) {
            this.map = new HashMap<>();
            this.MAP_X = 71;
            this.MAP_Y = 71;
            this.charMap = new HashMap<>();
            String line = null;
            for (int x = 0; x < MAP_X; x++) {
                for (int y = 0; y < MAP_Y; y++) {
                    charMap.computeIfAbsent(y, HashMap::new);
                    Map<Integer, Character> xCharRow = charMap.get(y);
                    xCharRow.put(x, DOT);
                }
            }
            int maxIterations = 1024;
            int iteration = 0;
            while ((line = br.readLine()) != null && iteration < maxIterations) {
                String[] xy = line.split(",");
                int x = Integer.parseInt(xy[0]);
                int y = Integer.parseInt(xy[1]);
                charMap.get(y).put(x, OBSTACLE);
                iteration++;
            }
            this.start = new Node(0, 0, DOT);
            this.target = new Node(70, 70, DOT);
        }
    }
    public static Map<Node, Node> visited = new HashMap<>();
    protected void loadAndCount(int iterations) throws Exception {
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day18_1_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day18_1.txt")))) {
            this.map = new HashMap<>();
            this.charMap = new HashMap<>();
            String line = null;
            for (int x = 0; x < MAP_X; x++) {
                for (int y = 0; y < MAP_Y; y++) {
                    charMap.computeIfAbsent(y, HashMap::new);
                    Map<Integer, Character> xCharRow = charMap.get(y);
                    xCharRow.put(x, DOT);
                }
            }
            this.start = new Node(0, 0, DOT);
            this.target = new Node(MAP_Y - 1, MAP_X - 1, DOT);

            LinkedList<Node> queue = new LinkedList<>();
            visited.clear();
            Set<Node> tileNodes = new HashSet<>();
            Long minScore = null;
            queue.add(start);
            int iteration = 0;
            while (!queue.isEmpty()) {
                if(queue.size() % 1000 == 0) {
                    System.out.println("queue.size() = " + queue.size());
                }
                if ((line = br.readLine()) != null && iteration < iterations) {
                    String[] xy = line.split(",");
                    int x = Integer.parseInt(xy[0]);
                    int y = Integer.parseInt(xy[1]);
                    charMap.get(y).put(x, OBSTACLE);
                    iteration++;
                }

                Node current = queue.poll();
                if (current.x == target.x && current.y == target.y) {
                    System.out.println("Found : " + current.score);
                    if (visited.get(target) == null || visited.get(target).score > current.score) {
                        visited.put(target, current);
                        minScore = current.score;
                    }
                /*if (current.score == visited.get(target).score) {
                    tileNodes.addAll(current.visited.keySet());
                    tileNodes.add(current);
                }*/
                }

                if (charMap.get(current.y).get(current.x) == OBSTACLE
                    || visited.containsKey(current) && visited.get(current).score < current.score) {
                    continue;
                }

                visited.put(current, current);
                //current.visited.put(current, current.score);

                Node right = current.right();
                if (right != null) {
                    queue.addFirst(right);
                }
                Node down = current.down();
                if (down != null) {
                    queue.addFirst(down);
                }
                Node up = current.up();
                if (up != null) {
                    // queue.add(forward); | This approach you can use if you operate with PriorityQueue<Node> queue = new PriorityQueue<>();
                    //queue.add(up); // this element will have the smallest score
                    queue.add(up); // this element will have the smallest score
                }
                Node left = current.left();
                if (left != null) {
                    queue.add(left);
                }
            }
            //System.out.println(visited);
            printVisited(visited.keySet());
            System.out.println("answer = " + minScore);

            /*while ((line = br.readLine()) != null) {
                String[] xy = line.split(",");
                int x = Integer.parseInt(xy[0]);
                int y = Integer.parseInt(xy[1]);
                charMap.get(y).put(x, OBSTACLE);
            }*/
        }
    }
}
