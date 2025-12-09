package com.adventofcode.year2024.day17;

import java.util.ArrayList;
import java.util.List;

public class Day17_2 extends Day17_1 {

    public static void main(String[] args) throws Exception {
        new Day17_2().count();
        // answer 202356708354602 is correct
    }

    @Override
    protected void count() throws Exception {
        loadMap();
        printMap();
        ProgramInfo probe = map.copy();
        probe.regA = 0;//regA;
        List<Long> valuesA =List.of(0L);
        for(int outputIndex = probe.prog.length - 1; outputIndex>=0;outputIndex-=1) {
            valuesA = countRegA(valuesA, outputIndex);
            System.out.println("values : " + valuesA);
        }
        System.out.println(valuesA);
        for(Long valueA : valuesA) {
            ProgramInfo test = map.copy();
            test.regA = valueA;//regA;
            System.out.println("test run = " + test.run());
        }
        System.out.println("answer = " + valuesA.stream().mapToLong(Long::longValue).min().orElseThrow());
        printMap();
    }
    
    protected List<Long> countRegA(List<Long> regA, int outputIndex) {
        List<Long> valuesA = new ArrayList<>();
        for(Long regVal : regA) {
            for(int residue = 0;residue<= 8; residue++) {
                ProgramInfo probe = map.copy();
                long probeRegA = regVal * 8 + residue;
                probe.regA = probeRegA;
                System.out.println("regA : " + probe.regA);
                probe.run();
                //System.out.println(probe.out.toString());
                //System.out.println(probe.outArr);
                Long outVal = probe.out.isEmpty() ? null : probe.out.getFirst();
                if (outVal != null && outVal.equals(Long.valueOf(map.prog[outputIndex]))) {
                    valuesA.add(probeRegA);
                   System.out.println("outVal : " + outVal + ", vs : " + map.prog[outputIndex] + ", probeRegA : " + probeRegA);
                }
            }
        }
        return valuesA;
    }
}
