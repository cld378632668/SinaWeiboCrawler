package cn.edu.xmu.cld.WeiboCrawler2.useVersion2014;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class WeiboCrawler3 {

	public static void main(String[] args) throws ClientProtocolException,
			URISyntaxException, IOException, InterruptedException {
		Sina sina=new Sina();
		long t1 = System.currentTimeMillis();

		WeiboCrawler3 crawler = new WeiboCrawler3();
		/*
		  String[] searchwords = {
				"汽车摇号","自贸区","雾霾%20PM2.5","医疗改革","土地改革","房价调控"
				,"农村集体产权%20改革","东莞扫黄","油价","城镇化","国产航母","中石油腐败","养老制度"
				,"嫣然基金","养老保险","拆迁","医患关系","计划生育%20超生","反腐%20纪委","城管"
				,"土地流转权","发改委","证监会","余额宝","理财产品","环保","教育部"
				,"高考","自主招生","股市%20A股","单独二胎%20单独二孩"
				,"乌克兰局势","钓鱼岛争端","越南%20南海争端","菲律宾%20黄岩岛","叙利亚%20化学武器"
				,"伊朗%20核问题%20危机","中期选举","埃及局势","泰国局势","朝韩冲突","南海识别区"
				};
		 */
		String[] searchwords = {
				"小米3"
		};
		String today = getMyTime();
		File dirGetweiboSub = new File("d:/data/weibo/getweibo/"+today);
		dirGetweiboSub.mkdirs();
		File dirWeibostxtSub = new File("d:/data/weibo/saveweibo/weibostxt/"+today);
		dirWeibostxtSub.mkdirs();
		//File dirWeibosxmlSub = new File("d:/data/weibo/saveweibo/weibosxml/"+today);
		//dirWeibosxmlSub.mkdirs();
		for (int n = 0; n < searchwords.length; n++) {
			String searchword = searchwords[n];
			File f = new File("d:/data/weibo/getweibo/"+today+"/"+searchword);
			f.mkdirs();//创建文件夹，另一方法mkdirs创建多层未创建的文件夹
			//String html;
			String saveWeibosTXTPath = "d:/data/weibo/saveweibo/weibostxt/"+today+"/"+searchword+".txt";
			//String saveWeibosXMLPath = "d:/data/weibo/saveweibo/weibosxml/"+today+"/"+searchword+".xml";
			int totalPage =2;//设置想要搜索的页数，则搜索范围为该搜索词下的第1到第totalPage页

			System.out.println("****开始爬取 \""+searchword+"\" 关键字的微博****");
			//将指定页数的搜索页面html文件爬取下来并保存
			crawler.crawl(totalPage, today, searchword);
			System.out.println("****爬取 \""+searchword+"\" 微博结果结束****");

			Vector<String> oneHTMLWeibos = new Vector<String>();//一个html下的所有微博
			Vector<String> allWeibos = new Vector<String>();//一个关键词搜索出来的所有微博
			/**
			 * 此循环从搜索页面html文件中得到微博，微博id，存入向量容器中
			 */
			for (int i = 1; i < totalPage+1; i++) {
				String htmlPath = "d:/data/weibo/getweibo/"+today+"/"+searchword+"/"+searchword+String.valueOf(i)+".html";
				File file = new File(htmlPath);

				if(file.isFile()){//如果这个文件存在，就解析出微博来
					String htmlString = crawler.htmltoString(htmlPath);
					oneHTMLWeibos = crawler.getWeiboInfo(htmlString);
				}
				for (int j = 0; j < oneHTMLWeibos.size(); j++) {//存到总微博里
					if(!allWeibos.contains(oneHTMLWeibos.get(j))){
						allWeibos.add(oneHTMLWeibos.get(j));

					}

				}
				System.out.println(allWeibos);
			}
			/**
			 * 将结果写入文件
			 */
			crawler.writeVector(allWeibos, saveWeibosTXTPath);
			//crawler.writeVector2xml(allWeibos, saveWeibosXMLPath);
			Toolkit.getDefaultToolkit().beep();
			Thread.sleep(500);
			Toolkit.getDefaultToolkit().beep();//响两声表示爬取一个关键词结束
		}
		Toolkit.getDefaultToolkit().beep();
		Thread.sleep(500);
		Toolkit.getDefaultToolkit().beep();
		Thread.sleep(500);
		Toolkit.getDefaultToolkit().beep();//响三声提示一下爬取结束

		long t2 = System.currentTimeMillis();
		System.out.println((t2 - t1)/60000 + "分钟");
	}


	/**
	 * 此函数调用底层函数，执行具体的爬取微博的过程
	 * 并保存微博搜索结果页面html文件
	 * @param totalPage
	 * @param searchword
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void crawl(int totalPage, String today, String searchword) throws ClientProtocolException, URISyntaxException, IOException, InterruptedException {
		// TODO Auto-generated method stub
		String html;
		WeiboCrawler3 crawler = new WeiboCrawler3();
		for(int i = totalPage; i > 0; i--){//开始爬取，先把一个话题下的html都爬下来，再用这些html文件
			html = crawler.getHTML(
					"http://s.weibo.com/weibo/"+searchword+"&nodup=1&page="+String.valueOf(i));
			if(html != "html获取失败"){
				if(crawler.isVerification(html)){
					System.out.println("****十秒内请在弹出页面输入验证码，程序会自动重连****");
					Toolkit.getDefaultToolkit().beep();//蜂鸣提示需要输入验证码
					crawler.runBroswer("http://s.weibo.com/weibo");
					Thread.sleep(10000);//你有十秒时间可以填入验证码
					i++;
				}
				else if(crawler.isExistResult(html)){

					crawler.writeString(html,
							"d:/data/weibo/getweibo/"+today+"/"+searchword+"/"+searchword+String.valueOf(i)+".html");
					System.out.println("获取第"+i+"页成功！");
				}
				else
					System.out.println("第"+i+"页内容不存在");
			}
			else {//如果因为连接超时问题，获得的html为"html获取失败"的话，则再重新连接一次
				i++;
			}
		}
	}

	public static String getMyTime(){

		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String today = dateFormat.format(date);
		System.out.println(today);

		return today;
	}

	/**
	 * 用默认浏览器打开指定网址
	 * @param url
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public void runBroswer(String url) throws URISyntaxException, IOException {  
            Desktop desktop = Desktop.getDesktop();  
            if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {  
                URI uri = new URI(url);  
                desktop.browse(uri);   
                }
	}
	/**
	 * 由url得到html
	 * @param url
	 * @return html
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public String getHTML(String url) throws URISyntaxException, ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		//采用用户自定义cookie策略，只是使cookie rejected的报错不出现，此错误仍然存在
		String hostName="111.205.122.222";
		int port=80;
		CookieStore cookieStore = Sina.login("330189287@qq.com","512107").cookiestore;
		System.out.println(cookieStore);
 
		HttpHost proxy = new HttpHost(hostName, port);// 58.22.28.133:80
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(
				proxy);
		CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
			public CookieSpec create(HttpContext context) {
				return new BrowserCompatSpec() {
					@Override
					public void validate(Cookie cookie, CookieOrigin origin)
							throws MalformedCookieException {
					}
				};
			}
		};
		Registry<CookieSpecProvider> r = RegistryBuilder
				.<CookieSpecProvider> create()
				.register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register(CookieSpecs.BROWSER_COMPATIBILITY,
						new BrowserCompatSpecFactory())
				.register("easy", easySpecProvider).build();
		RequestConfig requestConfig = RequestConfig.custom()
				.setCookieSpec("easy")
				.setSocketTimeout(1000)//设置socket超时时间
				.setConnectTimeout(1000)//设置connect超时时间
				.build();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultCookieSpecRegistry(r)
				.setDefaultRequestConfig(requestConfig).build();
		CloseableHttpClient httpClient2 = HttpClients.custom()
				.setDefaultCookieStore(cookieStore)
				.setDefaultCookieSpecRegistry(r).setRoutePlanner(routePlanner)
				.setDefaultRequestConfig(requestConfig).build();

//		CloseableHttpClient httpClient = HttpClients.createDefault();//若不自定义cookie策略，则报错cookie rejected
//		RequestConfig requestConfig = RequestConfig.custom()
//				.setSocketTimeout(5000)
//				.setConnectTimeout(5000)
//				.build();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		String html = "html获取失败";//用于验证是否正常取到html
		try{
			CloseableHttpResponse response = httpClient2.execute(httpGet);
			html = EntityUtils.toString(response.getEntity());
			
			System.out.println(html);//打印返回的html
		} catch(IOException e){
			System.out.println("****连接超时，程序自动重连****");
		}
	
		return html;
	}
	/**
	 * 把String写到本地文件
	 * @param s
	 * @param savePath
	 * @throws IOException
	 */
	
	public void writeString(String s, String savePath) throws IOException {
		// TODO Auto-generated method stub
        File f = new File(savePath);
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(s);
        bw.close();
	}
	/**
	 * 由html文件得到微博
	 * @param html
	 * @return
	 * @throws IOException
	 */
	public String htmltoString(String htmlPath) throws IOException {
		// TODO Auto-generated method stub
        String html = null;
        File f = new File(htmlPath);
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String s;
		while ((s = br.readLine()) != null){//读html文件为String
			html = html + s; 
		}
		br.close();
		
        return html;
	}
	
	public String getWeiboid(String iniWeibo) {
		// TODO Auto-generated method stub
		String weiboid = null;
		Pattern pweiboid = Pattern.compile("feed_list W_linecolor.+?mid=\\\\\"[0-9]{16}");
		Matcher mweiboid = pweiboid.matcher(iniWeibo);
		if(mweiboid.find()){
			weiboid = mweiboid.group();
			weiboid = weiboid.substring(weiboid.indexOf("mid=")+6);
		}
		//System.out.println("weiboid "+weiboid);
		
        return weiboid;
	}
	
	public String getUserid(String iniWeibo) {
		// TODO Auto-generated method stub
		String userid = null;
		Pattern puserid = Pattern.compile("class=\\\\\"face\\\\\".+?<img src");
		Matcher muserid = puserid.matcher(iniWeibo);
		if(muserid.find()){
			userid = muserid.group();
			userid = userid.substring(userid.indexOf("<img")-16, userid.indexOf("<img")-6);
		}
		//System.out.println("userid "+userid);
        
		return userid;
	}
	
	public String getWeiboSentence(String iniWeibo) {
		// TODO Auto-generated method stub
		Vector<String> vectorOneUTF8 = new Vector<String>();
		Vector<String> vectorOneChar = new Vector<String>();
		String weibo = "空";
		String weibotemp = null;
		String utf8CodeStr;
		String oneUTF8;
		char cUTF8;
		String cString;
		Pattern p = Pattern.compile("#\u6211\u7231<em>.+?<\\\\/em>");
		Matcher m = p.matcher(iniWeibo);
		Pattern pUTF8 = Pattern.compile("\\\\u[0-9a-f]{4}");//识别汉字utf-8编码码文
		if(m.find()){
			weibotemp = m.group();
			weibotemp = weibotemp.replaceAll("<.+?>", "");
			weibotemp = weibotemp.replaceAll("ff1a", "");
			weibotemp = weibotemp.replaceAll("&quot;", "\\\"");
			//System.out.println("weiboUTF8"+weibotemp);
            Matcher mUTF8 = pUTF8.matcher(weibotemp);
            while(mUTF8.find()){
            	oneUTF8 = mUTF8.group();
            	vectorOneUTF8.add(oneUTF8);
            	utf8CodeStr = oneUTF8.substring(2);
            	cUTF8 = (char) Integer.parseInt(utf8CodeStr,16);
            	cString = String.valueOf(cUTF8);
            	//System.out.print(cString);
            	vectorOneChar.add(cString);
            }
		}
		for(int i = 0; i < vectorOneUTF8.size(); i++){
			weibotemp = weibotemp.replaceFirst("\\"+vectorOneUTF8.get(i), vectorOneChar.get(i));
		}
		weibo = weibotemp.replaceAll("\\\\", "");
		//System.out.println(weibo);
		
		return weibo;
	}
	
	public String getPraisedNum(String iniWeibo) {
		// TODO Auto-generated method stub
		Pattern ppraised = Pattern.compile("W_ico12 icon_praised_b.+?<\\\\/a>");
		Pattern ppraisedNum = Pattern.compile("[0-9]+");
		Matcher mpraised = ppraised.matcher(iniWeibo);
		String praisedNum = null;
		if(mpraised.find()){
			praisedNum = mpraised.group().substring(mpraised.group().indexOf("em>")+3, mpraised.group().indexOf("a>")-3);
			Matcher mpraisedNum = ppraisedNum.matcher(praisedNum);
			if(mpraisedNum.find()){
				praisedNum = mpraisedNum.group();
			}
			if(praisedNum.equals("")){
				praisedNum = "0";
			}
		}
		//System.out.println("赞数"+praisedNum);
		
		return praisedNum;
	}
	
	public String getForwardNum(String iniWeibo) {
		// TODO Auto-generated method stub
		String forwardNum;
		String forwardNumtemp = null;
        Pattern pforward = Pattern.compile("action-data=\\\\\"allowForward.+?u53d1.+?/a>");//转发
        Matcher mforward = pforward.matcher(iniWeibo);
        Pattern pforwardNum = Pattern.compile("[0-9]+");
        if(mforward.find()){
        	forwardNumtemp = mforward.group();
        	forwardNumtemp = forwardNumtemp.substring(forwardNumtemp.indexOf("u53d1")+5, forwardNumtemp.indexOf("/a>")-2);
        	Matcher mforwardNum = pforwardNum.matcher(forwardNumtemp);
        	if(mforwardNum.find()){
            	forwardNumtemp = mforwardNum.group();
            }
            if(forwardNumtemp.equals("")){
            	forwardNumtemp = "0";
            }
        }
        forwardNum = forwardNumtemp;
        //System.out.println("转发数"+forwardNum);
        
        return forwardNum;
	}
	
	public String getCommentNum(String iniWeibo) {
		// TODO Auto-generated method stub
        String commenttemp = null;
        Pattern pcomment = Pattern.compile("\\\\u8bc4\\\\u8bba.+?/a>");
        Matcher mcomment = pcomment.matcher(iniWeibo);
        Pattern pcommentNum = Pattern.compile("[0-9]+");
        if(mcomment.find()){
        	commenttemp = mcomment.group();
        	//System.out.println("commenttemp"+commenttemp);
        	commenttemp = commenttemp.substring(commenttemp.indexOf("u8bba")+5, commenttemp.indexOf("a>")-3);
        	Matcher mcommentNum = pcommentNum.matcher(commenttemp);
            if(mcommentNum.find()){
            	commenttemp = mcommentNum.group();
            }
            if(commenttemp.equals("")){
            	commenttemp = "0";
            }
        }
        
       //System.out.println("评论数"+commenttemp);
        
        return commenttemp;
	}
	
	public String getDate(String iniWeibo) {
		// TODO Auto-generated method stub
        String date = "";
        Pattern pdate = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}");
        Matcher mdate = pdate.matcher(iniWeibo);
        if(mdate.find()){
        	date = mdate.group();
        }
        //System.out.println(date);
        
        return date;
	}
	
	public String getUserName(String iniWeibo) {
		// TODO Auto-generated method stub
        String userName = "";
        String oneUTF8;
        String utf8CodeStr;
        char cUTF8;
        String cString;
        Vector<String> nameUTF8 = new Vector<String>();
        Vector<String> nameChar = new Vector<String>();
        Pattern puserName = Pattern.compile("nick-name.+?href");
        Pattern pUTF8 = Pattern.compile("\\\\u[0-9a-f]{4}");
        Matcher muserName = puserName.matcher(iniWeibo);
        if(muserName.find()){
        	userName = muserName.group().substring(muserName.group().indexOf("nick-name=")+12, muserName.group().indexOf("href")-3);
        }
        Matcher mUTF8 = pUTF8.matcher(userName);
        while(mUTF8.find()){
        	oneUTF8 = mUTF8.group();
        	nameUTF8.add(oneUTF8);
        	utf8CodeStr = oneUTF8.substring(2);
        	cUTF8 = (char) Integer.parseInt(utf8CodeStr,16);
        	cString = String.valueOf(cUTF8);
        	nameChar.add(cString);
        }
        for(int i = 0; i < nameUTF8.size(); i++){
        	userName = userName.replaceFirst("\\"+nameUTF8.get(i), nameChar.get(i));
		}
        //System.out.println(userName);
        
        return userName;
	}
	public Vector<String> getWeiboInfo(String htmlString) {
		// TODO Auto-generated method stub
		WeiboCrawler3 clawler = new WeiboCrawler3();
		Vector<String> iniWeibo = new Vector<String>();
		String oneIniWeibo;
        Pattern poneIniWeibo = Pattern.compile("feed_list_new W_linecolor.+?W_ico12 icon_praised_b.+?feed_list_item_date");//根据微博搜索页面html的特点，只有当前微博有赞，转发的那条内嵌微博不呈现赞
        Pattern pforwardContent = Pattern.compile("comment_info");//是否转发自某条微博
        Matcher moneIniWeibo = poneIniWeibo.matcher(htmlString);
		while(moneIniWeibo.find()){
			oneIniWeibo = moneIniWeibo.group();
			Matcher mforwardContent = pforwardContent.matcher(oneIniWeibo);
			if(mforwardContent.find()){//去除掉转发的那条微博
				oneIniWeibo = oneIniWeibo.replaceAll("<div node-type=\\\\\"feed_list_forwardContent\\\\\">.+?<p class=\\\\\"W_linkb\\\\\">", "");
			}
			String userName = clawler.getUserName(oneIniWeibo);
			String date = clawler.getDate(oneIniWeibo);
			String userid = clawler.getUserid(oneIniWeibo);
			String weiboid = clawler.getWeiboid(oneIniWeibo);
			//String weiboSentence = clawler.getWeiboSentence(oneIniWeibo);
			String praisedNum = clawler.getPraisedNum(oneIniWeibo);
			String forwardNum = clawler.getForwardNum(oneIniWeibo);
			String commentNum = clawler.getCommentNum(oneIniWeibo);
			iniWeibo.add("<userName_s "+userName+" userName_e>"
					+"<userid_s "+userid+" userid_e>"
					+"<date_s "+date+" date_e>"
					+"<weiboid_s "+weiboid+" weiboid_e>"
					//+"<weiboSentence_s "+weiboSentence+" weiboSentence_e>"
					+"<praisedNum_s "+praisedNum+" praisedNum_e>"
					+"<forwardNum_s "+forwardNum+" forwardNum_e>"
					+"<commentNum_s "+commentNum+" commentNum_e>");
		}
		
		
        return iniWeibo;
	}
	/**
	 * 把某关键字搜索到的微博全写到文件中去
	 * 这里简单写成了txt格式
	 * @param vector
	 * @param savePath
	 * @throws IOException
	 */
	public void writeVector(Vector<String> vector, String savePath) throws IOException {
		// TODO Auto-generated method stub
		File f = new File(savePath);
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < vector.size(); i++) {
			bw.write(vector.get(i)+"\r\n");
		}
        bw.close();
	}
	/*
	public void writeVector2xml(Vector<String> vector, String savePath) throws IOException {
		// TODO Auto-generated method stub
		int vectorSize = vector.size();
		String oneIniWeibo;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("GB2312");//将xml格式定义为gb2321，这样才能识别。默认是utf8，不能识别
		File f = new File(savePath);
		f.createNewFile();//先建一个空xml文件
		FileWriter fw = new FileWriter(f);
		org.dom4j.Document document = DocumentHelper.createDocument();//建document对象实例
		Element rootElement = document.addElement("weibos");//节点增加方法
		rootElement.addAttribute("totalNumber", String.valueOf(vectorSize));//设置属性
		for (int j = 0; j < vectorSize; j++) {
			oneIniWeibo = vector.get(j);
			String userName = oneIniWeibo.substring(oneIniWeibo.indexOf("<userName_s ")+12, oneIniWeibo.indexOf(" userName_e>"));
			String userid = oneIniWeibo.substring(oneIniWeibo.indexOf("<userid_s ")+10, oneIniWeibo.indexOf(" userid_e>"));
			String date = oneIniWeibo.substring(oneIniWeibo.indexOf("<date_s ")+8, oneIniWeibo.indexOf(" date_e>"));
			String weiboid = oneIniWeibo.substring(oneIniWeibo.indexOf("<weiboid_s ")+11, oneIniWeibo.indexOf(" weiboid_e>"));
			String praisedNum = oneIniWeibo.substring(oneIniWeibo.indexOf("<praisedNum_s ")+14, oneIniWeibo.indexOf(" praisedNum_e>"));
			String forwardNum = oneIniWeibo.substring(oneIniWeibo.indexOf("<forwardNum_s ")+14, oneIniWeibo.indexOf(" forwardNum_e>"));
			String commentNum = oneIniWeibo.substring(oneIniWeibo.indexOf("<commentNum_s ")+14, oneIniWeibo.indexOf(" commentNum_e>"));
			String weiboSentence = oneIniWeibo.substring(oneIniWeibo.indexOf("<weiboSentence_s ")+17, oneIniWeibo.indexOf(" weiboSentence_e>"));
			Element weiboElement = rootElement.addElement("weibo");
				weiboElement.addAttribute("polarity", "unknown");
				weiboElement.addAttribute("opinionated", "unknown");
				weiboElement.addAttribute("userName", userName);
				weiboElement.addAttribute("userid", userid);
				weiboElement.addAttribute("date", date);
				weiboElement.addAttribute("weiboid", weiboid);
				weiboElement.addAttribute("praisedNum", praisedNum);
				weiboElement.addAttribute("forwardNum", forwardNum);
				weiboElement.addAttribute("commentNum", commentNum);
				weiboElement.setText(weiboSentence);//设置节点文本内容	
			}
			XMLWriter xw = new XMLWriter(fw,format);
			xw.write(document);//写document到xml文件
			xw.close();
			
	}
	/**
	 * 判断是否需要输入验证码
	 * @param html
	 * @return
	 */
	public boolean isVerification(String html){
		boolean isVerify = false;
		Pattern pVerify = Pattern.compile("\\\\u4f60\\\\u7684\\\\u884c\\\\u4e3a\\\\u6709"
				+ "\\\\u4e9b\\\\u5f02\\\\u5e38\\\\uff0c\\\\u8bf7\\\\u8f93\\\\u5165\\\\u9a8c"
				+ "\\\\u8bc1\\\\u7801\\\\uff1a");//你的行为有些异常，请输入验证码（被系统检测出你是机器，会蜂鸣提示，刷新微博输入验证码就可以继续了）
		Matcher mVerify = pVerify.matcher(html);
		if(mVerify.find()){
			isVerify = true;
		}
		
		return isVerify;
	}
	/**
	 * 是否存在指定页数的搜索结果页面
	 * @param html
	 * @return
	 */
	public boolean isExistResult(String html){
		boolean isExist =  true;
		Pattern pExist = Pattern.compile("\\\\u60a8\\\\u53ef\\\\u4ee5\\\\u5c1d\\\\u8bd5"
				+ "\\\\u66f4\\\\u6362\\\\u5173\\\\u952e\\\\u8bcd\\\\uff0c\\\\u518d\\\\u6b21"
				+ "\\\\u641c\\\\u7d22\\\\u3002");//您可以尝试更换关键词，再次搜索。（表示指定页没有结果）
		Matcher mExist = pExist.matcher(html);
		if(mExist.find()){
			isExist = false;
		}
		
		return isExist;
	}

	

}
