package org.uptospeed.seeknow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class RegexMatchProcessor extends AcceptAllProcessor {
	protected Pattern pattern;
	protected String regex;

	public RegexMatchProcessor(String regex) {
		this.regex = regex;
		this.pattern = Pattern.compile(regex);
	}

	@Override
	public boolean processMatch(SeeknowData match) {
		if (match == null || StringUtils.isBlank(match.getText())) { return !stopOnEmptyText; }

		Matcher matcher = pattern.matcher(match.getText());
		if (matcher.find()) { data.add(match); }

		return true;
	}
}
