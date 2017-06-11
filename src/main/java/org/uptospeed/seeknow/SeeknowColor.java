package org.uptospeed.seeknow;

import java.awt.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * custom color so that we can have our own {@link #toString()}.
 */
public class SeeknowColor {
	private Color color;

	public SeeknowColor(Color color) { this.color = color; }

	public int getRed() { return color.getRed(); }

	public int getGreen() { return color.getGreen(); }

	public int getBlue() { return color.getBlue(); }

	public int getAlpha() { return color.getAlpha(); }

	public int getRGB() { return color.getRGB(); }

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }

		SeeknowColor that = (SeeknowColor) o;
		return new EqualsBuilder().append(color, that.color).isEquals();
	}

	@Override
	public int hashCode() { return new HashCodeBuilder(17, 37).append(color).toHashCode(); }

	@Override
	public String toString() {
		return "[red=" + color.getRed() + ", green=" + color.getGreen() + ", blue=" + color.getBlue() + "]";
	}
}
