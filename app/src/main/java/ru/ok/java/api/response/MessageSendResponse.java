package ru.ok.java.api.response;

public final class MessageSendResponse {
    public final long date;
    public final String serverId;
    public final String serverText;

    public MessageSendResponse(String serverId, String serverText, long date) {
        this.serverId = serverId;
        this.serverText = serverText;
        this.date = date;
    }
}
