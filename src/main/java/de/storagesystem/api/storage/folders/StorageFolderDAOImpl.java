package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
@Repository
public class StorageFolderDAOImpl implements StorageFolderCustomDAO {

    /**
     * The {@link EntityManager} used to access the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<StorageFolder> findByPath(Bucket bucket, Path path) {
        return em.createQuery("SELECT b FROM StorageFolder b WHERE b.bucket.id = :bucketId AND b.path = :path", StorageFolder.class)
                .setParameter("bucketId", bucket.id())
                .setParameter("path", path.toString())
                .getResultStream()
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(Bucket bucket, String parentPath, String folder) {
        return findByPath(bucket, Path.of(parentPath).resolve(folder)).isPresent();
    }

    /**
     * {@inheritDoc}
     * @throws StorageEntityNotFoundException if the folder does not exist.
     */
    public StorageFolder getFolderByPath(Bucket bucket, Path folderPath) throws StorageEntityNotFoundException {
        return findByPath(bucket, folderPath)
                .orElseThrow(() -> new StorageEntityNotFoundException("Directory not found", "Directory " + folderPath));
    }

    /**
     * {@inheritDoc}
     */
    public StorageFolder getFolderIfNotBucketElseNull(Bucket bucket, Path path) {
        if(path.toString().equals(File.separator)) return null;
        return getFolderByPath(bucket, path);
    }
}
