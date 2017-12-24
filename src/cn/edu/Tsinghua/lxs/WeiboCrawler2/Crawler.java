package cn.edu.Tsinghua.lxs.WeiboCrawler2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import org.apache.http.client.ClientProtocolException;

public class Crawler {
	/**
	 * 是否存在指定页数的搜索结果页面
	 * 
	 * @param html
	 * @return
	 */
	public boolean isExistResult(String html) {
		boolean isExist = true;
		Pattern pExist = Pattern.compile("抱歉，没有找到.+?span>相关的结果");// 您可以尝试更换关键词，再次搜索。（表示指定页没有结果）
		Matcher mExist = pExist.matcher(html);
		if (mExist.find()) {
			isExist = false;
		}

		return isExist;
	}

	public static String getMyTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String today = dateFormat.format(date);
		// System.out.println(today);

		return today;
	}

	public void excute(String[] args, JTextArea jta)
			throws ClientProtocolException, URISyntaxException, IOException,
			InterruptedException {
		/*
		 * args: searchwords, saveHTMLPath, saveTXTPath, SaveXMLPath,
		 * plainIPsPath
		 */

		long t1 = System.currentTimeMillis();
		/*
		 * "苹果","htc","华为", "小米", "中兴", "联想", "魅族", "酷派", "两会",
		 * "马航失联","汽车摇号","自贸区",
		 */
		// String[] searchwords = {"三星", "苹果","htc","华为", "小米", "中兴", "联想",
		// "魅族", "酷派",
		// "两会", "马航失联","汽车摇号","自贸区","雾霾%20PM2.5", "医疗改革","土地改革","房价调控"
		// ,"农村集体产权%20改革","东莞扫黄","油价","城镇化","国产航母","中石油腐败","养老制度"
		// ,"嫣然基金","养老保险","拆迁","医患关系","计划生育%20超生","反腐%20纪委","城管"
		// ,"土地流转权","发改委","证监会","余额宝","理财产品","环保","教育部"
		// ,"高考","自主招生","股市%20A股","单独二胎%20单独二孩"
		// ,"乌克兰局势","钓鱼岛争端","越南%20南海争端","菲律宾%20黄岩岛","叙利亚%20化学武器"
		// ,"伊朗%20核问题%20危机","中期选举","埃及局势","泰国局势","朝韩冲突","南海识别区"
		// };
		String words = args[0];
		words = words.replaceAll("\n", " ");
		String[] searchwords = words.split(" ");
		for (int i = 0; i < searchwords.length; i++) {
			System.out.println(searchwords[i]);
		}
		String saveHTMLPath = args[1];
		String saveTXTPath = args[2];
		String saveXMLPath = args[3];
		String plainIPsPath = args[4];
		String pageNum = args[5];

		// String[] searchwords = {};
		String today = getMyTime();
		System.out.println("Today is " + today);
		jta.append("今日日期  " + today + "\r\n");
		File dirGetweiboSub = new File(saveHTMLPath + "/" + today);
		dirGetweiboSub.mkdirs();
		File dirWeibostxtSub = new File(saveTXTPath + "/" + today);
		dirWeibostxtSub.mkdirs();
		File dirWeibosxmlSub = new File(saveXMLPath + "/" + today);
		dirWeibosxmlSub.mkdirs();
		Vector<String> ip = new Vector<String>();
		ip = FileOperation.getLines(plainIPsPath);
		if (ip == null) {
			System.out.println("在给定路径下找不到plainIP.txt文件");
			jta.append("在给定路径下找不到plainIP.txt文件" + "\r\n");
		}
		int ipNum = ip.size();
		int iIP = 0;

		for (int n = 0; n < searchwords.length; n++) {

			String searchword = searchwords[n];
			String dirPath = saveHTMLPath + "/" + today + "/" + searchword;
			File f = new File(dirPath);
			f.mkdirs();// 创建文件夹，另一方法mkdirs创建多层未创建的文件夹
			int totalPage = Integer.parseInt(pageNum);// 设置想要搜索的页数，则搜索范围为该搜索词下的第1到第totalPage页

			System.out.println("****Start getting weibos of the keyword \""
					+ searchword + "\"****");
			jta.append("****开始获取关键词 \"" + searchword + "\" 下的微博****" + "\r\n");
			// 将指定页数的搜索页面html文件爬取下来并保存
			String html;

			for (int i = totalPage; i > 0; i--) {// 开始爬取，先把一个话题下的html都爬下来，再用这些html文件
				String hostName = ip.get(iIP).split(":")[0];
				int port = Integer.parseInt(ip.get(iIP).split(":")[1]);
				html = new HTML().getHTML("http://s.weibo.com/weibo/"
						+ searchword + "&nodup=1&page=" + String.valueOf(i),
						hostName, port, jta);
				int iReconn = 0;
				while (html.equals("null")) {
					html = new HTML().getHTML(
							"http://s.weibo.com/weibo/" + searchword
									+ "&nodup=1&page=" + String.valueOf(i),
							hostName, port, jta);
					iReconn++;
					System.out.println("****" + ip.get(iIP) + " reconnected "
							+ iReconn + " time(s)****");
					jta.append("****" + ip.get(iIP) + " 重连第 " + iReconn
							+ " 次****" + "\r\n");
					if (iReconn == 4) {// 4
						break;
					}
				}
				if (html.equals("null")) {
					System.out
							.println("****5 consecutive connections were failed, now using next IP****");
					jta.append("****连续 5 次连接失败，开始使用下一个代理IP****" + "\r\n");
					if (iIP == ipNum - 1) {
						System.out
								.println("****All valid proxy IPs have been tried, still can not get all the data. Now trying the valid proxy IP list again.****");
						jta.append("****所有可用代理IP都轮试过一遍，仍未能获取所有微博数据。开始下一轮尝试可用代理IP列表****"
								+ "\r\n");
						iIP = 0;
						System.out.println("****Turn to" + ip.get(iIP)
								+ ", start connecting****");
						jta.append("****使用此IP" + ip.get(iIP) + "，开始连接****"
								+ "\r\n");
					} else {
						iIP++;
						System.out.println("****Turn to" + ip.get(iIP)
								+ ", start connecting****");
						jta.append("****使用此IP" + ip.get(iIP) + "，开始连接****"
								+ "\r\n");
					}
					i++;
				}
				if (html.contains("version=2012")) {//此处是核心中的核心
					if (!html.contains("可用空格将多个关键词分开")) {
						FileOperation.writeString(html, saveHTMLPath + "/"
								+ today + "/" + searchword + "/" + searchword
								+ String.valueOf(i) + ".html");
						System.out.println("\"" + searchword + "\"" + " No."
								+ i
								+ " page's html have been saved successfully!");
						jta.append("\"" + searchword + "\"" + "成功获取此关键词搜索结果第 "
								+ i + " 页html文件!" + "\r\n");
					} else {
						System.out.println("****\"" + searchword + "\"" + "No."
								+ i + " page does not exist****");
						jta.append("****\"" + searchword + "\"" + "此关键词第 " + i
								+ " 页搜索结果不存在****" + "\r\n");
					}
				}
			}
			System.out.println("****\"" + searchword
					+ "\" crawling has been done!!****");
			jta.append("****\"" + searchword + "\" 爬取此关键词微博结束!!****" + "\r\n");
			System.out
					.println("****Now writing the weibos to local files (txt & xml)****");
			jta.append("****现在将结果写入本地文件 (txt & xml)****" + "\r\n");
			String saveEachTXTPath = saveTXTPath + "/" + today + "/"
					+ searchword + ".txt";
			HTMLParser htmlParser = new HTMLParser();
			Vector<String> weibos = htmlParser.write2txt(searchword, dirPath,
					saveEachTXTPath);
			String saveEachXMLPath = saveXMLPath + "/" + today + "/"
					+ searchword + ".xml";
			htmlParser.writeVector2xml(weibos, saveEachXMLPath);
			System.out.println("****Writing has been done!****");
			jta.append("****文件写入完毕!****" + "\r\n");

			long t2 = System.currentTimeMillis();
			System.out.println((double) (t2 - t1) / 60000 + " mins");
			jta.append("已耗时 " + (double) (t2 - t1) / 60000 + " 分钟" + "\r\n");
		}
	}

}
