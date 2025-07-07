package de.storagesystem.api.users;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.exceptions.InvalidTokenException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author Simon Brebeck
 */
@Controller
@RequestMapping("/api/users")
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
     * @return An {@link Optional<User>} containing the user information if the user exists.
     */
    @GetMapping("/{id}")
    public @ResponseBody Optional<User> getUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable("id") Long id) throws InvalidTokenException {
        return userService.getUser(authentication, id);
    }

    /**
     * Updates a user with the user object in the request body or creates the user if the id does not exist.
     * @param user The user object to update.
     * @return A {@link ResponseEntity<ObjectNode>} object with the status code and the user id.
     */
    @PutMapping
    public ResponseEntity<ObjectNode> createOrUpdateUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestBody User user) {
        return userService.createOrUpdateUser(user);
    }

    /**
     * Deletes a user if the current sender is the user to be deleted or has the privilege to delete users.
     * @param authentication The authentication header/JWT Token.
     * @param id The id of the user to be deleted.
     * @return A {@link ResponseEntity<ObjectNode>} object with the status code and message.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ObjectNode> deleteUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @PathVariable(value = "id") Long id)
            throws InvalidTokenException {
        return userService.deleteUser(authentication, id);
    }

    /**
     * Registers a new user and returns a JWT token
     * @param user User object
     * @return {@link ResponseEntity<ObjectNode>} object with status and JWT token
     */
    @PostMapping
    public ResponseEntity<ObjectNode> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
