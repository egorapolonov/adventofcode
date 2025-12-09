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

public class Day14_2 {

    protected static final char SPACE = '.';
    /*protected static final int WIDTH = 11;
    protected static final int HEIGHT = 7;*/
    protected static final int WIDTH = 101;
    protected static final int HEIGHT = 103;
    protected Map<Integer, Map<Integer, Cell>> map;
    protected Set<Robot> robots;
    private boolean easterEgg;

    public static void main(String[] args) throws Exception {
        new Day14_2().count();
        // answer 7094 is too high
        // answer 709 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println("answer = unknown so far");
        move(Integer.MAX_VALUE);
        //printMap();
        //int answer = countQuadrants();
        //System.out.println("answer = " + answer);
    }

    // TODO: need to check 100x100, too many iterations
    protected boolean isSymmetric() {
        boolean symmetric = true;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = WIDTH / 2 - 1; x >= 0; x--) {
                Cell left = map.get(y).get(x);
                Cell right = map.get(y).get(WIDTH - x - 1);
                if ((left == null || right == null) || (left.robots.size() != 1 || right.robots.size() != 1)) {
                    symmetric = false;
                    break;
                }
            }
        }
        return symmetric;
    }

    // TODO: need to check by 500 robots only
    protected boolean isSymmetricByRobots() {
        boolean symmetric = true;
        for (Robot robot : robots) {
            Cell current = map.get(robot.posY).get(robot.posX);
            int oppositeX = WIDTH - robot.posX - 1;
            Cell opposite = map.get(robot.posY).get(oppositeX);
            //System.out.println("%d vs %d".formatted(robot.posX, oppositeX));
            if (current.robots.isEmpty() && !opposite.robots.isEmpty()
            || (!current.robots.isEmpty() && opposite.robots.isEmpty())) {
                symmetric = false;
                break;
            }
            symmetric = symmetric;
        }
        return symmetric;
    }

    // there is no full-symmetry requirement. Read the description, there is "most of"!. Guessed that they aligned by center
    protected boolean isSymmetricByMostRobots() {
        boolean symmetric = false;
        int counter = 0;
        for (Robot robot : robots) {
            Cell current = map.get(robot.posY).get(robot.posX);
            int oppositeX = WIDTH - robot.posX - 1;
            Cell opposite = map.get(robot.posY).get(oppositeX);
            //System.out.println("%d vs %d".formatted(robot.posX, oppositeX));
            if (current.robots.isEmpty() && !opposite.robots.isEmpty()
                || (!current.robots.isEmpty() && opposite.robots.isEmpty())) {
                /*symmetric = false;
                break;*/
            }
            if(!current.robots.isEmpty() && !opposite.robots.isEmpty()) {
                counter++;
            }
            if(counter>=100) { // RANDOMLY played with this number, because size of christmas tree was unknown
                symmetric = true;
                break;
            }
            if(counter > 2 && current.robots.size() > 0 && opposite.robots.size() > 0){
                symmetric = symmetric;
            }
            if(counter > 10) {
                //printMap();
            }
        }
        return symmetric;
    }

    protected void move(int movements) {
        for (int move = 0; move < movements; move++) {
            //if(isSymmetric()) {
            if(move % 10000 == 0) {
                System.out.println("move so far = " + move);
            }
            //if (isSymmetricByRobots()) {
            if (isSymmetricByMostRobots()) {
            //if(containsBorder()) {
                printMap();
                System.out.println("christmas tree at " + (move + 1));
                break;
            }
            robots.forEach(Robot::move);
            //printMap();
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
            //System.out.println("newPosX=%d, newPosY=%d".formatted(posX, posY));
            map.get(posY).get(posX).robots.add(this);
            Set<Robot> robots = map.get(posY).get(posX).robots;
            long count = robots.stream().filter(r -> r.speedX == this.speedX && r.speedY == r.speedY).count();
            if (count > 1) {
                easterEgg = true;
            }
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

    // as soon as I get answer by isSymmetricByMostRobots, I guessed that probably X-mas tree is not centered
    protected boolean containsBorder() {
        String BORDER = "1111111111111111111111111111111";
        boolean retVal = false;
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int robots = map.get(y).get(x).robots.size();
                if (robots > 0) {
                    sb.append(robots);
                } else {
                    sb.append(SPACE);
                }
            }
            if(sb.toString().contains(BORDER)) {
                retVal = true;
                break;
            }
            sb.setLength(0);
        }
        return retVal;
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
