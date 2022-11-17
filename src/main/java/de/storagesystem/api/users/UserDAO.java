package de.storagesystem.api.users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Simon Brebeck
 */
@Repository
public interface UserDAO extends CrudRepository<User, Long> {

}
