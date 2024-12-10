package com.adventofcode.day10;

import java.util.List;

public class Day10_2 extends Day10_1 {

    public static void main(String[] args) throws Exception {
        new Day10_2().count();
        // answer 1497 is correct
    }

    @Override
    protected int sumTrailHeads() {
        int counter = 0;
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<Integer> row = rows.get(rowIndex);
            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                if (row.get(colIndex) != null && row.get(colIndex) == 0) {
                    RaitingNode node = new RaitingNode(rowIndex, colIndex);
                    //System.out.println(node);
                    counter += node.trailheads;
                    System.out.printf("node trailheads : [%dx%d] = %d%n", node.row, node.col, node.trailheads);
                }
            }
        }
        return counter;
    }

    protected class RaitingNode extends Node {

        RaitingNode(int row, int col) {
            super(row, col);
        }

        @Override
        void initNodes(Node node) {
            this.visited.clear();
            super.initNodes(node);
            this.visited.clear();
        }
    }
}
