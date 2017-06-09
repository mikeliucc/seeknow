package org.uptospeed.seeknow;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class Seeknow2Test {
	private static final int SLEEP_BETWEEN_TESTS = 2500;
	private static final int Y_OFFSET = 21;
	private static Seeknow seeknow = null;

	@BeforeClass
	public static void init() throws IOException {
		seeknow = SeeknowFactory.getInstance("glyphs/fixedsys/seeknow.json");
		try { Thread.sleep(1000); } catch (InterruptedException e) { }
	}

	@Test
	public void testLine1() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow2-test1.png");

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("PROD_PYRL_CA CA-PP-00146690 FINISHED 000032", text);
	}

	@Test
	public void testLine2() throws Exception {
		SeeknowFrame f = SeeknowFrameBuilder.newInstance()
		                                    .setPath("/images/seeknow2-test2.png")
		                                    .setAsResource(true)
		                                    .setX(0)
		                                    .setY(Y_OFFSET)
		                                    .setConvertBW(true)
		                                    .build();
		if (SLEEP_BETWEEN_TESTS > 0) { try { Thread.sleep(SLEEP_BETWEEN_TESTS);} catch (InterruptedException e) { } }

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("PROD_PYRL_CA CA-PP-00146693 RUNNING 011649", text);
	}

	@Test
	public void testLine3() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow2-test3.png");

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("PROD_PYRL_NY NY-PP-00146694 FINISHED 008308", text);
	}

	@Test
	public void testLine4() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow2-test4.png");

		String text = seeknow.read(f.getBounds());
		f.close();

		System.out.println(text);
		Assert.assertEquals("ROD_PYRL_FL FL-A-00146695 FINISHED 000031", text);
	}

	@Test
	public void testAllLines() throws Exception {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow2-test-all_lines.png");

		List<String> lines = seeknow.readMultilines(f.getBounds());
		f.close();

		System.out.println(lines);
		// Assert.assertEquals("TEST1234", text);
	}

	@Test
	public void testMultilineParsing() throws Throwable {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow2-test1 original.png");

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
			int y = 46 + (lineNo * lineHeight);
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
					if (red == 0 && green == 0 && blue == 0) {
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

			SeeknowFrame lineFrame = SeeknowFrameBuilder.newSeeknowFrame(lineImage, 0, 30 + capturedHeight);
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
	public void fromScreenSelection() throws Throwable {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow2-test1 original.png");
		Thread.sleep(3000);

		int x = 10;
		int y = 45;
		int width = f.getWidth() - x;
		int height = f.getHeight() - y + Y_OFFSET;

		System.out.println("seeknow over (" + x + "," + y + "," + width + "," + height + ")");
		AcceptAllProcessor processor = new AcceptAllProcessor();
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

		List<String> expected = Arrays.asList("PROD_PYRL_CA CA-PP-00146690 FINISHED 000032",
		                                      "PROD_PYRL_CA CA-PP-00146693 RUNNING 011649",
		                                      "PROD_PYRL_NY NY-PP-00146694 FINISHED 008308",
		                                      "PROD_PYRL_FL FL-A-00146695 FINISHED 000031");
		Assert.assertEquals(expected, found);
	}

	@Test
	public void fromScreenSelection2() throws Throwable {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow2-test1 original.png");
		Thread.sleep(3000);

		int x = 10;
		int y = 46;
		int width = f.getWidth() - x;
		int height = f.getHeight() - y + Y_OFFSET;

		System.out.println("seeknow over (" + x + "," + y + "," + width + "," + height + ")");
		FirstContainsProcessor processor = new FirstContainsProcessor("NY-PP-00146694");
		seeknow.fromScreenSelection(x, y, width, height, processor);
		f.close();
		List<SeeknowData> seeknowData = processor.listMatch();

		Assert.assertEquals(1, CollectionUtils.size(seeknowData));
		Assert.assertEquals("PROD_PYRL_NY NY-PP-00146694 FINISHED 008308", seeknowData.get(0).getText());
		Assert.assertEquals(2, seeknowData.get(0).getLineNumber());
	}

	@Test
	public void fromScreenSelection_test_red_text() throws Throwable {
		SeeknowFrame f = newSeeknowFrame("/images/seeknow2-test1 original.png");
		Thread.sleep(3000);

		int x = 10;
		int y = 46;
		int width = f.getWidth() - x;
		int height = f.getHeight() - y + Y_OFFSET;

		System.out.println("seeknow over (" + x + "," + y + "," + width + "," + height + ")");
		SeeknowProcessor firstRedProcessor = new SeeknowProcessor() {
			protected List<SeeknowData> data = new ArrayList<>();

			@Override
			public boolean processMatch(SeeknowData match) {
				if (match == null || StringUtils.isBlank(match.getText())) { return false; }
				if (CollectionUtils.isEmpty(match.getColors())) { return true; }

				String redColor = Color.RED.toString();
				for (Color color : match.getColors()) {
					if (StringUtils.equals(color.toString(), redColor)) {
						data.add(match);
						return false;
					}
				}

				return true;
			}

			@Override
			public List<SeeknowData> listMatch() { return data; }
		};

		seeknow.fromScreenSelection(x, y, width, height, firstRedProcessor);
		f.close();
		List<SeeknowData> seeknowData = firstRedProcessor.listMatch();

		Assert.assertEquals(1, CollectionUtils.size(seeknowData));
		Assert.assertEquals("PROD_PYRL_CA CA-PP-00146693 RUNNING 011649", seeknowData.get(0).getText());
		Assert.assertEquals(1, seeknowData.get(0).getLineNumber());
	}

	// poc on saving subimages
	// @Test
	// public void testMultilineParsing2() throws Throwable {
	// 	int lineNo = 1;
	// 	int height = 15;
	// 	int width = 522;
	//
	// 	BufferedImage image = ImageIO.read(this.getClass().getResource("/images/seeknow2-test-all_lines.png"));
	// 	BufferedImage img = image.getSubimage(0, (lineNo * height), width, height);
	// 	BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), TYPE_BYTE_BINARY);
	// 	Graphics g = copyOfImage.createGraphics();
	// 	g.drawImage(img, 0, 0, null);
	// 	ImageIO.write(copyOfImage, "PNG", new File("/Users/ml093043/tmp/junk.png"));
	// }

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
