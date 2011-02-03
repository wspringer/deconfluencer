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
package com.xebia.deconfluencer.confluence;

import java.io.File;
import java.io.InputStream;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import org.eclipse.jetty.server.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import com.xebia.deconfluencer.Loader;
import com.xebia.deconfluencer.Logger;
import com.xebia.deconfluencer.TransformingHandler;
import com.xebia.deconfluencer.loader.http.BasicAuthenticationRequestExtender;
import com.xebia.deconfluencer.loader.http.HttpLoader;
import com.xebia.deconfluencer.loader.http.UriBuilder;
import com.xebia.deconfluencer.loader.neko.NekoSourceLoader;
import com.xebia.deconfluencer.xslt.ReloadingTemplates;
import com.xebia.deconfluencer.xslt.Transformation;

public class Deconfluencer {

    private final static int DEFAULT_PORTNUMBER = 8082;
    private final static Logger logger = new Logger();

    @Option(name = "-u",
            metaVar = "USERNAME",
            usage = "The username used to authenticate to Confluence.",
            required = true)
    private String username;

    @Option(name = "-p",
            metaVar = "PASSWORD",
            usage = "The password used to authenticate to Confluence.",
            required = true)
    private String password;

    @Option(name = "-n",
            metaVar = "PORTNUMBER",
            usage = "The portnumber on which the reverse proxy will run (" + DEFAULT_PORTNUMBER + ")",
            required = false)
    private int portNumber = DEFAULT_PORTNUMBER;

    @Option(name = "-f",
            metaVar= "FILTER",
            usage= "The location of the XSLT filter to be applied",
            required= true)
    private File filter;

    public static final void main(String... args) throws Exception {
        Deconfluencer proxy = new Deconfluencer();
        CmdLineParser parser = new CmdLineParser(proxy);
        try {
            parser.parseArgument(args);
            proxy.start();
        } catch (CmdLineException cle) {
            System.err.println(cle.getMessage());
            System.err.println();
            printUsage(parser);
        }
    }

    private void start() throws Exception {
        UriBuilder builder = new UriBuilder() {
            @Override
            public String buildFrom(String path) {
                String result = "https://projects.xebia.com/confluence/display/CRAFT" + path;
                logger.debug("Trying " + result);
                return result;
            }
        };
//        TransformerFactory factory = TransformerFactory.newInstance();
//        Templates templates = factory.newTemplates(new StreamSource(Resources.getResource("filter.xsl").openStream()));
        Templates templates = new ReloadingTemplates(filter, TransformerFactory.newInstance());
        Server server = new Server(portNumber);
        Loader<InputStream> dataLoader = new HttpLoader(builder,
                new BasicAuthenticationRequestExtender(username, password));
        NekoSourceLoader nekoSourceLoader = new NekoSourceLoader(dataLoader);
        TransformingHandler handler = new TransformingHandler(nekoSourceLoader, new Transformation(templates));
        server.setHandler(handler);
        server.start();
    }

    private static void printUsage(CmdLineParser parser) {
        parser.printUsage(System.err);
    }

}
