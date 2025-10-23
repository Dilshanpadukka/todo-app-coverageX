package com.dilshan.coveragex.repository;

import com.dilshan.coveragex.entity.PriorityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriorityTypeRepository extends JpaRepository<PriorityType, Long> {

    Optional<PriorityType> findByType(String type);

//    boolean existsByType(String type);

//    @Query("SELECT p FROM PriorityType p WHERE UPPER(p.type) = UPPER(?1)")
//    Optional<PriorityType> findByTypeIgnoreCase(String type);
}
