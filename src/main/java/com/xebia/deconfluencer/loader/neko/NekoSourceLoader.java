package com.xebia.deconfluencer.loader.neko;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.dom.DOMSource;
import org.cyberneko.html.parsers.DOMParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.google.common.io.Closeables;
import com.xebia.deconfluencer.Loader;
import com.xebia.deconfluencer.Logger;

/**
 * A {@link Loader} that wraps around a Loader of InputStream, taking the bytes provided by the InputStream and turning
 * it into a DOM document.
 */
public class NekoSourceLoader implements Loader<DOMSource> {

    private final Loader<InputStream> source;
    private final static Logger logger = new Logger();

    /**
     * Constructs a new Loader, accepting the Loader that will provide the raw bytes.
     *
     * @param source The Loader that will provide the raw bytes.
     */
    public NekoSourceLoader(Loader<InputStream> source) {
        this.source = source;
    }

    @Override
    public DOMSource load(String path) throws IOException {
        InputStream in = null;
        try {
            in = source.load(path);
            if (in == null) {
                return null;
            } else {
                DOMParser parser = new DOMParser();
                parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
                parser.parse(new InputSource(in));
                logger.debug("Returning DOM Document based on HTML.");
                return new DOMSource(parser.getDocument());
            }
        } catch (SAXException e) {
            logger.error("Failed to parse document as DOM", e);
            return null;
        } finally {
            Closeables.closeQuietly(in);
        }
    }

}
