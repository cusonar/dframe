package com.doosan.dframe.core.grid;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TreeGrid 가 기대하는 레이아웃 JSON 포맷 응답 DTO.
 * <p>
 * null 값인 필드는 직렬화에서 제외하여 불필요한 속성 전송을 방지합니다.
 * </p>
 *
 * <pre>
 * {
 *   "Cfg": { "id": "adminEmployeeGrid", "Paging": 2, ... },
 *   "Cols": [ { "Name": "name", "Type": "Text", "Width": 150 }, ... ],
 *   "Header": { "name": "이름", "email": "이메일" },
 *   "Toolbar": { "Visible": 0 },
 *   "Pager":   { "Visible": 0 }
 * }
 * </pre>
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GridLayoutResponse {

    private Map<String, Object> Cfg;
    private List<Map<String, Object>> Cols;
    private Map<String, Object> Header;
    private Map<String, Object> Toolbar;
    private Map<String, Object> Pager;

    /**
     * {@link GridLayout} 엔티티를 TreeGrid 응답 포맷으로 변환합니다.
     */
    public static GridLayoutResponse from(GridLayout layout) {
        // ── Cfg ──────────────────────────────────────────
        Map<String, Object> cfg = new LinkedHashMap<>();
        cfg.put("id", layout.getGridId());
        putIfNotNull(cfg, "MainCol",       layout.getMainCol());
        putIfNotNull(cfg, "Paging",        layout.getPaging());
        putIfNotNull(cfg, "PageLength",    layout.getPageLength());
        putIfNotNull(cfg, "Deleting",      layout.getDeleting());
        putIfNotNull(cfg, "Selecting",     layout.getSelecting());
        putIfNotNull(cfg, "Editing",       layout.getEditing());
        putIfNotNull(cfg, "Sorting",       layout.getSorting());
        putIfNotNull(cfg, "FocusWholeRow", layout.getFocusWholeRow());
        putIfNotNull(cfg, "AlertError",    layout.getAlertError());
        putIfNotNull(cfg, "MaxHeight",     layout.getMaxHeight());
        putIfNotNull(cfg, "ConstHeight",   layout.getConstHeight());
        putIfNotNull(cfg, "MaxWidth",      layout.getMaxWidth());
        putIfNotNull(cfg, "ConstWidth",    layout.getConstWidth());

        // ── Cols & Header ─────────────────────────────────
        List<Map<String, Object>> cols = layout.getColumns().stream()
                .map(GridLayoutResponse::toColMap)
                .toList();

        Map<String, Object> header = new LinkedHashMap<>();
        layout.getColumns().forEach(col -> {
            if (col.getHeaderName() != null) {
                header.put(col.getColName(), col.getHeaderName());
            }
        });

        // ── Toolbar ───────────────────────────────────────
        Map<String, Object> toolbar = null;
        if (layout.getToolbarVisible() != null) {
            toolbar = new LinkedHashMap<>();
            toolbar.put("Visible", layout.getToolbarVisible());
        }

        // ── Pager ─────────────────────────────────────────
        Map<String, Object> pager = null;
        if (layout.getPagerVisible() != null) {
            pager = new LinkedHashMap<>();
            pager.put("Visible", layout.getPagerVisible());
        }

        return GridLayoutResponse.builder()
                .Cfg(cfg)
                .Cols(cols.isEmpty() ? null : cols)
                .Header(header.isEmpty() ? null : header)
                .Toolbar(toolbar)
                .Pager(pager)
                .build();
    }

    // ── 내부 유틸 ──────────────────────────────────────────────

    private static Map<String, Object> toColMap(GridLayoutColumn col) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Name", col.getColName());
        map.put("Type", col.getColType());
        putIfNotNull(map, "Width",    col.getWidth());
        putIfNotNull(map, "CanEdit",  col.getCanEdit());
        putIfNotNull(map, "Format",   col.getFormat());
        putIfNotNull(map, "Enum",     col.getEnumValues());
        putIfNotNull(map, "EnumKeys", col.getEnumKeys());
        return map;
    }

    private static void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
