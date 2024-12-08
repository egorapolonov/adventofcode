package com.adventofcode.day6;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.adventofcode.utils.FileUtils;

public class Day6_2 extends Day6_1 {

    private static final char UP = '^';
    private static final char RIGHT = '>';
    private static final char DOWN = 'V';
    private static final char LEFT = '<';
    private static final Set<Character> DIRECTION = Set.of(UP, RIGHT, DOWN, LEFT);
    private static final char OBSTACLE = '#';
    private LinkedHashMap<Position, Position> visited;
    //private Position direction;

    public static void main(String[] args) throws Exception {
        new Day6_2().count();
    }

    public void count() throws Exception {
        // 6 correct answer for tmp
        // 5461 is too high
        // 1836 correct answer
        this.visited = new LinkedHashMap<>();
        List<List<Position>> map = new ArrayList<>();
        Position cursor = loadMap(map);
        printMap(map);
        move(map, new Position(cursor));
        //System.out.println("answer : " + (visited.entrySet().size()));
        printMap(map);
        //System.out.println("dump : " + visited);
        List<List<Position>> newMap = new ArrayList<>();
        cursor = loadMap(newMap);
        printMap(map);
        predictRoute(map, new Position(cursor));
        //System.out.println(visited.values());
    }

    private void printMap(List<List<Position>> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (List<Position> row : map) {
            for (Position pos : row) {
                char mark = pos.visited ? 'x' : pos.ch;
                boolean horizontalMove = false;
                boolean verticalMove = false;
                if (pos.moves.contains(UP) || pos.moves.contains(DOWN)) {
                    verticalMove = true;
                }
                if (pos.moves.contains(LEFT) || pos.moves.contains(RIGHT)) {
                    horizontalMove = true;
                }
                if (horizontalMove && verticalMove) {
                    mark = '+';
                } else if (horizontalMove) {
                    mark = '-';
                } else if (verticalMove) {
                    mark = '|';
                }
                sb.append(mark);
            }
            //System.out.println(sb);
            sb.setLength(0);
        }
    }

