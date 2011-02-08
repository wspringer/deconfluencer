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
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Templates;
import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.FileResource;
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
    private final static File DEFAULT_FILTER = new File(System.getProperty("basedir"), "conf/filter.xsl");
    private final static File DEFAULT_RESOURCES = new File(System.getProperty("basedir"), "resources");

    @Option(name = "-o",
            usage = "Parameters to be passed to the transformation",
            metaVar = "NAME=VALUE",
            required = false)
    private Map<String, String> params = new HashMap<String, String>();

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
            metaVar = "FILTER",
            usage = "The location of the XSLT filter to be applied. (Defaults to conf/filter.xsl.)",
            required = false)
    private File filter = DEFAULT_FILTER;

    @Option(name = "-b",
            metaVar = "BASE URL",
            usage = "The base URL of the URL space we want to deconfluence.",
            required = true)
    private String space;

    @Option(name = "-s",
            metaVar = "PATH",
            usage = "Location to be used for serving static resources. (Defaults to resources/.)",
            required = false)
    private File directory = DEFAULT_RESOURCES;

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

    private Handler createTransformingHandler() {
        UriBuilder builder = new UriBuilder() {
            @Override
            public String buildFrom(String path) {
                String result = space + path;
                logger.debug("Trying " + result);
                return result;
            }
        };
        Templates templates = new ReloadingTemplates(filter, new TransformerFactoryImpl());
        Loader<InputStream> dataLoader = new HttpLoader(builder,
                new BasicAuthenticationRequestExtender(username, password));
        NekoSourceLoader nekoSourceLoader = new NekoSourceLoader(dataLoader);
        return new TransformingHandler(nekoSourceLoader, new Transformation(templates, params));
    }

    private void start() throws Exception {
        Handler handler = createTransformingHandler();
        if (directory != null && directory.exists() && directory.isDirectory() && directory.canRead()) {
            logger.debug("Serving resources from " + directory);
            handler = addResourceHandler(handler, directory);
        }
        Server server = new Server(portNumber);
        server.setHandler(handler);
        server.start();
    }

    private Handler addResourceHandler(Handler handler, File directory) throws Exception, URISyntaxException {
        HandlerList list = new HandlerList();
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(new FileResource(directory.toURL()));
        ContextHandler contextHandler = new ContextHandler("/resources");
        contextHandler.setHandler(resourceHandler);
        list.addHandler(contextHandler);
        list.addHandler(handler);
        return list;
    }

    private static void printUsage(CmdLineParser parser) {
        parser.printUsage(System.err);
    }

}
