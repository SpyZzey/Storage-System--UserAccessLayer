package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

@Repository
public class StorageFolderDAOImpl implements StorageFolderCustomDAO {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<StorageFolder> findByPath(Bucket bucket, Path path) {
        return em.createQuery("SELECT b FROM StorageFolder b WHERE b.bucket.id = :bucketId AND b.path = :path", StorageFolder.class)
                .setParameter("bucketId", bucket.id())
                .setParameter("path", path.toString())
                .getResultStream()
                .findFirst();
    }
    public boolean exists(Bucket bucket, String parentPath, String directory) {
        return findByPath(bucket, Path.of(parentPath).resolve(directory)).isPresent();
    }

    public StorageFolder getDirectoryByPath(Bucket bucket, Path dirPath) {
        return findByPath(bucket, dirPath)
                .orElseThrow(() -> new StorageEntityNotFoundException("Directory not found", "Directory " + dirPath));
    }

    public StorageFolder getFolderIfNotBucketElseNull(Bucket bucket, Path path) {
        if(path.toString().equals(File.separator)) return null;
        return getDirectoryByPath(bucket, path);
    }
}
