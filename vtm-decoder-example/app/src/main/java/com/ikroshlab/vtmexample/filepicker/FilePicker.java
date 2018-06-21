/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 * Copyright 2016 devemux86
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
package com.ikroshlab.vtmexample.filepicker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ikroshlab.vtmexample.R;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A FilePicker displays the contents of directories. The user can navigate
 * within the file system and select a single
 * file whose path is then returned to the calling activity. The ordering of
 * directory contents can be specified via
 * {@link #setFileComparator(Comparator)}. By default subfolders and files are
 * grouped and each group is ordered
 * alphabetically.
 * <p/>
 * A {@link FileFilter} can be activated via
 * {@link #setFileDisplayFilter(FileFilter)} to restrict the displayed files and
 * folders. By default all files and folders are visible.
 * <p/>
 * Another <code>FileFilter</code> can be applied via
 * {@link #setFileSelectFilter(ValidFileFilter)} to check if a selected file is
 * valid before its path is returned. By default all files are considered as
 * valid and can be selected.
 */
public class FilePicker extends Activity implements AdapterView.OnItemClickListener {
    /**
     * The name of the extra data in the result {@link Intent}.
     */
    public static final String SELECTED_FILE = "selectedFile";

    private static final String PREFERENCES_FILE = "FilePicker";
    private static final String CURRENT_DIRECTORY = "currentDirectory";
    private static final String DEFAULT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final int DIALOG_FILE_INVALID = 0;

    // private static final int DIALOG_FILE_SELECT = 1;
    protected Comparator<File> mFileComparator = getDefaultFileComparator();
    protected FileFilter mFileDisplayFilter;
    protected ValidFileFilter mFileSelectFilter;

    /**
     * Sets the file comparator which is used to order the contents of all
     * directories before displaying them. If set to
     * null, subfolders and files will not be ordered.
     *
     * @param fileComparator the file comparator (may be null).
     */
    public void setFileComparator(Comparator<File> fileComparator) {
        mFileComparator = fileComparator;
    }

    /**
     * Sets the file display filter. This filter is used to determine which
     * files and subfolders of directories will be
     * displayed. If set to null, all files and subfolders are shown.
     *
     * @param fileDisplayFilter the file display filter (may be null).
     */
    public void setFileDisplayFilter(FileFilter fileDisplayFilter) {
        mFileDisplayFilter = fileDisplayFilter;
    }

    /**
     * Sets the file select filter. This filter is used when the user selects a
     * file to determine if it is valid. If set
     * to null, all files are considered as valid.
     *
     * @param fileSelectFilter the file selection filter (may be null).
     */
    public void setFileSelectFilter(ValidFileFilter fileSelectFilter) {
        mFileSelectFilter = fileSelectFilter;
    }

    /**
     * Creates the default file comparator.
     *
     * @return the default file comparator.
     */
    private static Comparator<File> getDefaultFileComparator() {
        // order all files by type and alphabetically by name
        return new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                if (file1.isDirectory() && !file2.isDirectory()) {
                    return -1;
                } else if (!file1.isDirectory() && file2.isDirectory()) {
                    return 1;
                } else {
                    return file1.getName().compareToIgnoreCase(file2.getName());
                }
            }
        };
    }

    private File mDirectory;
    private FilePickerIconAdapter mFilePickerIconAdapter;
    private File[] mFiles;
    private File[] mFilesWithParentFolder;

    @SuppressWarnings("deprecation")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File selectedFile = mFiles[(int) id];
        if (selectedFile.isDirectory()) {
            mDirectory = selectedFile;
            browseToCurrentDirectory();
        } else if (mFileSelectFilter == null || mFileSelectFilter.accept(selectedFile)) {
            setResult(RESULT_OK,
                    new Intent().putExtra(SELECTED_FILE, selectedFile.getAbsolutePath()));
            finish();
        } else {
            showDialog(DIALOG_FILE_INVALID);
        }
    }

    /**
     * Browses to the current directory.
     */
    private void browseToCurrentDirectory() {
        setTitle(mDirectory.getAbsolutePath());

        // read the subfolders and files from the current directory
        if (mFileDisplayFilter == null) {
            mFiles = mDirectory.listFiles();
        } else {
            mFiles = mDirectory.listFiles(mFileDisplayFilter);
        }

        if (mFiles == null) {
            mFiles = new File[0];
        } else {
            // order the subfolders and files
            Arrays.sort(mFiles, mFileComparator);
        }

        // if a parent directory exists, add it at the first position
        if (mDirectory.getParentFile() != null) {
            mFilesWithParentFolder = new File[mFiles.length + 1];
            mFilesWithParentFolder[0] = mDirectory.getParentFile();
            System.arraycopy(mFiles, 0, mFilesWithParentFolder, 1,
                    mFiles.length);
            mFiles = mFilesWithParentFolder;
            mFilePickerIconAdapter.setFiles(mFiles, true);
        } else {
            mFilePickerIconAdapter.setFiles(mFiles, false);
        }
        mFilePickerIconAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_picker);

        mFilePickerIconAdapter = new FilePickerIconAdapter(this);
        GridView gridView = (GridView) findViewById(R.id.filePickerView);
        gridView.setOnItemClickListener(this);
        gridView.setAdapter(mFilePickerIconAdapter);

        // if (savedInstanceState == null) {
        // // first start of this instance
        // showDialog(DIALOG_FILE_SELECT);
        // }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case DIALOG_FILE_INVALID:
                builder.setIcon(android.R.drawable.ic_menu_info_details);
                builder.setTitle(R.string.error);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getString(R.string.file_invalid));
                stringBuilder.append("\n\n");
                stringBuilder.append(mFileSelectFilter.getFileOpenResult()
                        .getErrorMessage());

                builder.setMessage(stringBuilder.toString());
                builder.setPositiveButton(R.string.ok, null);
                return builder.create();
            // case DIALOG_FILE_SELECT:
            // builder.setMessage(R.string.file_select);
            // builder.setPositiveButton(R.string.ok, null);
            // return builder.create();
            default:
                // do dialog will be created
                return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save the current directory
        Editor editor = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE).edit();
        editor.clear();
        if (mDirectory != null) {
            editor.putString(CURRENT_DIRECTORY, mDirectory.getAbsolutePath());
        }
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // restore the current directory
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE,
                MODE_PRIVATE);
        mDirectory = new File(preferences.getString(CURRENT_DIRECTORY,
                DEFAULT_DIRECTORY));
        if (!mDirectory.exists() || !mDirectory.canRead()) {
            mDirectory = new File(DEFAULT_DIRECTORY);
        }
        browseToCurrentDirectory();
    }
}
