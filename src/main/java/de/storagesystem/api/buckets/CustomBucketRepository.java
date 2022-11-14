package de.storagesystem.api.buckets;

import de.storagesystem.api.buckets.directories.BucketDirectory;
import de.storagesystem.api.users.User;

import java.util.Optional;

public interface CustomBucketRepository {
    Optional<Bucket> findByName(User user, String name);
    Bucket getBucketByName(User user, String bucketName);
}
