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
package com.xebia.deconfluencer.loader.neko;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.dom.DOMSource;
import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
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
                DOMParser parser = new DOMParser(new HTMLConfiguration());
                parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
                parser.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
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
