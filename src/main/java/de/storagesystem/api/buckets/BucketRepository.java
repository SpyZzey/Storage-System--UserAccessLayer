package de.storagesystem.api.buckets;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketRepository extends CrudRepository<Bucket, Long>, CustomBucketRepository {
}