package com.xebia.deconfluencer;

import java.io.IOException;

/**
 * The interface implemented by objects capable of loading a certain type of object from a path passed in.
 *
 * @param <T> The type of object getting loaded.
 */
public interface Loader<T> {

    /**
     * Loads an instance of type <code>T</code> based on the path passed in.
     *
     * @param path A path.
     * @return An instance of type <code>T</code> if there is an instance of that type identified by the given path, or
     *         <code>null</code> otherwise.
     * @throws IOException If an IO problem occurs while loading an instance of <code>T</code>.
     */
    T load(String path) throws IOException;

}
