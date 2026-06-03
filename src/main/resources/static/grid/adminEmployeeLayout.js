const employeeLayout = {
    Cfg: {
        id: "adminEmployeeGrid",
        AlertError: 0, // MainCol: "Name", // Tree 사용 시
        Deleting: 0,
        Selecting: 0,
        FocusWholeRow: 1,
        Paging: 2,
        PageLength: 50,
        MaxHeight: "1",
        ConstHeight: "1",
        MaxWidth: "1",
        ConstWidth: "1",
    }, Cols: [{Name: "id", Type: "Text", CanEdit: 0}, {Name: "name", Type: "Text", CanEdit: 0}, {
        Name: "email", Type: "Text", CanEdit: 0
    }, {Name: "englishName", Type: "Text", CanEdit: 0}, {
        Name: "position", Type: "Enum", Enum: "|주임|사원|대리|과장|차장|부장", EnumKeys: "|j|s|d|g|c|b", CanEdit: 0
    }, {Name: "phone", Type: "Text"}, {Name: "deptCode", Type: "Text", CanEdit: 0}, {
        Name: "deptName", Type: "Text", CanEdit: 0
    }, {Name: "dispatchDeptCode", Type: "Text"}, {Name: "dispatchDeptName", Type: "Text", CanEdit: 0}, {
        Name: "workDeptCode", Type: "Text", CanEdit: 0
    }, {Name: "workDeptName", Type: "Text"}, {Name: "workDeptName", Type: "Text", CanEdit: 0}, {
        Name: "enabled", Type: "Bool"
    }, {Name: "accountNonExpired", Type: "Bool"}, {
        Name: "accountNonLocked", Type: "Bool"
    }, {Name: "credentialsNonExpired", Type: "Bool"}, {
        Name: "countLoginFail", Type: "Int"
    }, {Name: "lastPasswordChangedAt", Type: "Date", Format: "yyyy-MM-dd HH:mm:ss", CanEdit: 0}, {
        Name: "roleCodes", Type: "Enum", Range: 1, CanEdit: 1
    }, {Name: "lastLoginAt", Type: "Date", Format: "yyyy-MM-dd HH:mm:ss", CanEdit: 0},], Header: {
        id: "ID",
        name: "이름",
        email: "이메일",
        englishName: "영문명",
        position: "직급",
        phone: "전화번호",
        deptCode: "부서코드",
        deptName: "부서명",
        dispatchDeptCode: "발령부서코드",
        dispatchDeptName: "발령부서명",
        workDeptCode: "업무부서코드",
        workDeptName: "업무부서명",
        enabled: "활성여부",
        accountNonExpired: "계정만료여부",
        accountNonLocked: "계정잠김여부",
        credentialsNonExpired: "패스워드만료여부",
        countLoginFail: "패스워드실패횟수",
        lastPasswordChangedAt: "마지막패스워드변경일시",
        roleCodes: "권한코드",
        lastLoginAt: "마지막로그인일시",
    }, Pager: {
        Visible: 0,
    }
};