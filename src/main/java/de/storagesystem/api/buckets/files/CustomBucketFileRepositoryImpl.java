package de.storagesystem.api.buckets.files;

import de.storagesystem.api.buckets.Bucket;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.file.Path;
import java.util.Optional;

public class CustomBucketFileRepositoryImpl implements CustomBucketFileRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<BucketFile> findByPath(Bucket bucket, Path path) {
        return em.createQuery("SELECT b FROM BucketFile b WHERE b.bucket.id = :bucketId AND b.path = :path", BucketFile.class)
                .setParameter("bucketId", bucket.id())
                .setParameter("path", path.toString())
                .getResultStream()
                .findFirst();
    }

    public boolean exists(Bucket bucket, String parentPath, String filename) {
        return findByPath(bucket, Path.of(parentPath).resolve(filename)).isPresent();
    }

    public BucketFile getBucketFileByPath(Bucket bucket, Path filePath)
            throws StorageEntityNotFoundException, UserNotFoundException {
        return findByPath(bucket, filePath)
                .orElseThrow(() -> new StorageEntityNotFoundException("File not found", "File " + filePath));
    }

}