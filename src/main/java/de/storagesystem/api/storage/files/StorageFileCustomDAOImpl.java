package de.storagesystem.api.storage.files;

import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    public Optional<StorageFile> findByPath(Bucket bucket, String path) {
        return em.createQuery("SELECT b FROM StorageFile b WHERE b.bucket.id = :bucketId AND b.path = :path", StorageFile.class)
                .setParameter("bucketId", bucket.getId())
                .setParameter("path", path)
                .getResultStream()
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(Bucket bucket, String parentPath, String filename) {
        return findByPath(bucket, parentPath + "/" + filename).isPresent();
    }

    /**
     * {@inheritDoc}
     * @throws StorageEntityNotFoundException if the file does not exist.
     * @throws UserNotFoundException if the user does not exist.
     */
    public StorageFile getBucketFileByPath(Bucket bucket, String path)
            throws StorageEntityNotFoundException, UserNotFoundException {
        return findByPath(bucket, path)
                .orElseThrow(() -> new StorageEntityNotFoundException("File not found", "File " + path));
    }

}