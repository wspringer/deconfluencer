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
            if (response.getStatusCode() != 200) {
                logger.debug("Failed to retrieve " + path);
                return null;
            } else {
                return response.getResponseBodyAsStream();
            }
        } catch (InterruptedException e) {
            Thread.interrupted();
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }

}
