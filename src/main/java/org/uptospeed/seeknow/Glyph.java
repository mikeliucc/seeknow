package org.uptospeed.seeknow;

import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.sikuli.script.Pattern;

/** Single glyph to be recognized by Seeknow. */
class Glyph {

	/** Percentage similarity value. */
	private int similarity = 95;

	/** Character representing the glyph. */
	private String character = null;

	/** Image filename. */
	private String image = null;

	private transient Pattern pattern = null;

	public int getSimilarity() { return similarity; }

	public String getCharacter() { return character; }

	public String getImage() { return image; }

	public Pattern getPattern() { return pattern; }

	@Override
	public String toString() { return "glyph[" + character + "](" + image + ")"; }

	void init(String basePath, boolean asResource) {
		String path = StringUtils.appendIfMissing(basePath, "/") + image;

		if (asResource) {
			URL resource = Glyph.class.getResource(path);
			if (resource == null || StringUtils.isBlank(resource.getFile())) {
				throw new IllegalArgumentException("Invalid glyph resource file: " + path);
			}

			pattern = PatternUtils.pattern(resource, (float) similarity / 100);
			return;
		}

		pattern = PatternUtils.pattern(path, (float) similarity / 100);

	}
}
