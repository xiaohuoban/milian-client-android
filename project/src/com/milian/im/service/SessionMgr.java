package com.milian.im.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException.NotConnectedException;

import com.milian.im.entry.ContactML;
import com.milian.im.entry.MessageML;

public class SessionMgr
{
	AccountMLMgr accountMgr; //up layer pointer, passed by construction function
    private XMPPConnection connection = null;
    private ChatManager chatManager = null; 
    
    //key is contactJid, value is SessionMessageMLQueue, key must be cleanJid ( pure Jid)
    private ConcurrentHashMap<String, MessageMLQueue> msgMap = new ConcurrentHashMap<String, MessageMLQueue>();
    
    //key is contactJid, value is SessionMessageMLQueue, this key cotain resource, full JId
    private ConcurrentHashMap<String, Chat> chatMap = new ConcurrentHashMap<String, Chat>();
    

    

    private ChatManagerListener  chatManagerListener = new ChatManagerListener() {

        /**
         * Event fired when a new chat is created.
         *
         * @param chat the chat that was created.
         * @param createdLocally true if the chat was created by the local user and false if it wasn't.
         */
        public void chatCreated(Chat chat, boolean createdLocally){
        	if ( createdLocally == true)
        		return ;
        	if ( chatMap.get(chat.getParticipant()) != null ) {
        		Log.e(this.getClass().getName(), "Chat is duplicate ");
        		chatMap.remove(chat.getParticipant());
        	
        	} else {
        		Log.e("milian",this.getClass().getName() + "a chat is come: " + chat.getParticipant());
        		if (accountMgr.getContact(chat.getParticipant())  == null) {
        			Log.d("milian", this.getClass().getName() +  "new chat is come: " + chat.getParticipant() + "  UNFAMILIAR" );
        			accountMgr.addContact(ContactML.getCleanJid(chat.getParticipant()), ContactML.CONTACT_TYPE_UNFAMILIAR); //add to recent contact
        			//NotifyBroadcast.notify(accountMgr.getAccount().getJid(), NotifyBroadcast.CONTACT_RECENT_ADD__MSG);
         		} else {
         			Log.e("milian" , this.getClass().getName() +  "new chat is come: " + chat.getParticipant()+ " FAMILIAR, add it add Recent Contac" );
         			accountMgr.addRecentContactFromFixedContact(chat.getParticipant() );
           		}
        		NotifyBroadcast.notify(accountMgr.getAccount().getJid(), NotifyBroadcast.CONTACT_RECENT_ADD__MSG);
        		
        	}
        	
	        String cleanJid = ContactML.getCleanJid(chat.getParticipant());
	       	  
        	chatMap.put(chat.getParticipant(), chat);
        	chat.addMessageListener(msgListener);
        	
        }
        
    };
    
    MessageListener  msgListener = new MessageListener() {
        public void processMessage(Chat chat, Message message){
        	String cleanJid = ContactML.getCleanJid(chat.getParticipant());
        	 
        	if ( accountMgr.getContact(cleanJid )   == null) {
        		if (accountMgr.getRecentContact(cleanJid) == null) {
        			accountMgr.addContact(cleanJid,  ContactML.CONTACT_TYPE_UNFAMILIAR); //unfamiliar will be add to recent Contact
        			 NotifyBroadcast.notify(accountMgr.getAccount().getJid(),   NotifyBroadcast.CONTACT_RECENT_ADD__MSG);
        		} 
        	}
        	
        	 if (chatMap.get(chat.getParticipant() ) == null)  {
        		 chatMap.put(chat.getParticipant(), chat);
        	 }
        	 MessageMLQueue msgQueue = msgMap.get(cleanJid);
        	 if (msgQueue == null) {
        		  msgQueue = new MessageMLQueue (cleanJid);
        		 msgMap.put(cleanJid , msgQueue);
        	 }
        	 MessageML msg = new MessageML(cleanJid, message);
        	 msgQueue.addMessage(msg);
        	 
        	 ContactML recentContact;
        	 recentContact = accountMgr.getRecentContact(cleanJid);
        	 if (recentContact != null)
        		 	recentContact.setLastMsgTime(System.currentTimeMillis());
        	 
        	 Log.d("milian", "receive a text message from " + cleanJid); 
        	 Log.d("milian", "msg body: " + msg.getText()); 
        	 
        	 NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  cleanJid  , NotifyBroadcast.MESSAGE_NEW_RECEIVE__MSG);
        	 
        };
    };
    
    public boolean setConnection(XMPPConnection conn ) {
    	this.connection = conn;
    	chatManager =ChatManager.getInstanceFor(conn);
    	chatManager.addChatListener(chatManagerListener);
    	return true;
    }

 
    public SessionMgr(AccountMLMgr accountMgr) {
     	this.accountMgr  = accountMgr;
     	chatManager = null;
     	connection = null;
    }
    
    
    public	List<MessageML> getAllMessageML(String contactJid)
	{
    	MessageMLQueue  msgQueue = msgMap.get(ContactML.getCleanJid(contactJid));
    	if (msgQueue == null) {
    		Log.d("milian", "msgMap.get(" + contactJid+") is null");
		 	return null;
    	}
    	return msgQueue.getAllMessageML();
    	
	}
    public	MessageML getLastMessageML(String contactJid)
    {
    	MessageMLQueue  msgQueue = msgMap.get(ContactML.getCleanJid(contactJid));
    	if (msgQueue == null) {
    		Log.d("milian", "msgMap.get(" + contactJid+") is null");
		 	return null;
    	}
    	return msgQueue.getLastMessageML();
    }

