package modifiedfilesearch;

import modifiedfilesearch.data.FileInfo;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Queue;
import modifiedfilesearch.data.FileInfoImpl;

/**
 * A visitor of files that records path and attribute information if a file
 * matches a specified syntax and pattern.
 *
 * @author Brendan Cashman
 * @since 1.7
 */
class SpecifiedFileVisitor implements FileVisitor<Path> {

    /**
     * Creates a SpecifiedFileVisitor to record information about specified
     * types of files.
     *
     * @param fileInfoQueue Collection where file information will be stored for
     * files matching specified syntax and pattern. All directories visited will
     * also be added.
     * @param syntaxAndPattern Syntax and pattern for file types whose FileInfo
     * will be stored. 
     *
     */
    SpecifiedFileVisitor(Queue<FileInfo> fileInfoQueue, String syntaxAndPattern) {
        this.fileInfoQueue = fileInfoQueue;
        this.fileTypeMatcher = FileSystems.getDefault().getPathMatcher(syntaxAndPattern);
    }

    /**
     * Invoked for a directory before entries in the directory are visited.
     *
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        FileInfoImpl fileInfo = new FileInfoImpl(dir, attrs);
        fileInfoQueue.add(fileInfo);
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a file in a directory.
     *
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (fileTypeMatcher.matches(file.getFileName())) {
            FileInfoImpl fileInfo = new FileInfoImpl(file, attrs);
            fileInfoQueue.add(fileInfo);
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a file that could not be visited.
     *
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (exc != null) {
            // Want to record files that were inaccessable, but not stop
            if ( exc instanceof AccessDeniedException )
                fileInfoQueue.add(new FileInfoImpl(file, null));
            else
                throw exc;
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a directory after entries in the directory, and all of their
     * descendants, have been visited. Note: exc will be null unless an
     * exception has occurred.
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) {
            // Want to record directories that were inaccessable, but not stop
            if ( exc instanceof AccessDeniedException )
                fileInfoQueue.add(new FileInfoImpl(dir, null));
            else
                throw exc;
        }
        return FileVisitResult.CONTINUE;
    }
    private final Queue<FileInfo> fileInfoQueue;
    private final PathMatcher fileTypeMatcher;
    
}
