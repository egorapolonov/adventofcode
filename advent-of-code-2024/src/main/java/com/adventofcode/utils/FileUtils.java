package com.adventofcode.utils;

import java.io.InputStream;

public class FileUtils {

    public static InputStream resourceFileToInputStream(String fileName) {
        InputStream ioStream = FileUtils.class.getClassLoader().getResourceAsStream(fileName);
        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " not found");
        }
        return ioStream;
    }

}
