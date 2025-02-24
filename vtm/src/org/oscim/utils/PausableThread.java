/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2013 Hannes Janetzek
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
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
package org.oscim.utils;

import java.util.logging.Logger;

/**
 * An abstract base class for threads which support pausing and resuming.
 */
public abstract class PausableThread extends Thread {
    private static final Logger log = Logger.getLogger(PausableThread.class.getName());
    private static final boolean dbg = false;

    private boolean mPausing = true;
    private boolean mRunning = true;
    private boolean mShouldPause = false;
    private boolean mShouldStop = false;

    /**
     * Causes the current thread to wait until this thread is pausing.
     */
    public final void awaitPausing() {
        synchronized (this) {

            while (!isPausing()) {

                if (dbg)
                    log.fine("Await Pause " + getThreadName());

                try {
                    wait(100);
                } catch (InterruptedException e) {
                    /* restore the interrupted status */
                    this.interrupt();
                }
            }
        }
    }

    public synchronized void finish() {
        if (!mRunning)
            return;

        log.fine("Finish " + getThreadName());

        mShouldStop = true;
        this.interrupt();
    }

    /**
     * @return true if this thread is currently pausing, false otherwise.
     */
    public final synchronized boolean isPausing() {
        return mPausing;
    }

    /**
     * The thread should stop its work temporarily.
     */
    public final synchronized void pause() {
        if (!mShouldPause) {
            mShouldPause = true;
            this.interrupt();
        }
    }

    /**
     * The paused thread should continue with its work.
     */
    public final synchronized void proceed() {
        if (mShouldPause) {
            mShouldPause = false;
            notify();
        }
    }

    public final synchronized boolean isCanceled() {
        return mShouldPause;
    }

    @Override
    public final void run() {
        mRunning = true;
        setName(getThreadName());
        setPriority(getThreadPriority());

        O:
        while (!mShouldStop) {

            synchronized (this) {
                if (mShouldStop)
                    break;

                while ((mShouldPause || !hasWork())) {
                    try {
                        if (mShouldPause) {
                            mPausing = true;

                            if (dbg)
                                log.fine("Pausing: " + getThreadName());
                        }

                        wait();

                    } catch (InterruptedException e) {
                        if (dbg)
                            log.fine("Interrupted " + getThreadName() + " " + mShouldPause + ":" + mShouldStop);

                        if (mShouldStop)
                            break O;
                    }
                }

                if (mPausing) {
                    mPausing = false;
                    afterPause();
                }
            }

            try {
                doWork();
            } catch (InterruptedException e) {
                if (dbg)
                    log.fine("Interrupted " + getThreadName() + " " + mShouldPause + ":" + mShouldStop);

            }
        }

        log.fine("Done " + getThreadName());

        mPausing = true;
        mRunning = false;

        afterRun();
    }

    /**
     * Called once when this thread continues to work after a pause. The default
     * implementation is empty.
     */
    protected void afterPause() {
        // do nothing
    }

    /**
     * Called once at the end of the {@link #run()} method. The default
     * implementation is empty.
     */
    protected void afterRun() {
        // do nothing
    }

    /**
     * Called when this thread is not paused and should do its work.
     *
     * @throws InterruptedException if the thread has been interrupted.
     */
    protected abstract void doWork() throws InterruptedException;

    /**
     * @return the name of this thread.
     */
    protected abstract String getThreadName();

    /**
     * @return the priority of this thread. The default value is
     * {@link Thread#NORM_PRIORITY}.
     */
    protected int getThreadPriority() {
        return Thread.NORM_PRIORITY;
    }

    /**
     * @return true if this thread has some work to do, false otherwise.
     */
    protected abstract boolean hasWork();
}
