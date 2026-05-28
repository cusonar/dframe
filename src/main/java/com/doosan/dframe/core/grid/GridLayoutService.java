package com.doosan.dframe.core.grid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GridLayoutService {

    private final GridLayoutRepository gridLayoutRepository;

    /**
     * gridId 로 레이아웃을 조회하여 TreeGrid JSON 포맷으로 반환합니다.
     *
     * @param gridId TreeGrid 의 {@code id} 속성값 (예: adminEmployeeGrid)
     * @return TreeGrid 가 기대하는 레이아웃 응답 DTO
     * @throws ResponseStatusException gridId 에 해당하는 레이아웃이 없으면 404
     */
    public GridLayoutResponse getLayout(String gridId) {
        GridLayout layout = gridLayoutRepository.findByGridId(gridId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Grid layout not found: " + gridId
                ));
        return GridLayoutResponse.from(layout);
    }
}
