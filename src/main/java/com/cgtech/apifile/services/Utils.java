package com.cgtech.apifile.services;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Utils {

    public static String getRandomStr(int n) {
        // Choisissez un caractère au hasard à partir de cette chaîne
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz" + "1234567890";
        StringBuilder s = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (str.length() * Math.random());
            s.append(str.charAt(index));
        }
        return s.toString();
    }

    public static String getExtension(String filename){
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1);
        }
        return extension;
    }

    public static ResponseEntity<?> fileResponse(File file, MediaType mediaType) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamResource resource = new InputStreamResource(fileInputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + file.getName());
        return ResponseEntity.status(200)
                .headers(headers)
                .contentLength(file.length())
                .contentType(mediaType)
                .body(resource);
    }
}
