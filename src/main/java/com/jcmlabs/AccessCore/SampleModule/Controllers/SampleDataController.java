package com.jcmlabs.AccessCore.SampleModule.Controllers;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import com.jcmlabs.AccessCore.SampleModule.Entities.SampleDataEntity;
import com.jcmlabs.AccessCore.SampleModule.Payload.SampleDataFilterInput;
import com.jcmlabs.AccessCore.SampleModule.Services.SampleDataService;
import com.jcmlabs.AccessCore.Utilities.BaseResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SampleDataController {
    private final SampleDataService sampleDataService;

    @SchemaMapping(typeName = "Query")
    public BaseResponse<SampleDataEntity> getAllSampleData(@Argument SampleDataFilterInput filterInput){
        return sampleDataService.getAllSampleData(filterInput);
    }
}
