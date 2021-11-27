package com.zshnb.userservice.common;

import java.util.Collections;
import java.util.List;

public class ListResponse<T> {
    private List<T> data;
    private long total;

    public ListResponse(List<T> data, long total) {
        this.data = data;
        this.total = total;
    }

    public ListResponse() {
        this.data = Collections.emptyList();
        this.total = 0L;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}