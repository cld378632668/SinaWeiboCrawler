package cn.edu.Tsinghua.lxs.WeiboCrawler2.useVersion2014;



import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
public class WeiBoUser {

	private String userName;
	private String userPass;
	private String displayName;
    public CookieStore cookiestore;
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPass() {
		return userPass;
	}
	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}
	public void getCookieStore(CookieStore cookiestore)
	{
		this.cookiestore=cookiestore;
	}
	
	
}
