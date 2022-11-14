package de.storagesystem.api.buckets.directories;

import de.storagesystem.api.buckets.Bucket;
import de.storagesystem.api.buckets.files.CustomBucketFileRepository;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class CustomBucketDirectoryRepositoryImpl implements CustomBucketDirectoryRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<BucketDirectory> findByPath(Bucket bucket, Path path) {
        return em.createQuery("SELECT b FROM BucketDirectory b WHERE b.bucket.id = :bucketId AND b.path = :path", BucketDirectory.class)
                .setParameter("bucketId", bucket.id())
                .setParameter("path", path.toString())
                .getResultStream()
                .findFirst();
    }
    public boolean exists(Bucket bucket, String parentPath, String directory) {
        return findByPath(bucket, Path.of(parentPath).resolve(directory)).isPresent();
    }

    public BucketDirectory getDirectoryByPath(Bucket bucket, Path dirPath) {
        return findByPath(bucket, dirPath)
                .orElseThrow(() -> new StorageEntityNotFoundException("Directory not found", "Directory " + dirPath));
    }

    public BucketDirectory getDirectoryIfNotBucketElseNull(Bucket bucket, Path path) {
        if(path.toString().equals(File.separator)) return null;
        return getDirectoryByPath(bucket, path);
    }
}
