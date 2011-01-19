package com.xebia.deconfluencer;

import java.io.InputStream;
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
        TransformingHandler handler = new TransformingHandler(nekoSourceLoader, new Transformation(templates));
        server.setHandler(handler);
        server.start();
        while (true) {
            Thread.sleep(1000);
        }

    }


}
