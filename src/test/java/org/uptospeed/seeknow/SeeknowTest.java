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
	public void test_read1() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/test.png");

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("TEST1234", text);
	}

	@Test
	public void test_read2() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/test2.png");

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("ABCDEFGHIJKL", text);
	}

	@Test
	public void test_read3() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/test3.png");

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("0123456789", text);
	}

	@Test
	public void test_read4() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/test4.png");

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("MNOPRSTUVWXYZ", text);
	}

	@Test
	public void test_read5() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/test5.png");

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("ABCD1234", text);
	}
}
