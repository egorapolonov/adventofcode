package com.adventofcode.day10;

import java.util.LinkedHashSet;

public class Day10_2 extends Day10_1 {

    public static void main(String[] args) throws Exception {
        new Day10_2().count();
        // answer 1497 is correct
    }

    @Override
    protected Node creteRootNode(int row, int col) {
        return new RaitingNode(row, col);
    }

    protected class RaitingNode extends Node {

        RaitingNode(int row, int col) {
            super(row, col);
        }

        RaitingNode(int row, int col, LinkedHashSet<Node> visited) {
            super(row, col, visited);
        }

        @Override
        protected Node creteNewNode(int row, int col, LinkedHashSet<Node> visited) {
            return new RaitingNode(row, col, visited);
        }

        @Override
        boolean isInRange(Node node) {
            return node.row >= 0 && node.row < rows.size() && node.col >= 0 && node.col < rows.getFirst().size();
        }

    }
}
