package com.milian.im.entry;
import java.util.Random;

import org.jivesoftware.smack.XMPPConnection;

import android.util.Log;

public class AccountML {

	String  username;
	String  aliasName;
	String password;
	String domain;
	String jid;
	String resource;
	String state = STATE_OFF_LINE;;
	int port;
	boolean isCreated = false;
	public final static String STATE_ON_LINE  = "On Line";
	public final static String STATE_BUSY  = "Busy";
	public final static String STATE_CONNECTING = "Connecting";
	public final static String STATE_CONNECT_FAILED = "Connect Failed";
	public final static String STATE_LOGIN_FAIL = "Login Failed";
	public final static String STATE_LOGIN_PASSWORD_ERROR = "Password Error";
	public final static String STATE_LOGIN_RESOURCE_CONFLICT= "Resource Conflict";
	public final static String STATE_LOGIN_USER_NOT_EXIST= "User Not Exist";
	public final static String STATE_LEAVE  = "Leave";
	public final static String STATE_WANT_CHAT  = "Chatting";
    public final static String STATE_OFF_LINE  = "Off Line";
    public  final static String  RandAliasNameTbl []=  new String[]  {
    	"贾宝玉", "王羲之", "秦始皇", "阿三", "马云", "强哥", "雷总", "但丁",
    	"林黛玉", "诸葛亮", "王羲之", "郭靖", "董姐", "紫薇", "小燕子", "尔康",
    	"薛宝钗", "曹操", "林语堂", "黄蓉", "董小姐", "还珠格格", "阿哥", "教授",
    	"帅哥", "周瑜", "蒋介石", "李莫愁", "五毛", "段正淳", "贾君鹏", "都教授",
    	"美女", "李白", "乔峰", "阿朱", "江河", "段誉", "王羲之", "金秀贤",
    	"宋江", "杜甫", "杨过", "啊呀", "韩寒", "王语嫣", "涛涛", "全智贤",
    	"刘备", "白居易", "小龙女", "张无忌", "周芷若", "大叔", "鸟叔", "车太贤",
    
    
    };

	public AccountML(String userJid)
	{
		this.username = userJid.split("@")[0];
		this.domain = userJid.split("@")[1];
		//this.resource = userJid.split("@")[1].split("/")[1];
		this.aliasName = username;
		this.state = STATE_OFF_LINE;
	}
	public AccountML(String username, String password,String domain, String resource, int port) 
	{
		this.username = username;
		this.domain = domain;
		this.resource = resource;
		this.password = password;
		this.port = port;
		this.state = STATE_OFF_LINE;
		this.aliasName = username;
		if( resource != null && ( ! resource.equals(resource))) {
			this.jid = username +  "@" + domain+"/" + resource;
		}
	
	}
	
	public void AccountML(String username, String domain, String resource, int port)
	{
		this.username = username;
		this.domain = domain;
		this.resource = resource;
		this.port = port;
		//this.password = password;
		this.state = STATE_OFF_LINE;
		if( resource != null && ( ! resource.equals("") ) )  {
			this.jid = username +  "@" + domain+"/" + resource;
		}
	
	}
	public void setPassword(String password) 
	{
		this.password = password;
	
	}
 	
 
	

	 
	 
	public void setState(String state)
	{
		this.state = state;
		
	}
	
	public String getDomain()
	{
		return this.domain;
		
	}
	
	public String getAliasName()
	{
		return this.aliasName;
		
	}
	
	public void setAliasName(String aliasName)
	{
		 this.aliasName = aliasName;
		
	}
	
	
	public String getUsername()
	{
		return this.username;
		
	}
	public String getPassword()
	{
		return this.password;
		
	}
	
	public String getState()
	{
		return this.state ;
		
	}
	
	public String getJid(){
		
		return this.username + "@" + this.domain;
	}
	
	public int getPort(){
		return this.port;
		
	}
	
	public void show(){
		Log.d(this.getClass().getName(), " Account [ username: "+this.username + ", domain: " + this.domain + ", password: " + this.password + " ]");
		
	}
	
	public void setAccountCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}
	public boolean isCreated() {
		return this.isCreated  ;
	}
	
	public static  String getRandAliasName() {
		Random rand = new Random( );
		return RandAliasNameTbl[ rand.nextInt( RandAliasNameTbl.length   )];
	}
	
	
}
