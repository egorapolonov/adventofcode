package com.adventofcode.day16;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day16_1_BackwardScoreApproach {

    protected static final char UP = '^';
    protected static final char RIGHT = '>';
    protected static final char DOWN = 'V';
    protected static final char LEFT = '<';
    protected static final Set<Character> DIRECTION = Set.of(UP, RIGHT, DOWN, LEFT);
    protected static final char OBSTACLE = '#';
    protected List<List<Character>> map;
    protected static LinkedHashSet<Node> visited = new LinkedHashSet<>();
    protected static List<Node> scoreboard = new ArrayList<>();
    protected Map<Node, Long> scoreCache;
    protected Node cursor;

    public static void main(String[] args) throws Exception {
        new Day16_1_BackwardScoreApproach().count();
        // fails by StackOverflow on full set
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println(map);
        System.out.println("answer = unknown so far");
        Node node = creteRootNode(cursor.y, cursor.x);
        System.out.println(
                "answer = " + scoreboard.stream().min(Comparator.comparingLong(Node::getScore)).orElse(null));
    }

    protected long countMinScore() {
        List<Node> nodes = new ArrayList<>();
        for (int y = 0; y < map.size(); y++) {
            List<Character> xRow = map.get(y);
            for (int x = 0; x < xRow.size(); x++) {
                Node node = creteRootNode(y, x);
                if (node != null) {
                    System.out.printf("node : [%dx%d] = %d%n", node.x, node.y, node.score);
                    nodes.add(node);
                }
            }
        }
        nodes.forEach(System.out::println);
        long score = nodes.get(0).score;
        for (Node node : nodes) {
            System.out.printf("%n[%s]: s=%d%n", node.direction, node.score);
        }
        return score;
    }

    protected Node creteRootNode(int y, int x) {
        Node retVal = new Node(y, x);
        if (!visited.contains(retVal)) {
            visited.add(retVal);
            return retVal;
        }
        return null;
    }

    protected class Node {

        int y;
        int x;
        char direction;
        long score;
        long backwardScore;
        boolean found;

        public long getScore() {
            return score;
        }

        public void setScore(long score) {
            this.score = score;
        }

        Node forward;
        Node clockWise;
        Node counterClockWise;

        LinkedHashSet<Node> waypoints;

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Node node = (Node) o;
            return y == node.y && x == node.x && direction == node.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(y, x, direction);
        }

        @Override
        public String toString() {
            return "Node{" + "y=" + y + ", x=" + x + ", direction=" + direction + ", score=" + score + '}';
        }

        Node nodeKey(int y, int x, char direction) {
            Node retVal = new Node();
            retVal.y = y;
            retVal.x = x;
            retVal.direction = direction;
            return retVal;
        }

        Node() {
        }

        Node(int y, int x, long score, char direction, LinkedHashSet<Node> waypoints) {
            this.y = y;
            this.x = x;
            this.direction = direction;
            this.waypoints = new LinkedHashSet<>(waypoints);
            this.score = score;
        }

        Node(int y, int x) {
            this.y = y;
            this.x = x;
            this.direction = map.get(this.y).get(this.x);
            this.waypoints = new LinkedHashSet<>();
            initNodes(this);
            //maintainPerimeter();
        }

        void forward() {
            this.forward = switch (direction) {
                case UP -> createNodeIfValid(y - 1, x, score + 1, UP, waypoints);
                case RIGHT -> createNodeIfValid(y, x + 1, score + 1, RIGHT, waypoints);
                case DOWN -> createNodeIfValid(y + 1, x, score + 1, DOWN, waypoints);
                case LEFT -> createNodeIfValid(y, x - 1, score + 1, LEFT, waypoints);
                default -> null;
            };
            if (this.forward != null && this.forward.found) {
                this.backwardScore+= forward.backwardScore+1;
                scoreCache.put(this.forward, this.forward.backwardScore);
                System.out.println("scoreCache forward : " + scoreCache);
                this.found = true;
            }
        }

        void clockWise() {
            Node node = clockWiseForwardNode();
            Node validNode = null;
            if (node != null && isInRange(node)) {
                node.direction = direction;
                validNode = getIfValid(node);
            }
            this.clockWise = validNode;
            if (this.clockWise != null && this.clockWise.found) {
                this.backwardScore+= clockWise.backwardScore+1000;
                scoreCache.put(this.clockWise, this.clockWise.backwardScore);
                System.out.println("scoreCache clockWise : " + scoreCache);
                this.found = true;
            }
        }

        void counterClockWise() {
            Node node = counterClockWiseForwardNode();
            Node validNode = null;
            if (node != null && isInRange(node)) {
                node.direction = direction;
                validNode = getIfValid(node);
            }
            this.counterClockWise = validNode;
            if (this.counterClockWise != null && this.counterClockWise.found) {
                this.counterClockWise.backwardScore+=1000;
                this.backwardScore+= counterClockWise.backwardScore;
                scoreCache.put(this.counterClockWise, this.counterClockWise.backwardScore);
                System.out.println("scoreCache counterClockWise : " + scoreCache);
                this.found = true;
            }
        }

        Node clockWiseForwardNode() {
            return switch (direction) {
                case UP -> createNodeIfValid(y, x + 1, score + 1001, RIGHT, waypoints);
                case RIGHT -> createNodeIfValid(y + 1, x, score + 1001, DOWN, waypoints);
                case DOWN -> createNodeIfValid(y, x - 1, score + 1001, LEFT, waypoints);
                case LEFT -> createNodeIfValid(y - 1, x, score + 1001, UP, waypoints);
                default -> null;
            };
        }

        Node counterClockWiseForwardNode() {
            return switch (direction) {
                case UP -> createNodeIfValid(y, x - 1, score + 1001, LEFT, waypoints);
                case RIGHT -> createNodeIfValid(y - 1, x, score + 1001, UP, waypoints);
                case DOWN -> createNodeIfValid(y, x + 1, score + 1001, RIGHT, waypoints);
                case LEFT -> createNodeIfValid(y + 1, x, score + 1001, DOWN, waypoints);
                default -> null;
            };
        }

        private Node createNodeIfValid(int y, int x, long score, char direction, LinkedHashSet<Node> waypoints) {
            Node node = creteNewNode(y, x, score, direction, waypoints);
            Node validNode = null;
            if (isInRange(node)) {
                node.direction = direction;
                validNode = getIfValid(node);
            }
            return validNode;
        }

        protected Node creteNewNode(int y, int x, long score, char direction, LinkedHashSet<Node> waypoints) {
            return new Node(y, x, score, direction, waypoints);
        }

        Node getIfValid(Node node) {
            if (isValid(node)) {
                initNodes(node);
            }
            return node;
        }

        private boolean isValid(Node node) {
            return OBSTACLE != map.get(node.y).get(node.x);
        }

        void initNodes(Node node) {
            node.waypoints.add(node);
            visited.add(node);
            if (map.get(node.y).get(node.x) != 'E' && scoreCache.get(node) == null) {
                node.forward();
                node.counterClockWise();
                node.clockWise();
            } else if (scoreCache.get(node) != null) {
                node.found = true;
                node.score += scoreCache.get(node);
                node.backwardScore = scoreCache.get(node);
                scoreboard.add(node);
            } else if (map.get(node.y).get(node.x) == 'E') {
                node.direction = 'E';
                node.found = true;
                node.backwardScore = 0;
                scoreboard.add(node);
            }
        }

        boolean isInRange(Node node) {
            return node.y >= 0 && node.y < map.size() && node.x >= 0 && node.x < map.getFirst().size()
                   && !node.waypoints.contains(node) && !containsBackwardDirection(node);
        }

        boolean containsBackwardDirection(Node node) {
            return (UP == node.direction && node.waypoints.contains(node.nodeKey(node.y, node.x, DOWN))) || (
                    DOWN == node.direction && node.waypoints.contains(node.nodeKey(node.y, node.x, UP))) || (
                           LEFT == node.direction && node.waypoints.contains(node.nodeKey(node.y, node.x, RIGHT))) || (
                           RIGHT == node.direction && node.waypoints.contains(node.nodeKey(node.y, node.x, LEFT)));
        }

    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (List<Character> row : map) {
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
                new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day16_1.tmp_1.txt")))) {
            this.map = new ArrayList<>();
            this.scoreCache = new HashMap<>();
            String line = null;
            while ((line = br.readLine()) != null) {
                List<Character> xRow = new ArrayList<>();
                for (int x = 0; x < line.length(); x++) {
                    char ch = line.charAt(x);
                    if ('S' == ch) {
                        ch = RIGHT;
                        int y = map.size();
                        this.cursor = new Node(y, x, 0, RIGHT, new LinkedHashSet<>());
                    }
                    xRow.add(ch);
                }
                map.add(xRow);
            }

        }
    }
}
