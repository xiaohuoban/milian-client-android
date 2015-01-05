
package com.milian.im.entry;

import org.jivesoftware.smack.packet.Message;

import android.util.Log;

import    java.text.SimpleDateFormat; 
import java.util.Comparator;
import     java.util.Date; 

public class MessageML{
    private static final String TAG = MessageML.class.getSimpleName();
    public static int MSG_TYPE_TEXT = 0;
    public static int MSG_TYPE_IMG = 1;
    public static int MSG_TYPE_VOICE = 2;
    public static int MSG_TYPE_VIDEO = 3;
    public static boolean MSG_DIR_RECV = true;
    public static boolean MSG_DIR_SEND = false;
    private boolean isRecvMsg = MSG_DIR_RECV;
    private int type = MSG_TYPE_TEXT;
    private boolean isReaded;  // 
    private String contactJid;
    private String name;
    //private String date;

    private String text;
    
    private String timeStr;
    private long time = System.currentTimeMillis();;
    
    private String subject;
    
    private String packetId;
    private String thread;
    private String from, to;
    

    public MessageML() {
    	this.type = MSG_TYPE_TEXT;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
    	 time = System.currentTimeMillis();;
    	Date    tdate    =   new    Date(time);
    	timeStr    =    dateFormat.format(tdate); 
    	Log.d("milian", "new Msg: " + timeStr);
    }

    public MessageML(String contactJid,  String text, boolean isRecvMsg) {
        super();
        this.type = MSG_TYPE_TEXT;
        this.contactJid = contactJid;
        //this.timeStr = date;
        this.text = text;
        this.isRecvMsg = isRecvMsg;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
    	Date    tdate    =   new    Date(time);
    	timeStr    =    dateFormat.format(tdate); 
    }
    
    public MessageML( String contactJid, Message message) {
    	this.type = MSG_TYPE_TEXT;
    	this.text = message.getBody();
    	this.contactJid = contactJid;
    	this.subject = message.getSubject();
    	this.packetId = message.getPacketID();
    	this.thread = message.getThread();
    	this.to = message.getTo();
    	this.from = message.getFrom();
    	this.time = System.currentTimeMillis();
    	this.isRecvMsg = MSG_DIR_RECV;
    	
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
    	Date    tdate    =   new    Date(time);
    	timeStr    =    dateFormat.format(tdate); 

    }
    
    public String getName() {
    	return name;
    }
    public String setName(String name) {
    	this. name = name;
    	return name;
    }
    public void setReaded(boolean isReaded) {
    	this. isReaded = isReaded;
    }
    
    public boolean isReaded(boolean isReaded) {
    	return this. isReaded; 
    }
    public void setType(int type) {
    	this.type = type;
    }
    
    public int getType() {
    	return this.type;
    }
    
    public long getTime() {
		return time;
	}
    
    
    public String getTimeStr() {
		return this.timeStr;
	} 

	public void setTime(long time) {
		this.time = time;
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
    	Date    tdate    =   new    Date(time);
    	String    timeStr    =    dateFormat.format(tdate); 
	}
	
	/*
	public void setTimeStr(String time) {
		this.timeStr = time;
	} */

	

    public String getContactJid() {
        return contactJid;
    }

    public void setContactJid(String jid) {
        this.contactJid =  jid;
    }

    
    public String getDate() {
        return getTimeStr();
    }

    public void setDate(String date) {
        this.timeStr = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getMsgDir() {
        return isRecvMsg;
    }

    public void setMsgDir(boolean isRecvMsg) {
    	this.isRecvMsg = isRecvMsg;
    }
  
    public static  Comparator timeCompare = new Comparator() {
		public int compare(Object obj1,Object obj2){ 
			  MessageML msg1=(MessageML)obj1;
			  MessageML msg2=(MessageML)obj2;
			  if(msg1.getTime() >  msg2.getTime())
			   return 1;
			  else
			   return 0;
			} ;
		}  ;

    public static  Comparator sortByTimeComparator(){
    	 return timeCompare;
    }
}
