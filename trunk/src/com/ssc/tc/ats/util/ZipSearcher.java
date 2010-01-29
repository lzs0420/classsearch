package com.ssc.tc.ats.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import classfile.ClassFile;

import com.ssc.tc.ats.util.gui.ClassSearcherGui;

public class ZipSearcher {
	private String zipFileName;
	private String searchExp;
	private ZipFile zipFile;
	private ZipInputStream zis;
	private ClassSearcherGui gui;
	public static final int EXPRESSIONSELECED = 0X00;
	public static final int INTERFACESELECED = 0X01;
	public static final int CLASSSELECED = 0X02;
	public static final int METHODSELECED = 0X03;
	public int jobType = EXPRESSIONSELECED;
	private boolean detected = false;

	public void setGui(ClassSearcherGui gui) {
		this.gui = gui;
	}

	public String getSearchExp() {
		return searchExp;
	}

	public void setSearchExp(String searchExp) {
		this.searchExp = searchExp;
		this.initSearchExp();
	}

	public String getZipFileName() {
		return zipFileName;
	}

	public void setZipFileName(String zipFileName) throws IOException {
		this.zipFileName = zipFileName;
		this.initZipFile();
	}

	public ZipSearcher() {
		super();
		this.setJobType(EXPRESSIONSELECED);
	}

	public ZipSearcher(String zipFileName, String searchExp, int jobType)
			throws IOException {
		this(zipFileName);
		this.setJobType(jobType);
		this.setSearchExp(searchExp);
	}

	public ZipSearcher(String zipFileName, String searchExp) throws IOException {
		this(zipFileName);
		this.setJobType(ZipSearcher.EXPRESSIONSELECED);
		this.setSearchExp(searchExp);
	}

	public ZipSearcher(String zipFileName) throws IOException {
		this();
		this.setZipFileName(zipFileName);
		this.setJobType(EXPRESSIONSELECED);
	}

	public ZipSearcher(File zipFile, String searchExp, int jobType)
			throws IOException {
		this(zipFile.getCanonicalPath(), searchExp, jobType);
	}

	public ZipSearcher(File zipFile, String searchExp) throws IOException {
		this(zipFile.getCanonicalPath(), searchExp,
				ZipSearcher.EXPRESSIONSELECED);
	}

	public ZipSearcher(ClassSearcherGui gui, File zipFile, String searchExp,
			int jobType) throws IOException {
		this(zipFile, searchExp, jobType);
		this.setGui(gui);
	}

	private void initZipFile() throws IOException {
		if (this.zipFileName == null)
			return;
		this.zipFile = new ZipFile(this.zipFileName);
		this.zis = new ZipInputStream(new FileInputStream(this.zipFileName));
	}

	private void initSearchExp() {
		if (this.searchExp == null)
			return;
		if (this.jobType == ZipSearcher.EXPRESSIONSELECED) {
			this.searchExp = this.searchExp.replace('.', '/');
			this.searchExp = this.searchExp.replace('\\', '/');
			if (this.searchExp.endsWith("/"))
				return;
			this.searchExp = this.searchExp + ".class";
		}
	}

	private boolean zipEntryMatched(ZipEntry ze) {
		if (this.jobType == ZipSearcher.EXPRESSIONSELECED)
			return ze.getName().indexOf(this.searchExp) >= 0;
		else {
			if (ze.getName().endsWith(".class")) {
				ClassFile cf = new ClassFile(new DataInputStream(zis));
				switch (jobType) {
				case ZipSearcher.INTERFACESELECED:
					String[] inter = cf.getImplementedInterfaces();
					if (inter == null)
						return false;
					for (int i = 0; i < inter.length; ++i) {
						if (inter[i].endsWith(this.getSearchExp()))
							return true;
					}
					break;
				case ZipSearcher.CLASSSELECED:
					String superClass = cf.getSuperClass();
					if (superClass == null)
						return false;
					if (superClass.endsWith(this.getSearchExp()))
						return true;
					break;
				case ZipSearcher.METHODSELECED:
					String[] methods = cf.getMethodNames();
					if (methods == null)
						return false;
					for (int i = 0; i < methods.length; ++i) {
						if (methods[i].endsWith(this.getSearchExp()))
							return true;
					}
					break;
				default:
					break;
				}
			}
			return false;
		}
	}

	public boolean matched() {
		try {
			boolean result = false;
			if (gui != null) {
				gui.getCurrentProgressBar().setMaximum(this.zipFile.size());
				gui.getCurrentProgressBar().setValue(0);
			}
			ZipEntry ze;
			int count = 0;
			while ((ze = zis.getNextEntry()) != null) {
				if (gui != null) {
					gui.getCurrentProgressBar().setValue(++count);
					gui.getCurrentProgressLabel().setText(ze.getName());
				}
				try {
					if (this.zipEntryMatched(ze)) {
						if (gui != null) {
							if (detected == false) {
								gui
										.getEditorKit()
										.insertHTML(
												gui.getDocument(),
												gui.getDocument().getLength(),
												"<a href=\"file://"
														+ zipFileName
																.substring(
																		0,
																		zipFileName
																				.lastIndexOf(File.separator))
														+ "\">" + zipFileName
														+ "</a>",
												0,
												0,
												javax.swing.text.html.HTML.Tag.A);
								gui
										.getEditorKit()
										.insertHTML(
												gui.getDocument(),
												gui.getDocument().getLength(),
												"<br>",
												0,
												0,
												javax.swing.text.html.HTML.Tag.BR);
								detected = true;
							}
							if (this.jobType != ZipSearcher.EXPRESSIONSELECED) {
								gui.getDocument().insertString(
										gui.getDocument().getLength(), "|--",
										null);
								gui.getEditorKit().insertHTML(
										gui.getDocument(),
										gui.getDocument().getLength(),
										"<font color=green>" + ze.getName()
												+ "</font>", 0, 0,
										javax.swing.text.html.HTML.Tag.FONT);
								gui
										.getEditorKit()
										.insertHTML(
												gui.getDocument(),
												gui.getDocument().getLength(),
												"<br>",
												0,
												0,
												javax.swing.text.html.HTML.Tag.BR);
							}
						} else {
							if (detected == false) {
								System.out.println(zipFileName);
								detected = true;
							}
							if (this.jobType != ZipSearcher.EXPRESSIONSELECED) {
								System.out.println("|--" + ze.getName());
							}
						}
						result = true;
						if (this.jobType == ZipSearcher.EXPRESSIONSELECED)
							return result;
					}
				} catch (Exception e) {
					String warn = ze.getName() + " decompile failed";
					if (gui != null)
						gui.getWarnMsg().add(ze.getName());
					else
						System.err.println(warn);
				} finally {
					zis.closeEntry();
				}
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (this.zipFile != null)
					this.zipFile.close();
				if (zis != null)
					this.zis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getJobType() {
		return jobType;
	}

	public void setJobType(int jobType) {
		this.jobType = jobType;
	}
}
