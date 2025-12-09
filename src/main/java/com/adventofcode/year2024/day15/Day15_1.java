package com.adventofcode.year2024.day15;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adventofcode.utils.FileUtils;

public class Day15_1 {

    protected static final char DOT = '.';
    protected static final char WALL = '#';
    protected static final char BOX = 'O';
    protected static final char CURSOR = '@';
    protected Map<Integer, Map<Integer, Cell>> map;
    protected List<Character> movements;
    protected List<Box> boxes; // just for faster count
    protected Box cursor;

    // TODO: duplicate x,y and ch in objects just for better debugging, nothing essential
    public static void main(String[] args) throws Exception {
        new Day15_1().count();
        // answer 1499739 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println("movements of " + movements.size() + ", : " + movements);
        System.out.println("answer = unknown so far");
        move();
        printMap();
        System.out.println("answer = " + calculateBoxesSum());
    }

    protected long calculateBoxesSum() {
        long total = 0;
        for(Box box : boxes) {
            if(box.ch == CURSOR) {
                continue;
            }
            long sum = box.y * 100 + box.x;
            total+=sum;
        }
        return total;
    }

    protected void move() {
        for (int move = 0; move < movements.size(); move++) {
            char direction = movements.get(move);
            System.out.println("direction : " + direction);
            cursor.move(direction);
            //printMap();
        }
    }

    protected class Box {

        int x;
        int y;
        char ch;

        public Box(int x, int y, char ch) {
            this.x = x;
            this.y = y;
            this.ch = ch;
        }

        boolean move(char ch) {
            return switch (ch) {
                case '^' -> moveUp();
                case 'v' -> moveDown();
                case '<' -> moveLeft();
                case '>' -> moveRight();
                default -> throw new IllegalArgumentException("Unable to move like that : " + ch);
            };
        }

        boolean moveUp() {
            return moveVertical(-1);
        }

        boolean moveDown() {
            return moveVertical(1);
        }

        boolean moveLeft() {
            return moveHorizontal(-1);
        }

        boolean moveRight() {
            return moveHorizontal(1);
        }

        boolean moveHorizontal(int dx) {
            int newX = x + dx;
            if (newX >= map.get(0).size() || newX < 0) {
                return false;
            }
            Cell nextCell = map.get(y).get(newX);
            if (nextCell.wall == null) {
                if (nextCell.box != null) {
                    if (!nextCell.box.moveHorizontal(dx)) {
                        return false;
                    }
                }
                map.get(y).get(x).box = null;
                this.x = newX;
                map.get(y).get(newX).box = this;
                return true;
            } else {
                return false;
            }
        }

        boolean moveVertical(int dy) {
            int newY = y + dy;
            if (newY >= map.size() || newY < 0) {
                return false;
            }
            Cell nextCell = map.get(newY).get(x);
            if (nextCell.wall == null) {
                if (nextCell.box != null) {
                    if (!nextCell.box.moveVertical(dy)) {
                        return false;
                    }
                }
                map.get(y).get(x).box = null;
                this.y = newY;
                map.get(newY).get(x).box = this;
                return true;
            } else {
                return false;
            }
        }

    }

    protected class Wall {

        int x;
        int y;
        char ch = WALL;

        public Wall(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    protected class Cell {

        int x;
        int y;

        Wall wall;
        Box box;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (int y = 0; y < map.size(); y++) {
            for (int x = 0; x < map.get(0).size(); x++) {
                Cell cell = map.get(y).get(x);
                if (cell.wall != null) {
                    sb.append(cell.wall.ch);
                } else if (cell.box != null) {
                    sb.append(cell.box.ch);
                } else {
                    sb.append(DOT);
                }
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day15_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day15_1_tmp.txt")))) {
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day15_1_tmp_1.txt")))) {
            this.map = new HashMap<>();
            this.movements = new ArrayList<>();
            this.boxes = new ArrayList<>();
            printMap();
            String line = null;
            int y = 0;
            boolean movementsNext = false;
            while ((line = br.readLine()) != null) {
                if (line.trim().isBlank()) {
                    movementsNext = true;
                    continue;
                }
                if (!movementsNext) {
                    map.put(y, new HashMap<>());
                    for (int x = 0; x < line.length(); x++) {
                        char ch = line.charAt(x);
                        Cell cell = new Cell(x, y);
                        if (BOX == ch || CURSOR == ch) {
                            Box box = new Box(x, y, ch);
                            if (CURSOR == ch) {
                                cursor = box;
                            }
                            cell.box = box;
                            boxes.add(box);
                        } else if (WALL == ch) {
                            Wall wall = new Wall(x, y);
                            cell.wall = wall;
                        }
                        map.get(y).put(x, cell);
                    }
                    y++;
                } else {
                    for (int x = 0; x < line.length(); x++) {
                        char ch = line.charAt(x);
                        movements.add(ch);
                    }
                }
            }
        }
    }
}