public	List<MessageML> getAddedMessageML(String contactJid)
	{
	List<MessageML> list;
	String cleanJid = ContactML.getCleanJid(contactJid);
	MessageMLQueue  msgQueue = msgMap.get(cleanJid);
	if (msgQueue == null)
	 	return null;
	list =  msgQueue.getAddedMessageML();
	Log.d("milian" , this.getClass().getName() +  "  notify UI "  + cleanJid +"'s msg is  readed " );
	 NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  cleanJid,  NotifyBroadcast.MESSAGE_READED__MSG);
	return list;
	}

public	int getAddedMessageMLSize(String contactJid)
{
	MessageMLQueue  msgQueue = msgMap.get( ContactML.getCleanJid(contactJid));
	if (msgQueue == null)
	 	return 0;
	return msgQueue.getAddedMessageMLSize();
}

public	int  getAllMessageMLSize(String contactJid)
{ 
	MessageMLQueue  msgQueue = msgMap.get( ContactML.getCleanJid(contactJid));
	if (msgQueue == null)
	 	return 0;
	return msgQueue.getAllMessageMLSize();
}

public	boolean sendMessageML( String contactJid, MessageML msg)
	{
   // to be fixed, add create Chat code 
	if ( connection == null) {
		Log.e(this.getClass().getName(), "connection is null");
		return false;
	}
	msg.setMsgDir(MessageML.MSG_DIR_SEND);
	
	Chat chat = chatMap.get(contactJid);
	if ( chat == null) {
		chat = chatManager.createChat(contactJid, msgListener);
		
	}
	
	String cleanJid = ContactML.getCleanJid(contactJid);
	if (accountMgr.getRecentContact(cleanJid) == null) {
		if (accountMgr.getContact(cleanJid) != null) {
			accountMgr.addRecentContactFromFixedContact(cleanJid);
		}  else {
			accountMgr.addContact(cleanJid,  ContactML.CONTACT_TYPE_UNFAMILIAR); //unfamiliar will be add to recent Contact
		}
		Log.d("milian" , " notify QuikChat to add recentContact: " + cleanJid);
		 NotifyBroadcast.notify(accountMgr.getAccount().getJid(),   NotifyBroadcast.CONTACT_RECENT_ADD__MSG);
		 
	} else {
		Log.d("milian" , " recent Contact already has : " + cleanJid);
	}
	
	
	MessageMLQueue  msgQueue = msgMap.get(ContactML.getCleanJid(contactJid));
	if (msgQueue == null) {
		msgQueue = new MessageMLQueue(ContactML.getCleanJid(contactJid));
		msgMap.put(ContactML.getCleanJid(contactJid ), msgQueue);
	}
	msgQueue.addMessage(msg);
	
	 ContactML recentContact;
	 recentContact = accountMgr.getRecentContact(cleanJid);
	 if (recentContact != null) {
		    Log.d("milian",cleanJid + " setLastMsgTime " );
		 	recentContact.setLastMsgTime(System.currentTimeMillis());
		 	NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  cleanJid  , NotifyBroadcast.MESSAGE_NEW_SEND__MSG);
	 }
	try {
		chat.sendMessage(msg.getText());
	} catch(XMPPException e) {
		e.printStackTrace();
		Log.e(this.getClass().getName(), " XMPPException:" + e.getMessage());
	} catch (NotConnectedException e) {
		e.printStackTrace();
		Log.e(this.getClass().getName(), " NotConnectedException:" + e.getMessage());
	}
 	return true;		
	}
    

    public void release() {
    	
    	if (connection != null) {
    		if (chatManager != null)
    			chatManager.removeChatListener(chatManagerListener);
       	}
    	
    	Iterator<String> it = msgMap.keySet().iterator();
    	MessageMLQueue msgQueue;
        while(it.hasNext()) {
           msgQueue =  msgMap.get( it.next());
           if (msgQueue != null)
        	   msgQueue.release();
        }
    	msgMap.clear();
    	chatMap.clear();
    }
    

}
