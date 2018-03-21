package cn.edu.xmu.cld.WeiboCrawler2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

class OpenFile {
	private JFileChooser choose;
	private JFrame jf;
	private File f;
	private String filePath;
	public static String lastPath = "";

	public OpenFile() {
		// this.jf = jf;
		choose = new JFileChooser();
		choose.setCurrentDirectory(new File(lastPath));
		choose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		choose.setMultiSelectionEnabled(false);
	}

	public void open() {
		f = null;
		int click = choose.showOpenDialog(jf);
		if (click == JFileChooser.APPROVE_OPTION) {
			f = choose.getSelectedFile();
		}
		// choose.showOpenDialog(jf);
	}

	public String getFilePath() {
		filePath = null;
		if (f != null) {
			filePath = f.getPath();
		}
		return filePath;
	}
}

public class JWindowsFrame extends JFrame {
	static Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	//set the width and height of all windows
	static int SCREEN_WIDTH = scrSize.width;
	static int SCREEN_HEIGHT = scrSize.height;
	static int WINDOW_WIDTH = 700;
	static int WINDOW_HEIGHT = 565;
	static int AUTHOR_WIDTH = 290;
	static int AUTHOR_HEIGHT = 280;
	static int INSTRUCTION_WIDTH = 380;
	static int INSTRUCTION_HEIGHT = 230;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JMenuItem JMIAuthor, JMIInstruction;
	private JMenu JMAbout;
	private JMenuBar JMBOperation;

	private JScrollBar JScroB;
	private JScrollPane JSPRunInfo;
	private JScrollPane JSPSearchWords;

	public static JTextArea JTARunInfo;
	private JTextArea JTASearchWords;

	private JPanel JGetIPPanel;
	private JPanel JButtonGetIPPanel;
	private JPanel JWordsPanel;
	private JPanel JPathPanel;
	private JPanel JButtonPanel;
	private JPanel JLeftPanel;
	private JPanel JRightPanel;
	private JPanel JSaveHTMLPathPanel;
	private JPanel JSaveIPPathPanel;
	private JPanel JSaveTXTPathPanel;
	private JPanel JSaveXMLPathPanel;
	private JPanel JPagesPanel;

	private JLabel JLSavePlainIPs;
	private JLabel JLSearchWords;
	private JLabel JLSaveHTMLPath;
	private JLabel JLSaveTXTPath;
	private JLabel JLSaveXMLPath;
	private JLabel JLPlainIPs;
	private JLabel JLRunInfo;
	private JLabel JLpages;

	final private int PATH_FIELD_LENGTH = 32;
	private JTextField JTFSavePlainIPs;
	private JTextField JTFSaveHTMLPath;
	private JTextField JTFSaveTXTPath;
	private JTextField JTFSaveXMLPath;
	private JTextField JTFPlainIPs;
	private JTextField JTFPages;

	final private String OPEN_FILE_BUTTON_NAME = "浏览";
	private JButton JBCrawl;
	private JButton JBGetIP;
	private JButton JBSaveIPOpenFile;
	private JButton JBSaveHTMLOpenFile;
	private JButton JBSaveTXTOpenFile;
	private JButton JBSaveXMLOpenFile;
	private JButton JBIPOpenFile;

