package com.society.management.repository;

import com.society.management.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    Page<Event> findAllByOrderByEventDateTimeAsc(Pageable pageable);

    @Query("{ 'eventDateTime': { $gte: ?0 } }")
    List<Event> findUpcomingEvents(LocalDateTime now);

    long count();
}