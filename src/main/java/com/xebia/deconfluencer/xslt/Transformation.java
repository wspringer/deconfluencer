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
package com.xebia.deconfluencer.xslt;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

/**
 * The definition of an XSLT transormation, encapsulating some of the complexity of the TraX API.
 */
public class Transformation {

    private final Templates templates;

    /**
     * Constructs a new instance.
     *
     * @param templates The object capable of creating new {@link javax.xml.transform.Transformer}s, at will. (Required
     *                  for threadsafety.)
     */
    public Transformation(Templates templates) {
        this.templates = templates;
    }

    /**
     * Constructs a new instance, accepting a {@link TransformerFactory} and the Source containing the defintion of a
     * template.
     *
     * @param factory The factory responsible for creating an instance of {@link Templates}.
     * @param template The source of an XSLT definition of the transformation.
     */
    public Transformation(TransformerFactory factory, Source template) {
        try {
            this.templates = factory.newInstance().newTemplates(template);
        } catch (TransformerConfigurationException e) {
            throw new IllegalArgumentException("Failed to build transformer.", e);
        }
    }

    /**
     * Transforms the source and writes results to the target.
     */
    public void transform(Source source, Result target) {
        try {
            templates.newTransformer().transform(source, target);
        } catch (TransformerException e) {
            throw new TransformationException(e);
        }
    }

}
