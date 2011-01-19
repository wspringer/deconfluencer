package com.xebia.deconfluencer.loader.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.xebia.deconfluencer.Logger;
import com.xebia.deconfluencer.Loader;

/**
 * A {@link Loader} that offers the data from a web resource located by the path passed in.
 */
public class HttpLoader implements Loader<InputStream> {

    private final UriBuilder builder;
    private final RequestExtender extender;
    private final static Logger logger = new Logger();

    /**
     * Constructs a new instance, accepting an object that turns incoming path into URIs locating web resources.
     *
     * @param builder
     */
    public HttpLoader(UriBuilder builder) {
        this(builder, new NullRequestExtender());
    }

    /**
     * Same a {@link #HttpLoader(UriBuilder)}, but now accepting an {@link RequestExtender} to allow for things such as
     * authentication.
     *
     * @param builder
     * @param extender
     */
    public HttpLoader(UriBuilder builder, RequestExtender extender) {
        this.builder = builder;
        this.extender = extender;
    }

    @Override
    public InputStream load(String path) throws IOException {
        String uri = builder.buildFrom(path);
        AsyncHttpClient client = new AsyncHttpClient();
        Future<Response> future = extender.extend(client.prepareGet(uri)).execute();
        try {
            Response response = future.get();
            logger.debug("Got response with status code " + response.getStatusCode());
            return response.getResponseBodyAsStream();
        } catch (InterruptedException e) {
            Thread.interrupted();
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }

}
