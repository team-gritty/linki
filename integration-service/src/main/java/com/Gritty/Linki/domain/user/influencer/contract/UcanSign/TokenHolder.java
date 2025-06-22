package com.Gritty.Linki.domain.user.influencer.contract.UcanSign;

public class TokenHolder {

    private volatile static String token;

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        TokenHolder.token = token;
    }

}
