package edu.cmu.andrew.workbnb.server.exceptions;

public class AppInternalServerException extends AppException {
    public AppInternalServerException(int errorCode, String errorMessage) {
        super(AppException.INTERNAL_SERVER_EXCEPTION, errorCode, errorMessage);
    }

    public AppInternalServerException(int errorCode) {
        super(AppException.INTERNAL_SERVER_EXCEPTION, errorCode);
    }
}
