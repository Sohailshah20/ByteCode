package com.shah.compilerdemo.service;

import org.jvnet.hk2.annotations.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class FileOperationsService {
    public File saveFile(String code) {
        StringBuilder fullcode = formatCode(code);
        String fileName = "Hello.java";
        File newFile = new File("E:/temp/Hello.java");
        try {
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            System.out.println("*********************************" + fullcode);
            FileWriter writer = new FileWriter(newFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(fullcode.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newFile;
    }

    private static StringBuilder formatCode(String code) {
        StringBuilder fullcode = new StringBuilder("class Hello {");
        fullcode.append(code);
        fullcode.append("}");
        return fullcode;
    }

    public void deleteFile(String filePath, String filename, String compiledFileName) {
        File file1 = new File(filePath, filename);

        if (file1.exists()) {
            boolean delete = file1.delete();
            if (delete) System.out.println("***************************** FILE DELETED");
        }
        if (compiledFileName != null) {
            File file2 = new File(filePath, compiledFileName);
            System.out.println("***************************** FILE 2" + file2);
            if (file2.exists()) {
                boolean delete = file2.delete();
                if (delete) System.out.println("***************************** COMPILED FILE DELETED");
            }
        }
    }
}
