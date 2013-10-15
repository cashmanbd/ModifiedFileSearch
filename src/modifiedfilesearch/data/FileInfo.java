package modifiedfilesearch.data;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * An object that correlates a single file's Path with its BasicFileAttributes. 
 * @author Brendan
 */
public interface FileInfo {
    /**
     * Provides the Path for the file represented.
     * @return 
     */
    public Path getPath();
    
    /**
     * Provides the BasicFileAttributes for the file represented.
     * @return 
     */   
    public BasicFileAttributes getBasicFileAttributes();
}
