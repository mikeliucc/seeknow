package org.uptospeed.seeknow;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class AcceptAllProcessor implements SeeknowProcessor {
	protected boolean stopOnEmptyText = true;
	protected List<SeeknowData> data = new ArrayList<>();

 	public boolean isStopOnEmptyText() { return stopOnEmptyText; }

	public void setStopOnEmptyText(boolean stopOnEmptyText) { this.stopOnEmptyText = stopOnEmptyText; }

	@Override
	public boolean processMatch(SeeknowData match) {
		if (match == null || StringUtils.isBlank(match.getText())) { return !stopOnEmptyText; }

		data.add(match);
		return true;
	}

	@Override
	public List<SeeknowData> listMatch() {
		return data;
	}
}
