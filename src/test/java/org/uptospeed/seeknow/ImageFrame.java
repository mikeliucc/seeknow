package org.uptospeed.seeknow;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import static java.awt.event.KeyEvent.*;
import static java.awt.event.WindowEvent.*;
import static java.awt.image.BufferedImage.*;

/**
 * Display image on screen.
 */
public class ImageFrame extends JFrame implements KeyListener {

	private static final long serialVersionUID = 7468239430795306535L;

	private ImageFrame(byte[] imageData) {
		addKeyListener(this);
		setContentPane(new JLabel(new ImageIcon(imageData)));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setUndecorated(true);
		setLocation(100, 100);
		pack();
		setVisible(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	// public static void main(String[] args) {
	// 	new ImageFrame("src\\main\\resources\\images\\test.png");
	// }

	@Override
	public void keyPressed(KeyEvent e) { if (e.getKeyCode() == VK_ESCAPE) { close();}}

	static ImageFrame newInstance(String path) {
		if (StringUtils.isBlank(path)) { throw new IllegalArgumentException("path is required!"); }

		File f = new File(path);
		if (!f.exists()) { throw new IllegalArgumentException("Specific file does not exists: " + path); }

		try {
			byte[] imageData = FileUtils.readFileToByteArray(f);
			return new ImageFrame(imageData);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to read content from " + path + ": " + e.getMessage());
		}
	}

	static ImageFrame newInstanceViaResource(String resourcePath, boolean convertBW) {
		byte[] imageData = toImageData(resourcePath);

		if (convertBW) {
			try {
				ImageInputStream iis = new MemoryCacheImageInputStream(new ByteArrayInputStream(imageData));
				BufferedImage orginalImage = ImageIO.read(iis);
				BufferedImage blackAndWhiteImg = new BufferedImage(orginalImage.getWidth(),
				                                                   orginalImage.getHeight(),
				                                                   TYPE_BYTE_BINARY);
				Graphics2D graphics = blackAndWhiteImg.createGraphics();
				graphics.drawImage(orginalImage, 0, 0, null);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
				ImageIO.write(blackAndWhiteImg, "png", ios);

				imageData = baos.toByteArray();
			} catch (IOException e) {
				throw new IllegalArgumentException("Unable to convert image to black and white: " + e.getMessage());
			}
		}

		return new ImageFrame(imageData);
	}

	static ImageFrame newInstance(BufferedImage img) { return new ImageFrame(toImageData(img)); }

	static ImageFrame newInstanceViaResource(String resourcePath) { return new ImageFrame(toImageData(resourcePath)); }

	void close() {
		WindowEvent we = new WindowEvent(this, WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(we);
	}

	private static byte[] toImageData(BufferedImage img) {
		// WritableRaster raster = img.getRaster();
		// DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
		// return (data.getData());

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "PNG", baos);
			baos.flush();
			byte[] imageData = baos.toByteArray();
			baos.close();

			return imageData;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] toImageData(String resourcePath) {
		byte[] imageData;
		if (StringUtils.isBlank(resourcePath)) { throw new IllegalArgumentException("resourcePath is required!"); }

		resourcePath = StringUtils.prependIfMissing(resourcePath, "/");
		URL resource = ImageFrame.class.getResource(resourcePath);

		try {
			BufferedInputStream bais = new BufferedInputStream(resource.openStream());

			List<Byte> packed = new ArrayList<>();
			int bufferLength = 8 * 1024;
			byte[] buffer = new byte[bufferLength];
			int bytesRead = bais.read(buffer, 0, bufferLength);

			while (bytesRead != -1) {
				for (int i = 0; i < bytesRead; i++) { packed.add(buffer[i]); }
				buffer = new byte[bufferLength];
				bytesRead = bais.read(buffer, 0, bufferLength);
			}

			imageData = new byte[packed.size()];
			for (int i = 0; i < packed.size(); i++) { imageData[i] = packed.get(i); }
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to read content from " + resourcePath + ": " + e.getMessage());
		}
		return imageData;
	}
}
