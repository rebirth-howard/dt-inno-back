package com.deutschmotors.modulecommon.exception;


import com.deutschmotors.modulecommon.error.ErrorCodeIfs;

public interface ApiExceptionIfs {

    ErrorCodeIfs getErrorCodeIfs();
    String getErrorDescription();
}