package org.uptospeed.seeknow;

import java.awt.*;
import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SeeknowData implements Serializable {
	private String text;
	private int lineNumber;
	private int x;
	private int y;
	private int width;
	private int height;
	private SeeknowColorSet colors = new SeeknowColorSet();

	public String getText() { return text;}

	public void setText(String text) { this.text = text;}

	public int getLineNumber() { return lineNumber;}

	public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber;}

	public int getX() { return x;}

	public void setX(int x) { this.x = x;}

	public int getY() { return y;}

	public void setY(int y) { this.y = y;}

	public int getWidth() { return width;}

	public void setWidth(int width) { this.width = width;}

	public int getHeight() { return height;}

	public void setHeight(int height) { this.height = height;}

	public SeeknowColorSet getColors() { return colors;}

	public void setColors(SeeknowColorSet colors) { this.colors = colors;}

	public void addColor(Color color) { this.colors.add(new SeeknowColor(color)); }

	public void addColor(SeeknowColor color) { this.colors.add(color); }

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }

		if (o == null || getClass() != o.getClass()) { return false; }

		SeeknowData that = (SeeknowData) o;
		return new EqualsBuilder()
			       .append(x, that.x)
			       .append(y, that.y)
			       .append(width, that.width)
			       .append(height, that.height)
			       .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(x).append(y).append(width).append(height).toHashCode();
	}

	@Override
	public String toString() {
		return "lineNumber=" + lineNumber + "\n" +
		       "text=" + text + "\n" +
		       "colors=" + colors + "\n" +
		       "x=" + x + "\n" +
		       "y=" + y + "\n" +
		       "width=" + width + "\n" +
		       "height=" + height + "\n";
	}
}
