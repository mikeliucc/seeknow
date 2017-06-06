package org.uptospeed.seeknow;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SeeknowFactory {
	/** cache. */
	private final static Map<String, Seeknow> INSTANCES = new HashMap<>();

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
	                                                  .disableHtmlEscaping()
	                                                  .disableInnerClassSerialization()
	                                                  .setLenient()
	                                                  .create();
	private static final int BUFFER_SIZE = 8192;

	public static Seeknow getInstance(String path) throws IOException {
		if (StringUtils.isBlank(path)) { throw new IllegalArgumentException("path must be specified!"); }

		Seeknow seeknow = INSTANCES.get(path);
		if (seeknow == null) {
			File file = new File(path);
			boolean asResource = !file.isFile();

			String basePath;
			String jsonContent;

			if (asResource) {
				path = StringUtils.prependIfMissing(path, "/");
				basePath = StringUtils.substringBeforeLast(path, "/");
				jsonContent = getResourceContent(path);
				if (jsonContent == null) { return null; }
			} else {
				File configFile = new File(path);
				basePath = configFile.getAbsolutePath();
				jsonContent = FileUtils.readFileToString(configFile, "UTF-8");
			}

			seeknow = GSON.fromJson(jsonContent, Seeknow.class);
			if (seeknow == null || CollectionUtils.isEmpty(seeknow.getGlyphs())) {
				throw new IOException("Unable to load seeknow configuration from " + path);
			}

			INSTANCES.put(path, seeknow);
			seeknow.getGlyphs().forEach(glyph -> glyph.init(basePath, asResource));
		}
		return seeknow;
	}

	private static String getResourceContent(String path) throws IOException {
		String jsonContent;
		InputStream is = SeeknowFactory.class.getResourceAsStream(path);
		if (is == null) { return null; }

		BufferedInputStream bis = new BufferedInputStream(is);

		StringBuilder sb = new StringBuilder();
		byte[] buffer = new byte[BUFFER_SIZE];
		do {
			int read = bis.read(buffer);
			if (read == -1) { break; }

			sb.append(new String(buffer, 0, read, "UTF-8"));
			buffer = new byte[BUFFER_SIZE];
		} while (true);

		jsonContent = sb.toString();
		return jsonContent;
	}


}
