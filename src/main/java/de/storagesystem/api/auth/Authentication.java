package de.storagesystem.api.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

public class Authentication {

    private static final Logger logger = LogManager.getLogger(Authentication.class);

    /**
     * Extracts the token from the bearer string.
     * @param bearer The bearer string.
     * @return String The token.
     */
    public static String extractTokenFromBearer(String bearer) {
        if(bearer == null) return null;
        if(!bearer.startsWith("Bearer ")) return null;

        return bearer.substring(7);
    }

    /**
     * Creates, signs and returns a new RSA256 JSON Web Token with the public key stored at the path given by the
     * environment variable "PUBLIC_KEY_PATH" and the private key stored at the path given by the environment variable
     * "PRIVATE_KEY_PATH". The issuer of the token is StorageSystem.
     * @return String - RSA256 JSON Web Token
     */
    public static String createToken(Map<String, ?> payload) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPublicKey publicKey = getRSAPublicKey(getPathToPublicKey());
        RSAPrivateKey privateKey = getRSAPrivateKey(getPathToPrivateKey());
        Dotenv env = Dotenv.load();

        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        return JWT.create()
                .withIssuer(env.get("TOKEN_ISSUER"))
                .withPayload(payload)
                .sign(algorithm);
    }

    /**
     * Verify a given token with the public key stored at the path given by the environment variable "PUBLIC_KEY_PATH"
     * and the private key stored at the path given by the environment variable "PRIVATE_KEY_PATH".
     * The issuer must be "StorageSystem", otherwise the token is invalid.
     * @param token The token to verify
     * @return DecodedJWT if token is valid, null if token is invalid
     * @throws IOException if key file is not found
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeySpecException if the key is invalid
     * @throws JWTVerificationException if token is invalid
     */
    public static DecodedJWT verifyToken(String token) throws
            IOException,
            NoSuchAlgorithmException,
            InvalidKeySpecException,
            JWTVerificationException {

        RSAPublicKey publicKey = getRSAPublicKey(getPathToPublicKey());
        RSAPrivateKey privateKey = getRSAPrivateKey(getPathToPrivateKey());

        Dotenv env = Dotenv.load();
        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        return JWT.require(algorithm)
                .withIssuer(env.get("TOKEN_ISSUER"))
                .build()
                .verify(token);
    }


    /**
     * Loads a RSA private key from a file
     * @param keyPath the path to the key file
     * @return RSAKey the key from the file in keyPath
     * @throws IOException if the key file could not be read
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeySpecException if the key specification in the file in keyPath is invalid
     */
    public static RSAPublicKey getRSAPublicKey(String keyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec ks =  new X509EncodedKeySpec(readKeyBytes(keyPath));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(ks);
    }

    /**
     * Loads a RSA private key from a file
     * @param keyPath the path to the key file
     * @return RSAKey the key from the file in keyPath
     * @throws IOException if the key file could not be read
     * @throws NoSuchAlgorithmException if the algorithm is not supported
     * @throws InvalidKeySpecException if the key specification in the file in keyPath is invalid
     */
    public static RSAPrivateKey getRSAPrivateKey(String keyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec ks =  new PKCS8EncodedKeySpec(readKeyBytes(keyPath));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(ks);
    }

    /**
     * Returns the bytes of a given key file
     * @param keyPath the path to the key file
     * @throws IOException if the key file could not be read
     * @return byte[] the bytes of the key file
     */
    private static byte[] readKeyBytes(String keyPath) throws IOException {
        Path path = Paths.get(keyPath);
        return Files.readAllBytes(path);
    }

    /**
     * Generates an RSA key pair and saves it to the given path.
     * @param publicKeyPath The path to save the key public key to.
     * @param privateKeyPath The path to save the key private key to.
     * @throws NoSuchAlgorithmException If the RSA algorithm is not supported.
     * @throws IOException If the key pair could not be saved.
     */
    public static void createRSAKey(String publicKeyPath, String privateKeyPath) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();

        storeKeyInFile(publicKeyPath, kp.getPublic());
        storeKeyInFile(privateKeyPath, kp.getPrivate());
    }


    /**
     * Stores a given key in a file located at the given path.
     * @param path The path where the key should be stored.
     * @param key The key to be stored.
     * @throws IOException If the key could not be stored.
     */
    private static void storeKeyInFile(String path, Key key) throws IOException {
        FileOutputStream out = new FileOutputStream(path);
        out.write(key.getEncoded());
        out.close();
    }

    /**
     * Returns the path to the public key
     * @return String path to public key
     */
    public static String getPathToPublicKey() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("PATH_PUBLIC_KEY");
    }

    /**
     * Returns the path to the private key
     * @return String path to private key
     */
    public static String getPathToPrivateKey() {
        Dotenv dotenv = Dotenv.load();
        return dotenv.get("PATH_PRIVATE_KEY");
    }

}
