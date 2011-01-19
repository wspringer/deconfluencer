package com.xebia.deconfluencer.loader.http;

import com.ning.http.client.AsyncHttpClient;

/**
 * The interface to be implemented by objects that get a chance to extend the request sent by an {@link HttpLoader}.
 */
public interface RequestExtender {

    /**
     * Extends the request sent by using the operations available on <code>builder</code>.
     *
     * @param builder The builder accepting additional clues on how to send the request.
     * @return The builder passed in, in order to ease chaining.
     */
    AsyncHttpClient.BoundRequestBuilder extend(AsyncHttpClient.BoundRequestBuilder builder);

}
