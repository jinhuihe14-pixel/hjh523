package com.arttraining.common.page;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer current = 1;
    private Integer size = 10;
    private String orderBy;
    private String orderDirection = "desc";

    public Integer getCurrent() {
        return current == null || current < 1 ? 1 : current;
    }

    public Integer getSize() {
        return size == null || size < 1 ? 10 : (size > 100 ? 100 : size);
    }
}
