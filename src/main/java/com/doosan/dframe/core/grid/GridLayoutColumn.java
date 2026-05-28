package com.doosan.dframe.core.grid;

import com.doosan.dframe.core.config.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * TreeGrid 컬럼 정의 엔티티.
 * <p>
 * 하나의 {@link GridLayout}에 속하는 각 컬럼의 설정(Cols, Header)을 저장합니다.
 * </p>
 */
@Entity
@Table(name = "grid_layout_column")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GridLayoutColumn extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 소속 그리드 레이아웃 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grid_layout_id", nullable = false)
    private GridLayout gridLayout;

    /** Cols[].Name - 데이터 필드명 (예: name, email) */
    @Column(nullable = false, length = 100)
    private String colName;

    /**
     * Cols[].Type - TreeGrid 컬럼 타입.
     * <p>Text | Int | Date | Bool | Enum | Html | Float 등</p>
     */
    @Column(nullable = false, length = 50)
    private String colType;

    /** Header.{colName} - 화면 표시용 헤더명 (예: 이름, 이메일) */
    @Column(length = 200)
    private String headerName;

    /** Cols[].Width - 컬럼 너비(px) */
    private Integer width;

    /**
     * Cols[].CanEdit - 편집 가능 여부.
     * <p>null: 그리드 기본값 따름 / 0: 편집 불가</p>
     */
    private Integer canEdit;

    /** Cols[].Format - 날짜·숫자 포맷 (예: yyyy-MM-dd HH:mm:ss) */
    @Column(length = 100)
    private String format;

    /**
     * Cols[].Enum - Enum 타입의 선택지 문자열.
     * <p>파이프(|)로 구분 (예: |주임|사원|대리|과장|차장|부장)</p>
     */
    @Column(length = 500)
    private String enumValues;

    /**
     * Cols[].EnumKeys - Enum 타입의 키값 문자열.
     * <p>파이프(|)로 구분 (예: |j|s|d|g|c|b)</p>
     */
    @Column(length = 500)
    private String enumKeys;

    /** 컬럼 표시 순서 (오름차순 정렬) */
    @Column(nullable = false)
    private Integer sortOrder;
}