	public JWindowsFrame() {

		JMIAuthor = new JMenuItem("软件作者");
		JMIInstruction = new JMenuItem("使用说明");

		JMAbout = new JMenu("关于");

		JMAbout.add(JMIAuthor);
		JMAbout.add(JMIInstruction);

		JMBOperation = new JMenuBar();
		JMBOperation.add(JMAbout);

		setJMenuBar(JMBOperation);

		JLeftPanel = new JPanel();
		JLeftPanel.setLayout(new BorderLayout());

		JRightPanel = new JPanel();
		JRightPanel.setLayout(new BorderLayout());

		JGetIPPanel = new JPanel();
		JGetIPPanel.setLayout(new BorderLayout());

		JSaveIPPathPanel = new JPanel();
		JSaveIPPathPanel.setLayout(new BorderLayout());

		JLSavePlainIPs = new JLabel("保存可用的代理IP的路径（*不要以斜杠结尾*）");

		JTFSavePlainIPs = new JTextField(PATH_FIELD_LENGTH);
		JTFSavePlainIPs.setEditable(true);

		JBSaveIPOpenFile = new JButton(OPEN_FILE_BUTTON_NAME);

		JBGetIP = new JButton("获取代理IP");

		JSaveIPPathPanel.add(JTFSavePlainIPs, BorderLayout.WEST);
		JSaveIPPathPanel.add(JBSaveIPOpenFile, BorderLayout.EAST);

		JButtonGetIPPanel = new JPanel();
		JButtonGetIPPanel.setLayout(new FlowLayout());

		JButtonGetIPPanel.add(JBGetIP);

		JGetIPPanel.add(JLSavePlainIPs, BorderLayout.NORTH);
		JGetIPPanel.add(JSaveIPPathPanel, BorderLayout.CENTER);
		JGetIPPanel.add(JButtonGetIPPanel, BorderLayout.SOUTH);

		JScroB = new JScrollBar();
		JScroB.setVisible(true);

		JLRunInfo = new JLabel("相关信息");

		JTARunInfo = new JTextArea();
		// JTARunInfo.setBounds(300, 250, 600, 200);
		JTARunInfo.setEditable(false);// 设置文本框是否可以编辑

		JSPRunInfo = new JScrollPane(JTARunInfo);
		JSPRunInfo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JSPRunInfo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JSPRunInfo.add(JScroB);

		JRightPanel.add(JLRunInfo, BorderLayout.NORTH);
		JRightPanel.add(JSPRunInfo);

		JWordsPanel = new JPanel();
		JWordsPanel.setLayout(new BorderLayout());

		JLSearchWords = new JLabel("输入搜索话题，关键词用空格或换行分开");
		// 话题之间用空格分开，若一个话题有多个关键词，词之间加“%20”

		JTASearchWords = new JTextArea(8, 1);
		JTASearchWords.setEditable(true);

		JSPSearchWords = new JScrollPane(JTASearchWords);
		JSPSearchWords.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JSPSearchWords.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JSPSearchWords.add(JScroB);

		JWordsPanel.add(JGetIPPanel, BorderLayout.NORTH);

		JWordsPanel.add(JLSearchWords, BorderLayout.CENTER);
		JWordsPanel.add(JSPSearchWords, BorderLayout.SOUTH);

		JPathPanel = new JPanel();
		JPathPanel.setLayout(new GridLayout(9, 1));

		JSaveHTMLPathPanel = new JPanel();
		JSaveHTMLPathPanel.setLayout(new BorderLayout());

		JLSaveHTMLPath = new JLabel("保存所有HTML文件的路径（*不要以斜杠结尾*）");

		JTFSaveHTMLPath = new JTextField(PATH_FIELD_LENGTH);
		JTFSaveHTMLPath.setEditable(true);

		JBSaveHTMLOpenFile = new JButton(OPEN_FILE_BUTTON_NAME);

		JSaveHTMLPathPanel.add(JTFSaveHTMLPath, BorderLayout.WEST);
		JSaveHTMLPathPanel.add(JBSaveHTMLOpenFile, BorderLayout.EAST);

		JLSaveTXTPath = new JLabel("保存所有TXT文件的路径（*不要以斜杠结尾*）");

		JSaveTXTPathPanel = new JPanel();
		JSaveTXTPathPanel.setLayout(new BorderLayout());

		JTFSaveTXTPath = new JTextField(PATH_FIELD_LENGTH);
		JTFSaveTXTPath.setEditable(true);

		JBSaveTXTOpenFile = new JButton(OPEN_FILE_BUTTON_NAME);

		JSaveTXTPathPanel.add(JTFSaveTXTPath, BorderLayout.WEST);
		JSaveTXTPathPanel.add(JBSaveTXTOpenFile, BorderLayout.EAST);

		JLSaveXMLPath = new JLabel("保存所有XML文件的路径（*不要以斜杠结尾*）");

		JSaveXMLPathPanel = new JPanel();
		JSaveXMLPathPanel.setLayout(new BorderLayout());

		JTFSaveXMLPath = new JTextField(PATH_FIELD_LENGTH);
		JTFSaveXMLPath.setEditable(true);

		JBSaveXMLOpenFile = new JButton(OPEN_FILE_BUTTON_NAME);

		JSaveXMLPathPanel.add(JTFSaveXMLPath, BorderLayout.WEST);
		JSaveXMLPathPanel.add(JBSaveXMLOpenFile, BorderLayout.EAST);

		JLPlainIPs = new JLabel("可用代理IP的路径(plainIPs.txt文件)");

		JSaveIPPathPanel = new JPanel();
		JSaveIPPathPanel.setLayout(new BorderLayout());

		JTFPlainIPs = new JTextField(PATH_FIELD_LENGTH);
		JTFPlainIPs.setEditable(true);

		JBIPOpenFile = new JButton(OPEN_FILE_BUTTON_NAME);

		JSaveIPPathPanel.add(JTFPlainIPs, BorderLayout.WEST);
		JSaveIPPathPanel.add(JBIPOpenFile, BorderLayout.EAST);
		
		JPagesPanel = new JPanel();
		JPagesPanel.setLayout(new BorderLayout());
		
		JLpages = new JLabel("设置每个关键词获取页数(1~50，默认50): ");
		
		JTFPages = new JTextField(PATH_FIELD_LENGTH/10);
		JTFPages.setEditable(true);
		
		JPagesPanel.add(JLpages, BorderLayout.WEST);
		JPagesPanel.add(JTFPages, BorderLayout.EAST);

		JPathPanel.add(JLSaveHTMLPath);
		JPathPanel.add(JSaveHTMLPathPanel);
		JPathPanel.add(JLSaveTXTPath);
		JPathPanel.add(JSaveTXTPathPanel);
		JPathPanel.add(JLSaveXMLPath);
		JPathPanel.add(JSaveXMLPathPanel);
		JPathPanel.add(JLPlainIPs);
		JPathPanel.add(JSaveIPPathPanel);
		//JPathPanel.add(JLpages);
		JPathPanel.add(JPagesPanel);

		JBCrawl = new JButton("爬取微博");

		JButtonPanel = new JPanel();
		JButtonPanel.setLayout(new FlowLayout());

		// JButtonPanel.add(JBGetIP);
		JButtonPanel.add(JBCrawl);

		JLeftPanel.add(JWordsPanel, BorderLayout.NORTH);
		JLeftPanel.add(JPathPanel, BorderLayout.CENTER);
		JLeftPanel.add(JButtonPanel, BorderLayout.SOUTH);

		Container c = getContentPane();
		c.setLayout(new BorderLayout());

		c.add(JLeftPanel, BorderLayout.WEST);
		c.add(JRightPanel);
	}

