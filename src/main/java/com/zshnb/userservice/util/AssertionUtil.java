package com.zshnb.userservice.util;

import com.zshnb.userservice.exception.InvalidArgumentException;

public class AssertionUtil {
    public static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new InvalidArgumentException(message);
        }
    }
}
