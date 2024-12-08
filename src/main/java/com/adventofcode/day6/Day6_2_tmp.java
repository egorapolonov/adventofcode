/*
package com.adventofcode.day6;

public class Day6_2_tmp {

    private boolean isLoopIfFacingObstacleHere(Day6_2.Position direction, boolean prediction) {
        System.out.println("move : " + direction.ch);
        if (direction.ch == OBSTACLE) {
            return false;
        }
        if (direction.ch == UP) {
            if (direction.row == 0) {
                return false;
            }
            int facingRow = direction.row - 1;
            if (!prediction) {
                turnHere(direction);
                prediction = true;
                prepareNextUpDownMove(direction, facingRow);
                if (exists(direction))
                    return true;
                return isLoopIfFacingObstacleHere(direction, prediction);
            } else {
                Day6_2.Position facing = map.get(facingRow).get(direction.col);
                if (facing.moves.contains(direction.ch)) {
                    return true;
                }
                if (facing.ch == OBSTACLE) {
                    return false;
                }
                prepareNextUpDownMove(direction, facingRow);
                if (exists(direction))
                    return true;
                return isLoopIfFacingObstacleHere(direction, prediction);
            }
        } else if (direction.ch == DOWN) {
            if (direction.row == map.get(direction.row).size() - 1) {
                return false;
            }
            if (exists(direction))
                return true;
            int facingRow = direction.row + 1;
            if (!prediction) {
                turnHere(direction);
                prediction = true;
                Day6_2.Position facing = map.get(facingRow).get(direction.col);
                if (facing.moves.contains(direction.ch)) {
                    return true;
                }
                if (facing.ch == OBSTACLE) {
                    return false;
                }
                prepareNextUpDownMove(direction, facingRow);
                if (exists(direction))
                    return true;
                return isLoopIfFacingObstacleHere(direction, prediction);
            } else {
                Day6_2.Position facing = map.get(facingRow).get(direction.col);
                if (facing.moves.contains(direction.ch)) {
                    return true;
                }
                if (facing.ch == OBSTACLE) {
                    return false;
                }
                prepareNextUpDownMove(direction, facingRow);
                if (exists(direction))
                    return true;
                return isLoopIfFacingObstacleHere(direction, prediction);
            }
        } else if (direction.ch == LEFT) {
            if (direction.col == 0) {
                return false;
            }
            if (exists(direction))
                return true;
            int facingCol = direction.col - 1;
            if (!prediction) {
                turnHere(direction);
                prediction = true;
                Day6_2.Position facing = map.get(direction.row).get(facingCol);
                if (facing.moves.contains(direction.ch)) {
                    return true;
                }
                if (facing.ch == OBSTACLE) {
                    return false;
                }
                prepareNextSideMove(direction, facingCol);
                if (exists(direction))
                    return true;
                return isLoopIfFacingObstacleHere(direction, prediction);
            } else {
                prepareNextSideMove(direction, facingCol);
                if (exists(direction))
                    return true;
                return isLoopIfFacingObstacleHere(direction, prediction);
            }
        } else if (direction.ch == RIGHT) {
            if (direction.col == map.size() - 1) {
                return false;
            }
            if (exists(direction))
                return true;
            int facingCol = direction.col + 1;
            if (!prediction) {
                turnHere(direction);
                prediction = true;
                Day6_2.Position facing = map.get(direction.row).get(facingCol);
                if (facing.moves.contains(direction.ch)) {
                    return true;
                }
                if (facing.ch == OBSTACLE) {
                    return false;
                }
                prepareNextSideMove(direction, facingCol);
                if (exists(direction))
                    return true;
                return isLoopIfFacingObstacleHere(direction, prediction);
            } else {
                Day6_2.Position facing = map.get(direction.row).get(facingCol);
                if (facing.moves.contains(direction.ch)) {
                    return true;
                }
                if (facing.ch == OBSTACLE) {
                    return false;
                }
                prepareNextSideMove(direction, facingCol);
                if (exists(direction))
                    return true;
                return isLoopIfFacingObstacleHere(direction, prediction);
            }
        }
        System.out.println("WARNING! Incomplete recursion!");
        return false;
    }

    /*private boolean isLoopIfFacingObstacleHere(Position direction, Position next, boolean prediction) {
        System.out.println("move : " + next.ch);
        if(next.ch == OBSTACLE) {
            return false;
        }
        if (next.ch == UP) {
            if (next.row == 0) {
                return false;
            }
            int facingRow = next.row - 1;
            if(!prediction) {
                turnHere(next);
                prediction = true;
                next = prepareNextUpDownMove(next, facingRow);
                return isLoopIfFacingObstacleHere(direction, next, prediction);
            } else {
                Position facing = rows.get(facingRow).get(next.col);
                if(facing.moves.contains(next.ch)) {
                    return true;
                }
                if(facing.ch == OBSTACLE) {
                    return false;
                }
                next = prepareNextUpDownMove(next, facingRow);
                return isLoopIfFacingObstacleHere(direction, next, prediction);
            }
        } else if (next.ch == DOWN) {
            if (next.row == rows.get(next.row).size() - 1) {
                return false;
            }
            if(!direction.equals(next) && next.moves.contains(direction.ch)) {
                return true;
            }
            int facingRow = direction.row + 1;
            if(!prediction) {
                turnHere(next);
                prediction = true;
                Position facing = rows.get(facingRow).get(next.col);
                if(facing.moves.contains(next.ch)) {
                    return true;
                }
                if(facing.ch == OBSTACLE) {
                    return false;
                }
                next = prepareNextUpDownMove(next, facingRow);
                return isLoopIfFacingObstacleHere(direction, next, prediction);
            } else {
                Position facing = rows.get(facingRow).get(next.col);
                if(facing.moves.contains(next.ch)) {
                    return true;
                }
                if(facing.ch == OBSTACLE) {
                    return false;
                }
                next = prepareNextUpDownMove(next, facingRow);
                return isLoopIfFacingObstacleHere(direction, next, prediction);
            }
        } else if (next.ch == LEFT) {
            if (next.col == 0) {
                return false;
            }
            if(!direction.equals(next) && next.moves.contains(direction.ch)) {
                return true;
            }
            int facingCol = next.col - 1;
            if(!prediction) {
                turnHere(next);
                prediction = true;
                Position facing = rows.get(next.row).get(facingCol);
                if(facing.moves.contains(next.ch)) {
                    return true;
                }
                if(facing.ch == OBSTACLE) {
                    return false;
                }
                next = prepareNextSideMove(next, facingCol);
                return isLoopIfFacingObstacleHere(direction, next, prediction);
            } else {
                next = prepareNextSideMove(next, facingCol);
                return isLoopIfFacingObstacleHere(direction, next, prediction);
            }
        } else if (next.ch == RIGHT) {
            if (next.col == rows.size() - 1) {
                return false;
            }
            if(!direction.equals(next) && next.moves.contains(direction.ch)) {
                return true;
            }
            int facingCol = next.col + 1;
            if(!prediction) {
                turnHere(next);
                prediction = true;
                Position facing = rows.get(next.row).get(facingCol);
                if(facing.moves.contains(next.ch)) {
                    return true;
                }
                if(facing.ch == OBSTACLE) {
                    return false;
                }
                next = prepareNextSideMove(next, facingCol);
                return isLoopIfFacingObstacleHere(direction, next, prediction);
            } else {
                Position facing = rows.get(next.row).get(facingCol);
                if(facing.moves.contains(next.ch)) {
                    return true;
                }
                if(facing.ch == OBSTACLE) {
                    return false;
                }
                next = prepareNextSideMove(next, facingCol);
                return isLoopIfFacingObstacleHere(direction, next, prediction);
            }
        }
        System.out.println("WARNING! Incomplete recursion!");
        return false;
    }
}

*/