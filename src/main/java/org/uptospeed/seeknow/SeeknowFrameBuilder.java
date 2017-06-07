package org.uptospeed.seeknow;

import java.awt.*;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import static java.awt.image.BufferedImage.*;
import static java.lang.Integer.MIN_VALUE;

public final class SeeknowFrameBuilder {
	private String path;
	private boolean asResource;
	private boolean convertBW;
	private int x = MIN_VALUE;
	private int y = MIN_VALUE;

	private SeeknowFrameBuilder() { }

	static SeeknowFrameBuilder newInstance() { return new SeeknowFrameBuilder(); }

	SeeknowFrameBuilder setPath(String path) {
		this.path = path;
		return this;
	}

	SeeknowFrameBuilder setAsResource(boolean asResource) {
		this.asResource = asResource;
		return this;
	}

	SeeknowFrameBuilder setConvertBW(boolean convertBW) {
		this.convertBW = convertBW;
		return this;
	}

	SeeknowFrameBuilder setX(int x) {
		this.x = x;
		return this;
	}

	SeeknowFrameBuilder setY(int y) {
		this.y = y;
		return this;
	}

	SeeknowFrame build() throws IOException {
		if (StringUtils.isBlank(path)) { throw new IllegalArgumentException("path is required!"); }

		if (x < 0) { throw new IllegalArgumentException("x-axis must be zero or greater"); }
		if (y < 0) { throw new IllegalArgumentException("y-axis must be zero or greater"); }

		byte[] imageData;
		if (asResource) {
			imageData = toImageData(path);
		} else {
			File f = new File(path);
			if (!f.exists()) { throw new IllegalArgumentException("Specific file does not exists: " + path); }
			imageData = FileUtils.readFileToByteArray(f);
		}

		if (ArrayUtils.isEmpty(imageData)) {
			throw new IllegalArgumentException("Unable to obtain image data from " + path);
		}

		if (convertBW) { imageData = convertToBlackAndWhite(imageData);}

		return new SeeknowFrame(imageData, x, y);
	}

	static byte[] convertToBlackAndWhite(byte[] imageData) throws IOException {
		if (ArrayUtils.isEmpty(imageData)) { return imageData; }

		ImageInputStream iis = new MemoryCacheImageInputStream(new ByteArrayInputStream(imageData));
		BufferedImage orginalImage = ImageIO.read(iis);
		BufferedImage blackAndWhiteImg =
			new BufferedImage(orginalImage.getWidth(), orginalImage.getHeight(), TYPE_BYTE_BINARY);
		Graphics2D graphics = blackAndWhiteImg.createGraphics();
		graphics.drawImage(orginalImage, 0, 0, null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
		ImageIO.write(blackAndWhiteImg, "png", ios);

		return baos.toByteArray();
	}

	// static SeeknowFrame newInstance(String path) {
	// 	if (StringUtils.isBlank(path)) { throw new IllegalArgumentException("path is required!"); }
	//
	// 	File f = new File(path);
	// 	if (!f.exists()) { throw new IllegalArgumentException("Specific file does not exists: " + path); }
	//
	// 	try {
	// 		byte[] imageData = FileUtils.readFileToByteArray(f);
	// 		return new SeeknowFrame(imageData);
	// 	} catch (IOException e) {
	// 		throw new IllegalArgumentException("Unable to read content from " + path + ": " + e.getMessage());
	// 	}
	// }
	//
	// static SeeknowFrame newInstanceViaResource(String resourcePath, boolean convertBW) {
	// 	byte[] imageData = toImageData(resourcePath);
	//
	// 	if (convertBW) {
	// 		try {
	// 			imageData = convertToBlackAndWhite(imageData);
	// 		} catch (IOException e) {
	// 			throw new IllegalArgumentException("Unable to convert image to black and white: " + e.getMessage());
	// 		}
	// 	}
	//
	// 	return new SeeknowFrame(imageData);
	// }
	// static SeeknowFrame newInstanceViaResource(String resourcePath) { return new SeeknowFrame(toImageData(resourcePath)); }

	static SeeknowFrame newSeeknowFrame(BufferedImage img, int x, int y) {
		return new SeeknowFrame(toImageData(img), x, y);
	}

	static byte[] toImageData(BufferedImage img) {
		// WritableRaster raster = img.getRaster();
		// DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
		// return (data.getData());

		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			ImageIO.write(img, "PNG", baos);
			baos.flush();

			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (baos != null) { try { baos.close();} catch (IOException e) {}}
		}
	}

	static byte[] toImageData(String resourcePath) {
		byte[] imageData;
		if (StringUtils.isBlank(resourcePath)) { throw new IllegalArgumentException("resourcePath is required!"); }

		resourcePath = StringUtils.prependIfMissing(resourcePath, "/");
		URL resource = SeeknowFrame.class.getResource(resourcePath);

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
