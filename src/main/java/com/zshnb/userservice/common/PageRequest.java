package com.zshnb.userservice.common;

/**
 * Basic class for pageable request
 * */
public class PageRequest {
    int pageNumber = 1;

    int pageSize = 20;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
