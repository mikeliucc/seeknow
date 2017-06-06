package org.uptospeed.seeknow;

import java.io.File;
import java.net.URL;

import org.sikuli.script.Pattern;

public final class PatternUtils {
	private PatternUtils() {}

	/**
	 * Pattern from file.
	 *
	 * @param path       - image file path
	 * @param similarity - similarity fraction
	 * @return Pattern created from image
	 */
	public static Pattern pattern(String path, float similarity) { return pattern(path).similar(similarity); }

	public static Pattern pattern(URL resource, float similarity) { return pattern(resource).similar(similarity); }

	/**
	 * Pattern from file.
	 *
	 * @param path - image file path
	 * @return Pattern created from image
	 */
	public static Pattern pattern(String path) { return new Pattern(new File(path).getAbsolutePath()); }

	public static Pattern pattern(URL resource) { return new Pattern(resource); }
}
