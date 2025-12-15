package com.jcmlabs.AccessCore.Utilities.PaginationUtilities;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.jcmlabs.AccessCore.Utilities.BaseFilterInput;

@Service
public class PageableConfigurations {
    public PageRequest pageable(PageableParameters pageableParam) {

		if (pageableParam == null) {
			pageableParam = new PageableParameters("id", "DESC", 20, 0);
		}

		Direction direction = Direction.DESC;
		if (pageableParam.getSortDirection() != null && pageableParam.getSortDirection().contentEquals("ASC")) {
			direction = Direction.ASC;
		}
		if (pageableParam.getSortBy() == null || pageableParam.getSortBy().isEmpty()) {
			pageableParam.setSortBy("id");
		}
		int size = 20;
		int page = 0;

		if (pageableParam.getSize() != null && pageableParam.getSize() > 0) {
			size = pageableParam.getSize();
		}

		if (pageableParam.getFirst() != null && pageableParam.getFirst() >= 0) {
			page = pageableParam.getFirst();
		}

		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, pageableParam.getSortBy()));
		return pageRequest;
	}

	public PageRequest newPageable(BaseFilterInput baseFilterDto, String sortKey) {
		int pageSize = (baseFilterDto.getItemsPerPage() != null) ? baseFilterDto.getItemsPerPage() : 10;
		int pageNumber = (baseFilterDto.getPageNumber() != null) ? baseFilterDto.getPageNumber() : 0;
		String sortBy = (sortKey != null) ? sortKey : "createdAt";
		return PageRequest.of((pageNumber - 1), pageSize, Sort.by(sortBy));
	}
}
