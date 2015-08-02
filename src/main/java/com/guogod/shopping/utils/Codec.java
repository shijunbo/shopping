package com.guogod.shopping.utils;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class Codec {
    /**
     * Build an hexadecimal MD5 hash for a String
     * @param value The String to hash
     * @return An hexadecimal Hash
     */
    public static String hexMD5(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(value.getBytes("utf-8"));
            byte[] digest = messageDigest.digest();
            return Hex.encodeHexString(digest);
        } catch (Exception ex) {
        }
        return null;
    }
}
