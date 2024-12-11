package com.adventofcode.day11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Day11_2_correct extends Day11_1 {

    protected Map<CacheKey, Long> cache = new HashMap<>();

    public static void main(String[] args) throws Exception {
        new Day11_2_correct().count(75);
        // answer is correct 272673043446478
    }

    @Override
    protected long countTransformations(int blinks) {
        long total = 0;
        for (Long stone : stones) {
            total += transform(stone, 0, blinks);
            System.out.println("Done stone : " + stone);
        }
        return total;
    }

    protected long transform(Long stone, int blinkIndex, int blinks) {
        long retVal = 0;
        CacheKey key = new CacheKey(stone, blinkIndex);
        if (cache.containsKey(key)) {
            System.out.println("hit : " + key);
            return cache.get(key);
        }
        List<Long> spawn = transform(stone);
        if (blinkIndex < blinks - 1) {
            for (Long s : spawn) {
                retVal += transform(s, blinkIndex + 1, blinks);
            }
        } else {
            retVal += spawn.size();
        }
        cache.put(key, retVal);
        return retVal;
    }

    protected static class CacheKey {

        Long stone;
        int blinkIndex;

        public CacheKey(Long stone, int blinkIndex) {
            this.stone = stone;
            this.blinkIndex = blinkIndex;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            CacheKey pair = (CacheKey) o;
            return blinkIndex == pair.blinkIndex && Objects.equals(stone, pair.stone);
        }

        @Override
        public int hashCode() {
            return Objects.hash(stone, blinkIndex);
        }

        @Override
        public String toString() {
            return "CacheKey{" + "stone=" + stone + ", blinkIndex=" + blinkIndex + '}';
        }
    }

}
