package cn.edu.xmu.cld.WeiboCrawler2.useVersion2014;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;



public class Sina {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		System.out.println(login("330189287@qq.com", "512107"));

	}
	
	

	public static WeiBoUser login(String username, String password ) {

		WeiBoUser user = null;
        DefaultHttpClient client=new DefaultHttpClient();
		try {
			//获得rsaPubkey,rsakv,servertime等参数值
			HashMap<String, String> params = preLogin(encodeAccount(username),client);
			
			
			HttpPost post = new HttpPost(
					"http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.5)");
			post
					.setHeader("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			post
					.setHeader("User-Agent",
							"Mozilla/5.0 (Windows NT 5.1; rv:9.0.1) Gecko/20100101 Firefox/9.0.1");

			post.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			post.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
			post.setHeader("Referer",
					"http://weibo.com/?c=spr_web_sq_firefox_weibo_t001");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			

			String nonce = makeNonce(6);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
			nvps.add(new BasicNameValuePair("entry", "weibo"));
			nvps.add(new BasicNameValuePair("from", ""));
			nvps.add(new BasicNameValuePair("gateway", "1"));
			nvps.add(new BasicNameValuePair("nonce", nonce));
			nvps.add(new BasicNameValuePair("pagerefer", "http://i.firefoxchina.cn/old/"));
			nvps.add(new BasicNameValuePair("prelt", "111"));
			nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
			nvps.add(new BasicNameValuePair("returntype", "META"));
			nvps.add(new BasicNameValuePair("rsakv", params.get("rsakv")));
			nvps.add(new BasicNameValuePair("savestate", "0"));
			nvps.add(new BasicNameValuePair("servertime", params.get("servertime")));

			nvps.add(new BasicNameValuePair("service", "miniblog"));
			//nvps.add(new BasicNameValuePair("sp", new SinaSSOEncoder().encode(password, data, nonce)));
			
			
			/******************** *加密密码 ***************************/
			ScriptEngineManager sem = new ScriptEngineManager();
			ScriptEngine se = sem.getEngineByName("javascript");
			//FileReader f = new FileReader("d://sso.js");
		    se.eval(SinaSSOEncoder.getJs());
		    String pass = "";
		  
		    if (se instanceof Invocable) {
				Invocable invoke = (Invocable) se;
				// 调用preprocess方法，并传入两个参数密码和验证码

				pass = invoke.invokeFunction("getpass",
						password, params.get("servertime"), nonce,params.get("pubkey")).toString();
				
				System.out.println("c = " + pass);
			}
			
			
			
			
			
			nvps.add(new BasicNameValuePair("sp",pass));
			nvps.add(new BasicNameValuePair("su", encodeAccount(username)));
			nvps
			.add(new BasicNameValuePair(
					"url",
					"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
			
			nvps.add(new BasicNameValuePair("useticket", "1"));
		//	nvps.add(new BasicNameValuePair("ssosimplelogin", "1"));
			nvps.add(new BasicNameValuePair("vsnf", "1"));
			

			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = client.execute(post);

			String entity = EntityUtils.toString(response.getEntity());

			if (entity.replace("\"", "").indexOf("retcode=0") > -1) {
				System.out.println(entity);
				String url = entity.substring(entity
						.indexOf("http://passport.weibo.com/wbsso/login?"), entity
						.indexOf("code=0")+6 );				
				String strScr = "";      //首页用户script形式数据
				String nick = "暂无";     //昵称
                
				// 获取到实际url进行连接
				HttpGet getMethod = new HttpGet(url);
				response = client.execute(getMethod);
				 String set_cookie = response.getFirstHeader("Set-Cookie").getValue();
			        System.out.println(response);
				entity = EntityUtils.toString(response.getEntity());
		        System.out.println(entity);
				nick = entity.substring(entity.indexOf("displayname") + 14,
						entity.lastIndexOf("userdomain") - 3).trim();
				
				url = entity.substring(entity.indexOf("userdomain") + 13,
						entity.lastIndexOf("\""));
				url="?wvr=5&lf=reg";
				getMethod = new HttpGet("http://weibo.com/"+url);
				response = client.execute(getMethod);

				entity = EntityUtils.toString(response.getEntity());
				Document doc = Jsoup.parse(entity);
				Elements els = doc.select("script");
				
				if (els != null && els.size() > 0) {
					for (int i = 0, leg = els.size(); i < leg; i++) {
						
						if (els.get(i).html().indexOf("$CONFIG") > -1) {
							strScr = els.get(i).html();
							break;
						}
					}
				}

				if (!strScr.equals("")) {
					ScriptEngineManager manager = new ScriptEngineManager();
					ScriptEngine engine = manager.getEngineByName("javascript");

					engine.eval("function getMsg(){" + strScr
							+ "return $CONFIG['onick'];}");
					if (engine instanceof Invocable) {
						Invocable invoke = (Invocable) engine;
						// 调用preprocess方法，并传入两个参数密码和验证码

						nick = invoke.invokeFunction("getMsg", null).toString();
						System.out.println(2222222);
					}
				}
				user = new WeiBoUser();
				user.setUserName(username);
				user.setUserPass(password);
				user.setDisplayName(nick);
                user.getCookieStore(client.getCookieStore());
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			// logger.info(e.getMessage());
			user = null;
		}
	return user;

	}
	
	
	/** 
     * 根据URL,get网页 
     *  
     * @param url 
     * @throws IOException 
     */  
    private static String get(String url,CloseableHttpClient client) throws IOException {  
        HttpGet get = new HttpGet(url);  
        HttpResponse response = client.execute(get);  
        System.out.println(response.getStatusLine());  
        HttpEntity entity = response.getEntity();  
        String result = dump(entity);  
        get.abort();  
        return result;  
    }  
  
    /** 
		 * 新浪微博预登录，获取密码加密公钥
     *  
     * @param unameBase64 
     * @return 返回从结果获取的参数的哈希表 
     * @throws IOException 
     */  
    private static HashMap<String, String> preLogin(String unameBase64,DefaultHttpClient client)  
            throws IOException {  
        String url = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su=&rsakt=mod&client=ssologin.js(v1.4.5)&_=" + "_=" + new Date().getTime();  
        return getParaFromResult(get(url,client));  
    }  
    
    /** 
     * 从新浪返回的结果字符串中获得参数 
     *  
     * @param result 
     * @return 
     */  
    private static HashMap<String, String> getParaFromResult(String result) {  
        HashMap<String, String> hm = new HashMap<String, String>();  
        result = result.substring(result.indexOf("{") + 1, result.indexOf("}"));  
        String[] r = result.split(",");  
        String[] temp;  
        for (int i = 0; i < r.length; i++) {  
            temp = r[i].split(":");  
            for (int j = 0; j < 2; j++) {  
                if (temp[j].contains("\""))  
                    temp[j] = temp[j].substring(1, temp[j].length() - 1);  
            }  
            hm.put(temp[0], temp[1]);  
        }  
        return hm;  
    }  
    
    /**  
     * 打印页面  
     *   
     * @param entity  
     * @throws IOException  
     */  
    private static String dump(HttpEntity entity) throws IOException {  
        BufferedReader br = new BufferedReader(new InputStreamReader(  
                entity.getContent(), "utf8"));  
        return IOUtils.toString(br);  
    }  
	
	
	
	
	

	public static boolean Share(String u, String p, String content, String pic,
			String surl) {
		HttpClient client = new DefaultHttpClient(
				new ThreadSafeClientConnManager());

		client.getParams().setParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 5000);

		try {
			HttpPost post = new HttpPost(
					"http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.3.22)");

			String data = getServerTime();

			String nonce = makeNonce(6);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("entry", "weibo"));
			nvps.add(new BasicNameValuePair("gateway", "1"));
			nvps.add(new BasicNameValuePair("from", ""));
			nvps.add(new BasicNameValuePair("savestate", "7"));
			nvps.add(new BasicNameValuePair("useticket", "1"));
			nvps.add(new BasicNameValuePair("ssosimplelogin", "1"));
			nvps.add(new BasicNameValuePair("su", encodeAccount(u)));
			nvps.add(new BasicNameValuePair("service", "miniblog"));
			nvps.add(new BasicNameValuePair("servertime", data));
			nvps.add(new BasicNameValuePair("nonce", nonce));
			nvps.add(new BasicNameValuePair("pwencode", "wsse"));
			nvps.add(new BasicNameValuePair("sp", new SinaSSOEncoder().encode(
					p, data, nonce)));

			nvps
					.add(new BasicNameValuePair(
							"url",
							"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
			nvps.add(new BasicNameValuePair("returntype", "META"));
			nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
			nvps.add(new BasicNameValuePair("vsnf", "1"));
			nvps.add(new BasicNameValuePair("prelt", "1021"));

			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = client.execute(post);
			String entity = EntityUtils.toString(response.getEntity());
			System.out.println(entity);
			if (entity.replace("\"", "").indexOf("retcode=0") > -1) {

				String url = entity.substring(entity
						.indexOf("http://weibo.com/ajaxlogin.php?"), entity
						.indexOf("code=0") + 6);

				// 获取到实际url进行连接
				HttpGet getMethod = new HttpGet(url);

				response = client.execute(getMethod);
				entity = EntityUtils.toString(response.getEntity());
				System.out.println(entity);
				entity = entity.substring(entity.indexOf("userdomain") + 13,
						entity.lastIndexOf("\""));
				System.out.println(entity);

				/*************************************************************************************/

				post = new HttpPost("http://v.t.sina.com.cn/share/aj_share.php");
				post
						.addHeader(
								"Referer",
								"http://v.t.sina.com.cn/share/share.php?url=http%3A%2F%2Fnews.sina.com.cn%2Fc%2F2012-03-31%2F074424204961.shtml&title=%E5%B1%B1%E4%B8%9C%E6%BB%95%E5%B7%9E%E6%9D%91%E6%B0%91%E5%9B%A0%E7%8B%BC%E5%92%AC%E4%BA%BA%E4%BA%8B%E4%BB%B6%E7%95%99%E5%BF%83%E7%90%86%E9%98%B4%E5%BD%B1&ralateUid=1618051664&source=%E6%96%B0%E6%B5%AA%E6%96%B0%E9%97%BB&sourceUrl=http%3A%2F%2Fnews.sina.com.cn%2F&content=gb2312&pic=http%3A%2F%2Fi1.sinaimg.cn%2Fdy%2Fcr%2F2012%2F0331%2F1728605356.jpg");

				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("appkey", "1671520477"));
				nvps.add(new BasicNameValuePair("content", content));
				nvps.add(new BasicNameValuePair("from", "share"));
				nvps.add(new BasicNameValuePair("refer", ""));
				nvps.add(new BasicNameValuePair("share_pic", pic));
				nvps.add(new BasicNameValuePair("source", ""));
				nvps.add(new BasicNameValuePair("sourceUrl", surl));
				nvps.add(new BasicNameValuePair("styleid", "1"));
				nvps.add(new BasicNameValuePair("url_type", "0"));
				post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				response = client.execute(post);

				entity = EntityUtils.toString(response.getEntity());
				if (entity.replace("\"", "").indexOf("code:A00006") > -1) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}

	}

	public static boolean AddW(String u, String p, String text, byte[] pic) {
		DefaultHttpClient client = new DefaultHttpClient(
				new ThreadSafeClientConnManager());
		try {
            HashMap<String, String> params = preLogin(encodeAccount(u),client);
			
			
			HttpPost post = new HttpPost(
					"http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.5)");
			post
					.setHeader("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			post
					.setHeader("User-Agent",
							"Mozilla/5.0 (Windows NT 5.1; rv:9.0.1) Gecko/20100101 Firefox/9.0.1");

			post.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
			post.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");
			post.setHeader("Referer",
					"http://weibo.com/?c=spr_web_sq_firefox_weibo_t001");
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");

			

			String nonce = makeNonce(6);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
			nvps.add(new BasicNameValuePair("entry", "weibo"));
			nvps.add(new BasicNameValuePair("from", ""));
			nvps.add(new BasicNameValuePair("gateway", "1"));
			nvps.add(new BasicNameValuePair("nonce", nonce));
			nvps.add(new BasicNameValuePair("pagerefer", "http://i.firefoxchina.cn/old/"));
			nvps.add(new BasicNameValuePair("prelt", "111"));
			nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
			nvps.add(new BasicNameValuePair("returntype", "META"));
			nvps.add(new BasicNameValuePair("rsakv", params.get("rsakv")));
			nvps.add(new BasicNameValuePair("savestate", "0"));
			nvps.add(new BasicNameValuePair("servertime", params.get("servertime")));

			nvps.add(new BasicNameValuePair("service", "miniblog"));
			//nvps.add(new BasicNameValuePair("sp", new SinaSSOEncoder().encode(p, data, nonce)));
			
			
			/******************** *加密密码 ***************************/
			ScriptEngineManager sem = new ScriptEngineManager();
			ScriptEngine se = sem.getEngineByName("javascript");
			//FileReader f = new FileReader("d://sso.js");
		    se.eval(SinaSSOEncoder.getJs());
		    String pass = "";
		  
		    if (se instanceof Invocable) {
				Invocable invoke = (Invocable) se;
				// 调用preprocess方法，并传入两个参数密码和验证码

				pass = invoke.invokeFunction("getpass",
						p, params.get("servertime"), nonce,params.get("pubkey")).toString();
				
				System.out.println("c = " + pass);
			}
			
			
			
			
			
			nvps.add(new BasicNameValuePair("sp",pass));
			nvps.add(new BasicNameValuePair("su", encodeAccount(u)));
			nvps
			.add(new BasicNameValuePair(
					"url",
					"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));
			
			nvps.add(new BasicNameValuePair("useticket", "1"));
		//	nvps.add(new BasicNameValuePair("ssosimplelogin", "1"));
			nvps.add(new BasicNameValuePair("vsnf", "1"));
			

			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = client.execute(post);

			String entity = EntityUtils.toString(response.getEntity());
			if (entity.replace("\"", "").indexOf("retcode=0") > -1) {
				String url = entity.substring(entity
						.indexOf("http://weibo.com/sso/login.php?"), entity
						.indexOf("code=0")+6 );

				// 获取到实际url进行连接
				HttpGet getMethod = new HttpGet(url);
				response = client.execute(getMethod);
				entity = EntityUtils.toString(response.getEntity());
                  
				// 获取uid
				String uid = entity.substring(entity.indexOf("uniqueid") + 11,
						entity.indexOf("userid") - 3);
				// 获取昵称
				String nick = "";
				getMethod = new HttpGet("http://weibo.com/?wvr=3.6&lf=reg");
				response = client.execute(getMethod);
				entity = EntityUtils.toString(response.getEntity());
				Document doc = Jsoup.parse(entity);
				Elements e = doc.getElementsByClass("person_infos");
				if (e != null && e.size() > 0) {
					nick = e.get(0).text();
					System.out.println("昵称：" + nick);
				}

				String pid = "";
				if (pic != null && pic.length > 0) {
					// 上传图片
					post = new HttpPost(
							"http://picupload.service.weibo.com/interface/pic_upload.php?cb=http%3A%2F%2Fweibo.com%2Faj%2Fstatic%2Fupimgback.html%3Fcallback%3DSTK_ijax_133939711612881&url=weibo.com%2Fu%2F"
									+ uid
									+ "&markpos=1&logo=1&nick=%40"
									+ nick
									+ "&marks=1&app=miniblog&s=rdxt");

					post.setHeader("Host", "picupload.service.weibo.com");
					post.setHeader("Referer", "http://weibo.com/u/" + uid);
					ByteArrayBody bin = new ByteArrayBody(pic, "image/jpeg",
							"verifycode.png");
					MultipartEntity reqEntity = new MultipartEntity();
					reqEntity.addPart("pic1", bin);
					post.setEntity(reqEntity);
					response = client.execute(post);

					org.apache.http.Header[] hs = response
							.getHeaders("Location");
					System.out.println(hs[0].getValue());
					String picLocation = hs[0].getValue();
					pid = picLocation.substring(picLocation.indexOf("pid") + 4,
							picLocation.indexOf("token") - 1);
					System.out.println("pid: " + pid);
				}

				/*************************************************************************************/

				post = new HttpPost("http://weibo.com/aj/mblog/add?_rnd="
						+ System.currentTimeMillis());
				post.addHeader("Referer", "http://weibo.com/u/" + uid + "");
				nvps = new ArrayList<NameValuePair>();
				nvps.add(new BasicNameValuePair("_surl", ""));
				nvps.add(new BasicNameValuePair("_t", "0"));
				nvps.add(new BasicNameValuePair("hottopicid", ""));
				nvps.add(new BasicNameValuePair("location", "home"));
				nvps.add(new BasicNameValuePair("module", "stissue"));
				nvps.add(new BasicNameValuePair("pic_id", pid));
				nvps.add(new BasicNameValuePair("rank", "0"));
				nvps.add(new BasicNameValuePair("rankid", ""));
				nvps.add(new BasicNameValuePair("text", text));
				post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
				response = client.execute(post);

				entity = EntityUtils.toString(response.getEntity());
				System.out.println(entity);
				if (entity.replace("\"", "").indexOf("code:100000") > -1) {
					System.out.println("发布成功");
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}
	}

	public static byte[] readFileImage(String filename) throws IOException {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(filename));
		int len = bufferedInputStream.available();
		byte[] bytes = new byte[len];
		int r = bufferedInputStream.read(bytes);
		if (len != r) {
			bytes = null;
			throw new IOException("读取文件不正确");
		}
		bufferedInputStream.close();
		return bytes;
	}

	private static String encodeAccount(String account) {
		String userName = "";
		try {
			userName = Base64.encodeBase64String(URLEncoder.encode(account,"UTF-8").getBytes());
		//userName = BASE64Encoder.encode(URLEncoder.encode(account,"UTF-8").getBytes());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return userName;
	}

	private static String makeNonce(int len) {
		String x = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String str = "";
		for (int i = 0; i < len; i++) {
			str += x.charAt((int) (Math.ceil(Math.random() * 1000000) % x
					.length()));
		}
		return str;
	}

	private static String getServerTime() {
		// long servertime = new Date().getTime() / 1000;
		// return String.valueOf(servertime);

		return String.valueOf(System.currentTimeMillis() / 1000);
	}

	

}
