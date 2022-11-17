package integration_tests.users;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.auth.Authentication;
import de.storagesystem.api.exceptions.InvalidTokenException;
import de.storagesystem.api.users.User;
import de.storagesystem.api.users.UserDAO;
import de.storagesystem.api.users.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@ContextConfiguration(classes = {UserService.class, Authentication.class, UserDAO.class})
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Test
    public void createReadAndDeleteUserTest() throws NoSuchAlgorithmException, InvalidTokenException {
        User user = new User("Test", "User", "test@user.de");
        ResponseEntity<ObjectNode> creationResponseEntity = service.createUser(user);
        assertEquals(creationResponseEntity.getStatusCode(), HttpStatus.OK);

        ObjectNode creationResponse = creationResponseEntity.getBody();
        assertNotNull(creationResponse);

        String token = creationResponse.get("token").asText();
        String authentication = "Bearer " + token;
        Optional<User> userFromDB = service.getUser(authentication);
        assertTrue(userFromDB.isPresent());

        User userRead = userFromDB.get();
        assertEquals(userRead, user);

        Long id = userRead.id();
        ResponseEntity<ObjectNode> deletionResponseEntity = service.deleteUser(id, authentication);
        assertEquals(deletionResponseEntity.getStatusCode(), HttpStatus.OK);

        ObjectNode deletionResponse = deletionResponseEntity.getBody();
        assertNotNull(deletionResponse);

    }
}
