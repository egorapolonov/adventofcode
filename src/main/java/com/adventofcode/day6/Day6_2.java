package com.adventofcode.day6;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day6_2 extends Day6_1 {

    private Position testObstacle;

    public static void main(String[] args) throws Exception {
        new Day6_2().count();
    }

    public void count() throws Exception {
        // 6 correct answer for tmp
        // 5461 is too high
        // 1836 correct answer
        loadMap();
        printMap();
        Position start = new Position(direction);
        move(direction);
        System.out.println("answer : " + (visited.size()));
        printMap();
        predictRoute(start);
    }

    private void predictRoute(Position start) throws Exception {
        int counter = 0;
        int index = 0;
        Position direction = new Position(start);
        for (Position point : visited) {
            if (isNotStartPoint(start, point, index)) {
                char tmp = getValAndSetObstacle(point);
                testObstacle = new Position(point); // do not validate anything until face test obstacle
                Map<Position, Position> mem = new HashMap<>();
                if (isLoopDetectedCycle(rows, mem, direction, null)) {
                    counter++;
                    System.out.println("O = " + point);
                } else {
                    System.out.println("(/) = " + point);
                }
                resetObstacle(point, tmp);
                direction = resetToStart(start);
            }
            index++;
        }
        System.out.println("potential obstructions answer : " + counter);
    }

    // TODO: do not try this way. Because in cache there are possible directions which lead to exit. Waste of time
    private boolean existsInCache(List<List<Position>> map, Position direction) {
        if (map.get(direction.row).get(direction.col).moves.contains(direction.ch)) {
            return true;
        }
        return false;
    }

    private boolean isLoopDetectedCycle(List<List<Position>> map, Map<Position, Position> mem, Position direction,
            Boolean testObstacleDetected) {
        Boolean detected = null;
        while (detected == null) {
            if (direction.ch == UP) {
                if (direction.row == 0) {
                    detected = false;
                    break;
                }
                int facingRow = direction.row - 1;
                if (existsDirection(mem, direction, testObstacleDetected)) {
                    detected = true;
                    break;
                }
                mem(mem, direction);
                boolean fm = prepareNextUpDownMove(map, direction, facingRow);
                testObstacleDetected = testObstacleDetected(testObstacleDetected, fm);
            } else if (direction.ch == DOWN) {
                if (direction.row == map.get(direction.row).size() - 1) {
                    detected = false;
                    break;
                }
                int facingRow = direction.row + 1;
                if (existsDirection(mem, direction, testObstacleDetected)) {
                    detected = true;
                    break;
                }
                mem(mem, direction);
                boolean fm = prepareNextUpDownMove(map, direction, facingRow);
                testObstacleDetected = testObstacleDetected(testObstacleDetected, fm);
            } else if (direction.ch == LEFT) {
                if (direction.col == 0) {
                    detected = false;
                    break;
                }
                int facingCol = direction.col - 1;
                if (existsDirection(mem, direction, testObstacleDetected)) {
                    return true;
                }
                mem(mem, direction);
                boolean fm = prepareNextSideMove(map, direction, facingCol);
                testObstacleDetected = testObstacleDetected(testObstacleDetected, fm);
            } else if (direction.ch == RIGHT) {
                if (direction.col == map.size() - 1) {
                    detected = false;
                    break;
                }
                int facingCol = direction.col + 1;
                if (existsDirection(mem, direction, testObstacleDetected)) {
                    detected = true;
                    break;
                }
                mem(mem, direction);
                boolean fm = prepareNextSideMove(map, direction, facingCol);
                testObstacleDetected = testObstacleDetected(testObstacleDetected, fm);
            } else if (direction.ch == OBSTACLE) {
                //System.out.println("WARNING! Moved into obstacle " + direction);
                detected = false;
            }
        }
        return detected;
    }

    @Override
    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (List<Position> row : rows) {
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
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    private char getValAndSetObstacle(Position point) {
        char tmp = rows.get(point.row).get(point.col).ch;
        rows.get(point.row).get(point.col).ch = OBSTACLE;
        point.ch = OBSTACLE;
        return tmp;
    }

    private void resetObstacle(Position point, char tmp) {
        rows.get(point.row).get(point.col).ch = tmp;
    }

    private static Position resetToStart(Position start) {
        Position direction;
        direction = new Position(start);
        return direction;
    }

    private static boolean isNotStartPoint(Position start, Position point, int index) {
        return index != 0 && !point.equals(start);
    }

    private boolean prepareNextSideMove(List<List<Position>> map, Position direction, int facingCol) {
        Position facing = map.get(direction.row).get(facingCol);
        if (isFacingObstacle(facing)) {
            direction.ch = turn(direction.ch);
            return facing.equals(testObstacle);
        } else {
            direction.col = facingCol;
        }
        return false;
    }

    private boolean prepareNextUpDownMove(List<List<Position>> map, Position direction, int facingRow) {
        Position facing = map.get(facingRow).get(direction.col);
        if (isFacingObstacle(facing)) {
            direction.ch = turn(direction.ch);
            return facing.equals(testObstacle);
        } else {
            direction.row = facingRow;
        }
        return false;
    }

    private static Boolean testObstacleDetected(Boolean firstMove, boolean fm) {
        if (firstMove == null && fm) {
            firstMove = fm;
        }
        return firstMove;
    }

    private boolean existsDirection(Map<Position, Position> mem, Position next, Boolean firstMove) {
        if (firstMove == null) {
            return false;
        }
        return existsInMem(mem, next);
    }

    private static boolean existsInMem(Map<Position, Position> mem, Position next) {
        return mem.get(next) != null && mem.get(next).moves.contains(next.ch);
    }

    private void mem(Map<Position, Position> mem, Position direction) {
        Position record = new Position(direction);
        record.moves.clear();
        record.moves.add(direction.ch);
        if (!mem.containsKey(record)) {
            mem.put(record, record);
        } else {
            mem.get(record).moves.add(direction.ch);
        }
    }

    protected boolean isFacingObstacle(Position front) {
        return OBSTACLE == front.ch;
    }

    @Override
    protected void prepareNextUpDownMove(Position direction, int facingRow) {
        Position facing = rows.get(facingRow).get(direction.col);
        if (isFacingObstacle(facing)) {
            direction.ch = turn(direction.ch);
        } else {
            direction.row = facingRow;
        }
    }

    @Override
    protected char turn(char direction) {
        char turn = switch (direction) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
            default -> direction;
        };
        return turn;
    }

}
