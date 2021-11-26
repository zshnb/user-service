package com.zshnb.userservice.util;

import com.zshnb.userservice.exception.InvalidArgumentException;

public class AssertionUtil {
    /**
     * Throw InvalidArgumentException when condition is false
     * @param message error message
     * */
    public static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new InvalidArgumentException(message);
        }
    }
}
