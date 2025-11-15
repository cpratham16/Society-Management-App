package com.society.management.repository;

import com.society.management.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, String> {
    Page<Photo> findAll(Pageable pageable);

    Page<Photo> findByCategory(String category, Pageable pageable);

    List<Photo> findByCategory(String category);
}