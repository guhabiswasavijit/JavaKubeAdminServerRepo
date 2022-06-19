package org.server;

import java.util.concurrent.ThreadFactory;

public class KubeThreadFactory implements ThreadFactory {
    private static KubeThreadFactory factory = null;
    static {
    	factory = new KubeThreadFactory();
    }
    public static ThreadFactory getInstance() {
    	return factory;
    }
	@Override
	public Thread newThread(Runnable command) {
		return new Thread(command);
	}

}
