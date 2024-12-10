package com.adventofcode.day9;

import java.util.List;

public class Day9_2 extends Day9_1 {

    public static void main(String[] args) throws Exception {
        new Day9_2().count();
        // answer 2858 is correct for tmp
        // answer 6363268339304 is correct
    }

    @Override
    protected void maintainDisk() {
        for (List<Block> row : rows) {
            int insertIndexOffset = 0;
            for (int forwardIndex = 0, backwardIndex = row.size() - 1;
                 forwardIndex < row.size() && backwardIndex >= 0 && backwardIndex < row.size()
                 && forwardIndex < backwardIndex; ) {
                Block forward = row.get(forwardIndex);
                Block backward = row.get(backwardIndex);
                boolean replacementFound = false;
                if (forwardIndex < backwardIndex && backward.file > 0 && backward.file <= forward.space) {
                    // squish left block and add sub-block right after
                    replacementFound = true;
                    Block subBlock = new Block(backward.id, backward.file, 0);
                    backward.space += backward.file;
                    forward.space -= backward.file;
                    backward.file = 0;
                    int subBlockIndex = subBlockIndex(forwardIndex, insertIndexOffset);
                    row.add(subBlockIndex, subBlock);
                    insertIndexOffset++;
                    // we'd like to add residue sub-block of left block right after sub-block with replacement
                    int residueBlockIndex = subBlockIndex(forwardIndex, insertIndexOffset);
                    Block residue = new Block(forward.id, 0, forward.space);
                    forward.space = 0;
                    row.add(residueBlockIndex, residue);
                    insertIndexOffset++;
                    backwardIndex = backwardIndex + insertIndexOffset; // means next block from the back
                    insertIndexOffset = 0;
                    forwardIndex = 0;
                } else {
                    forwardIndex++;
                }
                //printMap();
                if (forwardIndex == backwardIndex || forwardIndex - backwardIndex == 1 || forwardIndex > backwardIndex
                    || replacementFound) {
                    forwardIndex = 0;
                    backwardIndex--;
                }
            }
        }

    }

    @Override
    protected long calculateCheckSum() {
        long retVal = 0;
        for (List<Block> row : rows) {
            int position = 0;
            for (int index = 0; index < row.size(); index++) {
                Block block = row.get(index);
                if (block.file > 0 || block.space > 0) {
                    if (block.file > 0) {
                        for (int step = 0; step < block.file; step++) {
                            retVal += position * block.id;
                            System.out.println("%s*%s, total = %s".formatted(position, block.id, retVal));
                            position++;
                        }
                    }
                    if (block.space > 0) {
                        for (int step = 0; step < block.space; step++) {
                            position++;
                        }
                    }
                }
            }
        }
        return retVal;
    }

}
