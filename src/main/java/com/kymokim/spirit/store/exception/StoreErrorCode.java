package com.kymokim.spirit.store.exception;

import com.kymokim.spirit.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StoreErrorCode implements ErrorCode {
    STORE_NAME_EMPTY(HttpStatus.BAD_REQUEST, 22000, "STORE_NAME_EMPTY"),
    HAS_SCREEN_EMPTY(HttpStatus.BAD_REQUEST, 22001, "HAS_SCREEN_EMPTY"),
    IS_GROUP_AVAILABLE_EMPTY(HttpStatus.BAD_REQUEST, 22002, "IS_GROUP_AVAILABLE_EMPTY"),
    STORE_CATEGORIES_EMPTY(HttpStatus.BAD_REQUEST, 22003, "STORE_CATEGORIES_EMPTY"),
    LOCATION_ADDRESS_EMPTY(HttpStatus.BAD_REQUEST, 22004, "LOCATION_ADDRESS_EMPTY"),
    LOCATION_LATITUDE_EMPTY(HttpStatus.BAD_REQUEST, 22005, "LOCATION_LATITUDE_EMPTY"),
    LOCATION_LONGITUDE_EMPTY(HttpStatus.BAD_REQUEST, 22006, "LOCATION_LONGITUDE_EMPTY"),
    STORE_ORIGIN_IMG_URL_EMPTY(HttpStatus.BAD_REQUEST, 22007, "STORE_ORIGIN_IMG_URL_EMPTY"),
    STORE_OPEN_TIME_EMPTY(HttpStatus.BAD_REQUEST, 22008, "STORE_OPEN_TIME_EMPTY"),
    STORE_CLOSE_TIME_EMPTY(HttpStatus.BAD_REQUEST, 22009, "STORE_CLOSE_TIME_EMPTY"),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, 22010, "STORE_NOT_FOUND"),
    STORE_IMG_FILE_EMPTY(HttpStatus.BAD_REQUEST, 22011, "STORE_IMG_FILE_EMPTY");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    StoreErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
