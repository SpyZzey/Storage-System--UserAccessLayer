package de.storagesystem.api.buckets.directories;

import de.storagesystem.api.buckets.BucketItemRepository;
import de.storagesystem.api.buckets.files.CustomBucketFileRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BucketDirectoryRepository extends BucketItemRepository<BucketDirectory>, CustomBucketDirectoryRepository {

}