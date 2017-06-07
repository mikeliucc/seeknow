package org.uptospeed.seeknow;

import java.util.List;

public interface SeeknowProcessor {
	boolean processMatch(SeeknowData match);

	List<SeeknowData> listMatch();
}
