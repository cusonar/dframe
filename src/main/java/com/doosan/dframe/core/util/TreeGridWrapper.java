package com.doosan.dframe.core.util;

import lombok.Getter;

import java.util.List;

@Getter
public class TreeGridWrapper<T> {

    private final List<List<T>> Body;

    public TreeGridWrapper(List<T> data) {
        Body = List.of(data);
    }
}
