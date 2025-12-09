package com.adventofcode.year2024.day15;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.adventofcode.utils.FileUtils;

public class Day15_2 {

    protected static final String DOT = ".";
    protected static final String WALL = "#";
    protected static final String BOX = "O";
    protected static final String BOX_LEFT = "[";
    protected static final String BOX_RIGHT = "]";
    protected static final String CURSOR = "@";
    protected Map<Integer, Map<Integer, Cell>> map;
    protected List<String> movements;
    protected List<Box> boxes; // just for faster count, we'll load here left corner of boxes '['
    protected Box cursor;

    // TODO: duplicate x,y and ch in objects just for better debugging, nothing essential
    public static void main(String[] args) throws Exception {
        new Day15_2().count();
        // answer 1522215 is correct
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
        for (Box box : boxes) {
            if (CURSOR.equals(box.ch)) {
                continue;
            }
            long sum = box.y * 100L + box.x;
            total += sum;
        }
        return total;
    }

    protected void move() {
        for (String direction : movements) {
            System.out.println(direction + " : " + cursor.move(direction));
            printMap(direction);
        }
    }

    protected class Box {

        int x;
        int y;
        String ch;

        public Box(int x, int y, String ch) {
            this.x = x;
            this.y = y;
            this.ch = ch;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Box box = (Box) o;
            return x == box.x && y == box.y && Objects.equals(ch, box.ch);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, ch);
        }

        @Override
        public String toString() {
            return "Box{" + "x=" + x + ", y=" + y + ", ch='" + ch + '\'' + '}';
        }

        boolean move(String ch) {
            return switch (ch) {
                case "^" -> moveUp();
                case "v" -> moveDown();
                case "<" -> moveLeft();
                case ">" -> moveRight();
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
                if (nextCell.box != null && !DOT.equals(nextCell.box.ch)) {
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
            Cell firstNextCell = map.get(newY).get(x);
            if (firstNextCell.wall == null) {
                if (firstNextCell.box != null) {
                    Cell secondNextCell = resolveSecondNextCell(firstNextCell.box);
                    if (unableToMoveBothBoxHalves(dy, firstNextCell, secondNextCell)) {
                        return false;
                    }
                    firstNextCell.box.moveVertical(dy);
                    if (secondNextCell != null && secondNextCell.box != null) {
                        secondNextCell.box.moveVertical(dy);
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

        private boolean unableToMoveBothBoxHalves(int dy, Cell firstNextCell, Cell secondNextCell) {
            return !ableToMoveBothBoxHalves(dy, firstNextCell, secondNextCell);
        }

        boolean canMoveVertical(int dy) {
            int newY = y + dy;
            if (newY >= map.size() || newY < 0) {
                return false;
            }
            Cell firstCell = map.get(newY).get(x);
            if (firstCell.wall == null) {
                if (firstCell.box != null) {
                    Cell secondCell = resolveSecondNextCell(firstCell.box);
                    return ableToMoveBothBoxHalves(dy, firstCell, secondCell);
                }
                return true;
            } else {
                return false;
            }
        }

        private boolean ableToMoveBothBoxHalves(int dy, Cell firstCell, Cell secondCell) {
            return firstCell.box.canMoveVertical(dy) && ((secondCell == null || secondCell.box == null)
                                                         || secondCell.box.canMoveVertical(dy));
        }

        Cell resolveSecondNextCell(Box firstBox) {
            if (BOX_LEFT.equals(firstBox.ch)) {
                return map.get(firstBox.y).get(firstBox.x + 1);
            } else if (BOX_RIGHT.equals(firstBox.ch)) {
                return map.get(firstBox.y).get(firstBox.x - 1);
            } else {
                return null;
            }
        }

    }

    protected class Wall {

        int x;
        int y;
        String ch = WALL;

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
                if (cell == null) {
                    sb.append("X");
                } else if (cell.wall != null) {
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

    protected void printMap(String direction) {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (int y = 0; y < map.size(); y++) {
            for (int x = 0; x < map.get(0).size(); x++) {
                Cell cell = map.get(y).get(x);
                if (cell == null) {
                    sb.append("X");
                } else if (cell.wall != null) {
                    sb.append(cell.wall.ch);
                } else if (cell.box != null) {
                    if (CURSOR.equals(cell.box.ch)) {
                        sb.append(direction);
                    } else {
                        sb.append(cell.box.ch);
                    }
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
                new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day15_1.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day15_1_tmp.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day15_1_tmp_1.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day15_1_tmp_2.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day15_1_tmp_3.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day15_1_tmp_4.txt")))) {
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
                        String ch = line.substring(x, x + 1);
                        int leftX = x * 2;
                        int rightX = x * 2 + 1;
                        if (BOX.equals(ch)) {
                            createBox(y, leftX, rightX, BOX_LEFT, BOX_RIGHT);
                        } else if (CURSOR.equals(ch)) {
                            createBox(y, leftX, rightX, CURSOR, DOT);
                        } else if (WALL.equals(ch)) {
                            createWall(y, leftX, rightX);
                        } else {
                            createDot(y, leftX, rightX);
                        }
                    }
                    y++;
                } else {
                    for (int x = 0; x < line.length(); x++) {
                        String ch = line.substring(x, x + 1);
                        movements.add(ch);
                    }
                }
            }
        }
    }

    private void createDot(int y, int leftX, int rightX) {
        Cell cellLeft = new Cell(leftX, y);
        Cell rightCell = new Cell(rightX, y);
        map.get(y).put(leftX, cellLeft);
        map.get(y).put(rightX, rightCell);
    }

    private void createBox(int y, int leftX, int rightX, String leftCh, String rightCh) {
        Box boxLeft = new Box(leftX, y, leftCh);
        Cell cellLeft = new Cell(leftX, y);
        cellLeft.box = boxLeft;
        map.get(y).put(leftX, cellLeft);
        Box boxRight = new Box(rightX, y, rightCh);
        Cell cellRight = new Cell(rightX, y);
        cellRight.box = boxRight;
        map.get(y).put(rightX, cellRight);
        if (CURSOR.equals(leftCh)) {
            cursor = boxLeft;
        }
        if (BOX_LEFT.equals(leftCh)) {
            boxes.add(boxLeft);
        }
    }

    private void createWall(int y, int leftX, int rightX) {
        Wall boxLeft = new Wall(leftX, y);
        Cell cellLeft = new Cell(leftX, y);
        cellLeft.wall = boxLeft;
        map.get(y).put(leftX, cellLeft);
        Wall boxRight = new Wall(rightX, y);
        Cell cellRight = new Cell(rightX, y);
        cellRight.wall = boxRight;
        map.get(y).put(rightX, cellRight);
    }
}
