package com.jcmlabs.AccessCore.SampleModule.ServiceImplementations;




import org.springframework.data.jpa.domain.Specification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.SampleModule.Entities.SampleDataEntity;
import com.jcmlabs.AccessCore.SampleModule.Payload.SampleDataFilterInput;
import com.jcmlabs.AccessCore.SampleModule.Payload.SampleDataInput;
import com.jcmlabs.AccessCore.SampleModule.Repositories.SampleDataRepository;
import com.jcmlabs.AccessCore.SampleModule.Services.SampleDataService;
import com.jcmlabs.AccessCore.Utilities.BaseFilterUtilities;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import com.jcmlabs.AccessCore.Utilities.PaginationUtilities.PageableConfigurations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SampleDataServiceImplementation implements SampleDataService{

    private final SampleDataRepository sampleDataRepository;
    private final PageableConfigurations pageableConfigurations;

    @Override
    public BaseResponse<SampleDataEntity> createUpdateSampleData(SampleDataInput input) {
        try {
            SampleDataEntity sampleDataEntity = new SampleDataEntity();
            sampleDataEntity.setName(input.getName());
            sampleDataEntity.setDescription(input.getDescription());

            SampleDataEntity saved = sampleDataRepository.save(sampleDataEntity);
            return new BaseResponse<SampleDataEntity>(true, ResponseCode.SUCCESS, "Sample saved successfully", saved);
        } catch (Exception e) {
            return BaseResponse.exception(e);
        }
    }

    @Override
    public BaseResponse<SampleDataEntity> getAllSampleData(SampleDataFilterInput sampleDataFilterInput) {
        try {
            Specification<SampleDataEntity> specification = BaseFilterUtilities.filterByCriteria(sampleDataFilterInput,SampleDataEntity.class);

            PageRequest pageRequest = pageableConfigurations.newPageable(sampleDataFilterInput, "id");
            Page<SampleDataEntity> data = sampleDataRepository.findAll(specification, pageRequest);

            return new BaseResponse<SampleDataEntity>(true, ResponseCode.SUCCESS, "Success", data);
        } catch (Exception e) {
            return BaseResponse.exception(e);
        }
    }
    
}
