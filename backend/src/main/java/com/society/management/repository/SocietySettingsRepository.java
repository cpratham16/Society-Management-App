package com.society.management.repository;

import com.society.management.model.SocietySettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocietySettingsRepository extends MongoRepository<SocietySettings, String> {
    Optional<SocietySettings> findBySocietyCode(String societyCode);

    Optional<SocietySettings> findFirstByOrderByCreatedAtAsc();
}