package com.zshnb.userservice.request;

import com.zshnb.userservice.common.PageRequest;

public class ListFollowUserRequest extends PageRequest {
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
