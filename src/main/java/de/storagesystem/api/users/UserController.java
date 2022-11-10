package de.storagesystem.api.users;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.tools.jconsole.JConsoleContext;
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
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

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
    public @ResponseBody Optional<User> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authentication) {
        return userService.getUser(authentication);
    }

    /**
     * Updates a user with the user object in the request body or creates the user if the id does not exist.
     * @param user The user object to update.
     * @return A JSON Object with the status code and the user id.
     */
    @PutMapping
    public @ResponseBody ObjectNode createOrUpdateUser(@RequestBody User user) {
        return userService.createOrUpdateUser(user);
    }

    /**
     * Deletes a user if the current sender is the user to be deleted or has the privilege to delete users.
     * @param authentication The authentication header/JWT Token.
     * @param id The id of the user to be deleted.
     * @return A JSON Object with the status code and message.
     */
    @DeleteMapping
    public @ResponseBody ObjectNode deleteUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
            @RequestParam(value = "id") Long id) {
        return userService.deleteUser(id, authentication);
    }

    /**
     * Registers a new user and returns a JWT token
     * @param user User object
     * @return JSON String with status and JWT token
     */
    @PostMapping
    public @ResponseBody ObjectNode createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
}
