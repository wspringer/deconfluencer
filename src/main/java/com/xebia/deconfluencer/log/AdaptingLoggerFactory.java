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
package com.xebia.deconfluencer.log;

import static com.xebia.deconfluencer.log.Level.DEBUG;
import static com.xebia.deconfluencer.log.Level.ERROR;
import static com.xebia.deconfluencer.log.Level.INFO;
import static com.xebia.deconfluencer.log.Level.WARN;

/**
 * Created by IntelliJ IDEA. User: wilfred Date: 2/8/11 Time: 9:05 PM To change this template use File | Settings | File
 * Templates.
 */
public class AdaptingLoggerFactory implements LoggerFactory {

    private LoggerAdapter adapter;

    public AdaptingLoggerFactory(LoggerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public Logger create(Class<?> cl) {
        return new AdaptingLogger(cl, adapter);
    }

    private static class AdaptingLogger extends Logger {

        private LoggerAdapter adapter;
        private Class<?> type;

        public AdaptingLogger(Class<?> type, LoggerAdapter adapter) {
            this.type = type;
            this.adapter = adapter;
        }

        @Override
        public void debug(String msg) {
            adapter.write(type, DEBUG, msg, null);
        }

        @Override
        public void error(String msg, Throwable cause) {
            adapter.write(type, ERROR, msg, cause);
        }

        @Override
        public void warn(String msg) {
            adapter.write(type, WARN, msg, null);
        }

        @Override
        public void error(String msg) {
            adapter.write(type, ERROR, msg, null);
        }

        @Override
        public void info(String msg) {
            adapter.write(type, INFO, msg, null);
        }
    }

}
