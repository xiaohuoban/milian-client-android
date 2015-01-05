package com.milian.im.service;

import java.util.List;

import com.milian.im.entry.MessageML;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import android.util.Log;
public class MessageMLQueue {
	
	private String contactJid;     //key, cleanJid ( pure Jid)
	private MessageML lastMsg = null;
	private List <MessageML> msgList  =  new ArrayList<MessageML>(); //all msg
	Lock msgListLock =  new ReentrantLock();
	private List <MessageML> addedMsgList  =  new ArrayList<MessageML>(); // new msg
	Lock addedMsgListLock =  new ReentrantLock();
	
	public MessageMLQueue(String contactJid){
		this.contactJid = contactJid;
	}
    public	List<MessageML> getAllMessageML()
	{
    	List<MessageML>  list = new ArrayList<MessageML >();
    	msgListLock.lock();
    	list.addAll(msgList);
    	msgListLock.unlock();
		return list;
	}
    
    public	int getAllMessageMLSize()
    { 
    	int size = 0;
    	 
    	msgListLock.lock();
    	size = msgList.size();
    	msgListLock.unlock(); 		
    	return size;
    }
    public	MessageML getLastMessageML()
	{
		return lastMsg;
	}
public	List<MessageML> getAddedMessageML()
	{
		List<MessageML>  list = new ArrayList<MessageML >();
		addedMsgListLock.lock();
		list.addAll(addedMsgList);
		addedMsgList.clear();
		addedMsgListLock.unlock();
		return list;
	}


public	int getAddedMessageMLSize()
{
	int size = 0;
 
	addedMsgListLock.lock();
	size = addedMsgList.size();
	addedMsgListLock.unlock();
	return size;

}

public	boolean addMessage(MessageML msg)
{
    Log.d("milian", "add Message  for Contact: " + this.contactJid);
	msgListLock.lock();
	msgList.add(msg);
	msgListLock.unlock();
	if ( msg.getMsgDir() == MessageML.MSG_DIR_RECV) {
		addedMsgListLock.lock();
		addedMsgList.add(msg);
		addedMsgListLock.unlock();
	}
	lastMsg = msg;
	return true;
	
}




public	boolean loadMessageFromDb()
{
	 	return true;		
}

public	boolean saveMessageToDb()
{
	 	return true;		
}

public void release () {
	msgListLock.lock();
	msgList.clear();;
	msgListLock.unlock();
	
	addedMsgListLock.lock();
	addedMsgList.clear();
	addedMsgListLock.unlock();
	
}


}
