package com.dilshan.coveragex.repository;

import com.dilshan.coveragex.entity.TaskStatusType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatusTypeRepository extends JpaRepository<TaskStatusType, Long> {

    Optional<TaskStatusType> findByType(String type);

//    boolean existsByType(String type);

//    @Query("SELECT t FROM TaskStatusType t WHERE UPPER(t.type) = UPPER(?1)")
//    Optional<TaskStatusType> findByTypeIgnoreCase(String type);
}
