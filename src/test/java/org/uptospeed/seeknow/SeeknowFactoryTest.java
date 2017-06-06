package org.uptospeed.seeknow;

public class SeeknowFactoryTest {
	public static void main(String[] args) throws Throwable {
		Seeknow instance = SeeknowFactory.getInstance("/glyphs/test/seeknow.json");
		System.out.println("instance = " + instance);
	}
}
