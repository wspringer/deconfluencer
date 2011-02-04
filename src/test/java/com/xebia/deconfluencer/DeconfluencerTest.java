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

import java.io.InputStream;
import java.util.Collections;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.jetty.server.Server;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.google.common.io.Resources;
import com.xebia.deconfluencer.loader.http.BasicAuthenticationRequestExtender;
import com.xebia.deconfluencer.loader.http.HttpLoader;
import com.xebia.deconfluencer.loader.http.UriBuilder;
import com.xebia.deconfluencer.loader.neko.NekoSourceLoader;
import com.xebia.deconfluencer.xslt.Transformation;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeconfluencerTest {

    @Mock
    private UriBuilder builder;

    @Test
    @Ignore
    public void shouldBothRedirectAndTransform() throws Exception, InterruptedException {
        String username = null; // Adjust this
        String password = null; // Adjust this
        when(builder.buildFrom(Mockito.anyString())).thenReturn("https://projects.xebia.com/confluence/display/CRAFT/time-for-testing");
        TransformerFactory factory = TransformerFactory.newInstance();
        Templates templates = factory.newTemplates(new StreamSource(Resources.getResource("filter.xsl").openStream()));
        Server server = new Server(8082);
        Loader<InputStream> dataLoader = new HttpLoader(builder,
                new BasicAuthenticationRequestExtender(username, password));
        NekoSourceLoader nekoSourceLoader = new NekoSourceLoader(dataLoader);
        TransformingHandler handler =
                new TransformingHandler(nekoSourceLoader, new Transformation(templates, Collections.<String, String>emptyMap()));
        server.setHandler(handler);
        server.start();
        while (true) {
            Thread.sleep(1000);
        }

    }


}
