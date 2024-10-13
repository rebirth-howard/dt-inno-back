package com.deutschmotors.modulecommon.error;

public interface GlobalErrorCodeIfs {

    Integer getHttpStatusCode();

    Integer getErrorCode();

    String getDescription();
}