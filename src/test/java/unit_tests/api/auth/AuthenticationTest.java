package unit_tests.api.auth;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import de.storagesystem.api.auth.Authentication;
import de.storagesystem.api.auth.RSAAuthentication;
import de.storagesystem.api.exceptions.InvalidTokenException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {
    String publicKeyPath = "./src/test/keys/test_key.pub";
    String privateKeyPath = "./src/test/keys/test_key.key";

    @Test
    public void createAndReadRSAPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Authentication auth = new RSAAuthentication("TestIssuer", publicKeyPath, privateKeyPath);


        RSAPublicKey publicKey = (RSAPublicKey) auth.getPublicKey(publicKeyPath);
        RSAPrivateKey privateKey = (RSAPrivateKey) auth.getPrivateKey(privateKeyPath);

        assertDoesNotThrow(() -> auth.createKeys(publicKeyPath, privateKeyPath));
        assertNotNull(publicKey);
        assertNotNull(privateKey);

    }

    @Test
    public void createTokenAndExtractContentTest() {
        try {
            Authentication auth = new RSAAuthentication("TestIssuer", publicKeyPath, privateKeyPath);

            Map<String, String> payload = Map.of("KeyA", "ValueA", "KeyB", "ValueB");
            String token = auth.createToken(payload);
            String bearer = "Bearer " + token;
            DecodedJWT decodedToken = (DecodedJWT) auth.verifyToken(token);
            Map<String, Claim> decodedPayload = decodedToken.getClaims();

            assertEquals(token, auth.extractTokenFromBearer(bearer));
            for(String key : payload.keySet()) {
                assertEquals(payload.get(key), decodedPayload.get(key).asString());
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidTokenException e) {
            throw new RuntimeException(e);
        }
    }
}
