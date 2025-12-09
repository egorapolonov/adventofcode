package com.adventofcode.year2024.day9;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.adventofcode.utils.FileUtils;

public class Day9_1 {

    protected static final char SPACE = '.';
    protected List<List<Block>> rows;
    protected LinkedHashSet<Block> visited;
    protected Map<Character, List<Block>> groups;

    public static void main(String[] args) throws Exception {
        new Day9_1().count();
        // answer 6331212425418 is correct
    }

    protected void count() throws Exception {
        loadMap();
        printMap();
        System.out.println(rows);
        maintainDisk();
        System.out.println(rows);
        printMap();
        System.out.println("answer = " + calculateCheckSum());
    }

    protected void maintainDisk() {
        for (List<Block> row : rows) {
            int insertIndexOffset = 0;
            for (int forwardIndex = 0, backwardIndex = row.size() - 1;
                 forwardIndex < row.size() && backwardIndex >= 0 && forwardIndex < backwardIndex; ) {
                Block forward = row.get(forwardIndex);
                Block backward = row.get(backwardIndex);
                System.out.printf("%nbefore = [%d]x[%d], [%s] | [%s]%n", forwardIndex, backwardIndex, forward,
                        backward);
                if (forward.space > 0) {
                    if (backward.file > 0 && backward.file >= forward.space) {
                        backward.file -= forward.space;
                        backward.space += backward.space;
                        Block subBlock = new Block(backward.id, forward.space, 0);
                        forward.space = 0;
                        int subBlockIndex = subBlockIndex(forwardIndex, insertIndexOffset);
                        System.out.printf("full insert at : [%d], subBlock : [%s]%n", subBlockIndex, subBlock);
                        //----insert and keep backward index, because array will change its size----/
                        row.add(subBlockIndex, subBlock);
                        insertIndexOffset++;
                        backwardIndex++;
                        //--------------------------------------------------------------------------/
                    } else if (backward.file > 0) {
                        Block subBlock = new Block(backward.id, backward.file, 0);
                        backward.space += backward.file;
                        forward.space -= backward.file;
                        backward.file = 0;
                        int subBlockIndex = subBlockIndex(forwardIndex, insertIndexOffset);
                        System.out.printf("partial insert at : [%d], subBlock : [%s]%n", subBlockIndex, subBlock);
                        //----insert cause decrement of backward block position in comparison with array size----/
                        row.add(subBlockIndex, subBlock);
                        insertIndexOffset++;
                    } else {
                        // nothing to move
                        backwardIndex--;
                    }
                    System.out.printf("%nafter = [%d]x[%d], [%s] | [%s]%n", forwardIndex, backwardIndex, forward,
                            backward);
                    System.out.printf(
                            "%n--->offset : [%d], forwardIndex : [%d], backwardIndex :  [%d], length : [%d]%n",
                            insertIndexOffset, forwardIndex, backwardIndex, row.size());
                    System.out.printf("%n<--offset : [%d], forwardIndex : [%d], backwardIndex :  [%d], length : [%d]%n",
                            insertIndexOffset, forwardIndex, backwardIndex, row.size());
                }
                // check if we still need maintenance of this forward block
                if (forward.space == 0) {
                    insertIndexOffset = 0;
                    forwardIndex++;
                }
            }
        }

    }

    protected long calculateCheckSum() {
        long retVal = 0;
        for (List<Block> row : rows) {
            int position = 0;
            for (int index = 0; index < row.size(); index++) {
                Block block = row.get(index);
                if (block.file > 0) {
                    System.out.println("block : " + block);
                    for (int step = 0; step < block.file; step++, position++) {
                        retVal += position * block.id;
                        System.out.println("%s*%s, total = %s".formatted(position, block.id, retVal));
                    }
                }
            }
        }
        return retVal;
    }

    protected int subBlockIndex(int forwardIndex, int indexOffset) {
        return forwardIndex + 1 + indexOffset;
    }

    protected static class Block {

        long id;
        long file;
        long space;

        public Block(Block block) {
            this(block.id, block.file, block.space);
        }

        public Block(long id, long file, long space) {
            this.id = id;
            this.file = file;
            this.space = space;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Block block = (Block) o;
            return id == block.id && file == block.file && space == block.space;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, file, space);
        }

        @Override
        public String toString() {
            return "[%d][%d][%d]".formatted(id, file, space);
        }
    }

    protected void printMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("_________MAP_________\n");
        for (List<Block> row : rows) {
            for (Block pos : row) {
                for (int file = 0; file < pos.file; file++) {
                    sb.append(pos.id);
                }
                for (int space = 0; space < pos.space; space++) {
                    sb.append(SPACE);
                }
            }
            System.out.println(sb);
            sb.setLength(0);
        }
    }

    protected void loadMap() throws Exception {
        try (BufferedReader br = new BufferedReader(
                //new InputStreamReader(FileUtils.resourceFileToInputStream("day9_1_tmp.txt")))) {
            new InputStreamReader(FileUtils.resourceFileToInputStream("year2024/day9_1.txt")))) {
            this.rows = new ArrayList<>();
            this.visited = new LinkedHashSet<>();
            this.groups = new LinkedHashMap<>();
            String line = null;
            int row = 0;
            while ((line = br.readLine()) != null) {
                List<Block> blocks = new ArrayList<>(line.length() / 2);
                for (int col = 0, id = 0; col < line.length(); col += 2, id++) {
                    long file = Long.parseLong(Character.toString(line.charAt(col)));
                    long space = 0;
                    if (col + 1 < line.length()) {
                        space = Long.parseLong(Character.toString(line.charAt(col + 1)));
                    }
                    Block position = new Block(id, file, space);
                    blocks.add(position);
                }
                rows.add(blocks);
                row++;
            }

        }
    }
}
