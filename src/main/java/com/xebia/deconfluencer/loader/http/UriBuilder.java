package com.xebia.deconfluencer.loader.http;

/**
 * The object responsible for deciding on the location of a web resource, based on path passed in.
 */
public interface UriBuilder {

    /**
     * Returns a String representation for the path passed in.
     *
     * @param path The path from which this {@link UriBuilder} needs to construct a URI.
     * @return A URI.
     */
    String buildFrom(String path);

}
