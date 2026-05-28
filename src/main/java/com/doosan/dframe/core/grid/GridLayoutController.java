package com.doosan.dframe.core.grid;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grid-layouts")
@RequiredArgsConstructor
public class GridLayoutController {

    private final GridLayoutService gridLayoutService;

    /**
     * TreeGrid 의 {@code Layout_Url} 속성이 호출하는 엔드포인트.
     * <p>
     * 사용 예: {@code <treegrid Layout_Url="/api/grid-layouts/adminEmployeeGrid">}
     * </p>
     *
     * @param gridId TreeGrid 의 {@code id} 속성값
     * @return TreeGrid 레이아웃 JSON
     */
    @GetMapping("/{gridId}")
    public GridLayoutResponse getLayout(@PathVariable String gridId) {
        return gridLayoutService.getLayout(gridId);
    }
}
