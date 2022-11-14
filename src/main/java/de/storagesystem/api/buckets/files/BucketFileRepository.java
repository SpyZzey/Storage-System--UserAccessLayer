package de.storagesystem.api.buckets.files;

import de.storagesystem.api.buckets.BucketItemRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketFileRepository extends BucketItemRepository<BucketFile>, CustomBucketFileRepository {
}