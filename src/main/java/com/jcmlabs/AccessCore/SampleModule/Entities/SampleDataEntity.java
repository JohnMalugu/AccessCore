package com.jcmlabs.AccessCore.SampleModule.Entities;

import org.hibernate.annotations.SoftDelete;

import com.jcmlabs.AccessCore.Utilities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sample")
@SoftDelete
public class SampleDataEntity extends BaseEntity{

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
