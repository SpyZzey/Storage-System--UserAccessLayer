package de.storagesystem.api.storage.buckets;

import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.users.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

/**
 * @author Simon Brebeck
 */
@Repository
public class BucketCustomDAOImpl implements BucketCustomDAO {

    /**
     * The {@link EntityManager} used to access the database.
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Bucket> findByName(User user, String name) {
        return em.createQuery("SELECT b FROM Bucket b WHERE b.creator.id = :user AND b.name = :name", Bucket.class)
                .setParameter("user", user.id())
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    public Bucket getBucketByName(User user, String bucketName) throws UserNotFoundException, StorageEntityNotFoundException {
        return findByName(user, bucketName)
                .orElseThrow(() -> new StorageEntityNotFoundException("Bucket not found", "Bucket " + bucketName));
    }
}
