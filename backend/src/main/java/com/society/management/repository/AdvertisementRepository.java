package com.society.management.repository;

import com.society.management.model.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends MongoRepository<Advertisement, String> {
    List<Advertisement> findByUserIdOrderByCreatedAtDesc(String userId);

    Page<Advertisement> findByActiveTrue(Pageable pageable);

    Page<Advertisement> findByActiveTrueAndCategory(String category, Pageable pageable);

    List<Advertisement> findByUserIdAndActive(String userId, boolean active);

    Long countByActive(boolean active);
}