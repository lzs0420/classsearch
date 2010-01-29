package com.ssc.tc.ats.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import com.ssc.tc.ats.util.FileCount;
import com.ssc.tc.ats.util.ZipSearcher;
import com.swtdesigner.SwingResourceManager;

public class ClassSearcherGui extends JFrame {

	private JList list;
	private JTextField expTextField;
	private JLabel currentProgressLabel;
	private JProgressBar currentProgressBar;
	private JLabel wholeProgressLabel;
	private JProgressBar wholeProgressBar;
	private JCheckBox jarCheckBox;
	private JCheckBox zipCheckBox;
	private JCheckBox warCheckBox;
	private JCheckBox earCheckBox;
	private JButton startButton;
	private JButton cancelButton;
	private JButton resetButton;
	private int matchedCount = 0, processCount = 0;
	private boolean jarTag = true, zipTag = true, warTag = true, earTag = true;
	private boolean jobCanceled = false;
	private Thread thread;
	private static final long serialVersionUID = -3860793932596409358L;
	private String exp;
	private File dir;
	private JEditorPane resultEditorPane;
	private Integer curJob = ZipSearcher.EXPRESSIONSELECED;
	private ArrayList<File> fileList = new ArrayList<File>();
	private HTMLDocument document;
	private HTMLEditorKit editorKit;
	private ArrayList<String> warnMsg = new ArrayList<String>();

	public ArrayList<String> getWarnMsg() {
		return warnMsg;
	}

	public HTMLEditorKit getEditorKit() {
		return editorKit;
	}

	public void openDialog() {
		try {
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			centerDialog(this);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ClassSearcherGui() {
		super();
		setIconImage(SwingResourceManager.getImage(ClassSearcherGui.class,
				"searcher.gif"));
		String os = System.getProperty("os.name");
		if (os.indexOf("indow") >= 0)
			ClassSearcherGui.setLookAndFeel();
		setSize(500, 600);
		setName("mainDialog");
		setTitle("Java Class Searcher");

		final JPanel panel_3 = new JPanel();
		panel_3.setMinimumSize(new Dimension(450, 500));
		panel_3.setLayout(new BorderLayout());
		getContentPane().add(panel_3);

		final JPanel inputPanel = new JPanel();
		panel_3.add(inputPanel, BorderLayout.NORTH);
		inputPanel.setMinimumSize(new Dimension(0, 165));
		inputPanel.setLayout(new BorderLayout());
		inputPanel.setPreferredSize(new Dimension(0, 280));
		inputPanel.setBorder(new TitledBorder(null,
				"Please specify following parameters ...",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel panel_4 = new JPanel();
		inputPanel.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new BorderLayout(0, 0));

		final JSplitPane splitPane = new JSplitPane();
		panel_4.add(splitPane, BorderLayout.CENTER);
		splitPane.setOneTouchExpandable(true);

		final JPanel filePanel = new JPanel();
		filePanel.setPreferredSize(new Dimension(200, 0));
		filePanel.setLayout(new BorderLayout());
		filePanel
				.setBorder(new TitledBorder(
						null,
						"Specify files and folders here, you can choose more than one location once.",
						TitledBorder.LEADING, TitledBorder.TOP, null, null));

		final JScrollPane scrollPane = new JScrollPane();
		filePanel.add(scrollPane);

		list = new JList();
		scrollPane.setViewportView(list);
		list.setCellRenderer(new CompanyLogoListCellRenderer());
		list.setModel(new DefaultListModel());

		final JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(0, 0, 0, 0));
		filePanel.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		final JButton chooseFilesAndButton = new JButton();
		panel_2.add(chooseFilesAndButton);
		chooseFilesAndButton.setFocusPainted(false);
		chooseFilesAndButton.setIcon(SwingResourceManager.getIcon(
				ClassSearcherGui.class, "BrowseView.gif"));
		chooseFilesAndButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setApproveButtonText("Select");
				if (fileList.size() >= 1) {
					File path = ((File) fileList.get(0)).getParentFile();
					chooser.setCurrentDirectory(path);
				}
				chooser
						.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setMultiSelectionEnabled(true);
				int ret = chooser.showDialog(ClassSearcherGui.this, "Select");
				if (ret == JFileChooser.APPROVE_OPTION) {
					File[] sFiles = chooser.getSelectedFiles();
					for (int i = 0; i < sFiles.length; ++i) {
						((DefaultListModel) list.getModel())
								.addElement(sFiles[i]);
						fileList.add(sFiles[i]);
					}
				}
			}
		});
		chooseFilesAndButton.setText("Add file(folder)...");

