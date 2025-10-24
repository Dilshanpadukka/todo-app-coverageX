package com.dilshan.coveragex.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_title", nullable = false, length = 255)
    @NotBlank(message = "Task title cannot be blank")
    @Size(min = 1, max = 255, message = "Task title must be between 1 and 255 characters")
    private String taskTitle;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Column(name = "create_date", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(name = "last_status_change_date", nullable = false)
    @UpdateTimestamp
    private LocalDateTime lastStatusChangeDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "priority_id", nullable = false)
    @NotNull(message = "Priority is required")
    private PriorityType priority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_status_id", nullable = false)
    @NotNull(message = "Task status is required")
    private TaskStatusType taskStatus;

    public Task(String taskTitle, String description, PriorityType priority, TaskStatusType taskStatus) {
        this.taskTitle = taskTitle;
        this.description = description;
        this.priority = priority;
        this.taskStatus = taskStatus;
    }

    @PrePersist
    protected void onCreate() {
        if (createDate == null) {
            createDate = LocalDateTime.now();
        }
        if (lastStatusChangeDate == null) {
            lastStatusChangeDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastStatusChangeDate = LocalDateTime.now();
    }
}
