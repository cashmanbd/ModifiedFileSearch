package modifiedfilesearch.data;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Immutable implementation of the FileInfo interface.
 * 
 *  @author Brendan Cashman
 */
public class FileInfoImpl implements FileInfo {

    /**
     * Constructs an implementation of the FileInfo interface.
     * @param filePath - Specified Path
     * @param attr - Associated BasicFileAttributes
     */
    public FileInfoImpl(Path filePath, BasicFileAttributes attr) {
        this.filePath = filePath;
        this.attr = attr;
    }

    @Override
    public Path getPath() {
        return filePath;
    }

    @Override
    public BasicFileAttributes getBasicFileAttributes() {
        return attr;
    }
    private final BasicFileAttributes attr;
    private final Path filePath;
}