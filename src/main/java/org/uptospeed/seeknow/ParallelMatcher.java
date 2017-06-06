package org.uptospeed.seeknow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.collections4.IteratorUtils;
import org.sikuli.script.Match;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parallel matcher is used to execute matching operation in separate thread.
 */
public class ParallelMatcher implements Callable<List<Match>> {
	private Logger logger = LoggerFactory.getLogger(getClass());

	/** Glyph to be recognized */
	private Glyph glyph = null;

	/** Region in which we should search for given glyph */
	private Region region = null;

	/** Match - glyph mapping */
	private Map<Match, Glyph> mapping = null;

	/** Latch */
	private CountDownLatch latch = null;

	/**
	 * Construct me.
	 *
	 * @param glyph   - glyph to be recognized
	 * @param region  - region in which we should search for glyps
	 * @param mapping - mapping
	 * @param latch   - latch
	 */
	public ParallelMatcher(Glyph glyph, Region region, Map<Match, Glyph> mapping, CountDownLatch latch) {
		this.glyph = glyph;
		this.region = region;
		this.mapping = mapping;
		this.latch = latch;
	}

	@Override
	public List<Match> call() throws Exception {
		String msgPrefix = "glyph " + glyph.getCharacter() + " ";

		try {
			Iterator<Match> all = region.findAll(glyph.getPattern());

			if (IteratorUtils.isEmpty(all)) {
				if (logger.isDebugEnabled()) { logger.debug(msgPrefix + "NOT MATCHED in specified region"); }
				return null;
			}

			List<Match> matches = new ArrayList<>();
			while (all.hasNext()) {
				Match m = all.next();
				matches.add(m);
				mapping.put(m, glyph);

				if (logger.isDebugEnabled()) { logger.debug(msgPrefix + "matched at " + m.getCenter()); }
			}

			return matches;
		} finally {
			latch.countDown();
		}
	}
}
