package org.uptospeed.seeknow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class Seeknow3Test {
	private static final int SLEEP_BETWEEN_TESTS = 2500;
	private static Seeknow seeknow = null;

	@BeforeClass
	public static void init() throws IOException {
		seeknow = SeeknowFactory.getInstance("glyphs/fixedsys/seeknow.json");
		try { Thread.sleep(1000); } catch (InterruptedException e) { }
	}

	@Test
	public void fromScreenSelection() throws Throwable {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow3-screencapture.png");
		Thread.sleep(3000);

		int x = 0;
		int y = 26;
		int width = f.getWidth();
		int height = f.getHeight() + 25;

		System.out.println("seeknow over (" + x + "," + y + "," + width + "," + height + ")");
		AcceptAllProcessor processor = new AcceptAllProcessor();
		processor.setStopOnEmptyText(false);
		seeknow.fromScreenSelection(x, y, width, height, processor);
		List<SeeknowData> seeknowData = processor.listMatch();

		List<String> found = new ArrayList<>();
		seeknowData.forEach(data -> {
			if (StringUtils.isNotBlank(data.getText())) {
				System.out.println(data);
				found.add(data.getText());
			}
		});

		System.out.println("found = " + found);
	}

	private SeeknowFrame newSeeknowFrame(String resource) throws IOException {
		SeeknowFrame frame = SeeknowFrameBuilder.newInstance()
		                                        .setPath(resource)
		                                        .setAsResource(true)
		                                        .setX(0)
		                                        .setY(25)
		                                        .build();
		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }
		return frame;
	}
}
