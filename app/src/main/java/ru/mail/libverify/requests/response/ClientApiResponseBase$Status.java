package ru.mail.libverify.requests.response;

public enum ClientApiResponseBase$Status {
    OK,
    ERROR,
    VERIFIED,
    UNSUPPORTED_NUMBER,
    INCORRECT_PHONE_NUMBER,
    PHONE_NUMBER_IN_BLACK_LIST,
    PHONE_NUMBER_TYPE_NOT_ALLOWED,
    RATELIMIT,
    ATTEMPTLIMIT,
    INCORRECT_SIGNATURE,
    NOT_ENOUGH_DATA,
    UNKNOWN
}
