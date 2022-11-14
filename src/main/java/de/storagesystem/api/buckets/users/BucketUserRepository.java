package de.storagesystem.api.buckets.users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketUserRepository extends CrudRepository<BucketUser, Long> {
}
