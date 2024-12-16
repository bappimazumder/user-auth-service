package com.bappi.userauthservice.utils.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.text.ParseException;


public interface ServiceExceptionHandler<T> {
    Logger logger = LoggerFactory.getLogger(ServiceExceptionHandler.class);

    T handler() throws IllegalAccessException, ParseException;

    default T executeHandler() {
        try {
            return this.handler();
        } catch (IllegalArgumentException var2) {
            logger.error("Exception !!!", var2);
            throw new CustomException(APIErrorCode.ILLEGAL_ARGUMENT, HttpStatus.BAD_REQUEST);
        } catch (ParseException var3) {
            logger.error("Exception !!!", var3);
            throw new CustomException(APIErrorCode.INVALID_API_INPUT_FORMAT, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (CustomException var4) {
            throw var4;
        } catch (Exception var5) {
            logger.error("Exception !!!", var5);
            throw new CustomException(APIErrorCode.INTERNAL_SERVER_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}