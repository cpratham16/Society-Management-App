package com.society.management.repository;

import com.society.management.model.ManagementPerson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagementPersonRepository extends MongoRepository<ManagementPerson, String> {
    List<ManagementPerson> findAllByOrderByDisplayOrderAsc();
}