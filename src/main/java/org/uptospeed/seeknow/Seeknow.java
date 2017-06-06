package org.uptospeed.seeknow;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.StopWatch;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Seeknow {
	/** Execution service. */
	private final static ExecutorService executor = Executors.newCachedThreadPool(new DaemonThreadFactory());

	private Logger logger = LoggerFactory.getLogger(getClass());

	private List<Glyph> glyphs;
	private double lineHeight;
	private boolean aggressivelyTrimSpace;
	private List<String> trimSpacesBefore;
	private List<String> trimSpacesAfter;

	public List<Glyph> getGlyphs() { return glyphs;}

	public void setGlyphs(List<Glyph> glyphs) { this.glyphs = glyphs;}

	public double getLineHeight() { return lineHeight;}

	public void setLineHeight(double lineHeight) { this.lineHeight = lineHeight;}

	public boolean isAggressivelyTrimSpace() { return aggressivelyTrimSpace;}

	public void setAggressivelyTrimSpace(boolean aggressivelyTrimSpace) {
		this.aggressivelyTrimSpace = aggressivelyTrimSpace;
	}

	public List<String> getTrimSpacesBefore() { return trimSpacesBefore; }

	public void setTrimSpacesBefore(List<String> trimSpacesBefore) { this.trimSpacesBefore = trimSpacesBefore; }

	public List<String> getTrimSpacesAfter() { return trimSpacesAfter; }

	public void setTrimSpacesAfter(List<String> trimSpacesAfter) { this.trimSpacesAfter = trimSpacesAfter; }

	public List<String> readMultilines(Rectangle rectangle) {
		if (rectangle == null) { return null; }

		List<String> lines = new ArrayList<>();
		if (lineHeight < 1) {
			lines.add(read(rectangle));
			return lines;
		}

		Rectangle original = new Rectangle(rectangle);

		int numberOfLines = (int) Math.ceil(rectangle.getHeight() / lineHeight);
		for (int i = 0; i < numberOfLines; i++) {
			Rectangle oneLine = new Rectangle(original);
			double height = (lineHeight * (i + 1) > original.getHeight()) ?
			                (original.getHeight() - (lineHeight * i)) : lineHeight;
			oneLine.setRect(0, (i * lineHeight), original.getWidth(), height);
			Region region = Region.create(oneLine);
			lines.add(read(region));
		}

		return lines;
	}

	/**
	 * Read text from screen rectangle basing on the glyphs data.
	 *
	 * @param rectangle rectangle object with the coordinates
	 * @return Recognized text as String
	 */
	public String read(Rectangle rectangle) { return read(Region.create(rectangle)); }

	/**
	 * Read text from screen region basing on the glyphs data.
	 *
	 * @param region region to read text from
	 * @return Recognized text as String
	 */
	public String read(Region region) {

		if (region.getThrowException()) { region.setThrowException(false); }

		StopWatch ticktock = new StopWatch();
		ticktock.start();

		Map<Match, Glyph> mapping = new HashMap<>();
		List<FutureTask<List<Match>>> futures = new ArrayList<>();
		CountDownLatch latch = new CountDownLatch(glyphs.size());

		if (logger.isDebugEnabled()) { logger.debug(ticktock.getTime() + ":\tsetting parallel matching");}

		for (Glyph g : glyphs) {
			ParallelMatcher matcher = new ParallelMatcher(g, region, mapping, latch);
			FutureTask<List<Match>> future = new FutureTask<>(matcher);
			futures.add(future);
			executor.execute(future);
		}

		if (logger.isDebugEnabled()) { logger.debug(ticktock.getTime() + ":\twaiting for matching to complete"); }

		try {
			latch.await();
			if (logger.isDebugEnabled()) { logger.debug(ticktock.getTime() + ":\tparallel matching completed"); }
		} catch (InterruptedException e) {
			logger.error("Error occurred while waiting for paralle matching threads to complete: " + e.getMessage(), e);
		}

		if (logger.isDebugEnabled()) { logger.debug(ticktock.getTime() + "\t: collect matches"); }

		List<Match> matches = new ArrayList<>();
		for (FutureTask<List<Match>> future : futures) {
			try {
				List<Match> matched = future.get();
				if (CollectionUtils.isNotEmpty(matched)) { matches.addAll(matched);}
			} catch (InterruptedException | ExecutionException e) {
				logger.error("Error while collecting matched result: " + e.getMessage(), e);
			}
		}

		matches.sort(new MatchesComparator());

		StringBuilder sb = new StringBuilder();
		for (Match m : matches) {
			if (m == null) { continue; }

			Glyph glyph = mapping.get(m);
			if (glyph == null) {
				logger.error("Null glyph found at " + m);
			} else {
				sb.append(glyph.getCharacter());
			}
		}

		if (logger.isDebugEnabled()) { logger.debug(ticktock.getTime() + "\t: done, found " + sb.toString()); }

		ticktock.stop();

		return trim(sb.toString());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			       .append("glyphs", glyphs)
			       .append("lineHeight", lineHeight)
			       .append("aggressivelyTrimSpace", aggressivelyTrimSpace)
			       .toString();
	}

	protected String trim(String oneLine) {
		if (StringUtils.isEmpty(oneLine)) { return oneLine; }

		if (CollectionUtils.isNotEmpty(trimSpacesBefore)) {
			for (String ch : trimSpacesBefore) { oneLine = StringUtils.replaceAll(oneLine, "\\s+(\\" + ch + ")", "$1");}
		}

		if (CollectionUtils.isNotEmpty(trimSpacesAfter)) {
			for (String ch : trimSpacesAfter) { oneLine = StringUtils.replaceAll(oneLine, "(\\" + ch + ")\\s", "$1"); }
		}

		if (aggressivelyTrimSpace) {
			oneLine = StringUtils.trim(oneLine);
			while (StringUtils.contains(oneLine, "  ")) { oneLine = StringUtils.replace(oneLine, "  ", " "); }
		}

		return oneLine;
	}
}
