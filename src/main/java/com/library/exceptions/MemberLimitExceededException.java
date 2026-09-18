package com.library.exceptions;

public class MemberLimitExceededException extends Exception {

    public MemberLimitExceededException(String message) {
        super(message);
    }
}
