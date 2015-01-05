package com.milian.im.service;

import android.app.AlarmManager;
import com.milian.im.xmpp.MilianInfo;
import android.telephony.TelephonyManager;
import org.jivesoftware.smackx.xdata.Form;
//import org.jivesoftware.smack.test.SmackTestCase;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import org.jivesoftware.smack.SmackException;
import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.milian.im.entry.*;
import com.milian.im.service.*;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.milian.im.entry.AccountML;

import java.io.IOException;

import org.jivesoftware.smack.packet.Presence;

import java.util.Map;
import java.util.HashMap; 
import java.util.Set; 
import java.util.HashSet; 
import java.util.Iterator; 

import com.milian.im.service.ContactMgr;

 

//this class is for single user management;
public class AccountMLMgr 
{
	AccountML account;
	ContactMgr contactMgr;
	SessionMgr sessionMgr;
	ConnectionMgr conMgr;
	
	
	public AccountMLMgr(AccountML account)
	{
		this.account = account;
		this.conMgr = new ConnectionMgr(this);
		this.contactMgr = new ContactMgr(this);
		this.sessionMgr = new SessionMgr(this);
		
		conMgr.startConnect();
	}
	
public XMPPConnection  getXMPPConnection()
{
	return conMgr.getXMPPConnection();
}

public void  closeConection()
{
	 conMgr.closeConnection();
}

public void  setContactMgr()
{
	this.contactMgr = new ContactMgr(this);
}

public AccountML  getAccount()
{
	return account;
}
public boolean  isConnected() {
	
	return conMgr.isConnected();
}

public boolean isAuthed() {
	
	return conMgr.isAuthed();
}
	
public	List<ContactML> getAllContactML()
	{
		return contactMgr.getAllContactML();
	}

public		List<ContactML> getAddedContactML( )
	{
		return contactMgr.getAddedContactML();	
	}

public	List<ContactML> getAllRecentContactML()
{
	return contactMgr.getAllRecentContactML();
}

public		List<ContactML> getAddedRecentContactML( )
{
	return contactMgr.getAddedRecentContactML();	
}

public	List<ContactML> getUpdatedContactML( )
	{
		return contactMgr.getUpdatedContactML();		
	}

public	List<ContactML> getRemovedContactML()
	{
		return contactMgr.getRemovedContactML();			
	}

//default is get from fixed Contact
public	ContactML getContact(String contactJid)
	{
		return contactMgr.getContact(contactJid);		
	}

//default is get from fixed Contact
public	ContactML getRecentContact(String contactJid)
	{
		return contactMgr.getRecentContact(contactJid);		
	}

/*addContact : don't addContact to server, just local add.
   FAMILIAR TO fixed contacts, unFAMILIAR TO RecentContacts
   */
public	boolean addContact(String contactJid, int type)
{
	 contactMgr.addContact(contactJid, type);		
	 return true;
}

public	boolean addRecentContactFromFixedContact(String contactJid)
{
	 contactMgr.addRecentContactFromFixedContact(contactJid);		
	 return true;
}

public boolean setConnection(XMPPConnection conn ) {
	contactMgr.setConnection(conn);
	sessionMgr.setConnection(conn);
	return true;
}

public	boolean requestRandContact( )
	{
		return contactMgr.requestRandContact();		
	}

public	boolean requestAddContact( ContactML contact)
	{
		return contactMgr.requestAddContact(contact);		
	}

public	boolean requestRemoveContact(ContactML contact)
	{
		return contactMgr.requestRemoveContact(contact);			
	}

public	List<MessageML> getAllMessageML(String contact)
	{
		return sessionMgr.getAllMessageML(contact);			
	}
public	MessageML getLastMessageML(String contactJid)
{
	return sessionMgr.getLastMessageML(contactJid);				
}

public	List<MessageML> getAddedMessageML(String contactJid)
	{
		return sessionMgr.getAddedMessageML(contactJid);				
	}

public	int getAddedMessageMLSize(String contactJid)
{
	return sessionMgr.getAddedMessageMLSize(contactJid);				
}

public	int  getAllMessageMLSize(String contactJid)
{ 
	return sessionMgr.getAllMessageMLSize(contactJid);				
}

public	boolean sendMessageML( String contact, MessageML msg)
	{
 		return sessionMgr.sendMessageML(contact, msg);		
	}

public	Form getPersonalInfo( String jid)
{
		return MilianInfo.getInstance().getPersonalInfo(this.getXMPPConnection(), jid);		
}

public	boolean setPersonalInfo( String jid, String name)
{
		return MilianInfo.getInstance().setPersonalInfo(this.getXMPPConnection(), jid, name);	
}


public void release(){
	 	
	 contactMgr.release();
	 sessionMgr.release();
	 conMgr.release();
}

public boolean updateAccountML(AccountML account) {
	
	return true;
}

 
	



	
}
