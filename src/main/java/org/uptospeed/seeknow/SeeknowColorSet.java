package org.uptospeed.seeknow;

import java.util.HashSet;

/**
 * custom set so that we can have our own {@link #toString()}
 */
public class SeeknowColorSet extends HashSet<SeeknowColor> {

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		iterator().forEachRemaining(c -> buffer.append(c.toString()));
		return buffer.toString();
	}
}
