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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    private final Authentication auth;

    /**
     * The {@link UserDAO} user repository
     */
    private final UserDAO userRepository;

    /**
     * Instantiates a new User service.
     * @param auth the {@link RSAAuthentication} to create and verify the JWT Token
     * @param userRepository the user repository
     */
    @Autowired
    public UserService(Authentication auth, UserDAO userRepository) {
        this.auth = auth;
        this.userRepository = userRepository;
    }

    /**
     * Returns the user information of the sender.
     * @param authentication The authentication header/JWT Token.
     * @return The user information of the sender.
     * @throws InvalidTokenException If the token is invalid.
     */
    public Optional<User> getUser(String authentication) throws InvalidTokenException {
        String token = auth.extractTokenFromBearer(authentication);
        ObjectNode response = new ObjectMapper().createObjectNode();
        try {
            DecodedJWT content = ((RSAAuthentication) auth).verifyToken(token);
            Map<String, Claim> claims = content.getClaims();
            if(claims.get("sub").asLong() != null) {
                return userRepository.findById(claims.get("sub").asLong());
            } else {
                throw new InvalidTokenException("No id in token");
            }
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            response.removeAll();
            response.put("status", "internal_error");
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
    public ResponseEntity<ObjectNode> deleteUser(Long id, String authentication) throws InvalidTokenException{
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
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            response.removeAll();
            response.put("status", "internal_error");
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
        if(userRepository.existsById(user.id())) return updateUser(user);
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
        Optional<User> repoUserOpt = userRepository.findById(user.id());
        if(repoUserOpt.isPresent()) {
            User repoUser = repoUserOpt.get();
            repoUser.update(user);
            response.put("status", "ok");
            response.put("userId", repoUser.id());
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
            long id = userRepository.save(user).id();
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
        payload.put("sub", user.id());
        payload.put("firstname", user.firstname());
        payload.put("lastname", user.lastname());

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
        Optional<User> user = getUser(authentication);
        if(user.isEmpty()) throw new UserNotFoundException("User not found");
        return user.get().id();
    }
}
