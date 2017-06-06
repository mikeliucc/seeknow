package org.uptospeed.seeknow;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.awt.image.BufferedImage.*;

public class Seeknow2Test {

	private static final int SLEEP_BETWEEN_TESTS = 2500;
	private static Seeknow seeknow = null;

	@BeforeClass
	public static void init() throws IOException {
		seeknow = SeeknowFactory.getInstance("glyphs/fixedsys/seeknow.json");
		try { Thread.sleep(1000);} catch (InterruptedException e) { }
	}

	@Test
	public void testLine1() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/seeknow2-test1.png");

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("PROD_PYRL_CA CA-PP-00146690 FINISHED 000032", text);
	}

	@Test
	public void testLine2() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/seeknow2-test2.png", true);

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("PROD_PYRL_CA CA-PP-00146693 RUNNING 011649", text);
	}

	@Test
	public void testLine3() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/seeknow2-test3.png");

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("PROD_PYRL_NY NY-PP-00146694 FINISHED 008308", text);
	}

	@Test
	public void testLine4() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/seeknow2-test4.png");

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("ROD_PYRL_FL FL-A-00146695 FINISHED 000031", text);
	}

	@Test
	public void testAllLines() {
		ImageFrame f = ImageFrame.newInstanceViaResource("/images/seeknow2-test-all_lines.png");
		// ImageFrame f = new ImageFrame(toImageData(resourcePath));

		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		List<String> lines = seeknow.readMultilines(f.getBounds());
		f.close();

		System.out.println(lines);
		// Assert.assertEquals("TEST1234", text);
	}

	@Test
	public void testMultilineParsing() throws Throwable {

		ImageFrame f = ImageFrame.newInstanceViaResource("/images/seeknow2-test1 original.png");
		f.setLocation(0, 20);
		f.setVisible(true);
		Thread.sleep(5000);

		int lineHeight = 15;
		int numberOfLines = (int) Math.ceil(f.getHeight() / lineHeight);

		int x = 10;
		int height = lineHeight;
		int capturedHeight = f.getHeight();

		System.out.println("numberOfLines=" + numberOfLines + ", captured height=" + capturedHeight);

		Robot robot = new Robot();
		robot.setAutoWaitForIdle(true);

		List<String> lines = new ArrayList<>();
		for (int lineNo = 0; lineNo < numberOfLines; lineNo++) {
			int y = 45 + (lineNo * lineHeight);
			int width = f.getWidth() - x;
			if ((y + height) > capturedHeight) { height = capturedHeight - y + lineHeight; }
			System.out.println("x=" + x + ", y=" + y + ", width=" + width + ", height=" + height);

			BufferedImage lineImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
			boolean allwhite = true;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					Color color = new Color(lineImage.getRGB(i, j));
					int red = color.getRed();
					int green = color.getGreen();
					int blue = color.getBlue();
					if (red == 255 && green == 255 && blue == 255) {
						// white
						continue;
					}

					allwhite = false;
					if (red == 0 && green == 255 && blue == 255) {
						// black
						continue;
					}

					lineImage.setRGB(i, j, 0);
				}
			}

			if (allwhite) {
				System.out.println("found blank lines... we're done");
				break;
			}

			ImageFrame lineFrame = ImageFrame.newInstance(lineImage);
			lineFrame.setLocation(0, 20 + capturedHeight + 10);

			if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) {}}

			String line = seeknow.read(lineFrame.getBounds());
			lineFrame.close();

			System.out.println(line);
			lines.add(line);
		}

		f.close();

		System.out.println("lines = " + lines);
	}

	@Test
	public void testMultilineParsing2() throws Throwable {
		int lineNo = 1;
		int height = 15;
		int width = 522;

		BufferedImage image = ImageIO.read(this.getClass().getResource("/images/seeknow2-test-all_lines.png"));
		BufferedImage img = image.getSubimage(0, (lineNo * height), width, height);
		BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), TYPE_BYTE_BINARY);
		Graphics g = copyOfImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		ImageIO.write(copyOfImage, "PNG", new File("/Users/ml093043/tmp/junk.png"));


	}
}