	public static void showData(String s) {
		JWindowsFrame.JTARunInfo.append(s);
		JTARunInfo.paintImmediately(JTARunInfo.getBounds());
	}

	public void action() {
		// JOptionPane.showMessageDialog(
		ActionListener actionAboutAuthor = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// AboutText about = new AboutText();
				// about.aboutAuthor();
				JDialog jDialog = new JDialog();
				jDialog.setVisible(true);
				jDialog.setResizable(false);
				// jd.setBackground(Color.BLACK);
				jDialog.setTitle("作者信息");
				// 480, 220, 210, 265
				// (480/1366)*SCREEN_WIDTH, (220/768)*SCREEN_HEIGHT,
				// (400/1366)*SCREEN_WIDTH, (265/768)*SCREEN_HEIGHT

				jDialog.setBounds((SCREEN_WIDTH-AUTHOR_WIDTH)/2, (SCREEN_HEIGHT-AUTHOR_HEIGHT)/2, AUTHOR_WIDTH, AUTHOR_HEIGHT);
				JTextArea jta = new JTextArea();
				jta.setBackground(getBackground());
				jta.setText("           软件作者：李雪山" + "\r\n"
						+ "\r\n  本软件由作者本人独立开发，旨在提供一个"
						+ "\r\n  抓取微博的便利工具。【请勿用于商业行为】" + "\r\n"
						+ "\r\n  本软件开源，所有代码见作者CSDN博客:"
						+ "\r\n  http://blog.csdn.net/codingmirai/" + "\r\n"
						+ "\r\n  如有疑问，欢迎交流，我的邮箱地址：" + ""
						+ "\r\n        hainanlxs@gmail.com" + "\r\n"
						+ "\r\n    LI XUESHAN, All Rights Reserved");
				jta.setEditable(false);
				jDialog.add(jta);
			}
		};
		JMIAuthor.addActionListener(actionAboutAuthor);
		ActionListener actionAboutInstruction = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// AboutText about = new AboutText();
				// about.aboutAuthor();
				JDialog jDialog = new JDialog();
				jDialog.setVisible(true);
				// jd.setBackground(Color.BLACK);
				jDialog.setTitle("使用说明");
				jDialog.setResizable(false);
				// 400, 300, 400, 230
				// (480/1366)*SCREEN_WIDTH, (220/768)*SCREEN_HEIGHT,
				// (400/1366)*SCREEN_WIDTH, (265/768)*SCREEN_HEIGHT

				jDialog.setBounds((SCREEN_WIDTH-INSTRUCTION_WIDTH)/2, 
						(SCREEN_HEIGHT-INSTRUCTION_HEIGHT)/2, INSTRUCTION_WIDTH, INSTRUCTION_HEIGHT);
				JTextArea jta = new JTextArea();
				jta.setBackground(getBackground());
				jta.setText("               WeiboCrawler Version: 2.2"
						+ "\r\n  此软件是基于代理IP的新浪微博数据获取器，有效躲避了新浪"
						+ "\r\n微博的反爬虫机制，能够全自动连续获取相关微博数据。" + "\r\n  软件分为两个主要功能："
						+ "\r\n1. 获取有效代理IP；" + "\r\n2. 根据关键词爬取相关微博数据。"
						+ "\r\n  " + "\r\n  写不动了，软件功能很简单，看看就懂用了，如果还有疑问，"
						+ "\r\n点一下菜单里的“关于”——>“作者信息”，里面有我的博客"
						+ "\r\n地址。博客文章里有详细说明。");
				jta.setEditable(false);
				jDialog.add(jta);
			}
		};
		JMIInstruction.addActionListener(actionAboutInstruction);

		ActionListener actionSaveHTMLOpenFile = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				OpenFile openFile = new OpenFile();
				openFile.open();
				String path = openFile.getFilePath();
				OpenFile.lastPath = path;
				JTFSaveHTMLPath.setText(path);
			}
		};
		JBSaveHTMLOpenFile.addActionListener(actionSaveHTMLOpenFile);

		ActionListener actionPlainIPOpenFile = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				OpenFile openFile = new OpenFile();
				openFile.open();
				String path = openFile.getFilePath();
				OpenFile.lastPath = path;
				JTFPlainIPs.setText(path);
			}
		};
		JBIPOpenFile.addActionListener(actionPlainIPOpenFile);

		ActionListener actionSaveIPOpenFile = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				OpenFile openFile = new OpenFile();
				openFile.open();
				String path = openFile.getFilePath();
				OpenFile.lastPath = path;
				JTFSavePlainIPs.setText(path);
			}
		};
		JBSaveIPOpenFile.addActionListener(actionSaveIPOpenFile);

		ActionListener actionSaveXMLOpenFile = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				OpenFile openFile = new OpenFile();
				openFile.open();
				String path = openFile.getFilePath();
				OpenFile.lastPath = path;
				JTFSaveXMLPath.setText(path);
			}
		};
		JBSaveXMLOpenFile.addActionListener(actionSaveXMLOpenFile);

		ActionListener actionSaveTXTOpenFile = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				OpenFile openFile = new OpenFile();
				openFile.open();
				String path = openFile.getFilePath();
				OpenFile.lastPath = path;
				JTFSaveTXTPath.setText(path);
			}
		};
		JBSaveTXTOpenFile.addActionListener(actionSaveTXTOpenFile);

		ActionListener actionGetIP = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent click) {
				// TODO Auto-generated method stub
				String savePlainIPs;
				savePlainIPs = JTFSavePlainIPs.getText();

				if (savePlainIPs.equals("")) {
					JTARunInfo.append("path error\r\n请输入保存IP的路径\r\n");
				} else {
					final String[] args = new String[3];
					args[0] = savePlainIPs + "\\allIPs.txt";
					args[1] = savePlainIPs + "\\validIPs.txt";
					args[2] = savePlainIPs + "\\plainIPs.txt";
					final ProxyIP p = new ProxyIP();

					// JTARunInfo.append("hahah\r\n");
					// JTARunInfo.paintImmediately(JTARunInfo.getBounds());

					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								p.excute(args, JTARunInfo);
							} catch (IOException | URISyntaxException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		};
		JBGetIP.addActionListener(actionGetIP);

		ActionListener actionCrawl = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent click) {
				// TODO Auto-generated method stub
				String searchwords, saveHTMLPath, saveTXTPath, SaveXMLPath, plainIPsPath, pageNum;
				searchwords = JTASearchWords.getText();
				saveHTMLPath = JTFSaveHTMLPath.getText();
				saveTXTPath = JTFSaveTXTPath.getText();
				SaveXMLPath = JTFSaveXMLPath.getText();
				plainIPsPath = JTFPlainIPs.getText();
				pageNum = JTFPages.getText();
				final String[] crawlerArgs = { searchwords, saveHTMLPath,
						saveTXTPath, SaveXMLPath, plainIPsPath, pageNum };

				int ifCrawlFlag = 0;
				for (int i = 0; i < 6; i++) {
					if (crawlerArgs[i].equals("")) {
						switch (i) {
						case 0:
							ifCrawlFlag++;
							System.out.println("error\r\n请输入搜索关键词");
							JTARunInfo.append("error\r\n请输入搜索关键词\r\n");
							break;
						case 1:
							ifCrawlFlag++;
							System.out.println("error\r\n请输入保存html文件路径");
							JTARunInfo
									.append("error\r\n请输入保存html文件路径\r\n");
							break;
						case 2:
							ifCrawlFlag++;
							System.out.println("error\r\n请输入保存txt文件路径");
							JTARunInfo.append("error\r\n请输入保存txt文件路径\r\n");
							break;
						case 3:
							ifCrawlFlag++;
							System.out.println("error\r\n请输入保存xml文件路径");
							JTARunInfo.append("error\r\n请输入保存xml文件路径\r\n");
							break;
						case 4:
							ifCrawlFlag++;
							System.out.println("error\r\n请输入可用代理IP文件路径(plainIPs.txt)");
							JTARunInfo.append("error\r\n请输入可用代理IP文件路径(plainIPs.txt)\r\n");
							break;
						case 5:
							//ifCrawlFlag++;
							System.out.println("warning\r\n未指定获取页数，默认将获取最大页数50页");
							JTARunInfo.append("warning\r\n未指定获取页数，默认将获取最大页数50页\r\n");
							crawlerArgs[5] = "50";
							break;
						}
					}
				}
				if (ifCrawlFlag == 0) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Crawler c = new Crawler();
								c.excute(crawlerArgs, JTARunInfo);
							} catch (IOException | URISyntaxException
									| InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
				}

			}
		};
		JBCrawl.addActionListener(actionCrawl);

	}

	public static void main(String[] args) {
		// System.out.println(scrSize.width+"\r\n"+scrSize.height);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				final JWindowsFrame frame = new JWindowsFrame();

				frame.setTitle("WeiboCrawler");
				// 350, 150, 700, 520
				// (480/1366)*SCREEN_WIDTH, (220/768)*SCREEN_HEIGHT,
				// (700/1366)*SCREEN_WIDTH, (265/768)*SCREEN_HEIGHT
				// System.out.println((350/1366)*SCREEN_WIDTH+"\r\n"+(150/768)*SCREEN_HEIGHT+"\r\n"+
				// (700/1366)*SCREEN_WIDTH+"\r\n"+(520/768)*SCREEN_HEIGHT);
				frame.setBounds((SCREEN_WIDTH-WINDOW_WIDTH)/2, (SCREEN_HEIGHT-WINDOW_HEIGHT)/2,
						WINDOW_WIDTH, WINDOW_HEIGHT);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				frame.setResizable(false);
				frame.action();
			}
		});
	}
}
