package de.storagesystem.api.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import de.storagesystem.api.storage.StorageItem;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Simon Brebeck on 15.12.2022
 */
public class Util {

    public static long calculateTotalPages(long totalItems, int limit) {
        return (long) Math.ceil((double) totalItems / limit);
    }

    public static int calculateItemCount(long totalPages, long totalItems, int page, int limit) {
        if(totalItems == 0) return 0;
        if(page == totalPages - 1) return (int) (totalItems % limit);
        return limit;
    }

    public static <T extends StorageItem> ObjectNode createStorageItemList(List<T> content, int page, int limit) {
        Stream<T> fileStream = content.stream()
                .sorted(Comparator.comparing(T::getOriginalName))
                .skip((long) page * limit)
                .limit(limit);

        long totalItems = content.size();
        long totalPages = Util.calculateTotalPages(totalItems, limit);
        int itemCount = Util.calculateItemCount(totalPages, totalItems, page, limit);

        return new ResponseBuilder()
                .setStatus(ResponseState.OK)
                .setMessage("Storage items successfully loaded.")
                .addArray("data", fileStream)
                .add("count", itemCount)
                .add("total", totalItems)
                .add("page", page)
                .add("pages", totalPages)
                .build();
    }
}
