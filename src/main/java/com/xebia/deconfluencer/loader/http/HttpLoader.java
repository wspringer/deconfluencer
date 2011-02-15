/**
 * Copyright (c) 2011, Wilfred Springer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the <organization> nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.xebia.deconfluencer.loader.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.xebia.deconfluencer.log.Logger;
import com.xebia.deconfluencer.Loader;

/**
 * A {@link Loader} that offers the data from a web resource located by the path passed in.
 */
public class HttpLoader implements Loader<com.xebia.deconfluencer.Response> {

    private final UriBuilder builder;
    private final RequestExtender extender;
    private final static Logger logger = Logger.forClass(HttpLoader.class);
    private static final long DEFAULT_TIMEOUT = 5;
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.SECONDS;

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
    public com.xebia.deconfluencer.Response load(String path) throws IOException {
        String uri = builder.buildFrom(path);
        logger.info("Downloading " + uri);
        AsyncHttpClient client = new AsyncHttpClient();
        final Future<Response> future = extender.extend(client.prepareGet(uri)).execute();
        return new FutureResponse(future, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT);
    }

    private static class FutureResponse implements com.xebia.deconfluencer.Response {

        private final Future<Response> future;
        private final long timeout;
        private final TimeUnit unit;

        public FutureResponse(Future<Response> future, long timeout, TimeUnit unit) {
            this.future = future;
            this.timeout = timeout;
            this.unit = unit;
        }

        @Override
        public InputStream getInputStream() {
            return execute(new ResponseHandler<InputStream>() {
                @Override
                public InputStream handle(com.xebia.deconfluencer.Response response) throws IOException {
                    return response.getInputStream();
                }
            });
        }

        @Override
        public String getContentType() {
            return execute(new ResponseHandler<String>() {
                @Override
                public String handle(com.xebia.deconfluencer.Response response) throws IOException {
                    return response.getContentType();
                }
            });
        }

        @Override
        public int getStatusCode() {
            return execute(new ResponseHandler<Integer>() {
                @Override
                public Integer handle(com.xebia.deconfluencer.Response response) throws IOException {
                    return response.getStatusCode();
                }
            });
        }

        private interface ResponseHandler<T> {

            T handle(com.xebia.deconfluencer.Response response) throws Throwable;

        }

        private <T> T execute(ResponseHandler<T> handler) {
            try {
                return handler.handle(getResponse());
            } catch(Throwable t) {
                throw new IllegalStateException("Failed to get data from response.");
            }
        }

        private com.xebia.deconfluencer.Response getResponse() {
            try {
                return new ResponseWrapper(future.get(timeout, unit));
            } catch (InterruptedException e) {
                Thread.interrupted();
                return new FailedResponse();
            } catch (ExecutionException e) {
                logger.error("Failed to retrieve content.", e);
                return new FailedResponse();
            } catch (TimeoutException e) {
                logger.error("Failed to retrieve content in a timely way.", e);
                return new FailedResponse();
            }
        }

    }

    private static class ResponseWrapper implements com.xebia.deconfluencer.Response {

        private final Response response;

        public ResponseWrapper(Response response) {
            this.response = response;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return response.getResponseBodyAsStream();
        }

        @Override
        public String getContentType() {
            return response.getContentType();
        }

        @Override
        public int getStatusCode() {
            return response.getStatusCode();
        }
    }

    private static class FailedResponse implements com.xebia.deconfluencer.Response {

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public int getStatusCode() {
            return 404;
        }
    }

}
