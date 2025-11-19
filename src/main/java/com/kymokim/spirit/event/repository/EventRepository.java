package com.kymokim.spirit.event.repository;

import com.kymokim.spirit.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
}
