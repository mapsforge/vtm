/*
 * Copyright 2017 Longri
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.theme.comparator.logging;

import com.badlogic.gdx.utils.StringBuilder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.util.CachingDateFormatter;

public abstract class BaseAppender extends AppenderBase<ILoggingEvent> {
    private RSyntaxTextArea textArea;
    private StringBuilder stringBuilder = new StringBuilder();
    private final CachingDateFormatter cachingDateFormatter = new CachingDateFormatter("HH:mm:ss.SSS");
    private final ThrowableProxyConverter tpc = new ThrowableProxyConverter();


    //Layout settings
    private final boolean writeTime = true;
    private final boolean writeThread = false;
    private final boolean writeLevel = true;
    private final boolean writeShortLoggerName = true;

    BaseAppender() {

        //set stackTrace count to 10
        List<String> optionList = new ArrayList<>();
        optionList.add("10");
        tpc.setOptionList(optionList);
        tpc.start();
    }

    @Override
    protected void append(final ILoggingEvent eventObject) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (eventObject != null && canLogClass(eventObject.getLoggerName())) {
                    textArea.append(doLayout(eventObject));
                    textArea.setCaretPosition(textArea.getDocument().getLength());
                }
            }
        });
        thread.start();

//TODO set Highlight for LogLevel [WARN], [ERROR]
    }

    String doLayout(ILoggingEvent event) {
        if (!isStarted()) {
            return CoreConstants.EMPTY_STRING;
        }
        java.lang.StringBuilder sb = new java.lang.StringBuilder();

        if (writeTime) {
            sb.append(cachingDateFormatter.format(event.getTimeStamp()));
            sb.append(" ");
        }

        if (writeThread) {
            sb.append("[");
            sb.append(event.getThreadName());
            sb.append("] ");
        }

        if (writeLevel) {
            sb.append(event.getLevel().toString());
            sb.append(" ");
        }

        if (writeShortLoggerName) {
            String name = event.getLoggerName();
            int pos = name.lastIndexOf(".") + 1;
            sb.append(name.substring(pos));
        } else {
            sb.append(event.getLoggerName());
        }


        sb.append(" - ");
        sb.append(event.getFormattedMessage());
        sb.append(CoreConstants.LINE_SEPARATOR);
        IThrowableProxy tp = event.getThrowableProxy();
        if (tp != null) {
            String stackTrace = tpc.convert(event);
            sb.append(stackTrace);
        }
        return sb.toString();
    }

    void setTextArea(RSyntaxTextArea textArea) {
        this.textArea = textArea;
    }

    abstract boolean canLogClass(String className);
}
