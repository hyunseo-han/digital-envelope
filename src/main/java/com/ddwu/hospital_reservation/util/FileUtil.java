package com.ddwu.hospital_reservation.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static byte[] readBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static void writeBytes(String path, byte[] data) throws IOException {
        Files.write(Paths.get(path), data);
    }
}