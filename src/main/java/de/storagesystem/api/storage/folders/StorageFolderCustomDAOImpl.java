package de.storagesystem.api.storage.folders;

import de.storagesystem.api.storage.buckets.Bucket;
import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
@Repository
public class StorageFolderCustomDAOImpl implements StorageFolderCustomDAO {

    /**
     * The {@link EntityManager} used to access the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<StorageFolder> findByPath(Bucket bucket, String path) {
        return em.createQuery("SELECT b FROM StorageFolder b WHERE b.bucket.id = :bucketId AND b.path = :path", StorageFolder.class)
                .setParameter("bucketId", bucket.getId())
                .setParameter("path", path)
                .getResultStream()
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(Bucket bucket, String parentPath, String folder) {
        String path = (parentPath != null ? parentPath : "") + "/" + folder;

        return findByPath(bucket, path).isPresent();
    }

    /**
     * {@inheritDoc}
     * @throws StorageEntityNotFoundException if the folder does not exist.
     */
    public StorageFolder getFolderByPath(Bucket bucket, String path) throws StorageEntityNotFoundException {
        return findByPath(bucket, path)
                .orElseThrow(() -> new StorageEntityNotFoundException("Directory not found", "Directory " + path));
    }

    /**
     * {@inheritDoc}
     */
    public StorageFolder getFolder(Bucket bucket, String path) {
        if(path == null || path.equalsIgnoreCase("/")) return bucket.getRootFolder();
        return getFolderByPath(bucket, path);
    }
}
