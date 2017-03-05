/*
 * DigestMe.java
 *
 * Nathan Geist
 * 10152796
 * October 4th 2016
 *
 */


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestMe {
    public static byte[] execute(byte[] buffer) throws  NoSuchAlgorithmException{
        byte[] hashVal = null;
        MessageDigest toDigest = MessageDigest.getInstance("MD5");

        hashVal = toDigest.digest(buffer);
        return hashVal;

    }

}