package com.bappi.userauthservice.utils.error;

public enum APIErrorCode implements IAPIErrorCode{
    INVALID_REQUEST(1000, "Invalid Request"),
    INVALID_API_INPUT_FORMAT(1002, "Invalid Input Data Type"),
    INVALID_API_INPUT_SET(1003, "Invalid Input Set"),
    ILLEGAL_ARGUMENT(1004, "Illegal Argument"),
    REQUIRED_ARGUMENT_MISSING(1005, "Missing Required Argument"),
    WRONG_INFORMATION_PROVIDED(2010, "Wrong Information Provided"),
    NO_RECORD_FOUND(4003, "No Record Found"),
    DUPLICATE_REQUEST(4009, "Duplicate Request"),
    INTERNAL_SERVER_EXCEPTION(5000, "Internal Server Exception");

    private final int errorCode;
    private final String errorMessage;

    private APIErrorCode(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getDescription() {
        return this.errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String toString() {
        return this.errorCode + ": " + this.errorMessage;
    }

    public int getErrorStatus() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
