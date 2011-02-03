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

import java.io.File;
import java.util.Properties;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import com.xebia.deconfluencer.Logger;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

/**
 * A {@link Templates} object that will load new versions of a template into the system if it finds out they have been
 * changed.
 */
public class ReloadingTemplates implements Templates, JNotifyListener {

    private final Logger logger = new Logger();

    private static final int MASK = JNotify.FILE_MODIFIED;

    /**
     * The file to be monitored.
     */
    private final File file;

    /**
     * The factory used for constructing XSLT.
     */
    private final TransformerFactory factory;

    private Templates templates;

    private final int watchId;

    public ReloadingTemplates(File file, TransformerFactory factory) {
        this.file = file;
        this.factory = factory;
        try {
            watchId = JNotify.addWatch(file.getParentFile().getAbsolutePath(), MASK, false, this);
            templates = load(file);
        } catch (JNotifyException e) {
            throw new IllegalStateException("Failed to monitor " + file);
        }
    }

    @Override
    public Transformer newTransformer() throws TransformerConfigurationException {
        return templates.newTransformer();
    }

    @Override
    public Properties getOutputProperties() {
        return templates.getOutputProperties();
    }

    private Templates load(File file) {
        logger.debug("Loading XSLT from file " + file);
        try {
            Source source = new StreamSource(file);
            return factory.newTemplates(source);
        } catch (TransformerConfigurationException e) {
            return templates = new NullTemplates();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        JNotify.removeWatch(watchId);
    }

    @Override
    public void fileCreated(int i, String rootPath, String name) {
        logger.debug("Unexpected file creation.");
    }

    @Override
    public void fileDeleted(int i, String rootPath, String name) {
        logger.debug("Unexpected file deletion.");
        templates = new NullTemplates();
    }

    @Override
    public void fileModified(int i, String rootPath, String name) {
        logger.debug("XSLT updated.");
        templates = load(file);
    }

    @Override
    public void fileRenamed(int i, String rootPath, String oldName, String newName) {
        logger.debug("Unexpected file rename.");
    }
}
