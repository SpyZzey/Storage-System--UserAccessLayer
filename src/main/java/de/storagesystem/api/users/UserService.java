package de.storagesystem.api.users;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.auth.Authentication;
import de.storagesystem.api.auth.RSAAuthentication;
import de.storagesystem.api.exceptions.InvalidTokenException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.properties.StorageServerConfigProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
@Service
public class UserService {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(UserService.class);

    /**
     * The {@link Authentication} to create and verify the JWT Token
     */
    private Authentication auth;

    /**
     * The {@link UserDAO} user repository
     */
    private final UserDAO userRepository;

    private final StorageServerConfigProperty storageServerConfigProperties;

    /**
     * Instantiates a new User service.
     * @param userRepository the user repository
     */
    @Autowired
    public UserService(
            StorageServerConfigProperty storageServerConfigProperties,
            UserDAO userRepository) {
        this.userRepository = userRepository;
        this.storageServerConfigProperties = storageServerConfigProperties;
    }

    @PostConstruct
    public void init() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String issuer = storageServerConfigProperties.getStorage().getIssuer();
        String publicKeyPath = storageServerConfigProperties.getStorage().getPublicKey();
        String privateKeyPath = storageServerConfigProperties.getStorage().getPrivateKey();
        this.auth = new RSAAuthentication(issuer, publicKeyPath, privateKeyPath);
    }

    /**
     * Returns the user information of the sender.
     * @param authentication The authentication header/JWT Token.
     * @return The user information of the sender.
     * @throws InvalidTokenException If the token is invalid.
     */
    public Optional<User> getUser(String authentication, Long id) throws InvalidTokenException {
        String token = auth.extractTokenFromBearer(authentication);
        ObjectNode response = new ObjectMapper().createObjectNode();
        try {
            DecodedJWT content = ((RSAAuthentication) auth).verifyToken(token);
            Map<String, Claim> claims = content.getClaims();
            Long userId = claims.get("sub").asLong();
            if(userId != null && userId.equals(id)) {
                return userRepository.findById(userId);
            } else {
                throw new InvalidTokenException("No permission to access this user");
            }
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            response.removeAll();
            response.put("status", "error");
        }

        return Optional.empty();
    }


    /**
     * Deletes a user if the current sender is the user to be deleted or has the privilege to delete users.
     * @param id The id of the user to be deleted.
     * @param authentication The authentication header/JWT Token.
     * @return A JSON Object with the status code and message.
     */
    public ResponseEntity<ObjectNode> deleteUser(String authentication, Long id) throws InvalidTokenException{
        logger.info("Delete user: " + id);
        String token = auth.extractTokenFromBearer(authentication);
        ObjectNode response = new ObjectMapper().createObjectNode();
        try {
            DecodedJWT content = ((RSAAuthentication) auth).verifyToken(token);
            Map<String, Claim> claims = content.getClaims();
            if(Objects.equals(claims.get("sub").asLong(), id)) {
                userRepository.deleteById(id);
                response.put("status", "ok");
                response.put("message", "User deleted");
            } else {
                response.put("status", "error");
                response.put("message", "You are not allowed to delete users.");
            }
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            response.removeAll();
            response.put("status", "error");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Creates a user if the user does not exist or updates the user if the user already exists.
     * @param user The user object to create or update.
     * @return A JSON Object with the status code and the user id.
     */
    public ResponseEntity<ObjectNode> createOrUpdateUser(User user) {
        logger.info("Creating or updating user: " + user.toString());
        if(userRepository.existsById(user.getId())) return updateUser(user);
        return createUser(user);
    }

    /**
     * Updates a user with the content of user object.
     * @param user User object to update.
     * @return JSON Object with status code (and user id on success).
     */
    public ResponseEntity<ObjectNode> updateUser(User user) {
        logger.info("Updating user: " + user.toString());
        ObjectNode response = new ObjectMapper().createObjectNode();
        Optional<User> repoUserOpt = userRepository.findById(user.getId());
        if(repoUserOpt.isPresent()) {
            User repoUser = repoUserOpt.get();
            repoUser.update(user);
            response.put("status", "ok");
            response.put("userId", repoUser.getId());
        } else {
            response.put("status", "error");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Creates a new user with the content of the user object.
     * @param user User object to create.
     * @return JSON Object with status code (and user id and token on success).
     */
    public ResponseEntity<ObjectNode> createUser(User user) {
        logger.info("Creating user: " + user.toString());
        ObjectNode response = new ObjectMapper().createObjectNode();
        try {
            long id = userRepository.save(user).getId();
            String token = createTokenForUser(user);
            response.put("status", "ok");
            response.put("userId", id);
            response.put("token", token);
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            e.printStackTrace();
            response.removeAll();
            response.put("status", "error");
        }
        return ResponseEntity.ok(response);
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
        logger.info("Creating token for user: " + user.toString());
        Map<String, Object> payload = generateUserPayload(user);
        return auth.createToken(payload);
    }

    /**
     * Generates a payload for a given user
     * @param user User object
     * @return {@code Map<String, Object>} with the payload
     */
    private Map<String, Object> generateUserPayload(User user) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", user.getId());
        payload.put("firstname", user.getFirstname());
        payload.put("lastname", user.getLastname());

        return payload;
    }

    /**
     * Returns the id of the user with the authentication token.
     * @param authentication The authentication header/JWT Token.
     * @return The id of the user.
     * @throws InvalidTokenException If the token is invalid.
     * @throws JWTVerificationException If the token cannot be verified.
     */
    public Long getUserId(String authentication) throws InvalidTokenException, JWTVerificationException {
        String token = auth.extractTokenFromBearer(authentication);
        DecodedJWT content = ((RSAAuthentication) auth).verifyToken(token);
        Map<String, Claim> claims = content.getClaims();
        Long userId = claims.get("sub").asLong();
        Optional<User> user;
        if(userId != null) {
            user = userRepository.findById(userId);
        } else {
            throw new InvalidTokenException("No id in token");
        }

        if(user.isEmpty()) throw new UserNotFoundException("User not found");
        return user.get().getId();
    }
}
