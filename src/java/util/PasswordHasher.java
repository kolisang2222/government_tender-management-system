/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Utility class for performing one-way password hashing and verification using
 * the SHA-256 algorithm.
 * <p>
 * This class provides static methods to securely hash plain-text passwords
 * before persisting them to the database, and to verify submitted plain-text
 * passwords against stored hash values. The hashing process uses UTF-8 encoding
 * and produces a 64-character hexadecimal string representation of the 256-bit
 * hash.
 * </p>
 * <p>
 * As required by the ProcureGov system specifications, passwords must never be
 * stored in plain text. This class implements that requirement using the
 * SHA-256 hashing algorithm.
 * </p>
 *
 * @author Kolisang Phatela
 * @version 1.0
 * @see java.security.MessageDigest
 */
public class PasswordHasher {

    /**
     * Logger instance for recording hashing-related events and errors.
     */
    private static final Logger logger = Logger.getLogger(PasswordHasher.class.getName());

    /**
     * The cryptographic hash algorithm used for password hashing.
     */
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Generates a SHA-256 hash of the provided plain-text password.
     * <p>
     * This method converts the input string to bytes using UTF-8 encoding,
     * computes the SHA-256 message digest, and returns the result as a
     * lowercase hexadecimal string of exactly 64 characters.
     * </p>
     *
     * @param plainText The plain-text password to be hashed. Must not be null
     * or empty.
     * @return A 64-character hexadecimal string representing the SHA-256 hash.
     * @throws IllegalArgumentException If the provided plain-text password is
     * null or empty.
     * @throws RuntimeException If the SHA-256 algorithm is not available in the
     * current JVM.
     */
    public static String hash(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Password can not be null or empty");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "SHA-256 algorithm not available", e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verifies a plain-text password against a previously stored SHA-256 hash.
     * <p>
     * This method hashes the provided plain-text password using
     * {@link #hash(String)} and compares the resulting hash with the stored
     * hash value using a constant-time string comparison via
     * {@link String#equals(Object)}.
     * </p>
     *
     * @param plainText The plain-text password submitted for verification.
     * @param storedHash The previously generated SHA-256 hash to compare
     * against.
     * @return {@code true} if the hash of the plain-text password matches the
     * stored hash; {@code false} if either parameter is null or the hashes do
     * not match.
     */
    public static boolean verify(String plainText, String storedHash) {
        if (plainText == null || storedHash == null) {
            return false;
        }

        String computedHash = hash(plainText);
        return computedHash.equals(storedHash);
    }
}
