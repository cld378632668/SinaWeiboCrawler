package cn.edu.xmu.cld.WeiboCrawler2.useVersion2014;

import java.io.File;
import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.Tsinghua.lxs.WeiboCrawler2.FileOperation;
import cn.edu.Tsinghua.lxs.WeiboCrawler2.HTMLParser;

public class ReParser {
	public String utf8Parser(String utf8){
		String utf8CodeStr = utf8.substring(2);
    	char cUTF8 = (char) Integer.parseInt(utf8CodeStr,16);
    	String cString = String.valueOf(cUTF8);
    	
    	return cString;
	}
	
	public String getRealHTML(String htmlPath) throws IOException{
		String html = FileOperation.html2String(htmlPath);
		Document doc = Jsoup.parse(html);
		Elements eles = doc.select("script");
		String realHTML = "null";
		for(Element ele : eles){
			String script = ele.toString();
			
			if(script.contains("\"pl_weibo_direct\"")){
				
				script = script.substring(script.indexOf("{"), script.indexOf("}")+1);//<\/div>\n  "})</script>
				try{
					JSONObject json = JSON.parseObject(script);
					realHTML = json.getString("html");
				}
				catch(JSONException e){
					System.out.println("****JSON error****");
				}
				
				//System.out.println(realHTML);
			}
			else{
				//System.out.println("非目标script节点");
			}
		}
		
		return realHTML;
	}
//	public static void main(String[] args) throws IOException {
//		String[] days = {"20140307","20140308","20140309","20140310","20140311","20140312","20140313"};
//		 String[] searchwords = {"汽车摇号","自贸区","雾霾%20PM2.5", "医疗改革"
//		 ,"土地改革","房价调控","农村集体产权%20改革","东莞扫黄","油价","城镇化","国产航母"
//		 ,"中石油腐败","养老制度","嫣然基金","养老保险","拆迁","医患关系","计划生育%20超生"
//		 ,"反腐%20纪委","城管","土地流转权","发改委","证监会","余额宝","理财产品","环保"
//		 ,"教育部","高考","自主招生","股市%20A股","单独二胎%20单独二孩"
//		 ,"乌克兰局势","钓鱼岛争端","越南%20南海争端","菲律宾%20黄岩岛","叙利亚%20化学武器"
//		 ,"伊朗%20核问题%20危机","中期选举","埃及局势","泰国局势","朝韩冲突","南海识别区"
//		 };
//			int daysLength = days.length;
//			int searchwordsLength = searchwords.length;
//			for(int i = 0; i < daysLength; i++){//daysLength
//				for(int j = 0; j < searchwordsLength; j++){//searchwordsLength
//					for(int k = 1; k < 51; k++){
//						String day = days[i];
//						String searchword = searchwords[j];
//						String htmlPath = "d:/data/weibo/getweibo/"+day+"/"+searchword+"/"+searchword+k+".html";
//						File f = new File(htmlPath);
//						if(f.exists()){
//							System.out.println(day+searchword+k);
//							ReParser rp = new ReParser();
//							String html = rp.getRealHTML(htmlPath);
//							String path = "d:/data/weibo/getweibof/"+day+"/"+searchword;
//							File ff = new File(path);
//							
//							ff.mkdirs();
//							String savePath = "d:/data/weibo/getweibof/"+day+"/"+searchword+"/"+searchword+k+".html";
//							FileOperation.writeString(html, savePath);
//						}
//						
//					}
//				}
//	        }
//	}

}
