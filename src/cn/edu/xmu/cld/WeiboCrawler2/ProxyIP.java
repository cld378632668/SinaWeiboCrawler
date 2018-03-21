package cn.edu.xmu.cld.WeiboCrawler2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextArea;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ProxyIP {
	/**
	 * There are proxy ips which haven't been verified on the page like
	 * http://www.youdaili.cn/Daili/guonei/1843.html, use regex to match all
	 * them out.
	 * 
	 * @param a
	 *            saved String html file
	 * @return a String Vector contains all the IP on the html file
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Vector<String> getProxyIPs(String html)
			throws ClientProtocolException, IOException {
		Vector<String> IPs = new Vector<String>();
		Pattern p = Pattern
				.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}</td>\n.+<td>\\d{1,5}</td>");
		Matcher m = p.matcher(html);
		String s;
		String port;
		while (m.find()) {
			s = m.group();
			s=s.substring(0,s.indexOf("</td>"))+":"+s.substring(s.indexOf("<td>")+4,s.lastIndexOf("</td>"));
			System.out.println(s);
			port = s.split(":")[1];
			if (Integer.parseInt(port) < 65535) {// The top range of the port
													// number is 65535
				if (!IPs.contains(s)) {
					IPs.add(s);
				}
			}
			// System.out.println("找到一条ip "+s);
			
		}

		return IPs;
	}

	/**
	 * Find all IP library links on the homepage of "http://www.xici.net.co/".
	 * 
	 * @param a
	 *            specified URL "http://www.youdaili.cn/"
	 * @return a String Vector contains all URLs that contain some proxy IPs
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static Vector<String> getIPsPageLinks(String ipLibURL,
			JTextArea JTARunInfo) throws ClientProtocolException,
			URISyntaxException, IOException {
		Vector<String> IPsPageLinks = new Vector<String>();
		String html = new HTML().getHTML(ipLibURL, JTARunInfo)[1];
		while(html.equals("null")){
			System.out.println("****重新连接****");
			JTARunInfo.append("****重新连接****"+"\r\n");
			html = new HTML().getHTML(ipLibURL, JTARunInfo)[1];
		}
		Pattern p = Pattern.compile("li.+([国内]|[国外]).+?li");// "【国内】|【国外】).+?title"
		Matcher m = p.matcher(html);
		String s;
		while (m.find()) {
			s = m.group();

			s = "http://www.xici.net.co"+s.substring(s.indexOf("href") + 6, s.indexOf("class") - 2);
			System.out.println(s+"\n");
			IPsPageLinks.add(s);
			System.out.println("find ip library link: " + s);
			JTARunInfo.append("找到一条代理IP库链接: " + s + "\r\n");
		}

		return IPsPageLinks;
	}

	/**
	 * Get all unverified proxy IP in all IP library links.
	 * 
	 * @param a specified URL "http://www.youdaili.cn/"
	 * @return a String Vector contains all unverified IPs
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static Vector<String> getAllProxyIPs(String ipLibURL,
			JTextArea JTARunInfo) throws ClientProtocolException, IOException,
			URISyntaxException {
		Vector<String> IPsPageLinks = getIPsPageLinks(ipLibURL, JTARunInfo);// "http://www.xici.net.co/"
		Vector<String> onePageIPs = new Vector<String>();
		Vector<String> allIPs = new Vector<String>();
		for (int i = 0; i < 1; i++) {
			String url = IPsPageLinks.get(i);
			String[] html = new HTML().getHTML(url, JTARunInfo);
			int iReconn1 = 0;
			int reConnectTimes1 = 5;
			while(html[1].equals("null")){
				if(iReconn1 == (reConnectTimes1-1)){
					System.out.println("连续"+reConnectTimes1+"次连接失败，继续获取下一条IP库的代理IP");
					JTARunInfo.append("连续"+reConnectTimes1+"次连接失败，继续获取下一条IP库的代理IP"+"\r\n");
				    break;
				}
				System.out.println("****重新连接****");
				JTARunInfo.append("****重新连接****"+"\r\n");
				html[1] = new HTML().getHTML(ipLibURL, JTARunInfo)[1];
				iReconn1++;
			}
			System.out.println("next");
			int page = 180;
			while (html[0].equals("200")) {
				System.out.println("start finding proxy IPs under this link: "
						+ url);
				JTARunInfo.append("开始获取这条链接下的代理IP: " + url + "\r\n");
				// JTARunInfo.paintImmediately(JTARunInfo.getBounds());
				
				onePageIPs = getProxyIPs(html[1]);
			    if(onePageIPs.size()==0)
			    	break;
				for (int j = 0; j < onePageIPs.size(); j++) {
					String s = onePageIPs.get(j);
					if (!allIPs.contains(s)) {
						allIPs.add(s);
					}
				}

					url = url.substring(0,url.lastIndexOf("/")+1) + page;
				// System.out.println("page = "+page);
				html = new HTML().getHTML(url, JTARunInfo);
				int iReconn = 0;
				int reConnectTimes = 5;
				while(html[1].equals("null")){
					if(iReconn == (reConnectTimes-1)){
						System.out.println("连续"+reConnectTimes+"次连接失败，继续获取下一条IP库的代理IP");
						JTARunInfo.append("连续"+reConnectTimes+"次连接失败，继续获取下一条IP库的代理IP"+"\r\n");
					    break;
					}
					System.out.println("****重新链接****");
					JTARunInfo.append("****重新连接****"+"\r\n");
					html = new HTML().getHTML(url, JTARunInfo);
					iReconn++;
				}
				System.out.println("状态码 "+html[0]);
				page++;
			}
		}
		System.out.println("total proxy IP number:： " + allIPs.size());
		JTARunInfo.append("获取到的代理IP总数:： " + allIPs.size() + "\r\n");

		return allIPs;
	}

	/**
	 * Test all proxy IPs and select the valid ones.
	 * 
	 * @param a
	 *            String Vector which contains all proxy IPs
	 * @return a String Vector which contains all valid IPs selected from all
	 *         candidate proxy IPs
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Vector<String> getValidProxyIPs(Vector<String> allIPs,
			JTextArea JTARunInfo) throws ClientProtocolException, IOException {
		System.out.println("********start getting valid proxy IPs********");
		JTARunInfo.append("********开始获取有效代理IP********" + "\r\n");

		//Vector<String> validHostname = new Vector<String>();
		Vector<String> validHostWithPort = new Vector<String>();
		int validIPNum = 0;
		for (int i = 0; i < allIPs.size(); i++) {
			// if(i == 100){
			// break;
			// }
			String ip = allIPs.get(i);
			String hostWithPort = "null";
			String hostName = ip.split(":")[0];
			String portString = ip.split(":")[1];
			int port = Integer.parseInt(portString);
			String varifyURL = "http://ip.uee.cn/";// http://ip.uee.cn/
																// http://iframe.ip138.com/ic.asp

			String html = new HTML().getHTMLbyProxy(varifyURL, hostName, port, JTARunInfo);
			int iReconn = 0;
			int reConnectTimes = 2;//视网速而定
			while (html.equals("null")) {// reconnect 2 times (total 3 times
											// connection)
				if (iReconn == (reConnectTimes-1)) {
					System.out.println(reConnectTimes+" 次连接超时，放弃此IP");
					JTARunInfo.append(reConnectTimes+" 次连接超时，放弃此IP"+"\r\n");
					break;
				}
				System.out.println("****重新连接****");
				JTARunInfo.append("****重新连接****"+"\r\n");
				html = new HTML().getHTMLbyProxy(varifyURL, hostName, port, JTARunInfo);
				iReconn++;
			}
			Pattern p = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
			Matcher m = p.matcher(html);
			//String s;
			if (m.find()) {
				Document doc = Jsoup.parse(html);
				Element ele = doc.select("center").first();
				String showIP = ele.text();
				System.out.println(showIP);
				JTARunInfo.append("第 "+(i+1)+" 条IP信息返回结果： "+ showIP +"\r\n");
				String s = m.group();
				hostWithPort = s + ":" + portString;
//				if (!validHostname.contains(s)) {
//					validHostname.add(s);//
//					validIPs.add(s + ":" + String.valueOf(port));
//					// bw.write(s+"\r\n");//write a valid proxy ip
//					validIPNum++;
//					System.out.println("valid proxy IP " + s + ":"+ String.valueOf(port));
//					JTARunInfo.append("第 "+(i+1)+"条IP是可用代理IP " + s + ":"+ String.valueOf(port) + "\r\n");
//				}
				if (!validHostWithPort.contains(hostWithPort)) {
					validHostWithPort.add(hostWithPort);//
				//validIPs.add(s + ":" + String.valueOf(port));
				// bw.write(s+"\r\n");//write a valid proxy ip
				validIPNum++;
				System.out.println("valid proxy IP " + hostWithPort);
				JTARunInfo.append("第 "+(i+1)+"条IP是可用代理IP " + hostWithPort + "\r\n");
			    }
				else{
					System.out.println("No."+(i+1)+" IP " + hostWithPort + "have been saved.");
					JTARunInfo.append("第 "+(i+1)+"条IP " + hostWithPort + "已存在" +"\r\n");
				}
			} else {
				System.out.println("No."+(i+1)+" IP is invalid.");
				JTARunInfo.append("第 "+(i+1)+"条代理IP不可用" + "\r\n");
			}
			System.out.println("NO." + (i+1) + " ip"+ ip +" be verified");
			JTARunInfo.append("第 " + (i+1) + " 条IP"+ ip +"可用性验证完毕，实际使用时ip为" +hostWithPort+ "\r\n");

		}
		System.out.println("total number of valid IPs " + validIPNum);
		JTARunInfo.append("有效代理IP总数：  " + validIPNum + "\r\n");

		return validHostWithPort;
	}

	/**
	 * Verify all valid IPs then save the "plain" IPs.
	 * 
	 * @param validIPs
	 *            - the Vector<String> contains all valid IPs
	 * @param plainPath
	 *            - a String giving a path to save all "plain" IPs. "plain" IP
	 *            indicate if it is used as a proxy IP to connect a weibo search
	 *            page, the weibo sentence can be read directly in the response
	 *            HTML in a 2012 version but not enclosed as UTF8/GB2312/GBK
	 *            charset format in 2014 version. I still do not know why it
	 *            returns two versions HTML by different IPs. (2014/3/22)
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public Vector<String> classifyIPs(Vector<String> validIPs,
			String plainPath, JTextArea JTARunInfo)
			throws ClientProtocolException, IOException {
		final String verificationURL = "http://s.weibo.com/weibo/李雪山hakka&nodup=1&page=1";
		// Vector<String> utf8IPs = new Vector<String>();
		Vector<String> plainIPs = new Vector<String>();
		String ip;
		
		for (int i = 0; i < validIPs.size(); i++) {
			System.out.println("****开始验证第"+(i+1)+"个validIP");
			JTARunInfo.append("****开始验证第 "+(i+1)+" 个validIP"+"\r\n");
			ip = validIPs.get(i);
			String html = new HTML().getHTMLbyProxy(verificationURL,
					ip.split(":")[0], Integer.parseInt(ip.split(":")[1]), JTARunInfo);
			int iReconn = 0;
			int reConnectTimes = 5;
			while (html.equals("null")) {
				if (iReconn == (reConnectTimes-1)) {
					System.out
							.println("****连续"+reConnectTimes+"次连接微博搜索站点(http://s.weibo.com/weibo)失败，放弃此IP****");
					JTARunInfo
							.append("****连续"+reConnectTimes+"次连接微博搜索站点(http://s.weibo.com/weibo)失败，放弃此IP****"
									+ "\r\n");
					break;
				}
				html = new HTML().getHTMLbyProxy(verificationURL,
						ip.split(":")[0], Integer.parseInt(ip.split(":")[1]), JTARunInfo);
				iReconn++;
				System.out.println("****" + ip + "is reconnecting the"
						+ iReconn + " time****");
				JTARunInfo.append("****" + ip + "重连第" + iReconn + " 次****"
						+ "\r\n");

			}
			if (html.contains("version=2012")) {
				plainIPs.add(ip);
				System.out.println("第 "+(i+1)+" 个validIP是可用IP(plainIP): " + ip);
				JTARunInfo.append("第 "+(i+1)+" 个validIP是可用IP(plainIP): " + ip + "\r\n");
				// write2txt(html, "d:/data/weibo/test/2012_"+i+".html");
			}
			else{
				if(html.contains("version=2014")){
					System.out.println("第 "+(i+1)+" 个validIP: " + ip +"可用于2014版本的html，但不可用于2012版即不可用于此软件");
					JTARunInfo.append("第 "+(i+1)+" 个validIP: " + ip +"可用于2014版本的html，但不可用于2012版即不可用于此软件"+ "\r\n");
				}
				else{
					System.out.println("第 "+(i+1)+" 个validIP: "+ip+" 无效（难以连接或不适用于2012及2014版的微博搜索站点）");
					JTARunInfo.append("第 "+(i+1)+" 个validIP: "+ip+" 无效（难以连接或不适用于2012及2014版的微博搜索站点）"+"\r\n");
				}
			}
		}

		return plainIPs;
	}

	public void excute(String[] args, JTextArea JTARunInfo)
			throws ClientProtocolException, IOException, URISyntaxException {
		long t1 = System.currentTimeMillis();
		// args[0] = savePlainIPs + "allIPs.txt";
		// args[1] = savePlainIPs + "validIPs.txt";
		// args[2] = savePlainIPs + "plainIPs.txt";
		String allIPsPath = args[0];
		String validIPsPath = args[1];
		String plainIPsPath = args[2];
		String ipLibURL = "http://www.xici.net.co/";
		JTARunInfo.append("代理IP源为：http://www.xici.net.co/，感谢此网站的资源！"+"\r\n");
		Vector<String> validIPs = new Vector<String>();
		Vector<String> allIPs = new Vector<String>();
		Vector<String> plainIPs = new Vector<String>();
		allIPs = getAllProxyIPs(ipLibURL, JTARunInfo);
		FileOperation.write2txt(allIPs, allIPsPath);
		validIPs = getValidProxyIPs(allIPs, JTARunInfo);
		FileOperation.write2txt(validIPs, validIPsPath);
		plainIPs = new ProxyIP()
				.classifyIPs(validIPs, plainIPsPath, JTARunInfo);
		
		int plainIPsNum = plainIPs.size();
		JTARunInfo.append("最终得到 "+plainIPsNum+ "个plainIP，如下："+"\r\n");
		for(int i = 0; i < plainIPs.size(); i++){
			JTARunInfo.append(plainIPs.get(i)+"\r\n");
		}
		FileOperation.write2txt(plainIPs, plainIPsPath);
		long t2 = System.currentTimeMillis();
		System.out.println("获取可用IP耗时" + (double) (t2 - t1) / 60000 + "分钟");
		JTARunInfo.append("获取可用IP耗时" + (double) (t2 - t1) / 60000 + "分钟"
				+ "\r\n");
	}
}
