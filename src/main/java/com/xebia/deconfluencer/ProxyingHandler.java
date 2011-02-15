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
package com.xebia.deconfluencer;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import com.google.common.io.ByteStreams;
import com.xebia.deconfluencer.log.Logger;

/**
 * A {@link org.eclipse.jetty.server.Handler} that loads a Source using a {@link Loader}, and transforms it using XSLT.
 */
public class ProxyingHandler extends AbstractHandler {

    private final Loader<Response> loader;
    private Logger logger = Logger.forClass(ProxyingHandler.class);
    private final ProcessorSelector selector;


    /**
     * Constructs a new instance.
     *
     * @param loader   The object loading the source from a path.
     * @param selector The object responsible for picking the appropriate Processor.
     */
    public ProxyingHandler(Loader<Response> loader, ProcessorSelector selector) {
        this.loader = loader;
        this.selector = selector;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String path = baseRequest.getPathInfo();
        Response loadedResponse = loader.load(path);
        if (loadedResponse.getStatusCode() == 200) {
            Processor processor = selector.select(loadedResponse);
            if (processor == null) {
                processor = new DefaultProcessor();
            }
            processor.process(loadedResponse, response, Collections.singletonMap("path", path));
        } else {
            logger.error("Got no data for " + path);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private static class DefaultProcessor implements Processor {

        @Override
        public void process(Response in, HttpServletResponse out, Map<String, String> path) throws IOException {
            out.setContentType(in.getContentType());
            out.setStatus(in.getStatusCode());
            ByteStreams.copy(in.getInputStream(), out.getOutputStream());
        }
    }

}
