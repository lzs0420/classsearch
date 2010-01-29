package com.ssc.tc.ats.util.test;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTest {

	public ZipTest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		ZipFile zf = new ZipFile("test.zip");
		Enumeration<? extends ZipEntry> all = zf.entries();
		while (all.hasMoreElements()) {
			System.out.println((all.nextElement()).getName());
		}
		zf.close();
	}

}
