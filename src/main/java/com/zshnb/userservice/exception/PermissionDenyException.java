package com.zshnb.userservice.exception;

public class PermissionDenyException extends RuntimeException {

    public PermissionDenyException() {
        super("权限拒绝");
    }
}
