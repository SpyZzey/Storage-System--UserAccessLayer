package de.storagesystem.api.users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketUserDAO extends CrudRepository<BucketUser, Long> {
}
