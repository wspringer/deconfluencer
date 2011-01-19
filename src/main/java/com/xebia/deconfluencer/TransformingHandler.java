package com.xebia.deconfluencer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import com.xebia.deconfluencer.xslt.Transformation;

/**
 * A {@link org.eclipse.jetty.server.Handler} that loads a Source using a {@link Loader}, and transforms it using XSLT.
 */
public class TransformingHandler extends AbstractHandler {

    private final Loader<? extends Source> loader;
    private Transformation template;

    /**
     * Constructs a new instance.
     *
     * @param loader The object loading the source from a path.
     * @param template The object hiding some of the complexity for executing XSLT transformations.
     */
    public TransformingHandler(Loader<? extends Source> loader, Transformation template) {
        this.loader = loader;
        this.template = template;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String path = baseRequest.getPathInfo();
        Source source = loader.load(path);
        response.setContentType("text/html");
        template.transform(source, new StreamResult(response.getOutputStream()));
    }

}
