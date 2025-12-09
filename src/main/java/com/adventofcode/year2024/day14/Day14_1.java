package com.adventofcode.year2024.day14;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day14_1 {

    protected static final char SPACE = '.';
    protected static final int WIDTH = 101;
    protected static final int HEIGHT = 103;
    protected Map<Integer, Map<Integer, Cell>> map;
    protected Set<Robot> robots;

    public static void main(String[] args) throws Exception {
        new Day14_1().count();
        // answer 228690000 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println("answer = unknown so far");
        move(100);
        printMap();
        int answer = countQuadrants();
        System.out.println("answer = " + answer);
    }

    protected void move(int movements) {
        for (int move = 0; move < movements; move++) {
            robots.forEach(Robot::move);
        }
    }

    protected int countQuadrants() {
        int centerOffsetX = WIDTH % 2;
        int centerOffsetY = HEIGHT % 2;
        int quadrant0 = 0;
        for (int y = 0; y < HEIGHT / 2; y++) {
            for (int x = 0; x < WIDTH / 2; x++) {
                //System.out.println("y=%d,x=%d".formatted(y, x));
                quadrant0 += map.get(y).get(x).robots.size();
            }
        }
        System.out.println("__________________");
        int quadrant1 = 0;
        for (int y = 0; y < HEIGHT / 2; y++) {
            for (int x = WIDTH / 2 + centerOffsetX; x < WIDTH; x++) {
                //System.out.println("y=%d,x=%d".formatted(y, x));
                quadrant1 += map.get(y).get(x).robots.size();
            }
        }
        System.out.println("__________________");
        int quadrant2 = 0;
        for (int y = HEIGHT / 2 + centerOffsetY; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH / 2; x++) {
                //System.out.println("y=%d,x=%d".formatted(y, x));
                quadrant2 += map.get(y).get(x).robots.size();
            }
        }
        System.out.println("__________________");
        int quadrant3 = 0;
        for (int y = HEIGHT / 2 + centerOffsetY; y < HEIGHT; y++) {
            for (int x = WIDTH / 2 + centerOffsetX; x < WIDTH; x++) {
                //System.out.println("y=%d,x=%d".formatted(y, x));
                quadrant3 += map.get(y).get(x).robots.size();
            }
        }
        return quadrant0 * quadrant1 * quadrant2 * quadrant3;
    }

    protected class Robot {

        int id;
        int posX;
        int posY;

        int speedX;
        int speedY;

        public Robot(int id, int posX, int posY, int speedX, int speedY) {
            this.id = id;
            this.posX = posX;
            this.posY = posY;
            this.speedX = speedX;
            this.speedY = speedY;
        }

        public void move() {
            map.get(posY).get(posX).robots.remove(this);
            moveX();
            moveY();
            System.out.println("newPosX=%d, newPosY=%d".formatted(posX, posY));
            map.get(posY).get(posX).robots.add(this);
        }

        private void moveX() {
            int newPosX = posX + speedX;
            if (newPosX >= WIDTH) {
                newPosX = newPosX % WIDTH;
            } else if (newPosX < 0) {
                newPosX = (WIDTH + (newPosX % WIDTH)); // in case of enormous speed
            }
            this.posX = newPosX;
        }

        private void moveY() {
            int newPosY = posY + speedY;
            if (newPosY >= HEIGHT) {
                newPosY = newPosY % HEIGHT;
            } else if (newPosY < 0) {
                newPosY = (HEIGHT + (newPosY % HEIGHT)); // in case of enormous speed
            }
            this.posY = newPosY;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Robot robot = (Robot) o;
            return id == robot.id && posX == robot.posX && posY == robot.posY && speedX == robot.speedX
                   && speedY == robot.speedY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, posX, posY, speedX, speedY);
        }

        @Override
        public String toString() {
            return "Robot{" + "id=" + id + ", posX=" + posX + ", posY=" + posY + ", speedX=" + speedX + ", speedY="
                   + speedY + '}';
        }

    }

    protected class Cell {

        int posX;
        int posY;

        Set<Robot> robots;

        public Cell(int posX, int posY) {
            this.posX = posX;
            this.posY = posY;
            this.robots = new HashSet<>();
        }
    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int robots = map.get(y).get(x).robots.size();
                if (robots > 0) {
                    sb.append(robots);
                } else {
                    sb.append(SPACE);
                }
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day14_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day14_1_tmp_1.txt")))) {
            // new InputStreamReader(FileUtils.resourceFileToInputStream("day14_1_tmp_2.txt")))) {
            this.map = new HashMap<>();
            this.robots = new HashSet<>();
            for (int y = 0; y < HEIGHT; y++) {
                map.put(y, new HashMap<>());
                for (int x = 0; x < WIDTH; x++) {
                    map.get(y).put(x, new Cell(x, y));
                }
            }
            printMap();
            String line = null;
            int robotId = 0;
            while ((line = br.readLine()) != null) {
                //p=0,4 v=3,-3
                String pos = line.trim().substring(line.indexOf("p") + 2);
                pos = pos.substring(0, pos.indexOf(" "));
                System.out.println("pos = " + pos);
                String[] posArr = pos.split(",");
                System.out.println("posArr = " + Arrays.toString(posArr));
                int x = Integer.parseInt(posArr[0]);
                int y = Integer.parseInt(posArr[1]);
                String vel = line.trim().substring(line.indexOf("v") + 2);
                String[] velArr = vel.split(",");
                int sX = Integer.parseInt(velArr[0]);
                int sY = Integer.parseInt(velArr[1]);
                Robot robot = new Robot(robotId, x, y, sX, sY);
                System.out.println("robot = " + robot);
                robots.add(robot);
                map.get(robot.posY).get(robot.posX).robots.add(robot);
                robotId++;
            }
        }
    }
}
