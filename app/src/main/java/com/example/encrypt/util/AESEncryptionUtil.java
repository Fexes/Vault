package com.example.encrypt.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dongrp on 2017/7/10.
 */

public class AESEncryptionUtil {
    private static final String VIPARA = "8617953614927059";
    private static final String KEY = "tdvhlgsxcbkvfdrm";

    /**
     * 初始化 AES Cipher
     *
     * @param sKey 秘钥
     * @param cipherMode 加密/解密 模式
     * @return Cipher
     */
    private static Cipher initAESCipher(String sKey, int cipherMode) {
        Cipher cipher = null;
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
            SecretKeySpec key = new SecretKeySpec(sKey.getBytes(), "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(cipherMode, key, zeroIv);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {

            e.printStackTrace();
        }
        return cipher;
    }

    /**
     * 对文件进行AES加密（文件到文件）
     *
     * @param sourceFilePath 待加密的明文件绝对路径
     * @param destFilePath 加密后的密文件存储绝对路径
     * @return
     */
    public static boolean encryptFile(String sourceFilePath, String destFilePath) {
        FileInputStream in;
        FileOutputStream out;
        File destFile;
        File sourceFile;
        try {
            sourceFile = new File(sourceFilePath);
            destFile = new File(destFilePath);
            if (sourceFile.exists() && sourceFile.isFile()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                Cipher cipher = initAESCipher(KEY, Cipher.ENCRYPT_MODE);

                CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
                byte[] cache = new byte[1024*1024];
                int nRead = 0;
                while ((nRead = cipherInputStream.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                in.close();
                out.close();
                cipherInputStream.close();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean decryptFile(String sourceFilePath, String destFilePath) {
        FileInputStream in;
        FileOutputStream out;
        File destFile;
        File sourceFile;
        try {
            sourceFile = new File(sourceFilePath);
            destFile = new File(destFilePath);
            if (sourceFile.exists() && sourceFile.isFile()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                Cipher cipher = initAESCipher(KEY, Cipher.DECRYPT_MODE);
                CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher);
                byte[] buffer = new byte[1024*1024];
                int r;
                while ((r = in.read(buffer)) >= 0) {
                    cipherOutputStream.write(buffer, 0, r);
                }
                in.close();
                out.close();
                cipherOutputStream.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 解密私密文件 解密为 CipherInputStream
     * @param sourceFilePath 密文件绝对路路径
     * @return 文件解密输出流
     */
    public static CipherInputStream decryptFileToInputStream(String sourceFilePath) {
        FileInputStream in = null;
        File sourceFile;
        try {
            sourceFile = new File(sourceFilePath);
            if (sourceFile.exists() && sourceFile.isFile()) {
                in = new FileInputStream(sourceFile);
                Cipher cipher = initAESCipher(KEY, Cipher.DECRYPT_MODE);

                CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
                return cipherInputStream;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 将私密文件解密为 byte[] 输出
     * @param sourceFilePath
     * @return
     */
    public static byte[] decryptFileToByteArray(String sourceFilePath) {
        FileInputStream in = null;
        File sourceFile;
        try {
            sourceFile = new File(sourceFilePath);
            if (sourceFile.exists() && sourceFile.isFile()) {
                in = new FileInputStream(sourceFile);
                Cipher cipher = initAESCipher(KEY, Cipher.DECRYPT_MODE);

                CipherInputStream cipherInputStream = new CipherInputStream(in, cipher);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 1024];
                int n = 0;
                while ((n = cipherInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
                in.close();
                return out.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将私密文件解密, 解密后的文件以File的形式返回
     */
    public static File decryptFile2(String sourceFilePath, String destFilePath) {
        FileInputStream in;
        FileOutputStream out;
        File destFile = null;
        File sourceFile;
        try {
            sourceFile = new File(sourceFilePath);
            destFile = new File(destFilePath);
            if (sourceFile.exists() && sourceFile.isFile()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                Cipher cipher = initAESCipher(KEY, Cipher.DECRYPT_MODE);
                CipherOutputStream cipherOutputStream = new CipherOutputStream(out, cipher);
                byte[] buffer = new byte[1024*1024];
                int r;
                while ((r = in.read(buffer)) >= 0) {
                    cipherOutputStream.write(buffer, 0, r);
                }
                in.close();
                out.close();
                cipherOutputStream.close();
                return destFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destFile;
    }




}
