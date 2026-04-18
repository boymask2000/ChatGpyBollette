package com.boymask.myapplication;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileUtil {
    private static final String ALGO = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 128;
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;

    private static final String ALGORITHM = "AES";


    public static File from(Context context, Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        File file = new File(context.getCacheDir(), "temp.pdf");

        OutputStream output = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int length;

        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.close();
        input.close();

        return file;
    }

/*    public static void saveToDisk(String val) throws Exception {
        FileOutputStream fileout=openFileOutput("mytextfile.txt", MODE_PRIVATE);
        OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
        outputWriter.write(textmsg.getText().toString());
        outputWriter.close();

        //display file saved message
        Toast.makeText(getBaseContext(), "File saved successfully!",
                Toast.LENGTH_SHORT).show();
    }*/







    public static String decrypt(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(encryptedData));
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // Key size
        return keyGen.generateKey();
    }






    public static String dec(String scadenza1,String scadenza2, String scadenza3) throws Exception {

        byte[] keyBytes = Base64.getDecoder().decode(scadenza2);
        byte[] iv = Base64.getDecoder().decode(scadenza3);
        byte[] encrypted = Base64.getDecoder().decode(scadenza1);

        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        Cipher decipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        decipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decrypted = decipher.doFinal(encrypted);



        String risultato = new String(decrypted);

        System.out.println("Decifrato: " + risultato);
        return risultato;
    }


}