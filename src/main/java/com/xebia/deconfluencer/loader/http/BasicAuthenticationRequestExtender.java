package com.xebia.deconfluencer.loader.http;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Realm;

public class BasicAuthenticationRequestExtender implements RequestExtender {

    private final String user;
    private final String password;

    public BasicAuthenticationRequestExtender(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public AsyncHttpClient.BoundRequestBuilder extend(AsyncHttpClient.BoundRequestBuilder builder) {
        Realm realm = new Realm.RealmBuilder()
               .setPrincipal(user)
               .setPassword(password)
               .setUsePreemptiveAuth(true)
               .setScheme(Realm.AuthScheme.BASIC)
               .build();
        return builder.setRealm(realm).addQueryParameter("os_authType", "basic");
    }
}
