package org.uptospeed.seeknow;

import java.awt.*;
import java.awt.image.*;
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
import org.apache.commons.lang3.time.StopWatch;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

public class Seeknow {
	/** Execution service. */
	private final static ExecutorService executor = Executors.newCachedThreadPool(new DaemonThreadFactory());

	private Logger logger = LoggerFactory.getLogger(getClass());

	private List<Glyph> glyphs;
	private int lineHeight = 16;
	private boolean aggressivelyTrimSpace;
	private List<String> trimSpacesBefore;
	private List<String> trimSpacesAfter;
	private List<String> backgroundColors;

	public List<Glyph> getGlyphs() { return glyphs;}

	public void setGlyphs(List<Glyph> glyphs) { this.glyphs = glyphs;}

	public int getLineHeight() { return lineHeight;}

	public void setLineHeight(int lineHeight) { this.lineHeight = lineHeight;}

	public boolean isAggressivelyTrimSpace() { return aggressivelyTrimSpace;}

	public void setAggressivelyTrimSpace(boolean aggressivelyTrimSpace) {
		this.aggressivelyTrimSpace = aggressivelyTrimSpace;
	}

	public List<String> getTrimSpacesBefore() { return trimSpacesBefore; }

	public void setTrimSpacesBefore(List<String> trimSpacesBefore) { this.trimSpacesBefore = trimSpacesBefore; }

	public List<String> getTrimSpacesAfter() { return trimSpacesAfter; }

	public void setTrimSpacesAfter(List<String> trimSpacesAfter) { this.trimSpacesAfter = trimSpacesAfter; }

	public List<String> getBackgroundColors() { return backgroundColors; }

	public void setBackgroundColors(List<String> backgroundColors) { this.backgroundColors = backgroundColors; }

	public Seeknow makeClone() {
		Seeknow clone = new Seeknow();
		clone.setLineHeight(lineHeight);
		if (CollectionUtils.isNotEmpty(trimSpacesBefore)) {
			clone.setTrimSpacesBefore(new ArrayList<>(trimSpacesBefore));
		}
		if (CollectionUtils.isNotEmpty(trimSpacesAfter)) {
			clone.setTrimSpacesAfter(new ArrayList<>(trimSpacesAfter));
		}
		if (CollectionUtils.isNotEmpty(backgroundColors)) {
			clone.setBackgroundColors(new ArrayList<>(backgroundColors));
		}
		if (CollectionUtils.isNotEmpty(glyphs)) {
			clone.setGlyphs(new ArrayList<>(glyphs));
		}

		return clone;
	}

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

	public void fromScreenSelection(int x, int y, int width, int height, SeeknowProcessor processor) {
		if (processor == null) { throw new IllegalArgumentException("seeknow process is null"); }
		if (x < 0) { throw new IllegalArgumentException("x must be greater or equal to zero"); }
		if (y < 0) { throw new IllegalArgumentException("y must be greater or equal to zero"); }
		if (width < 0) { throw new IllegalArgumentException("width must be greater or equal to zero"); }
		if (height < 0) { throw new IllegalArgumentException("height must be greater or equal to zero"); }

		// we'll accept 40% as another line to scan
		double lineCount = (double) (height - y) / lineHeight;
		if (lineCount < 0.8) {
			if (logger.isInfoEnabled()) { logger.info("Unable to parse since specified height is too short"); }
			return;
		}

		int numberOfLines = (int) ((lineCount - Math.round(lineCount)) > 0.4 ?
		                           Math.ceil(lineCount) : Math.floor(lineCount));

		if (logger.isDebugEnabled()) { logger.debug("found (up to) " + numberOfLines + " available for scanning"); }

		Robot robot;
		try {
			robot = new Robot();
			robot.setAutoWaitForIdle(true);
		} catch (AWTException e) {
			logger.error("Unable to read text due to failure to capture screen: " + e.getMessage(), e);
			return;
		}

		int capturedX = x;
		int capturedY = y;
		int capturedWidth = width - x;
		int capturedHeight = lineHeight;

		// Set<String> distinctColors = new HashSet<>();

		for (int lineNo = 0; lineNo < numberOfLines; lineNo++) {
			capturedHeight = (capturedY + lineHeight) > height ? height - capturedY : lineHeight;
			capturedY += lineNo == 0 ? 0 : capturedHeight;

			String msgPrefix = "(" + capturedX + "," + capturedY + "," + capturedWidth + "," + capturedHeight + ") ";
			if (logger.isDebugEnabled()) { logger.debug(msgPrefix + "start capturing"); }

			SeeknowData oneLine = new SeeknowData();
			oneLine.setLineNumber(lineNo);
			oneLine.setX(capturedX);
			oneLine.setY(capturedY);
			oneLine.setWidth(capturedWidth);
			oneLine.setHeight(capturedHeight);

			BufferedImage lineImage =
				robot.createScreenCapture(new Rectangle(capturedX, capturedY, capturedWidth, capturedHeight));

			boolean allwhite = true;
			for (int i = 0; i < capturedWidth; i++) {
				for (int j = 0; j < capturedHeight; j++) {
					Color color = new Color(lineImage.getRGB(i, j));
					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();

					// white
					if (red == 255 && green == 255 && blue == 255) {
						continue;
					}

					String colorString = red + "x" + green + "x" + blue;
					if (CollectionUtils.isNotEmpty(backgroundColors) && backgroundColors.contains(colorString)) {
						// uniformly force all "background" as white to improve matching %
						lineImage.setRGB(i, j, -1);
						continue;
					}

					allwhite = false;
					oneLine.addColor(color);

					// distinctColors.add(colorString);

					// black
					if (red == 0 && green == 0 && blue == 0) { continue; }

					// some other color
					lineImage.setRGB(i, j, 0);
				}
			}

			if (allwhite) {
				if (logger.isDebugEnabled()) { logger.debug(msgPrefix + "found blank line"); }
				oneLine.setText(null);
				if (!processor.processMatch(oneLine)) {
					if (logger.isDebugEnabled()) { logger.debug("stopping now"); }
					return;
				}
			}

			SeeknowFrame lineFrame = SeeknowFrameBuilder.newSeeknowFrame(lineImage, capturedX, capturedY);
			try { Thread.sleep(500);} catch (InterruptedException e) {}

			Rectangle bounds = lineFrame.getBounds();
			String text = read(bounds);
			lineFrame.close();
			try { Thread.sleep(100);} catch (InterruptedException e) {}

			// String text = read(new Rectangle(capturedX, capturedY, capturedWidth, capturedHeight));

			oneLine.setText(text);
			if (logger.isDebugEnabled()) { logger.debug(msgPrefix + "captured " + text); }

			if (!processor.processMatch(oneLine)) {
				if (logger.isDebugEnabled()) { logger.debug("specified processor vetoed to terminate scanning"); }
				return;
			}
		}

		// System.out.println("distinctColors = " + distinctColors);
	}

	public List<SeeknowData> fromScreenSelection(int x, int y, int width, int height) {
		AcceptAllProcessor processor = new AcceptAllProcessor();
		fromScreenSelection(x, y, width, height, processor);
		return processor.listMatch();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, MULTI_LINE_STYLE)
			       .append("glyphs", glyphs)
			       .append("lineHeight", lineHeight)
			       .append("aggressivelyTrimSpace", aggressivelyTrimSpace)
			       .append("trimSpacesBefore", trimSpacesBefore)
			       .append("trimSpacesAfter", trimSpacesAfter)
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
