package de.storagesystem.api.storage.files;

import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.file.Path;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
@Repository
public class StorageFileCustomDAOImpl implements StorageFileCustomDAO {

    /**
     * The {@link EntityManager} used to access the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<StorageFile> findByPath(Bucket bucket, Path path) {
        return em.createQuery("SELECT b FROM StorageFile b WHERE b.bucket.id = :bucketId AND b.path = :path", StorageFile.class)
                .setParameter("bucketId", bucket.id())
                .setParameter("path", path.toString())
                .getResultStream()
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(Bucket bucket, String parentPath, String filename) {
        return findByPath(bucket, Path.of(parentPath).resolve(filename)).isPresent();
    }

    /**
     * {@inheritDoc}
     * @throws StorageEntityNotFoundException if the file does not exist.
     * @throws UserNotFoundException if the user does not exist.
     */
    public StorageFile getBucketFileByPath(Bucket bucket, Path filePath)
            throws StorageEntityNotFoundException, UserNotFoundException {
        return findByPath(bucket, filePath)
                .orElseThrow(() -> new StorageEntityNotFoundException("File not found", "File " + filePath));
    }

}