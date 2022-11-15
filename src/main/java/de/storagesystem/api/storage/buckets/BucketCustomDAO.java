package de.storagesystem.api.storage.buckets;

import de.storagesystem.api.users.User;

import java.util.Optional;

public interface BucketCustomDAO {
    Optional<Bucket> findByName(User user, String name);
    Bucket getBucketByName(User user, String bucketName);
}
