package cn.edu.zju.nwrt.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.StringTokenizer;

import cn.edu.zju.nwrt.utill.gui.ClassSearcherGui;

public class ClassSearcher {

	private File[] dirs;

	private int count = 0;

	private String exp;

	private String className;

	private String interName;

	private String methodName;

	private boolean gui = true;

	public ClassSearcher(String[] args) {
		super();
		if (this.parseCmd(args)) {
			if (this.gui) {
				ClassSearcherGui csg = new ClassSearcherGui(null, exp);
				csg.openDialog();
			} else {
				for (int i = 0; i < dirs.length; ++i)
					this.process(dirs[i]);
				System.out.println("\nSearch Job completed!\nTotally " + count
						+ " detected!");
			}
		}
	}

	private boolean parseCmd(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			if (args[i].equals("-e"))
				this.exp = args[++i];
			if (args[i].equals("-c"))
				this.className = args[++i];
			if (args[i].equals("-i"))
				this.interName = args[++i];
			if (args[i].equals("-m"))
				this.methodName = args[++i];
			if (args[i].equals("-dir")) {
				StringTokenizer st = new StringTokenizer(args[++i], ";");
				dirs = new File[st.countTokens()];
				int j = 0;
				while (st.hasMoreTokens()) {
					this.dirs[j++] = new File(st.nextToken());
				}
			}
			if (args[i].equals("-cmd"))
				this.gui = false;
		}
		if (((this.exp == null && this.interName == null
				&& this.className == null && this.methodName == null) || this.dirs == null)
				&& this.gui == false) {
			System.out.println("ClassSearcher [options]");
			System.out.println("-e Expressoin, follow a query expression");
			System.out.println("-c ClassName, follow a query class name");
			System.out
					.println("-i InterfaceName, follow a query interface name");
			System.out.println("-m MethodName, follow a query method name");
			System.out
					.println("-dir directories, follow to-be-queried files and directories, seprated by ;");
			System.out
					.println("\nInstructions to expressions\n . \\ and / seprate is supported\n\".ws.Buffer\" presents \"com/ibm/ws/Buffer.class\" and \"com/sun/ws/Buffer.class\" will be matched.");
			System.out
					.println(" \".ws.\" presents \"com/ibm/ws/Buffer.class\" and \"com/sun/ws/Client.class\" will be matched");
			System.out
					.println("\n.zip .jar .war and .ear will be supported in this util.");
			System.out
					.println("\nYou can run this application in command line mode with \"-cmd\" option.");
			return false;
		}
		return true;
	}

	private void process(File file) {
		if (file.isDirectory() == false) {
			try {
				ZipSearcher zs = null;
				if (this.exp != null)
					zs = new ZipSearcher(file, this.exp);
				else if (this.interName != null)
					zs = new ZipSearcher(file, this.interName,
							ZipSearcher.INTERFACESELECED);
				else if (this.className != null)
					zs = new ZipSearcher(file, this.className,
							ZipSearcher.CLASSSELECED);
				else if (this.methodName != null)
					zs = new ZipSearcher(file, this.methodName,
							ZipSearcher.METHODSELECED);
				else
					return;
				if (zs.matched()) {
					count++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		File[] files = file.listFiles(new MyFilter());
		for (int i = 0; i < files.length; ++i) {
			try {
				process(files[i]);
			} catch (Exception e) {
				String warn = files[i].getName() + " process failed";
				System.err.println(warn);
			}
		}
	}

	private class MyFilter implements FileFilter {
		public boolean accept(File pathname) {
			if (pathname.getName().indexOf(".zip") != -1)
				return true;
			if (pathname.getName().indexOf(".jar") != -1)
				return true;
			if (pathname.getName().indexOf(".war") != -1)
				return true;
			if (pathname.getName().indexOf(".ear") != -1)
				return true;
			if (pathname.isDirectory())
				return true;
			return false;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ClassSearcher(args);
	}

}
