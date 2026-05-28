package com.doosan.dframe.core.employee;

/**
 * 고급 검색 조건 DTO
 *
 * @param field    컬럼명 (e.g. "name", "countLoginFail")
 * @param operator 연산자 (equals, starts, ends, contains, gt, lt, between)
 * @param value    검색값 (범위 조건의 경우 시작값)
 * @param value2   범위 조건(between)의 종료값 (선택)
 */
public record SearchFilter(String field, String operator, String value, String value2) {
}
