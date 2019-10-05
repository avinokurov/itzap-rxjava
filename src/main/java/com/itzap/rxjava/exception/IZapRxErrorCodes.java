package com.itzap.rxjava.exception;

import com.itzap.common.ErrorCode;

public enum IZapRxErrorCodes implements ErrorCode {
    COMMAND_ERROR;

    @Override
    public String getErrorCode() {
        return "itzap.command.error";
    }

    @Override
    public String getMessage() {
        return "Command error";
    }
}
