package ru.ok.android.model;

public class AuthorizedUser {
    public String token;
    public UserWithLogin user;

    public AuthorizedUser() {
        this.user = new UserWithLogin("");
        this.token = "";
    }
}