    private Position loadMap(List<List<Position>> map) throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(FileUtils.resourceFileToInputStream("day6_1.txt")))) {
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day6_1_tmp.txt")))) {
            String line = null;
            int row = 0;
            Position direction = null;
            while ((line = br.readLine()) != null) {
                List<Position> rowPositions = new ArrayList<>(line.length());
                for (int col = 0; col < line.length(); col++) {
                    Position position = new Position(line.charAt(col), row, col);
                    rowPositions.add(position);
                    Position pos = loadDirectionIfPresent(position);
                    if (pos != null) {
                        direction = pos;
                    }
                }
                map.add(rowPositions);
                row++;
            }
            visitAndCollectClone(map, direction);
            return direction;
        }
    }

    private static class Position {

        char ch;
        int row;
        int col;

        boolean visited;
        int counter;
        LinkedHashSet<Character> moves;

        public Position(Position position) {
            this.ch = position.ch;
            this.row = position.row;
            this.col = position.col;
            this.visited = position.visited;
            this.moves = new LinkedHashSet<>(position.moves);
        }

        public Position(char ch, int row, int col) {
            this.ch = ch;
            this.row = row;
            this.col = col;
            this.counter = 1;
            this.moves = new LinkedHashSet<>();
            moves.add(ch);
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
            return "[%s][%dx%d][%s][%s]counter=[%s]".formatted(ch, row, col, visited, moves, counter);
        }
    }

    private void move(List<List<Position>> map, Position direction) {
        //System.out.println(direction);
        if (direction.ch == UP) {
            if (direction.row == 0) {
                return;
            }
            visitAndCollectClone(map, direction);
            int facingRow = direction.row - 1;
            prepareNextUpDownMove(map, direction, facingRow);
            move(map, direction);
        } else if (direction.ch == DOWN) {
            if (direction.row == map.get(direction.row).size() - 1) {
                return;
            }
            visitAndCollectClone(map, direction);
            int facingRow = direction.row + 1;
            prepareNextUpDownMove(map, direction, facingRow);
            move(map, direction);
        } else if (direction.ch == LEFT) {
            if (direction.col == 0) {
                return;
            }
            visitAndCollectClone(map, direction);
            int facingCol = direction.col - 1;
            prepareNextSideMove(map, direction, facingCol);
            move(map, direction);
        }
        if (direction.ch == RIGHT) {
            if (direction.col == map.size() - 1) {
                return;
            }
            visitAndCollectClone(map, direction);
            int facingCol = direction.col + 1;
            prepareNextSideMove(map, direction, facingCol);
            move(map, direction);
        }
        visitAndCollectClone(map, direction);
    }

    private Position obstacleTmp;

    private void predictRoute(List<List<Position>> map, Position direction) throws Exception {
        int counter = 0;
        int index = 0;
        Position start = new Position(direction);
        Position cursor = new Position(start);
        for (var point : visited.entrySet()) {
            System.out.println("entry : " + point.getValue() + ", start : " + start);
            if(index !=0 && !point.getKey().equals(start)) {
                char tmp = map.get(point.getKey().row).get(point.getKey().col).ch;
                map.get(point.getKey().row).get(point.getKey().col).ch = OBSTACLE;
                point.getValue().ch = OBSTACLE;
                obstacleTmp = new Position(point.getValue());

                    /*if (isLoopIfFacingObstacleHere(cursor, false)) {
                        counter++;
                    }*/
                Map<Position, Position> mem = new HashMap<>();
                //if (isLoopDetected(map, mem, cursor, true)) {
                if(isLoopDetectedCycle(map, mem, cursor, null)) {
                    counter++;
                    System.out.println("O = " + point);
                }
                map.get(point.getKey().row).get(point.getKey().col).ch = tmp;
                cursor = new Position(start);
            }
            index++;
        }
        System.out.println("potential obstructions answer : " + counter);
    }

    private boolean prepareNextSideMove(List<List<Position>> map, Position direction, int facingCol) {
        Position facing = map.get(direction.row).get(facingCol);
        if (isFacingObstacle(facing)) {
            direction.ch = turn(direction.ch);
            //System.out.printf("OBSTACLE : %s, DIRECTION : %s, TURN : %s%n", facing, direction, direction.ch);
            boolean retVal = facing.equals(obstacleTmp);
            if(retVal) {
                //System.out.println("first move here : " + facing);
            }
            return retVal;
        } else {
            direction.col = facingCol;
        }
        return false;
    }

    private boolean prepareNextUpDownMove(List<List<Position>> map, Position direction, int facingRow) {
        Position facing = map.get(facingRow).get(direction.col);
        if (isFacingObstacle(facing)) {
            direction.ch = turn(direction.ch);
            //System.out.printf("OBSTACLE : %s, DIRECTION : %s, TURN : %s%n", facing, direction, direction.ch);
            boolean retVal = facing.equals(obstacleTmp);
            if(retVal) {
                //System.out.println("first move here : " + facing);
            }
            return retVal;
        } else {
            direction.row = facingRow;
        }
        return false;
    }

    private void predictNextUpDownMove(List<List<Position>> map, Position direction, int facingRow) {
        Position facing = map.get(facingRow).get(direction.col);
        if (isFacingObstacle(facing)) {
            direction.ch = turn(direction.ch);
        } else {
            direction.row = facingRow;
        }
    }

    private void turnHere(Position direction) {
        direction.ch = turn(direction.ch);
    }

    private void visitAndCollectClone(List<List<Position>> map, Position cursor) {
        Position mapPos = map.get(cursor.row).get(cursor.col);
        mapPos.visited = true;
        mapPos.moves.add(cursor.ch);
        Position pos = new Position(cursor);
        pos.moves.addAll(mapPos.moves);
        visited.merge(pos, pos, (k, v) -> {
            v.counter++;
            v.moves.add(cursor.ch);
            return v;
        });
    }

    private boolean isFacingObstacle(Position front) {
        return OBSTACLE == front.ch;
    }

    private char turn(char direction) {
        char turn = switch (direction) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
            default -> direction;
        };
        ////System.out.printf("TURN : %s%n", turn);
        return turn;
    }

    private Position loadDirectionIfPresent(Position position) {
        if (DIRECTION.contains(position.ch)) {
            return position;
        }
        return null;
    }

    private Map<Position, Position> deepCopyMap() {
        Map<Position, Position> retVal = new HashMap<>();
        for (var visit : visited.entrySet()) {
            retVal.put(new Position(visit.getKey()), new Position(visit.getValue()));
        }
        return retVal;
    }

    private boolean isLoopDetectedCycle(List<List<Position>> map, Map<Position, Position> mem, Position direction,
            Boolean firstMove) {
        Boolean detected = null;
        while (detected == null) {
            //System.out.println("move : " + direction);
            if (direction.ch == UP) {
                if (direction.row == 0) {
                    detected = false;
                    break;
                }
                int facingRow = direction.row - 1;
                if (existsDirection(map, mem, direction, firstMove)) {
                    detected = true;
                    break;
                }
                mem(mem, direction);
                boolean fm = prepareNextUpDownMove(map, direction, facingRow);
                if(firstMove == null && fm) {
                    //System.out.println("first move detected at : " + facingRow);
                    //detected = false;
                    firstMove = fm;
                }
            } else if (direction.ch == DOWN) {
                if (direction.row == map.get(direction.row).size() - 1) {
                    detected = false;
                    break;
                }
                int facingRow = direction.row + 1;
                if (existsDirection(map, mem, direction, firstMove)) {
                    detected = true;
                    break;
                }
                mem(mem, direction);
                boolean fm = prepareNextUpDownMove(map, direction, facingRow);
                if(firstMove == null && fm) {
                    //System.out.println("first move detected at : " + facingRow);
                    //detected = false;
                    firstMove = fm;
                }
            } else if (direction.ch == LEFT) {
                if (direction.col == 0) {
                    detected = false;
                    break;
                }
                int facingCol = direction.col - 1;
                if (existsDirection(map, mem, direction, firstMove)) {
                    return true;
                }
                mem(mem, direction);
                boolean fm = prepareNextSideMove(map, direction, facingCol);
                if(firstMove == null && fm) {
                    //System.out.println("first move detected at : " + facingCol);
                    //detected = false;
                    firstMove = fm;
                }
            } else if (direction.ch == RIGHT) {
                if (direction.col == map.size() - 1) {
                    detected = false;
                    break;
                }
                int facingCol = direction.col + 1;
                if (existsDirection(map, mem, direction, firstMove)) {
                    detected = true;
                    break;
                }
                mem(mem, direction);
                boolean fm = prepareNextSideMove(map, direction, facingCol);
                if(firstMove == null && fm) {
                    //System.out.println("first move detected at : " + facingCol);
                    //detected = false;
                    firstMove = fm;
                }
            } else if (direction.ch == OBSTACLE) {
                //System.out.println("WARNING! Moved into obstacle " + direction);
                detected = false;
            }
        }
        /*if(detected) {
            //System.out.println("loop at : " + direction + " , mem : " + mem);
        } else {
            //System.out.println("exit at : " + direction + ", mem : " + mem);
        }*/
        return detected;
    }


    private boolean isLoopDetected(List<List<Position>> map, Map<Position, Position> mem, Position direction,
            boolean firstMove) {
        //System.out.println("move : " + direction);
        if (direction.ch == UP) {
            if (direction.row == 0) {
                return false;
            }
            int facingRow = direction.row - 1;
            if (existsDirection(map, mem, direction, firstMove)) {
                return true;
            }
            mem(mem, direction);
            prepareNextUpDownMove(map, direction, facingRow);
            return isLoopDetected(map, mem, direction, false);
        } else if (direction.ch == DOWN) {
            if (direction.row == map.get(direction.row).size() - 1) {
                return false;
            }
            int facingRow = direction.row + 1;
            if (existsDirection(map, mem, direction, firstMove)) {
                return true;
            }
            mem(mem, direction);
            prepareNextUpDownMove(map, direction, facingRow);
            return isLoopDetected(map, mem, direction, false);
        } else if (direction.ch == LEFT) {
            if (direction.col == 0) {
                return false;
            }
            int facingCol = direction.col - 1;
            if (existsDirection(map, mem, direction, firstMove)) {
                return true;
            }
            mem(mem, direction);
            prepareNextSideMove(map, direction, facingCol);
            return isLoopDetected(map, mem, direction, false);
        } else if (direction.ch == RIGHT) {
            if (direction.col == map.size() - 1) {
                return false;
            }
            int facingCol = direction.col + 1;
            if (existsDirection(map, mem, direction, firstMove)) {
                return true;
            }
            mem(mem, direction);
            prepareNextSideMove(map, direction, facingCol);
            return isLoopDetected(map, mem, direction, false);
        }
        //System.out.println("WARNING! Incomplete recursion!");
        return false;
    }

    private boolean exists(List<List<Position>> map, Position direction) {
        if (map.get(direction.row).get(direction.col).moves.contains(direction.ch)) {
            return true;
        }
        return false;
    }

    private boolean existsDirection(List<List<Position>> map, Map<Position, Position> mem, Position next,
            Boolean firstMove) {
        ////System.out.println("membered : " + mem);
        if (firstMove == null) {
            return false;
        }
        if (exists(map, next)) {
            //System.out.println("IN CACHE : " + visited.get(next) + ", of : " + next);
        }
        if (mem.get(next) != null && mem.get(next).moves.contains(next.ch)) {
            //System.out.println("IN MEM : " + mem.get(next) + ", of : " + next);
        }
        //return exists(map, next) || mem.get(next) != null && mem.get(next).moves.contains(next.ch);
        return mem.get(next) != null && mem.get(next).moves.contains(next.ch);
    }

    private void mem(Map<Position, Position> mem, Position direction) {
        Position record = new Position(direction);
        record.moves.clear();
        record.moves.add(direction.ch);
        if(!mem.containsKey(record)) {
            mem.put(record, record);
        } else {
            mem.get(record).moves.add(direction.ch);
        }
        /*mem.merge(record, record, (k, v) -> {
            v.moves.add(direction.ch);
            return v;
        });*/
    }

}
