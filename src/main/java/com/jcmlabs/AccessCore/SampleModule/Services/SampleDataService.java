package com.jcmlabs.AccessCore.SampleModule.Services;

import com.jcmlabs.AccessCore.SampleModule.Entities.SampleDataEntity;
import com.jcmlabs.AccessCore.SampleModule.Payload.SampleDataFilterInput;
import com.jcmlabs.AccessCore.SampleModule.Payload.SampleDataInput;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;

public interface SampleDataService {
    BaseResponse<SampleDataEntity> createUpdateSampleData(SampleDataInput input);
    BaseResponse<SampleDataEntity> getAllSampleData(SampleDataFilterInput sampleDataFilterInput);
}
