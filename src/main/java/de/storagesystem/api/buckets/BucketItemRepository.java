package de.storagesystem.api.buckets;

import de.storagesystem.api.buckets.BucketItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface BucketItemRepository<T extends BucketItem> extends CrudRepository<T, Long> {

}
