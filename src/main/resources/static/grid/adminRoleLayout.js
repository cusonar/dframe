const adminRoleLayout = {
    Cfg: {
        id: "adminRoleGrid",
        Deleting: 0,
        Selecting: 0,
        Editing: 0,
        FocusWholeRow: 1,
        MaxHeight: 1, ConstHeight: 1,
        MaxWidth: 1, ConstWidth: 1,
    },
    Cols: [
        {Name: "code", Type: "Text", Width: 120},
        {Name: "description", Type: "Text", Width: 180},
    ],
    Header: {
        code: "역할 코드", description: "설명",
    },
    Toolbar: {
        Visible: 0,
    }
};
