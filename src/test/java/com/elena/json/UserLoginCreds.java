package com.elena.json;

public class UserLoginCreds {
    private String email;
    private String password;

    public UserLoginCreds(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
