package net.trajano.openidconnect.jaspic.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class to decrypt and encrypt data. It is a compressing stream.
 */
public final class CipherUtil {

    /**
     * Cipher algorithm to use. "AES"
     */
    private static final String CIPHER_ALGORITHM = "AES";

    /**
     * Creates a decryption stream. It is a compressed then encrypted stream.
     *
     * @param inputStream
     *            source stream
     * @param secret
     *            secret for the cipher
     * @return the stream
     * @throws GeneralSecurityException
     */
    public static InputStream buildDecryptStream(final InputStream inputStream,
            final SecretKey secret) throws GeneralSecurityException {

        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secret);
        return new InflaterInputStream(new CipherInputStream(inputStream, cipher), new Inflater(false));
    }

    /**
     * Creates a encryption stream. It is a compressed then encrypted stream.
     *
     * @param outputStream
     *            source stream
     * @param secret
     *            secret for the cipher
     * @return the stream
     * @throws GeneralSecurityException
     */
    public static OutputStream buildEncryptStream(final OutputStream outputStream,
            final SecretKey secret) throws GeneralSecurityException {

        final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        return new DeflaterOutputStream(new CipherOutputStream(outputStream, cipher), new Deflater(9, false));
    }

    /**
     * Build secret key.
     *
     * @param clientId
     *            client ID (used for creating the {@link SecretKey})
     * @param clientSecret
     *            client secret (used for creating the {@link SecretKey})
     * @return a secret key
     * @throws GeneralSecurityException
     *             crypto API problem
     */
    public static SecretKey buildSecretKey(final String clientId,
            final String clientSecret) throws GeneralSecurityException {

        final PBEKeySpec pbeSpec = new PBEKeySpec(clientSecret.toCharArray(), clientId.getBytes(), 42, 128);

        final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return new SecretKeySpec(factory.generateSecret(pbeSpec)
                .getEncoded(), CIPHER_ALGORITHM);
    }

    /**
     * Decrypts a byte array.
     *
     * @param cipherText
     *            cipher text
     * @param secret
     *            secret for the cipher
     * @return clear text
     * @throws GeneralSecurityException
     */
    public static byte[] decrypt(final byte[] cipherText,
            final SecretKey secret) throws GeneralSecurityException {

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(2000);
            try (final InputStream zis = CipherUtil.buildDecryptStream(new ByteArrayInputStream(cipherText), secret)) {

                int ch = zis.read();
                while (ch != -1) {
                    baos.write(ch);
                    ch = zis.read();
                }
            }
            baos.close();
            return baos.toByteArray();
        } catch (final IOException e) {
            throw new GeneralSecurityException(e);
        }
    }

    /**
     * Encrypts a byte array.
     *
     * @param clearText
     *            clear text
     * @param secret
     *            secret for the cipher
     * @return cipher text
     * @throws GeneralSecurityException
     */
    public static byte[] encrypt(final byte[] clearText,
            final SecretKey secret) throws GeneralSecurityException {

        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(2000);
            try (final OutputStream zos = CipherUtil.buildEncryptStream(baos, secret)) {
                zos.write(clearText);
                zos.close();
            }
            baos.close();
            return baos.toByteArray();
        } catch (final IOException e) {
            throw new GeneralSecurityException(e);
        }
    }

    /**
     * Prevent instantiation of utility class.
     */
    private CipherUtil() {
    }

}
