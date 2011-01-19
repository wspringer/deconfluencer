package com.xebia.deconfluencer.xslt;

import javax.xml.transform.TransformerException;

/**
 * A convenience wrapper of the exceptions normally thrown by the TraX API.
 */
public class TransformationException extends RuntimeException {

    public TransformationException(TransformerException cause) {
        super(cause);
    }

}
