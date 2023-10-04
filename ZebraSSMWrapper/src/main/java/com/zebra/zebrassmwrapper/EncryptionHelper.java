package com.zebra.zebrassmwrapper;

import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptionHelper {

    public static SecretKey getRandomKey(String algorithmType)
    {
        SecureRandom rand = new SecureRandom();
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance(algorithmType);
            generator.init(128, rand);
            SecretKey secretKey = generator.generateKey();
            Log.d(Constants.TAG, "mSecretKey = "+ secretKey);  // "mSecretKey" returns the secret key.
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
