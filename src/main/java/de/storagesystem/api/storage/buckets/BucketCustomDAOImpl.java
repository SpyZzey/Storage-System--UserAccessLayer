package de.storagesystem.api.storage.buckets;

import de.storagesystem.api.exceptions.StorageEntityNotFoundException;
import de.storagesystem.api.exceptions.UserNotFoundException;
import de.storagesystem.api.users.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;
import java.util.stream.Stream;

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
                .setParameter("user", user.getId())
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Bucket> findPageByUser(User user, int page, int limit) {
        return em.createQuery("SELECT b FROM Bucket b WHERE b.creator.id = :user ORDER BY b.id", Bucket.class)
                .setParameter("user", user.getId())
                .setFirstResult(page * limit)
                .setMaxResults(limit)
                .getResultStream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countBucketsByUser(User user) {
        return em.createQuery("SELECT COUNT(b) FROM Bucket b WHERE b.creator.id = :user", Long.class)
                .setParameter("user", user.getId())
                .getSingleResult();
    }


    /**
     * {@inheritDoc}
     *
     * @throws StorageEntityNotFoundException if the bucket with the given id does not exist
     * @throws UserNotFoundException if the user with the given id does not exist
     */
    public Bucket getBucketByName(User user, String bucketName) throws UserNotFoundException, StorageEntityNotFoundException {
        return findByName(user, bucketName)
                .orElseThrow(() -> new StorageEntityNotFoundException("Bucket not found", "Bucket " + bucketName));
    }
}
