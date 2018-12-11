/*
 * Copyright 2014 Hannes Janetzek
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
package org.oscim.utils.async;

public abstract class AsyncTask extends Task {
    private TaskQueue mainloop;

    void setTaskQueue(TaskQueue mainloop) {
        this.mainloop = mainloop;
    }

    /**
     * Do not override! Unless you have a reason, of course.
     */
    @Override
    public void run() {
        if (state == GO) {
            /* running on worker thread */
            state = go(false);

            if (state == GO)
                /* run on worker again */
                mainloop.addTask(this);
            else
                mainloop.post(this);
        } else {
            /* post result on main-loop */
            onPostExecute(state);
        }
    }

    /**
     * Executed on worker thread.
     *
     * @return Task.DONE on success, Task.ERROR otherwise
     */
    @Override
    public abstract int go(boolean canceled);

    /**
     * Executed on mainloop thread.
     */
    public abstract void onPostExecute(int state);

}
