package com.devon.building.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "rentarea")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RentAreaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", nullable = false)
    private Long value;

    @Column(name = "createddate")
    private Date createdDate;

    @Column(name = "modifieddate")
    private Date modifiedDate;

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "modifiedby")
    private String modifiedBy;

    @ManyToOne
    @JoinColumn(name = "buildingid")
    private BuildingEntity building;
}
