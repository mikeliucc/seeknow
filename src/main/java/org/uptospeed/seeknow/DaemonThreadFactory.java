package org.uptospeed.seeknow;

import java.util.concurrent.ThreadFactory;

/** Daemon threads factory. */
public class DaemonThreadFactory implements ThreadFactory {

	/** number of created threads. */
	private int i = 0;

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, "seeknow-matcher-thread-" + ++i);
		t.setDaemon(true);
		return t;
	}
}