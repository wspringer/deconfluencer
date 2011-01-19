package com.xebia.deconfluencer.confluence;

import java.io.InputStream;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.jetty.server.Server;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import com.google.common.io.Resources;
import com.xebia.deconfluencer.Loader;
import com.xebia.deconfluencer.Logger;
import com.xebia.deconfluencer.TransformingHandler;
import com.xebia.deconfluencer.loader.http.BasicAuthenticationRequestExtender;
import com.xebia.deconfluencer.loader.http.HttpLoader;
import com.xebia.deconfluencer.loader.http.UriBuilder;
import com.xebia.deconfluencer.loader.neko.NekoSourceLoader;
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
        TransformerFactory factory = TransformerFactory.newInstance();
        Templates templates = factory.newTemplates(new StreamSource(Resources.getResource("filter.xsl").openStream()));
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
