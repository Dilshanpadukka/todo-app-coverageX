package com.dilshan.coveragex.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "priority_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriorityType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Priority type cannot be blank")
    private String type;

    public PriorityType(String type) {
        this.type = type;
    }

    public enum PriorityTypeEnum {
        HIGH("HIGH"),
        MEDIUM("MEDIUM"),
        LOW("LOW");

        private final String value;

        PriorityTypeEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
