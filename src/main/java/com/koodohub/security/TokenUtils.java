package com.koodohub.security;

import com.koodohub.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class TokenUtils {

    public static final String MAGIC_KEY = "obfuscate";


    public static String createToken(User userDetails) {
        StringBuilder tokenBuilder = new StringBuilder();
        tokenBuilder.append(userDetails.getUserName());
        tokenBuilder.append(":");
        tokenBuilder.append(computeSignature(userDetails));

        return tokenBuilder.toString();
    }


    public static String computeSignature(User userDetails) {

        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(userDetails.getUserName());
        signatureBuilder.append(":");
        signatureBuilder.append(userDetails.getPassword());
        signatureBuilder.append(":");
        signatureBuilder.append(TokenUtils.MAGIC_KEY);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public static String getUserNameFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }
        String[] parts = authToken.split(":");
        return parts[0];
    }


    public static boolean validateToken(String authToken, User userDetails) {

        String[] parts = authToken.split(":");
        String signature = parts[1];

        return signature.equals(TokenUtils.computeSignature(userDetails));
    }
}