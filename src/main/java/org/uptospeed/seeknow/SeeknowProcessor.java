package org.uptospeed.seeknow;

import java.util.List;

interface SeeknowProcessor {
	boolean processMatch(SeeknowData match);

	List<SeeknowData> listMatch();
}
