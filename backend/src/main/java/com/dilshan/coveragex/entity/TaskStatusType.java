package com.dilshan.coveragex.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task_status_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Task status type cannot be blank")
    private String type;

    public TaskStatusType(String type) {
        this.type = type;
    }

    public enum TaskStatusTypeEnum {
        OPEN("OPEN"),
        IN_PROGRESS("IN_PROGRESS"),
        HOLD("HOLD"),
        DONE("DONE"),
        CLOSED("CLOSED");

        private final String value;

        TaskStatusTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
