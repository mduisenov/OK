package ru.ok.java.api.exceptions;

public class ServerReturnErrorException extends LogicLevelException {
    private static final long serialVersionUID = -1217356305545515439L;
    protected final int errorCode;
    protected final String errorMessage;

    public ServerReturnErrorException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ServerReturnErrorException)) {
            return false;
        }
        ServerReturnErrorException other = (ServerReturnErrorException) o;
        if (this.errorCode == other.errorCode && equals(this.errorMessage, other.errorMessage)) {
            return true;
        }
        return false;
    }

    public static boolean equals(String s1, String s2) {
        if (s1 == null) {
            return s1 == s2;
        } else {
            return s1.equals(s2);
        }
    }

    public int hashCode() {
        return (this.errorMessage == null ? 0 : -1200867377 * this.errorMessage.hashCode()) + (this.errorCode * 1243053373);
    }
}
