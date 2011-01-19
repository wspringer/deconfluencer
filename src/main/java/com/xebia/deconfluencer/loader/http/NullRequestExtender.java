package com.xebia.deconfluencer.loader.http;

import com.ning.http.client.AsyncHttpClient;

public class NullRequestExtender implements RequestExtender {

    @Override
    public AsyncHttpClient.BoundRequestBuilder extend(AsyncHttpClient.BoundRequestBuilder builder) {
        return builder;
    }
}
