package com.jcmlabs.AccessCore.SampleModule.Controllers;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.jcmlabs.AccessCore.SampleModule.Entities.SampleDataEntity;
import com.jcmlabs.AccessCore.SampleModule.Payload.SampleDataFilterInput;
import com.jcmlabs.AccessCore.SampleModule.Payload.SampleDataInput;
import com.jcmlabs.AccessCore.SampleModule.Services.SampleDataService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SampleDataController {
    private final SampleDataService sampleDataService;

    @SchemaMapping(typeName = "Mutation")
    public BaseResponse<SampleDataEntity> createUpdateSampleData(@Argument SampleDataInput input){
        return sampleDataService.createUpdateSampleData(input);
    }

    @SchemaMapping(typeName = "Query")
    public BaseResponse<SampleDataEntity> getSampleData(@Argument SampleDataFilterInput filter){
        return sampleDataService.getAllSampleData(filter);
    }
}
