package org.uptospeed.seeknow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class Seeknow3Test {
	private static final int SLEEP_BETWEEN_TESTS = 2500;
	private static final int Y_OFFSET = 25;
	private static Seeknow seeknow = null;

	@BeforeClass
	public static void init() throws IOException {
		seeknow = SeeknowFactory.getInstance("glyphs/fixedsys/seeknow.json");
		seeknow.setLineHeight(15);
		try { Thread.sleep(1000); } catch (InterruptedException e) { }
	}

	@Test
	public void fromScreenSelection_uneven_line_heights() throws Throwable {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow3-screencapture.png");
		Thread.sleep(3000);

		List<String> found = new ArrayList<>();

		int x = 0;
		int y = 26;
		int width = f.getWidth() - x;
		int height = 48 - y + Y_OFFSET;

		System.out.println("seeknow over (" + x + "," + y + "," + width + "," + height + ")");
		AcceptAllProcessor processor = new AcceptAllProcessor();
		processor.setStopOnEmptyText(false);
		seeknow.fromScreenSelection(x, y, width, height, processor);
		List<SeeknowData> seeknowData = processor.listMatch();
		seeknowData.forEach(data -> { if (StringUtils.isNotBlank(data.getText())) { found.add(data.getText()); }});

		x = 3;
		y = 72;
		width = f.getWidth() - x;
		height = f.getHeight() - y + Y_OFFSET; // 133

		System.out.println("seeknow over (" + x + "," + y + "," + width + "," + height + ")");
		processor = new AcceptAllProcessor();
		processor.setStopOnEmptyText(false);
		seeknow.fromScreenSelection(x, y, width, height, processor);
		seeknowData = processor.listMatch();
		seeknowData.forEach(data -> { if (StringUtils.isNotBlank(data.getText())) { found.add(data.getText()); }});

		f.close();
		found.forEach(System.out::println);

		// Assert.assertEquals("Runsheet Name: PROD_PYRL_NY Runsheet Desc: NY-PP-00146694", found.get(0));
		// Assert.assertEquals("Runsheet ID: 0009005651", found.get(1));
		Assert.assertEquals("Process Name Process Desc. Jobno Status Pulse", found.get(2));
		Assert.assertEquals("!NUL PROCESS! Site: NYA 100 FINISHED 000000", found.get(3));
		Assert.assertEquals("pdps806 Site: NYA 300 FINISHED 000001", found.get(4));
		Assert.assertEquals("pdps811 Site: NYA 400 FINISHED 000001", found.get(5));
		Assert.assertEquals("4ctl_run Site: 450 FINISHED 000015", found.get(6));
	}

	@Test
	public void fromScreenSelection2() throws Throwable {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow4-processes.png");
		Thread.sleep(3000);

		int x = 15;
		int y = 137;
		int width = 650 - x;
		int height = 557 - y;

		System.out.println("seeknow over (" + x + "," + y + "," + width + "," + height + ")");
		AcceptAllProcessor processor = new AcceptAllProcessor();
		processor.setStopOnEmptyText(false);
		seeknow.fromScreenSelection(x, y, width, height, processor);
		f.close();
		List<SeeknowData> seeknowData = processor.listMatch();

		List<String> found = new ArrayList<>();
		seeknowData.forEach(data -> {
			if (StringUtils.isNotBlank(data.getText())) {
				System.out.println(data);
				found.add(data.getText());
			}
		});

		found.forEach(System.out::println);
		Assert.assertEquals(28, found.size());
		Assert.assertEquals("!NUL PROCESS! Site: CAA 100 FINISHED 000000", found.get(0));
		Assert.assertEquals("pdps806 Site: CAA 300 FINISHED 000001", found.get(1));
		Assert.assertEquals("pdps811 Site: CAA 400 FINISHED 000001", found.get(2));
		Assert.assertEquals("4ctl_run Site: 450 FINISHED 000003", found.get(3));
		Assert.assertEquals("newerrs Site: 475 FINISHED 000001", found.get(4));
		Assert.assertEquals("!NUL PROCESS! Site: CAA 500 FINISHED 000000", found.get(5));
		Assert.assertEquals("pdps816 Site: CAA 600 FINISHED 000001", found.get(6));
		Assert.assertEquals("pdps825 Site: CAA 700 FINISHED 000001", found.get(7));
		Assert.assertEquals("!NUL PROCESS! Site: CAA 800 FINISHED 000000", found.get(8));
		Assert.assertEquals("pdps835 Site: CAA 900 FINISHED 000001", found.get(9));
		Assert.assertEquals("disc842 Site: CAA 1000 FINISHED 000001", found.get(10));
		Assert.assertEquals("pdps855 Site: CAA 1100 FINISHED 000001", found.get(11));
		Assert.assertEquals("pdps852 Site: CAA 1200 FINISHED 000001", found.get(12));
		Assert.assertEquals("pdps858 Site: CAA 1300 FINISHED 000001", found.get(13));
		Assert.assertEquals("pdps880 Site: CAA 1400 FINISHED 000002", found.get(14));
		Assert.assertEquals("pdps033 Site: CAA 1450 FINISHED 000001", found.get(15));
		Assert.assertEquals("printready Site: CAA 1500 FINISHED 000001", found.get(16));
		Assert.assertEquals("!NUL PROCESS! Site: 1501 FINISHED 000000", found.get(17));
		Assert.assertEquals("exec805 Site: CAA 1525 FINISHED 000001", found.get(18));
		Assert.assertEquals("!NUL PROCESS! Site: CAA 1600 FINISHED 000000", found.get(19));
		Assert.assertEquals("!NUL PROCESS! Site: CAA 1615 FINISHED 000000", found.get(20));
		Assert.assertEquals("ddpost.shn Site: CAA 1625 FINISHED 000001", found.get(21));
		Assert.assertEquals("!NUL PROCESS! Site: 1630 FINISHED 000000", found.get(22));
		Assert.assertEquals("!NUL PROCESS! Site: CAA 1650 FINISHED 000000", found.get(23));
		Assert.assertEquals("!NUL PROCESS! Site: CAA 1675 FINISHED 000000", found.get(24));
		Assert.assertEquals("!NUL PROCESS! Site: 1800 FINISHED 000000", found.get(25));
		Assert.assertEquals("!NUL PROCESS! Site: 1900 FINISHED 000000", found.get(26));

	}

	private SeeknowFrame newSeeknowFrame(String resource) throws IOException {
		SeeknowFrame frame = SeeknowFrameBuilder.newInstance()
		                                        .setPath(resource)
		                                        .setAsResource(true)
		                                        .setX(0)
		                                        .setY(Y_OFFSET)
		                                        .build();
		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }
		return frame;
	}
}
