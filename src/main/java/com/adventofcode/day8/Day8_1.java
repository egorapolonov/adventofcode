package com.adventofcode.day8;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.adventofcode.utils.FileUtils;

public class Day8_1 {

    protected static final char ANTINODE = 'O';
    protected List<List<Position>> rows;
    protected LinkedHashSet<Position> visited;
    protected Map<Character, List<Position>> groups;

    public static void main(String[] args) throws Exception {
        new Day8_1().count();
        // answer is unknown
    }

    public void count() throws Exception {
        loadMap();
        printMap();
        System.out.println(groups);
        for(var entry : groups.entrySet()) {
            calculateGroup(entry.getValue());
        }
    }

    protected List<Position> calculateGroup(List<Position> pos) {
        for(int index = 0;index < pos.size();index++) {
            for (int next = index+1;next < pos.size();next++) {
                List<Position> antinodes = calculateAntiNodes(pos.get(index), pos.get(next));
                System.out.printf("%n%s+%s --->%s", pos.get(index), pos.get(next), antinodes);
            }
        }
        return null; // TODO: fixt if necessary
    }

    private void countIfInRange(Position pos) {
        // TODO: mark and increment counter in rows array
    }

    protected List<Position> calculateAntiNodes(Position one, Position two) {
        int rowDiff = one.row - two.row;
        int colDiff = one.col - two.col;
        int topRow = 0;
        int downRow = 0;
        if(rowDiff > 0) { // one.row > two.row
            topRow = two.row - rowDiff;
            downRow = one.row + rowDiff;
        } else { // two.row > one.row
            topRow = two.row - rowDiff;
            downRow = one.row + rowDiff;
        }
        int leftCol = 0;
        int rightCol = 0;
        if(colDiff > 0) { // one.row > two.row
            leftCol = two.col - colDiff;
            rightCol = one.col + colDiff;
        } else { // two.row > one.row
            leftCol = two.col - colDiff;
            rightCol = one.col + colDiff;
        }
        Position topLeft = new Position(ANTINODE, topRow, leftCol, true);
        Position downRight = new Position(ANTINODE, downRow, rightCol, true);
        return List.of(topLeft, downRight);
    }

    protected static class Position {

        char ch;
        int row;
        int col;
        int count;
        boolean antinode;

        public Position(Position position) {
            this.ch = position.ch;
            this.row = position.row;
            this.col = position.col;
            this.antinode = position.antinode;
        }

        public Position(char ch, int row, int col, boolean antinode) {
            this.ch = ch;
            this.row = row;
            this.col = col;
            this.antinode = antinode;
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
            return "[%s][%dx%d][%s]".formatted(ch, row, col, antinode);
        }
    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (List<Position> row : rows) {
            for (Position pos : row) {
                char mark = pos.antinode ? 'O' : pos.ch;
                sb.append(mark);
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day8_1_tmp.txt")))) {
            //new InputStreamReader(FileUtils.resourceFileToInputStream("day8_1.txt")))) {
            this.rows = new ArrayList<>();
            this.visited = new LinkedHashSet<>();
            this.groups = new LinkedHashMap<>();
            String line = null;
            int row = 0;
            while ((line = br.readLine()) != null) {
                List<Position> rowPositions = new ArrayList<>(line.length());
                for (int col = 0; col < line.length(); col++) {
                    Position position = new Position(line.charAt(col), row, col, false);
                    rowPositions.add(position);
                    if(Character.isLetterOrDigit(position.ch)) {
                        groups.computeIfAbsent(position.ch, ArrayList::new);
                        groups.get(position.ch).add(position);
                    }
                }
                rows.add(rowPositions);
                row++;
            }

        }
    }
}
