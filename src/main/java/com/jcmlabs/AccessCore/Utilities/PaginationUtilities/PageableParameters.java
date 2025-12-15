package com.jcmlabs.AccessCore.Utilities.PaginationUtilities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageableParameters {
    private String sortBy;
	private String sortDirection;
	private Integer size;
	private Integer first;

	List<SearchFieldsInput> searchFields;

	public PageableParameters(Integer size, Integer first) {
		this.size = size;
		this.first = first;
	}

	public PageableParameters(String sortBy, String sortDirection, Integer size, Integer first) {
		this.sortBy = sortBy;
		this.sortDirection = sortDirection;
		this.size = size;
		this.first = first;
	}
}
