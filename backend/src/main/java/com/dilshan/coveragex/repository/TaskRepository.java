package com.dilshan.coveragex.repository;

import com.dilshan.coveragex.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByTaskStatusId(Long statusId, Pageable pageable);

    Page<Task> findByPriorityId(Long priorityId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.taskTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Task> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);

//    @Query("SELECT t FROM Task t WHERE t.taskStatus.type = :statusType")
//    Page<Task> findByTaskStatusType(@Param("statusType") String statusType, Pageable pageable);

//    @Query("SELECT t FROM Task t WHERE t.priority.type = :priorityType")
//    Page<Task> findByPriorityType(@Param("priorityType") String priorityType, Pageable pageable);


    @Query("SELECT t.taskStatus.type as status, COUNT(t) as count FROM Task t GROUP BY t.taskStatus.type")
    List<Object[]> countTasksByStatus();


    @Query("SELECT t.priority.type as priority, COUNT(t) as count FROM Task t GROUP BY t.priority.type")
    List<Object[]> countTasksByPriority();

    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskStatus.type IN ('DONE', 'CLOSED')")
    Long countCompletedTasks();

    @Query("SELECT COUNT(t) FROM Task t WHERE t.taskStatus.type IN ('OPEN', 'IN_PROGRESS', 'HOLD')")
    Long countActiveTasks();

    @Query("SELECT t FROM Task t WHERE " +
           "(:statusId IS NULL OR t.taskStatus.id = :statusId) AND " +
           "(:priorityId IS NULL OR t.priority.id = :priorityId) AND " +
           "(:searchTerm IS NULL OR " +
           "LOWER(t.taskTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> findTasksWithFilters(@Param("statusId") Long statusId,
                                   @Param("priorityId") Long priorityId,
                                   @Param("searchTerm") String searchTerm,
                                   Pageable pageable);
}
