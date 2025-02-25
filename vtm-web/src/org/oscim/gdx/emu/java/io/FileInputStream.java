package java.io;

import java.util.logging.Logger;

public class FileInputStream extends InputStream {

    private static final Logger log = Logger.getLogger(FileInputStream.class.getName());

    public FileInputStream(File f) {

    }

    public FileInputStream(String s) throws FileNotFoundException {
        log.fine("FileInputStream " + s);
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
