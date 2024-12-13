package com.adventofcode.day13;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.adventofcode.utils.FileUtils;

public class Day13_1_1 {

    protected static final char SPACE = '.';
    protected List<Config> configs;
    //protected static List<Node> trailHeads = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new Day13_1_1().count();
        // answer 694 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println(configs);
        System.out.println("answer = unknown so far");
        long answer = winPrize();
        System.out.println("answer = " + answer);
        /*int answer = sumTrailHeads();
        System.out.println(trailHeads);
        System.out.println("answer = " + answer);
        System.out.println("visited = " + trailHeads.size());*/
    }

    protected long winPrize() {
        long total = 0;
        for(Config config : configs) {
            Button buttonA = config.buttonA;
            Button buttonB = config.buttonB;
            Prize prize = config.prize;
            long xMaxA = prize.x / buttonA.dx + 1;
            long yMaxA = prize.y / buttonA.dy + 1;
            long xMaxB = prize.x / buttonB.dx + 1;
            long yMaxB = prize.y / buttonB.dy + 1;
            //System.out.printf("%nxMaxA=%d,yMaxA=%d%n", xMaxA, yMaxA);
            //System.out.printf("%nxMaxB=%d,yMaxB=%d%n", xMaxB, yMaxB);
            long maxPressA = 100;///Long.max(xMaxA, yMaxA);
            long maxPressB = 100;//Long.max(xMaxB, yMaxB);
            //System.out.printf("%nmaxPressA=%d,maxPressB=%d%n", maxPressA, maxPressB);

            Long minTokens = null;
            for(long pressA = 0; pressA <= maxPressA;pressA++) {
                for(long pressB = 0; pressB <= maxPressB;pressB++) {
                    long x = buttonA.dx * pressA + buttonB.dx*pressB;
                    long y = buttonA.dy * pressA + buttonB.dy*pressB;
                    long tokens = 3 * pressA + 1* pressB;
                    //System.out.printf("%npressA=%d,pressB=%d%n", pressA, pressB);
                    if(x == prize.x && y == prize.y) {
                        System.out.printf("%n%d,%d=%d%n", x, y, tokens);
                        if(minTokens == null) {
                            minTokens = tokens;
                        } else {
                            if(minTokens > tokens) {
                                minTokens = tokens;
                            }
                        }
                    }
                    if(x > prize.x || y > prize.y) {
                        break;
                    }
                }
            }
            if(minTokens == null) {
                minTokens = 0L;
            }
            System.out.println("minTokens = " + minTokens);
            total+=minTokens;
        }
        return total;
    }

    protected class Config {
        Button buttonA;
        Button buttonB;
        Prize prize;

        public Config(Button buttonA, Button bB, Prize prize) {
            this.buttonA = buttonA;
            this.buttonB = bB;
            this.prize = prize;
        }

        @Override
        public String toString() {
            return "Config{" + "bA=" + buttonA + ", bB=" + buttonB + ", p=" + prize + '}';
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
                new InputStreamReader(FileUtils.resourceFileToInputStream("day13_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day13_1_tmp.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day13_1_tmp_1.txt")))) {
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
