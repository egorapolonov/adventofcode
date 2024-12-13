package com.adventofcode.day13;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.adventofcode.utils.FileUtils;

public class Day13_2 {

    protected static final char SPACE = '.';
    protected List<Config> configs;
    //protected static List<Node> trailHeads = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new Day13_2().count();
        // answer 694 is correct
        // 111212245056357 is too high.
        // 111528226986779 is too high
        // 101406661266314 is the right answer
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println(configs);
        System.out.println("answer = unknown so far");
        long answer = solvePrize();
        System.out.println("answer = " + answer);
    }

    protected long solvePrize() {
        long total = 0;
        for (Config config : configs) {
            Long a = solvePrizeA(config);
            Long b = solvePrizeB(config);
            if(a != null || b!= null) {
                if(a != null && b!= null) {
                    total+=Long.min(a, b);
                } else if (a!=null) {
                    total+=a;
                } else {
                    total+=b;
                }
            }
        }
        return total;
    }

    protected Long solvePrizeA(Config config) {
        Button buttonA = config.buttonA;
        Button buttonB = config.buttonB;
        Prize prize = config.prize;
        long pressA =
                (prize.x * buttonB.dy - prize.y * buttonB.dx) / (buttonA.dx * buttonB.dy * -buttonA.dy * buttonB.dx);
        long pressB = (prize.x - pressA * buttonA.dx) / buttonB.dx;
        if(pressA < 0 || pressB < 0) {
            System.out.println("WARN! There is no solution");
            return null;
        }
        long tokensA = pressA * 3;
        long tokensB = pressB * 1;
        long tokens = tokensA + tokensB;
        if(((pressA * buttonA.dx + pressB * buttonB.dx) != prize.x)
            || (pressA * buttonA.dy + pressB * buttonB.dy != prize.y)) {
            System.out.println("MISS!");
            return null;
        }
        System.out.printf("%npressA=%d,pressB=%d,tokens=%d%n", pressA, pressB, tokens);
        System.out.println("tokens = " + tokens);
        return tokens;
    }

    protected Long solvePrizeB(Config config) {
        Button buttonA = config.buttonA;
        Button buttonB = config.buttonB;
        Prize prize = config.prize;
        long pressB =
                (prize.x * buttonA.dy - prize.y * buttonA.dx) / (buttonB.dx * buttonA.dy - buttonB.dy * buttonA.dx);
        long pressA = (prize.x - pressB * buttonB.dx) / buttonA.dx;
        if(pressA < 0 || pressB < 0) {
            System.out.println("WARN! There is no solution");
            return null;
        }
        long tokensA = pressA * 3;
        long tokensB = pressB * 1;
        long tokens = tokensA + tokensB;
        if(((pressA * buttonA.dx + pressB * buttonB.dx) != prize.x)
           || (pressA * buttonA.dy + pressB * buttonB.dy != prize.y)) {
            System.out.println("MISS!");
            return null;
        }
        System.out.printf("%npressA=%d,pressB=%d,tokens=%d%n", pressA, pressB, tokens);
        System.out.println("tokens = " + tokens);
        return tokens;
    }

    // TODO: waste of time
    protected long winPrize() {
        long total = 0;
        for (Config config : configs) {
            Button buttonA = config.buttonA;
            Button buttonB = config.buttonB;
            Prize prize = config.prize;
            long xMaxA = prize.x / buttonA.dx + 1;
            long yMaxA = prize.y / buttonA.dy + 1;
            long xMaxB = prize.x / buttonB.dx + 1;
            long yMaxB = prize.y / buttonB.dy + 1;
            long maxPressA = Long.max(xMaxA, yMaxA);
            long maxPressB = Long.max(xMaxB, yMaxB);

            Long minTokens = null;
            for (long pressA = 0; pressA <= maxPressA; pressA++) {
                long aX = buttonA.dx * pressA;
                long aY = buttonA.dy * pressA;
                long tokensA = 3 * pressA;

                long mX = prize.x - aX;
                long mY = prize.y - aY;

                if (mX < 0 || mY < 0) {
                    break;
                }

                long pressBX = mX / buttonB.dx;
                long pressBY = mY / buttonB.dy;

                if (pressBX == pressBY) {
                    long x = aX + pressBX * buttonB.dx;
                    long y = aY + pressBY * buttonB.dy;
                    long tokensB = 1 * pressBX;
                    System.out.printf("%nFound : %d,%d=%d%n, pressBX=%s, tokens=%d", aX, aY, tokensA, pressBX, tokensB);
                    long tokens = tokensA + tokensB;
                    if (x == prize.x && y == prize.y) {
                        System.out.printf("%n%d,%d=%d%n", x, y, tokens);
                        if (minTokens == null) {
                            minTokens = tokens;
                        } else {
                            if (minTokens > tokens) {
                                minTokens = tokens;
                            }
                        }
                    }
                }
            }
            if (minTokens == null) {
                minTokens = 0L;
            }
            System.out.println("minTokens = " + minTokens);
            total += minTokens;
        }
        return total;
    }

    // Do not push your computer so hard!
    protected long winPrizeWitchCache() {
        long total = 0;
        for (Config config : configs) {
            Button buttonA = config.buttonA;
            Button buttonB = config.buttonB;
            Prize prize = config.prize;
            long xMaxA = prize.x / buttonA.dx + 1;
            long yMaxA = prize.y / buttonA.dy + 1;
            long xMaxB = prize.x / buttonB.dx + 1;
            long yMaxB = prize.y / buttonB.dy + 1;
            long maxPressA = Long.max(xMaxA, yMaxA);
            long maxPressB = Long.max(xMaxB, yMaxB);

            // warm-up cache
            Map<ButtonCache, Long> cacheB = new HashMap<>();
            for (long pressB = 0; pressB <= maxPressB; pressB++) {
                long bX = buttonB.dx * pressB;
                long bY = buttonB.dy * pressB;
                long tokensB = 1 * pressB;
                ButtonCache cacheKey = new ButtonCache(bX, bY);
                cacheB.put(cacheKey, tokensB);
            }

            Long minTokens = null;
            for (long pressA = 0; pressA <= maxPressA; pressA++) {
                long aX = buttonA.dx * pressA;
                long aY = buttonA.dy * pressA;
                long tokensA = 3 * pressA;

                long mX = prize.x - aX;
                long mY = prize.y - aY;

                ButtonCache buttonBCacheKey = new ButtonCache(mX, mY);
                Long tokensB = cacheB.get(buttonBCacheKey);

                if (tokensB != null) {
                    long tokens = tokensA + tokensB;
                    if (minTokens == null) {
                        minTokens = tokens;
                    } else {
                        if (minTokens > tokens) {
                            minTokens = tokens;
                        }
                    }
                }
            }
            if (minTokens == null) {
                minTokens = 0L;
            }
            System.out.println("minTokens = " + minTokens);
            total += minTokens;
        }
        return total;
    }

    protected class ButtonCache {
        long x;
        long y;

        public ButtonCache(long x, long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            ButtonCache that = (ButtonCache) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
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
                        long topping = 10000000000000L;
                        //long topping = 0L;
                        p = new Prize(Long.parseLong(xs) + topping, Long.parseLong(ys) + topping);
                    }
                }
            }
            configs.add(new Config(bA, bB, p));
        }
    }
}
