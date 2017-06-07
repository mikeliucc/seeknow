package org.uptospeed.seeknow;

import java.awt.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SeeknowData implements Serializable {
	private String text;
	private int lineNumber;
	private int x;
	private int y;
	private int width;
	private int height;
	private Set<Color> colors = new HashSet<>();

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

	public Set<Color> getColors() { return colors;}

	public void setColors(Set<Color> colors) { this.colors = colors;}

	public void addColor(Color color) { this.colors.add(color); }

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
			       .append("lineNumber", lineNumber)
			       .append("text", text)
			       .append("colors", colors)
			       .append("dimension", "(" + x + "," + y + "," + width + "," + height + ")")
			       .toString();
	}

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
		return new HashCodeBuilder(17, 37)
			       .append(x)
			       .append(y)
			       .append(width)
			       .append(height)
			       .toHashCode();
	}
}
