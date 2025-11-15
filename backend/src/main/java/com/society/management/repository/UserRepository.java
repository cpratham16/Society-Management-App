package com.society.management.repository;

import com.society.management.model.User;
import com.society.management.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findBySocietyCode(String societyCode);

    Optional<User> findBySocietyCodeAndEmail(String societyCode, String email);

    List<User> findByRole(Role role);

    @Query("{ 'email': ?0, 'verified': true }")
    Optional<User> findVerifiedByEmail(String email);

    Long countByRole(Role role);

    @Query("{ 'societyCode': ?0, 'role': 'MEMBER' }")
    Long countMembersByCode(String societyCode);
}