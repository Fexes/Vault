package com.example.encrypt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by ruipan.dong on 2017/9/13.
 */

public class XorEncryptionUtil {

    private static int REVERSE_LENGTH = 5;


    public static boolean encrypt(String sourceFilePath, String destFilePath) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(sourceFilePath);
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            long totalLen = raf.length();

            if (totalLen < REVERSE_LENGTH)
                len = (int) totalLen;

            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
            byte tmp;
            for (int i = 0; i < len; ++i) {
                byte rawByte = buffer.get(i);
                tmp = (byte) (rawByte ^ i);
                buffer.put(i, tmp);
            }
            buffer.force();
            buffer.clear();
            channel.close();
            raf.close();

            if (null != destFilePath) {
                return copyFile(sourceFilePath, destFilePath);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean copyFile(String srcFilePath, String destFilePath) {

        long size = 0;
        long length = -1;
        File srcFile = new File(srcFilePath);
        File destFile = new File(destFilePath);
        File destDir = new File(destFile.getParent());
        try {
            RandomAccessFile raf = new RandomAccessFile(srcFile, "rw");
            length = raf.length();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!srcFile.exists()) {
            return false;
        } else if (!destDir.exists()) {
            destDir.mkdirs();
        } else {
            try {
                FileChannel fcin = new FileInputStream(srcFile).getChannel();
                FileChannel fcout = new FileOutputStream(new File(destDir, srcFile.getName())).getChannel();
                size = fcin.size();
                fcin.transferTo(0, fcin.size(), fcout);
                fcin.close();
                fcout.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        return length == size;
    }


}
