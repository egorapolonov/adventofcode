package com.adventofcode.day6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day6_1 {

    private static final char UP = '^';
    private static final char RIGHT = '>';
    private static final char DOWN = 'V';
    private static final char LEFT = '<';
    private static final Set<Character> DIRECTION = Set.of(UP, RIGHT, DOWN, LEFT);
    private static final char OBSTACLE = '#';
    private List<List<Position>> rows;
    private LinkedHashSet<Position> visited;
    private Position direction;

    public static void main(String[] args) throws Exception {
        new Day6_1().count();
    }

    public void count() throws Exception {
        // 41 correct answer for tmp
        // 5460 is too low
        // 5461 is correct
        loadMap();
        printMap();
        move(direction);
        System.out.println("answer : " + visited.size());
        printMap();
        System.out.println("last seen at " + direction);
        System.out.printf("dump : %s%n", visited);
    }

    private void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (List<Position> row : rows) {
            for (Position pos : row) {
                char mark = pos.visited ? 'x' : pos.ch;
                sb.append(mark);
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    private void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day6_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day6_1_tmp.txt")))) {
            List<List<Position>> rows = new ArrayList<>();
            String line = null;
            int row = 0;
            while ((line = br.readLine()) != null) {
                List<Position> rowPositions = new ArrayList<>(line.length());
                for (int col = 0; col < line.length(); col++) {
                    Position position = new Position(line.charAt(col), row, col);
                    rowPositions.add(position);
                    loadDirectionIfPresent(position);
                }
                rows.add(rowPositions);
                row++;
            }
            this.rows = rows;
            this.visited = new LinkedHashSet<>();
            visitAndCollectClone();
        }
    }

    private static class Position {

        char ch;
        int row;
        int col;

        boolean visited;

        public Position(Position position) {
            this.ch = position.ch;
            this.row = position.row;
            this.col = position.col;
            this.visited = position.visited;
        }

        public Position(char ch, int row, int col) {
            this.ch = ch;
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Position position = (Position) o;
            return row == position.row && col == position.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public String toString() {
            return "[%s][%dx%d][%s]".formatted(ch, row, col, visited);
        }
    }

    private void move(Position direction) {
        System.out.println(direction);
        if (direction.ch == UP) {
            if (direction.row == 0) {
                return;
            }
            visitAndCollectClone();
            int facingRow = direction.row - 1;
            prepareNextUpDownMove(direction, facingRow);
            move(direction);
        } else if (direction.ch == DOWN) {
            if (direction.row == rows.get(direction.row).size() - 1) {
                return;
            }
            visitAndCollectClone();
            int facingRow = direction.row + 1;
            prepareNextUpDownMove(direction, facingRow);
            move(direction);
        } else if (direction.ch == LEFT) {
            if (direction.col == 0) {
                return;
            }
            visitAndCollectClone();
            int facingCol = direction.col - 1;
            prepareNextSideMove(direction, facingCol);
            move(direction);
        }
        if (direction.ch == RIGHT) {
            if (direction.col == rows.size() - 1) {
                return;
            }
            visitAndCollectClone();
            int facingCol = direction.col + 1;
            prepareNextSideMove(direction, facingCol);
            move(direction);
        }
        visitAndCollectClone();
    }

    private void prepareNextSideMove(Position direction, int facingCol) {
        Position facing = rows.get(direction.row).get(facingCol);
        if (isFacingObstacle(facing)) {
            direction.ch = turn(direction.ch);
        } else {
            direction.col = facingCol;
        }
    }

    private void prepareNextUpDownMove(Position direction, int facingRow) {
        Position facing = rows.get(facingRow).get(direction.col);
        System.out.printf("%s|%s%n", direction, facing);
        if (isFacingObstacle(facing)) {
            direction.ch = turn(direction.ch);
        } else {
            direction.row = facingRow;
        }
    }

    private void visitAndCollectClone() {
        direction.visited = true;
        rows.get(direction.row).get(direction.col).visited = true;
        visited.add(new Position(direction));
    }

    private static boolean isFacingObstacle(Position front) {
        boolean retVal = OBSTACLE == front.ch;
        if (retVal) {
            System.out.printf("OBSTACLE : %s%n", front);
        }
        return retVal;
    }

    private char turn(char direction) {
        char turn = switch (direction) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
            default -> direction;
        };
        System.out.printf("TURN : %s%n", turn);
        return turn;
    }

    private void loadDirectionIfPresent(Position position) {
        if (DIRECTION.contains(position.ch)) {
            if (this.direction != null) {
                throw new IllegalArgumentException(
                        "Double direction symbols found : [%dx%d] and [%dx%d]".formatted(this.direction.row,
                                this.direction.col, position.row, position.col));
            }
            this.direction = position;
        }
    }

}
