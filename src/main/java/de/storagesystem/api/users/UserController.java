package de.storagesystem.api.users;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.auth.Authentication;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get")
    public @ResponseBody Optional<User> getUser(@RequestParam(value = "id") Long id) {
        return userRepository.findById(id);
    }

    @DeleteMapping("/delete")
    public @ResponseBody ObjectNode deleteUser(@RequestBody User user) {
        // TODO: Bearer Token for verification
        String token = "";
        ObjectNode response = new ObjectMapper().createObjectNode();
        try {
            DecodedJWT content = Authentication.verifyToken(token);
            Map<String, Claim> claims = content.getClaims();

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            response.put("status", "error");
            //TODO: LOGGING
        }

        return response;
    }

    /**
     * Registers a new user and returns a JWT token
     * @param user User object
     * @return JSON String with status and JWT token
     */
    @PostMapping("/register")
    public @ResponseBody ObjectNode registerUser(@RequestBody User user) {
        userRepository.save(user);

        ObjectNode response = new ObjectMapper().createObjectNode();
        try {
            String token = createTokenForUser(user);
            response.put("status", "ok");
            response.put("token", token);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            response.put("status", "error");
            //TODO: LOGGING
        }
        return response;
    }

    /**
     * Creates a JWT token for a given user
     * @param user User object
     * @return JWT token
     * @throws NoSuchAlgorithmException If the algorithm is not supported
     * @throws IOException If the key cannot be read
     * @throws InvalidKeySpecException If the key is invalid
     */
    private String createTokenForUser(User user) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        String publicKeyPath = Authentication.getPathToPublicKey();
        String privateKeyPath = Authentication.getPathToPrivateKey();

        Authentication.createRSAKey(publicKeyPath, privateKeyPath);
        Map<String, Object> payload = generateUserPayload(user);
        return Authentication.createToken(payload);
    }

    /**
     * Generates a payload for a given user
     * @param user User object
     * @return {@code Map<String, Object>} with the payload
     */
    private Map<String, Object> generateUserPayload(User user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", user.id());
        payload.put("firstname", user.firstname());
        payload.put("lastname", user.lastname());

        return payload;
    }

}
