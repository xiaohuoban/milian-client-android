package com.milian.im.service;


import android.content.Context;
import android.content.Intent;

public class NotifyBroadcast {
	//msg ID,  ADDED HERE
	
	public final static String  NOTIFY_TYPE_MESSAGE = "xmpp.notify.type.message";

	//xxx_SUCCESS|FAIL_REASON_MSG
	
	public final static int  UNKNOWN_MSG =  0;
	
	//public final static String  CONNECT_SUCCESS_MSG = "xmpp.connect.success";	
	public final static int  CONNECT_SUCCESS_MSG = 1000;
	
	//public final static String  CONNECT_FAIL_MSG = "xmpp.connect.fail";
	public final static int  CONNECT_FAIL_MSG = 1001;
	
	public final static int  ACCOUNT_REMOVE_MSG = 1002;
	public final static int  ACCOUNT_ADD_MSG = 1003;
	
	//public final static String  LOGIN_SUCCESS_MSG = "xmpp.login.success";
	public final static int  LOGIN_SUCCESS_MSG = 1100;
	
	//public final static String  LOGIN_FAIL_MSG = "xmpp.login.fail";
	public final static int  LOGIN_FAIL_MSG = 1101;
	
	//public final static String  LOGIN_FAIL_PASSWORD_ERROR_MSG = "xmpp.login.password.error";
	public final static int  LOGIN_FAIL_PASSWORD_ERROR_MSG = 1102;
	
	//public final static String  LOGIN_FAIL_USER_NOT_EXIST_MSG = "xmpp.login.user.not.exist";
	public final static int  LOGIN_FAIL_USER_NOT_EXIST_MSG = 1103;
	
	//public final static String  LOGIN_FAIL_XMPP_ERROR_MSG = "xmpp.login.xmpp.error";
	public final static int  LOGIN_FAIL_XMPP_ERROR_MSG = 1104;
	
	//public final static String  LOGIN_FAIL_RESOURCE_CONFLICT_MSG = "xmpp.login.resource.conflict";
	public final static int  LOGIN_FAIL_RESOURCE_CONFLICT_MSG = 1105;
	
	public final static int CONTACT_UPDATE__MSG = 1200;
	public final static int CONTACT_ADD__MSG = 1201;
	public final static int CONTACT_REMOVE__MSG = 1202;
	
	public final static int CONTACT_RECENT_UPDATE__MSG = 1203;
	public final static int CONTACT_RECENT_ADD__MSG = 1204;
	public final static int CONTACT_RECENT_REMOVE__MSG = 1205;
	public final static int CONTACT_RECENT_INFO_UPDATE__MSG = 1206;
	
	
	

	
	public final static int GROUP_JOIN_SUCCESS__MSG = 1300;
	public final static int GROUP_JOIN_FAIL__MSG = 1301;
	public final static int GROUP_LEAVE_SUCCESS__MSG = 1302;
	public final static int GROUP_LEAVE_FAIL__MSG = 1303;
	public final static int GROUP_CREATE_SUCCESS__MSG = 1304;
	public final static int GROUP_CREATE_FAIL__MSG = 1305;
	
	
	
	public final static int MESSAGE_NEW_RECEIVE__MSG = 1400;
	public final static int MESSAGE_NEW_SEND__MSG = 1401;
	public final static int MESSAGE_READED__MSG = 1402;
	//
	
	
	
	
	//--------------------------------------------------------------------------------------------
	public final static String  ACCOUNT_JID = "account.jid";
	public final static String  CONTACT_JID = "contact.jid";
	public final static String  MSG_ID = "msg.jid";
	
	static Context context;
	
	/*
	public static  void notify(String accountJid, String msg){
		Intent intent = new Intent(msg) ;
		intent.putExtra(ACCOUNT_JID, accountJid);
		if(context != null) {
			context.sendBroadcast(intent);
		}
	} */
	
	// ACCOUNT LEVEL msg
	public static  void notify(String accountJid, int msgID) {
		Intent intent = new Intent(NOTIFY_TYPE_MESSAGE) ;
		intent.putExtra(MSG_ID, msgID);
		intent.putExtra(ACCOUNT_JID, accountJid);
		if(context != null) {
			context.sendBroadcast(intent);
		}
	}  
	
	//contact level msg
	public static  void notify(String accountJid, String contactJid, int msgID) {
		Intent intent = new Intent(NOTIFY_TYPE_MESSAGE) ;
		intent.putExtra(MSG_ID, msgID);
		intent.putExtra(ACCOUNT_JID, accountJid);
		intent.putExtra(CONTACT_JID, contactJid);
		
		if(context != null) {
			context.sendBroadcast(intent);
		}
	}
	public static void setContext(Context arg) {
		context = arg;

	}
	
}
