package com.doosan.dframe.core.grid;

import com.doosan.dframe.core.config.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TreeGrid 레이아웃 마스터 엔티티.
 * <p>
 * 각 그리드의 Cfg, Toolbar, Pager 설정을 저장합니다.
 * gridId 는 TreeGrid 컴포넌트의 {@code id} 속성(예: adminEmployeeGrid)과 매핑됩니다.
 * </p>
 */
@Entity
@Table(name = "grid_layout")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GridLayout extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** TreeGrid Cfg.id 와 1:1 매핑되는 고유 식별자 (예: adminEmployeeGrid) */
    @Column(nullable = false, unique = true, length = 100)
    private String gridId;

    /** 관리용 설명 */
    @Column(length = 200)
    private String description;

    // ────────────────────────────────────────────────────
    // Cfg 속성
    // ────────────────────────────────────────────────────

    /** Cfg.MainCol - 트리 계층의 기준 컬럼명 */
    @Column(length = 100)
    private String mainCol;

    /** Cfg.Paging - 0: 미사용, 2: 서버 페이징 */
    private Integer paging;

    /** Cfg.PageLength - 페이지당 행 수 */
    private Integer pageLength;

    /** Cfg.Deleting - 0: 삭제 비허용 */
    private Integer deleting;

    /** Cfg.Selecting - 0: 선택 비허용 */
    private Integer selecting;

    /** Cfg.Editing - 0: 편집 비허용 */
    private Integer editing;

    /** Cfg.Sorting - 0: 정렬 비허용 */
    private Integer sorting;

    /** Cfg.FocusWholeRow - 1: 행 전체 포커스 */
    private Integer focusWholeRow;

    /** Cfg.AlertError - 0: 오류 알림 비표시 */
    private Integer alertError;

    /** Cfg.MaxHeight - 1: 최대 높이 자동 조절 */
    @Builder.Default
    private Integer maxHeight = 1;

    /** Cfg.ConstHeight - 1: 고정 높이 */
    @Builder.Default
    private Integer constHeight = 1;

    /** Cfg.MaxWidth - 1: 최대 너비 자동 조절 */
    @Builder.Default
    private Integer maxWidth = 1;

    /** Cfg.ConstWidth - 1: 고정 너비 */
    @Builder.Default
    private Integer constWidth = 1;

    // ────────────────────────────────────────────────────
    // Toolbar / Pager 속성
    // ────────────────────────────────────────────────────

    /** Toolbar.Visible - 0: 툴바 숨김 */
    private Integer toolbarVisible;

    /** Pager.Visible - 0: 페이저 숨김 */
    private Integer pagerVisible;

    // ────────────────────────────────────────────────────
    // 연관관계
    // ────────────────────────────────────────────────────

    @OneToMany(mappedBy = "gridLayout", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<GridLayoutColumn> columns = new ArrayList<>();
}
