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
