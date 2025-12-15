package com.jcmlabs.AccessCore.SampleModule.Payload;

import com.jcmlabs.AccessCore.Utilities.BaseFilterInput;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SampleDataFilterInput extends BaseFilterInput{
    private String sampleFilteringData;
}
