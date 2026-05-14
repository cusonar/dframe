package com.doosan.dframe.core.department;

import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DepartmentDto(
        String code,
        String name,
        String englishName,
        String parentCode,
        String managerId,
        Integer sortOrder,
        List<DepartmentDto> children) {
    public static DepartmentDto fromEntity(Department d) {
        return DepartmentDto.builder()
                .code(d.getCode())
                .name(d.getName())
                .englishName(d.getEnglishName())
                .parentCode(d.getParent() != null ? d.getParent().getCode() : null)
                .managerId(d.getManager() != null ? d.getManager().getId() : null)
                .sortOrder(d.getSortOrder())
                .children(d.getChildren().stream().map(DepartmentDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
}
