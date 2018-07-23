# SinaWeiboCrawler
新浪微博主题爬虫

- 项目说明
- - 爬取新浪微博用户数据，为用户画像、情感分析和关系建模等提供结构化数据。
项目依赖的第三方库
HTTPClient
Jsoup ：解析HTML
fastjson
程序核心逻辑：
在 useVersion2014/WeiboCrawler3.main() 中，WeiboCrawler3的实例对象crawler调用crawl()爬取原始数据后存在文件里，剩余代码再解析磁盘上的文件进行抽取和转换得到最后的数据。
crawl()是执行爬取动作的具体函数
# String html = crawl.getHTML(url) //根据url获取网址
crawler.isVerification(html) //判断是否需要输入验证码
如果连接超时重新连接

新浪微博模拟登录逻辑 Sina.main()
Sina.login(username,passwprd)
 preLogin(encodeAccount(username),client);//新浪微博预登录，获取密码加密公钥 
加密密码
登录
获得结果
SinaSSOEncoder作为且仅作为Sina的依赖类

其他类文件说明
WeiboUser对新浪微博用户的基本信息进行面向对象的建模
Reparser 对本地HTML文件的二次解析，依赖HTMLParser,具有单独的main函数，不被其他类使用
JWindowsFrame窗体程序，提供安全的多线程爬取能力
ProxyIP
主函数excute()
String ipLibURL = "http://www.xici.net.co/";//ip库
allIPs = getAllUnverifiedProxyIPs(ipLibURL, JTARunInfo);
validIPs = getValidProxyIPs(allIPs);
plainIPs = new ProxyIP() .classifyIPs(validIPs, plainIPsPath);

python爬虫与Java爬虫
python的http库类更佳丰富,用java需要几十行代码才能完成的事情,python往往只需要十几行。
打开并且存储一个网页的安全和不安全写法（java\python），另见我的轮子库。
https://blog.csdn.net/qq_35488769/article/details/72682016
模拟登陆新浪微博HTTP抓包及分析
https://www.cnblogs.com/xyqhello/p/3622658.html
https://blog.csdn.net/erliang20088/article/details/45790185