package com.jcmlabs.AccessCore.SampleModule.Payload;

import com.jcmlabs.AccessCore.Utilities.BaseFilterDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SampleDataFilterDto extends BaseFilterDto {
    private String sampleFilteringData;
}
