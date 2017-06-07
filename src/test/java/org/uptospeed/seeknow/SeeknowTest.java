package org.uptospeed.seeknow;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SeeknowTest {

	private static final int SLEEP_BETWEEN_TESTS = 100;
	private static Seeknow seeknow = null;

	@BeforeClass
	public static void init() throws IOException {
		seeknow = SeeknowFactory.getInstance("glyphs/test/seeknow.json");
		try { Thread.sleep(1000);} catch (InterruptedException e) { }
	}

	@Test
	public void test_read1() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/test.png");

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("TEST1234", text);
	}

	@Test
	public void test_read2() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/test2.png");

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("ABCDEFGHIJKL", text);
	}

	@Test
	public void test_read3() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/test3.png");

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("0123456789", text);
	}

	@Test
	public void test_read4() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/test4.png");

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("MNOPRSTUVWXYZ", text);
	}

	@Test
	public void test_read5() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/test5.png");

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("ABCD1234", text);
	}

	private SeeknowFrame newSeeknowFrame(String resource) throws IOException {
		SeeknowFrame frame = SeeknowFrameBuilder.newInstance()
		                                        .setPath(resource)
		                                        .setAsResource(true)
		                                        .setX(0)
		                                        .setY(20)
		                                        .build();
		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }
		return frame;
	}
}
