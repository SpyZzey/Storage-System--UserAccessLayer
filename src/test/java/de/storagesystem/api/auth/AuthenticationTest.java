package de.storagesystem.api.auth;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    @Test
    public void createTokenAndExtractContentTest() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Map<String, String> payload = Map.of("KeyA", "ValueA", "KeyB", "ValueB");
        String token = Authentication.createToken(payload);
        String bearer = "Bearer " + token;
        DecodedJWT decodedToken =  Authentication.verifyToken(token);
        Map<String, Claim> decodedPayload = decodedToken.getClaims();

        assertEquals(token, Authentication.extractTokenFromBearer(bearer));
        for(String key : payload.keySet()) {
            assertEquals(payload.get(key), decodedPayload.get(key).asString());
        }
    }

    @Test
    public void createAndReadRSAPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPath = "./src/test/keys/test_key.pub";
        String privateKeyPath = "./src/test/keys/test_key.key";

        assertDoesNotThrow(() -> Authentication.createRSAKey(publicKeyPath, privateKeyPath));
        assertDoesNotThrow(() -> Authentication.getRSAPublicKey(publicKeyPath));
        assertDoesNotThrow(() -> Authentication.getRSAPrivateKey(privateKeyPath));
        assertNotNull(Authentication.getRSAPublicKey(publicKeyPath));
        assertNotNull(Authentication.getRSAPrivateKey(publicKeyPath));

    }
}
