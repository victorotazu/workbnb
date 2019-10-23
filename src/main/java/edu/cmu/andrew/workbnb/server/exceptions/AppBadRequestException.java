package edu.cmu.andrew.workbnb.server.exceptions;

public class AppBadRequestException extends AppException {
    public AppBadRequestException(int errorCode, String errorMessage) {
        super(AppException.BAD_REQUEST_EXCEPTION, errorCode, errorMessage);
    }

    public AppBadRequestException(int errorCode) {
        super(AppException.BAD_REQUEST_EXCEPTION, errorCode);
    }
}