		final JButton deleteButton = new JButton();
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] sObs = list.getSelectedValues();
				if (sObs == null || sObs.length == 0)
					JOptionPane.showMessageDialog(ClassSearcherGui.this,
							"No item is selected", "Error",
							JOptionPane.ERROR_MESSAGE);
				for (int i = 0; i < sObs.length; ++i) {
					((DefaultListModel) list.getModel()).removeElement(sObs[i]);
					fileList.remove(sObs[i]);
				}
			}
		});
		deleteButton.setIcon(SwingResourceManager.getIcon(
				ClassSearcherGui.class, "delete.gif"));
		deleteButton.setFocusPainted(false);
		deleteButton.setText("Remove");
		panel_2.add(deleteButton);

		final JPanel formatPanel = new JPanel();
		final GridLayout gl_formatPanel = new GridLayout();
		gl_formatPanel.setRows(5);
		formatPanel.setLayout(gl_formatPanel);
		formatPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		final JLabel fileTypeLabel = new JLabel();
		formatPanel.add(fileTypeLabel);
		fileTypeLabel.setText("Search for:");

		jarCheckBox = new JCheckBox();
		formatPanel.add(jarCheckBox);
		jarCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jarTag = jarCheckBox.isSelected();
			}
		});
		jarCheckBox.setSelected(true);
		jarCheckBox.setText(".jar");

		zipCheckBox = new JCheckBox();
		formatPanel.add(zipCheckBox);
		zipCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zipTag = zipCheckBox.isSelected();
			}
		});
		zipCheckBox.setSelected(true);
		zipCheckBox.setText(".zip");

		warCheckBox = new JCheckBox();
		formatPanel.add(warCheckBox);
		warCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				warTag = warCheckBox.isSelected();
			}
		});
		warCheckBox.setText(".war");

		earCheckBox = new JCheckBox();
		formatPanel.add(earCheckBox);
		earCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				earTag = earCheckBox.isSelected();
			}
		});
		earCheckBox.setText(".ear");
		splitPane.setLeftComponent(formatPanel);
		splitPane.setRightComponent(filePanel);

		final JPanel expressionPanel = new JPanel();
		panel_4.add(expressionPanel, BorderLayout.SOUTH);
		expressionPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		expressionPanel.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		expressionPanel.add(panel, BorderLayout.NORTH);

		final JRadioButton rdbtnClassName = new JRadioButton("With Class Name");
		rdbtnClassName.setSelected(true);
		panel.add(rdbtnClassName);

		final JRadioButton rdbtnMethodName = new JRadioButton("Has Method Like");
		panel.add(rdbtnMethodName);

		final JRadioButton rdbtnExtendClassLike = new JRadioButton(
				"Subclass Of");
		panel.add(rdbtnExtendClassLike);

		final JRadioButton rdbtnImplementInterface = new JRadioButton(
				"Implementer of");
		panel.add(rdbtnImplementInterface);
		final ButtonGroup group = new ButtonGroup();
		group.add(rdbtnImplementInterface);
		group.add(rdbtnExtendClassLike);
		group.add(rdbtnMethodName);
		group.add(rdbtnClassName);

		JPanel panel_1 = new JPanel();
		expressionPanel.add(panel_1, BorderLayout.SOUTH);

		final JLabel expressionLabel = new JLabel();
		panel_1.add(expressionLabel);
		expressionLabel.setText("Specify expression here:");

		expTextField = new JTextField();
		panel_1.add(expTextField);
		expTextField
				.setToolTipText("<html>Fill your expresstion here.<br>\"com.ibm.ws.Buffer\", \".ws.Buffer\" or \".ws.\"</html>");
		expTextField.setFont(new Font("", Font.PLAIN, 12));
		expTextField.setPreferredSize(new Dimension(120, 20));
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// setMaximizedBounds(new Rectangle(screenSize.width / 2 - 500 / 2,
		// screenSize.height / 2 - 600 / 2, 500, 600));

		final JPanel controlPanel = new JPanel();
		inputPanel.add(controlPanel, BorderLayout.SOUTH);
		controlPanel.setPreferredSize(new Dimension(0, 55));
		controlPanel.setMinimumSize(new Dimension(0, 60));
		controlPanel.setBorder(new TitledBorder(new EtchedBorder(), "Control",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(20);
		controlPanel.setLayout(flowLayout);

		startButton = new JButton();
		startButton.setIcon(SwingResourceManager.getIcon(
				ClassSearcherGui.class, "start.gif"));
		startButton.setPreferredSize(new Dimension(90, 22));
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileList.size() == 0) {
					JOptionPane.showMessageDialog(ClassSearcherGui.this,
							"Select files and folders first.", "warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (rdbtnExtendClassLike.isSelected())
					curJob = ZipSearcher.CLASSSELECED;
				else if (rdbtnImplementInterface.isSelected())
					curJob = ZipSearcher.INTERFACESELECED;
				else if (rdbtnMethodName.isSelected())
					curJob = ZipSearcher.METHODSELECED;
				else
					curJob = ZipSearcher.EXPRESSIONSELECED;
				exp = expTextField.getText();
				jobCanceled = false;
				thread = new Thread(new Runnable() {
					public void run() {
						wholeProgressBar.setValue(0);
						currentProgressBar.setValue(0);
						wholeProgressLabel.setText("Initializing...");
						try {
							editorKit.insertHTML(document,
									document.getLength(), "<br>", 0, 0,
									javax.swing.text.html.HTML.Tag.BR);
							editorKit.insertHTML(document,
									document.getLength(), "<hr>", 0, 0,
									javax.swing.text.html.HTML.Tag.HR);
						} catch (BadLocationException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						int processFileCount = 0;
						for (int i = 0; i < fileList.size(); i++) {
							FileCount fc = new FileCount(fileList.get(i));
							fc.setFilter(new MyFilter());
							processFileCount += fc.getFileCount();
							if (jobCanceled == true) {
								searchJobCanceled();
								return;
							}
						}
						wholeProgressBar.setMaximum(processFileCount);
						for (int i = 0; i < fileList.size(); i++) {
							ClassSearcherGui.this.process(fileList.get(i));
							if (jobCanceled == true) {
								searchJobCanceled();
								return;
							}
						}
						try {
							document.insertString(document.getLength(),
									"Totally ", null);
							editorKit.insertHTML(document,
									document.getLength(), "<font color=red>"
											+ matchedCount
											+ "</font> entries were matched.",
									0, 0, javax.swing.text.html.HTML.Tag.FONT);
							editorKit.insertHTML(document,
									document.getLength(), "<br>", 0, 0,
									javax.swing.text.html.HTML.Tag.BR);
							editorKit.insertHTML(document,
									document.getLength(), "<hr>", 0, 0,
									javax.swing.text.html.HTML.Tag.HR);
						} catch (BadLocationException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						wholeProgressLabel.setText("Job completed!");
						currentProgressLabel.setText("Zip Search Complete.");
						wholeProgressBar.setValue(processFileCount);
						currentProgressBar.setValue(currentProgressBar
								.getMaximum());
						searchJobCompleted();
					}
				});
				thread.start();
				cancelButton.setEnabled(true);
				startButton.setEnabled(false);
				resetButton.setEnabled(false);
			}
		});
		startButton.setText("Start");
		controlPanel.add(startButton);

		cancelButton = new JButton();
		cancelButton.setIcon(SwingResourceManager.getIcon(
				ClassSearcherGui.class, "cancel.gif"));
		cancelButton.setPreferredSize(new Dimension(90, 22));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (thread != null) {
					jobCanceled = true;
					try {
						thread.join();
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		cancelButton.setText("Cancel");
		cancelButton.setEnabled(false);
		controlPanel.add(cancelButton);

		resetButton = new JButton();
		resetButton.setIcon(SwingResourceManager.getIcon(
				ClassSearcherGui.class, "reset.gif"));
		resetButton.setPreferredSize(new Dimension(90, 22));
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jarTag = zipTag = warTag = earTag = true;
				jarCheckBox.setSelected(true);
				zipCheckBox.setSelected(true);
				fileList.clear();
				((DefaultListModel) list.getModel()).clear();
				ClassSearcherGui.this.expTextField.setText("");
				wholeProgressBar.setValue(0);
				currentProgressBar.setValue(0);
				ClassSearcherGui.this.resultEditorPane.setText("");
				wholeProgressLabel.setText("Not initialized");
				currentProgressLabel.setText("Not initialized");
			}
		});
		resetButton.setText("Reset");
		controlPanel.add(resetButton);
		final JPanel statusPanel = new JPanel();
		panel_3.add(statusPanel);
		statusPanel.setMinimumSize(new Dimension(0, 200));
		statusPanel.setBorder(new TitledBorder(new EtchedBorder(), "Status",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		statusPanel.setLayout(new BorderLayout());

		final JPanel progressPanel = new JPanel();
		final FlowLayout flowLayout_1 = new FlowLayout();
		flowLayout_1.setVgap(2);
		progressPanel.setLayout(flowLayout_1);
		progressPanel.setPreferredSize(new Dimension(0, 105));
		progressPanel.setBorder(new TitledBorder(new EtchedBorder(),
				"Progress", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		statusPanel.add(progressPanel, BorderLayout.NORTH);

		currentProgressLabel = new JLabel();
		currentProgressLabel.setPreferredSize(new Dimension(400, 15));
		currentProgressLabel.setText("Not initialized...");
		progressPanel.add(currentProgressLabel);

		currentProgressBar = new JProgressBar();
		currentProgressBar.setPreferredSize(new Dimension(300, 15));
		progressPanel.add(currentProgressBar);

		wholeProgressLabel = new JLabel();
		wholeProgressLabel.setPreferredSize(new Dimension(400, 15));
		wholeProgressLabel.setText("Not initialized...");
		progressPanel.add(wholeProgressLabel);

		wholeProgressBar = new JProgressBar();
		wholeProgressBar.setPreferredSize(new Dimension(300, 15));
		progressPanel.add(wholeProgressBar);

		final JScrollPane resultScrollPane = new JScrollPane();
		resultScrollPane.setMinimumSize(new Dimension(0, 30));
		resultScrollPane.setBorder(new TitledBorder(new EtchedBorder(),
				"Result", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		statusPanel.add(resultScrollPane);

		resultEditorPane = new JTextPane();
		resultEditorPane.addHyperlinkListener(createHyperLinkListener());
		resultEditorPane.setEditable(false);
		resultEditorPane.setContentType("text/html");
		resultEditorPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.document = (HTMLDocument) resultEditorPane.getDocument();
		this.editorKit = (HTMLEditorKit) resultEditorPane.getEditorKit();
		resultScrollPane.setViewportView(resultEditorPane);

		final JPopupMenu popupMenu = new JPopupMenu();
		addPopup(resultEditorPane, popupMenu);

		final JMenuItem copyMenuItem = new JMenuItem();
		copyMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TextTransfer textTransfer = new TextTransfer();
				textTransfer.setClipboardContents(resultEditorPane
						.getSelectedText());
			}
		});
		copyMenuItem.setIcon(SwingResourceManager.getIcon(
				ClassSearcherGui.class, "copy.gif"));
		copyMenuItem.setAutoscrolls(true);
		copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				InputEvent.CTRL_MASK));
		copyMenuItem.setText("Copy");
		popupMenu.add(copyMenuItem);

		final JPanel aboutPanel = new JPanel();
		aboutPanel.setLayout(new BorderLayout());
		getContentPane().add(aboutPanel, BorderLayout.SOUTH);

		final JLabel aboutLabel = new JLabel();
		aboutLabel.setHorizontalAlignment(SwingConstants.CENTER);
		aboutLabel
				.setText("<html><b>Copy Right &copy; 2010</b>  All rights reserved.  <a href=\"mailto:new.root@gmail.com\">Xingen Wang</a> Zhejiang University </html>");
		aboutPanel.add(aboutLabel);
		initDataBindings();
	}

	public HyperlinkListener createHyperLinkListener() {
		return new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e instanceof HTMLFrameHyperlinkEvent) {
						((HTMLDocument) resultEditorPane.getDocument())
								.processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) e);
					} else {
						try {
							String os = System.getProperty("os.name");
							if (os.indexOf("indow") >= 0)
								Runtime.getRuntime().exec(
										"explorer " + e.getURL());
							else if (os.indexOf("inux") >= 0)
								Runtime.getRuntime().exec(
										"mozilla " + e.getURL());
						} catch (IOException ioe) {
							System.out.println("IOE: " + ioe);
						}
					}
				}
			}
		};
	}

	public ClassSearcherGui(File dir, String exp) {
		this();
		this.setExp(exp);
		this.setDir(dir);
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public static void setLookAndFeel() {
		try {
			javax.swing.UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JProgressBar getCurrentProgressBar() {
		return currentProgressBar;
	}

	public JLabel getCurrentProgressLabel() {
		return currentProgressLabel;
	}

	private void process(File file) {
		if (file.isDirectory() == false) {
			try {
				ZipSearcher zs = new ZipSearcher(this, file, this.exp, curJob);
				if (zs.matched()) {
					matchedCount++;
				}
				wholeProgressBar.setValue(++processCount);
				wholeProgressLabel.setText(file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		File[] files = file.listFiles(new MyFilter());
		for (int i = 0; (i < files.length) && !this.jobCanceled; ++i) {
			try {
				process(files[i]);
			} catch (Exception e) {
				warnMsg.add(files[i].getName());
			}
		}
	}

	private class MyFilter implements FileFilter {
		public boolean accept(File pathname) {
			if (zipTag && pathname.getName().endsWith(".zip"))
				return true;
			if (jarTag && pathname.getName().endsWith(".jar"))
				return true;
			if (warTag && pathname.getName().endsWith(".war"))
				return true;
			if (earTag && pathname.getName().endsWith(".ear"))
				return true;
			if (pathname.isDirectory())
				return true;
			return false;
		}
	}

	private void searchJobCompleted() {
		cancelButton.setEnabled(false);
		startButton.setEnabled(true);
		resetButton.setEnabled(true);
		jobCanceled = false;
		this.processCount = this.matchedCount = 0;
		if (warnMsg.size() > 0)
			JOptionPane.showMessageDialog(this, "<html>Totally <b color=red>"
					+ warnMsg.size() + "</b> files failed to decompile.<br>"
					+ "The result is not accurate.</html>", "Warning",
					JOptionPane.WARNING_MESSAGE);
	}

	private void searchJobCanceled() {
		wholeProgressLabel.setText("Job canceled!");
		cancelButton.setEnabled(false);
		startButton.setEnabled(true);
		resetButton.setEnabled(true);
		jobCanceled = false;
		this.processCount = this.matchedCount = 0;
	}

	public static void centerDialog(Window frame) {
		Dimension dialogSize = frame.getSize();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(screenSize.width / 2 - dialogSize.width / 2,
				screenSize.height / 2 - dialogSize.height / 2);
	}

	private static void addPopup(final Component component,
			final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					if (component instanceof JEditorPane) {
						String text = ((JEditorPane) component)
								.getSelectedText();
						if (text == null || text.equals(""))
							popup.getComponent(0).setEnabled(false);
						else
							popup.getComponent(0).setEnabled(true);
					}
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					if (component instanceof JEditorPane) {
						String text = ((JEditorPane) component)
								.getSelectedText();
						if (text == null || text.equals(""))
							popup.getComponent(0).setEnabled(false);
						else
							popup.getComponent(0).setEnabled(true);
					}
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	public HTMLDocument getDocument() {
		return document;
	}

	protected void initDataBindings() {
	}
}

class CompanyLogoListCellRenderer extends DefaultListCellRenderer {
	/**
   * 
   */
	private static final long serialVersionUID = 8779560660143958081L;

	private static ImageIcon fileImage = SwingResourceManager.getIcon(
			ClassSearcherGui.class, "file_obj.gif");

	private static ImageIcon folderImage = SwingResourceManager.getIcon(
			ClassSearcherGui.class, "folder.gif");

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component retValue = super.getListCellRendererComponent(list, value,
				index, isSelected, cellHasFocus);
		if (((File) value).isDirectory())
			setIcon(folderImage);
		else
			setIcon(fileImage);
		return retValue;
	}

	public CompanyLogoListCellRenderer() {
		super();
	}
}

final class TextTransfer implements ClipboardOwner {

	/**
	 * Empty implementation of the ClipboardOwner interface.
	 */
	public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
		// do nothing
	}

	/**
	 * Place a String on the clipboard, and make this class the owner of the
	 * Clipboard's contents.
	 */
	public void setClipboardContents(String aString) {
		StringSelection stringSelection = new StringSelection(aString);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/**
	 * Get the String residing on the clipboard.
	 * 
	 * @return any text found on the Clipboard; if none found, return an empty
	 *         String.
	 */
	public String getClipboardContents() {
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		// odd: the Object param of getContents is not currently used
		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableText = (contents != null)
				&& contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		if (hasTransferableText) {
			try {
				result = (String) contents
						.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException ex) {
				// highly unlikely since we are using a standard DataFlavor
				System.out.println(ex);
			} catch (IOException ex) {
				System.out.println(ex);
			}
		}
		return result;
	}
}