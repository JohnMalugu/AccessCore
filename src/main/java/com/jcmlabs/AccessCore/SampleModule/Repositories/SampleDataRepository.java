package com.jcmlabs.AccessCore.SampleModule.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.jcmlabs.AccessCore.SampleModule.Entities.SampleDataEntity;

@Repository
public interface SampleDataRepository extends JpaRepository<SampleDataEntity,Long>, JpaSpecificationExecutor<SampleDataEntity>{

    
}
