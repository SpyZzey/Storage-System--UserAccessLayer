package de.storagesystem.api.users;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.tools.jconsole.JConsoleContext;
import de.storagesystem.api.auth.Authentication;
import de.storagesystem.api.exceptions.InvalidTokenException;
import de.storagesystem.api.storage.folders.StorageFolderController;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
@Controller
@RequestMapping("/api/user")
public class UserController {

    /**
     * The {@link Logger} for this class
     */
    private static final Logger logger = LogManager.getLogger(UserService.class);

    /**
     * The {@link UserService} to access the users
     */
    private final UserService userService;

    /**
     * Instantiates a new User controller.
     * @param userService the user service to access the users
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Returns the user information of the sender.
     * @param authentication The authentication header/JWT Token.
     * @return
     */
    @GetMapping
    public @ResponseBody Optional<User> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authentication)
            throws InvalidTokenException {
        return userService.getUser(authentication);
    }

    /**
     * Updates a user with the user object in the request body or creates the user if the id does not exist.
     * @param user The user object to update.
     * @return A JSON Object with the status code and the user id.
     */
    @PutMapping
    public @ResponseBody ResponseEntity<ObjectNode> createOrUpdateUser(@RequestBody User user) {
        return userService.createOrUpdateUser(user);
    }

    /**
     * Deletes a user if the current sender is the user to be deleted or has the privilege to delete users.
     * @param authentication The authentication header/JWT Token.
     * @param id The id of the user to be deleted.
     * @return A JSON Object with the status code and message.
     */
    @DeleteMapping
    public @ResponseBody ResponseEntity<ObjectNode> deleteUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam(value = "id") Long id)
            throws InvalidTokenException {
        return userService.deleteUser(id, authentication);
    }

    /**
     * Registers a new user and returns a JWT token
     * @param user User object
     * @return JSON String with status and JWT token
     */
    @PostMapping
    public @ResponseBody ResponseEntity<ObjectNode> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
