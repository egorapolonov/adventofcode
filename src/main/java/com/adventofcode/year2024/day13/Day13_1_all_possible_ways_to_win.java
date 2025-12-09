package com.adventofcode.year2024.day13;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.adventofcode.utils.FileUtils;

public class Day13_1_all_possible_ways_to_win {

    protected static final char SPACE = '.';
    protected List<Config> configs;
    //protected static List<Node> trailHeads = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new Day13_1_all_possible_ways_to_win().count();
        // answer 694 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println(configs);
        System.out.println("answer = unknown so far");
        winPrize();
        /*int answer = sumTrailHeads();
        System.out.println(trailHeads);
        System.out.println("answer = " + answer);
        System.out.println("visited = " + trailHeads.size());*/
    }

    protected int winPrize() {
        int counter = 0;
        for(Config config : configs) {
            Node node = creteRootNode(config);
            System.out.println("Node : " + node);
            System.out.println("Best : " + node.best);
        }
        return counter;
    }

    protected class Config {
        Button bA;
        Button bB;
        Prize p;

        public Config(Button bA, Button bB, Prize p) {
            this.bA = bA;
            this.bB = bB;
            this.p = p;
        }

        @Override
        public String toString() {
            return "Config{" + "bA=" + bA + ", bB=" + bB + ", p=" + p + '}';
        }
    }

    protected class Button {
        long dx;
        long dy;

        public Button(long dx, long dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public String toString() {
            return "Button{" + "dx=" + dx + ", dy=" + dy + '}';
        }
    }

    protected class Prize {
        long x;
        long y;

        public Prize(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Prize{" + "x=" + x + ", y=" + y + '}';
        }
    }

    protected Node creteRootNode(Config config) {
        return new Node(config);
    }

    protected class Node {

        long tokens;
        long x;
        long y;

        Node a;
        Node b;

        Config config;

        Set<Node> visited;

        AtomicReference<Node> best;

        Node(Config config, Set<Node> visited, AtomicReference<Node> best) {
            this.visited = visited;
            this.config = config;
            this.best = best;
        }

        Node(Config config) {
            this.x = 0;
            this.y = 0;
            this.tokens = 0;
            this.visited = new HashSet<>();
            this.config = config;
            this.best = new AtomicReference<>();
            pressA();
            pressB();
        }

        void pressA() {
            Node node = creteNewNode();
            node.x = this.x + config.bA.dx;
            node.y = this.y + config.bA.dy;
            node.tokens = this.tokens + 3;
            if(node.x < config.p.x && node.y < config.p.y) {
                initNodes(node);
                this.a = node;
            }
            findBest(node);
        }

        void pressB() {
            Node node = creteNewNode();
            node.y = this.y + config.bB.dy;
            node.x = this.x + config.bB.dx;
            node.tokens = this.tokens + 3;
            if(node.x < config.p.x && node.y < config.p.y) {
                initNodes(node);
                this.b = node;
            }
            findBest(node);
        }

        private void findBest(Node node) {
            if(node.x == config.p.x && node.y == config.p.y) {
                System.out.println("[%dx%d]".formatted(node.x, node.y));
                if(best.get() == null) {
                    best.set(node);
                } else {
                    if(best.get().tokens > node.tokens) {
                        best.set(node);
                    }
                }
            }
        }

        protected Node creteNewNode() {
            return new Node(config, visited, best);
        }

        void initNodes(Node node) {
            //System.out.println("[%dx%d]".formatted(node.x, node.y));
            /*if(node.x <= 8400) {
                System.out.println("[%dx%d]".formatted(node.x, node.y));
            }*/
            node.pressA();
            node.pressB();
            //this.visited.add(node);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Node node = (Node) o;
            return tokens == node.tokens && x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(tokens, x, y);
        }

        @Override
        public String toString() {
            return "Node{" + "tokens=" + tokens + ", x=" + x + ", y=" + y + ", a=" + a + ", b=" + b + '}';
        }
    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (Config config : configs) {
            sb.append(config);
            System.out.println(config);
            sb.setLength(0);
        }
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day13_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day13_1_tmp_1.txt")))) {
            this.configs = new ArrayList<>();
            String line = null;
            Button bA = null;
            Button bB = null;
            Prize p = null;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) {
                    configs.add(new Config(bA, bB, p));
                    bA = null;
                    bB = null;
                    p = null;
                    continue;
                } else {
                    if (line.startsWith("Button A: ")) {
                        String dxs = line.substring(line.indexOf("X+") + 1, line.indexOf(","));
                        String dys = line.substring(line.indexOf("Y+") + 1);
                        bA = new Button(Long.parseLong(dxs), Long.parseLong(dys));
                    } else if (line.startsWith("Button B: ")) {
                        String dxs = line.substring(line.indexOf("X+") + 1, line.indexOf(","));
                        String dys = line.substring(line.indexOf("Y+") + 1);
                        bB = new Button(Long.parseLong(dxs), Long.parseLong(dys));
                    } else if (line.startsWith("Prize: ")) {
                        String xs = line.substring(line.indexOf("X=") + 2, line.indexOf(","));
                        String ys = line.substring(line.indexOf("Y=") + 2);
                        p = new Prize(Long.parseLong(xs), Long.parseLong(ys));
                    }
                }
            }
            configs.add(new Config(bA, bB, p));
        }
    }
}
