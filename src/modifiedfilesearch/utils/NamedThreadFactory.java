package modifiedfilesearch.utils;

import java.util.concurrent.ThreadFactory;

/**
 * This implementation of a ThreadFactory will name new Threads with the 
 * String provided. Subsequent new Threads will have a number appended to the
 * name.
 * @author Brendan Cashman
 * @since 1.5
 */
public class NamedThreadFactory implements ThreadFactory {
    /**
     * Creates a ThreadFactory that will set the name of new Threads with the
     * String provided.
     * @param threadNamePrefix - Name for new threads.
     */
    public NamedThreadFactory(final String threadNamePrefix) {
        this.threadName = threadNamePrefix;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        threadCount++;
        String name;
        if ( threadCount == 1)
            name = threadName;
        else
            name = threadName + "-" + String.valueOf(threadCount); 
        return new Thread(r,name);
    }
    
    private int threadCount;
    private final String threadName;
}
