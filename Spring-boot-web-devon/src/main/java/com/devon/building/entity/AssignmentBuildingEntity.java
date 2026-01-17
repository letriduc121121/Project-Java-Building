package com.devon.building.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "assignmentbuilding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentBuildingEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staffid", nullable = false)
    private Long staffId;

    @Column(name = "buildingid", nullable = false)
    private Long buildingId;
}
