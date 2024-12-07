package com.adventofcode.day6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
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
    private List<List<Position>> cols;
    private LinkedHashSet<Position> visited;
    private Position direction;

    public static void main(String[] args) throws Exception {
        new Day6_1().count();
    }

    public void count() throws Exception {
        // 41 correct answer for tmp
        // 5460 is too low
        // 5461 is correct
        enterTheMatrix();
        printFieldRows();
        /*System.out.println(rows);
        System.out.println(cols);*/
        move(direction);
        System.out.println(visited.size());
        printFieldRows();
        System.out.println("last seen at " + direction);
        System.out.println("dump : %s".formatted(visited));
        printFieldRows();
    }

    private void printFieldRows() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________________________\n");
        for (List<Position> row : rows) {
            for (Position pos : row) {
                char mark = pos.visited ? 'x' : pos.ch;
                sb.append(mark);
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    private void printFieldCols() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________________________\n");
        for (List<Position> col : cols) {
            for (Position pos : col) {
                char mark = pos.visited ? 'x' : pos.ch;
                sb.append(mark);
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    private void enterTheMatrix() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day6_1.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day6_1_tmp.txt")))) {
            List<List<Position>> rows = new ArrayList<>();
            List<List<Position>> cols = new ArrayList<>();
            String line = null;
            int row = 0;
            while ((line = br.readLine()) != null) {
                List<Position> rowPositions = new ArrayList<>(line.length());
                for (int col = 0; col < line.length(); col++) {
                    Position position = new Position(line.charAt(col), row, col);
                    rowPositions.add(position);
                    loadDirectionIfPresent(position);
                    if (cols.size() <= col) {
                        cols.add(new ArrayList<>());
                    }
                    cols.get(col).add(position);
                }
                rows.add(rowPositions);
                row++;
            }
            this.rows = rows;
            this.cols = cols;
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

        /*@Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Position position = (Position) o;
            return ch == position.ch && row == position.row && col == position.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ch, row, col);
        }*/

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
            List<Position> col = cols.get(direction.col);
            boolean facingObstacle = false;
            while (direction.row > 0) {
                visitAndCollectClone();
                int facingRow = direction.row - 1;
                Position front = col.get(facingRow);
                System.out.println("%s|%s".formatted(direction, front));
                if (isFacingObstacle(front)) {
                    direction.ch = turn(direction.ch);
                    break;
                } else {
                    direction.row = facingRow;
                }
            }
            move(direction);
        } else if (direction.ch == DOWN) {
            List<Position> col = cols.get(direction.col);
            if (direction.row == col.size() - 1) {
                return;
            }
            boolean facingObstacle = false;
            for (int rowIndex = direction.row + 1; rowIndex < col.size(); rowIndex++) {
                visitAndCollectClone();
                Position front = col.get(rowIndex);
                if (isFacingObstacle(front)) {
                    direction.ch = turn(direction.ch);
                    break;
                } else {
                    direction.row = rowIndex;
                }
            }
            move(direction);
        } else if (direction.ch == LEFT) {
            List<Position> row = rows.get(direction.row);
            if (direction.col == 0) {
                return;
            }
            boolean facingObstacle = false;
            while (direction.col > 0) {
                visitAndCollectClone();
                int colIndex = direction.col - 1;
                Position front = row.get(colIndex);
                if (isFacingObstacle(front)) {
                    direction.ch = turn(direction.ch);
                    facingObstacle = true;
                    break;
                } else {
                    direction.col = colIndex;
                }
            }
            move(direction);
        }
        if (direction.ch == RIGHT) {
            List<Position> row = rows.get(direction.row);
            if (direction.col == rows.size() - 1) {
                return;
            }
            boolean facingObstacle = false;
            for (int colIndex = direction.col + 1; colIndex < row.size(); colIndex++) {
                visitAndCollectClone();
                Position front = row.get(colIndex);
                if (isFacingObstacle(front)) {
                    direction.ch = turn(direction.ch);
                    facingObstacle = true;
                    break;
                } else {
                    direction.col = colIndex;
                }
            }
            move(direction);
        }
        visitAndCollectClone();
    }

    private void visitAndCollectClone() {
        direction.visited = true;
        rows.get(direction.row).get(direction.col).visited = true;
        visited.add(new Position(direction));
    }

    /*private void move(Position direction) {
        List<Position> lane = getRoad().get(getSelector());
        boolean facingObstacle = false;
        if(direction.ch == UP || direction.ch == LEFT) {
            while (getSelector() > 0 ) {
                int facingPoint = getSelector() - 1;
                Position front = lane.get(facingPoint);
                System.out.println("direction : %s, front : %s, facing K : %s".formatted(direction, front, facingPoint));
                if (isFacingObstacle(front)) {
                    direction.ch = turn(direction.ch);
                    facingObstacle = true;
                    break;
                } else {
                    visited.add(direction);
                    moveForward(facingPoint);
                }
            }
        } else {
            while (getSelector() < lane.size() -1 ) {
                int facingPoint = getSelector() + 1;
                Position front = lane.get(facingPoint);
                if (isFacingObstacle(front)) {
                    direction.ch = turn(direction.ch);
                    facingObstacle = true;
                    break;
                } else {
                    visited.add(direction);
                    moveForward(facingPoint);
                }
            }
        }
        if(facingObstacle) {
            move(direction);
        }
    }*/

    /*private int getSelector() {
        return direction.ch == UP || direction.ch == DOWN ? direction.col : direction.row;
    }

    private List<List<Position>> getRoad() {
        return direction.ch == UP || direction.ch == DOWN ? cols: rows;
    }

    private void moveForward(int value) {
        if(direction.ch == UP || direction.ch == DOWN ){
            direction.row = value;
        } else {
            direction.col = value;
        }
        System.out.printf("\n---%s---\n", direction);
        //printFieldRows();
        //printFieldCols();
    }*/

    private static boolean isFacingObstacle(Position front) {
        boolean retVal = OBSTACLE == front.ch;
        if (retVal) {
            System.out.println("OBSTACLE : %s".formatted(front));
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
        System.out.println("TURN : %s".formatted(turn));
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
