package ru.ok.java.api;

public class HttpResult {
    public String httpResponse;
    public int httpStatus;

    public HttpResult(int httpStatus, String httpResponse) {
        this.httpStatus = httpStatus;
        this.httpResponse = httpResponse;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

    public String getHttpResponse() {
        return this.httpResponse;
    }

    public String toString() {
        return "HttpResult[status=" + this.httpStatus + " response=\"" + this.httpResponse + "\"]";
    }
}
