package com.milian.im.service;
 
import android.app.AlarmManager;

import org.jivesoftware.smack.SmackException;
import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.milian.im.entry.*;

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
import com.milian.im.service.ContactMgr;
import com.milian.im.service.ConnectionMgr;
import com.milian.im.service.SessionMgr;
import com.milian.im.xmpp.MilianInfo;

import java.io.IOException;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.xdata.Form;

import java.util.Map;
import java.util.HashMap; 
import java.util.Set; 
import java.util.HashSet; 
import java.util.Iterator; 
 


public class XmppMgrService  extends Service {
	Map <String , AccountMLMgr > accountMap;  //first is Jid
	
	static {
        java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
    }

	public class XmppMgrBinder extends Binder {
		public XmppMgrService getService() {
			return XmppMgrService.this;
		}
	}

	
	private static  XmppMgrBinder mBinder;
	@Override
	public IBinder onBind(Intent intent) {
		Log.e("milian", "service onBind" );
		return mBinder;
	}
	 @Override 
     public int onStartCommand(Intent intent, int flags, int startId) { 
             Log.i("LocalService", "Received start id " + startId + ": " + intent); 
             // We want this service to continue running until it is explicitly 
             // stopped, so return sticky. 
             
             //connectServer();
             return START_STICKY; 
     } 
	@Override
	public void onCreate() {
		
		accountMap = new HashMap <String, AccountMLMgr> ();
		mBinder = new XmppMgrBinder();
		NotifyBroadcast.setContext(getBaseContext());
		
	}
 
	
	@Override
	public void onDestroy() 
	{
		
	}
	
	public boolean addAccountML(AccountML account)  //建立连接
	{
		AccountMLMgr accountSearch;
		AccountMLMgr accountMgr ;
		
		accountSearch = accountMap.get(account.getJid());
		if (accountSearch != null)
			return true;
		Log.d(this.getClass().getName(), "add new Account: ");
		account.show();
		accountMgr = new AccountMLMgr(account);
		
		if (accountMgr == null) {
			Log.e(this.getClass().getName(),  "alloc AccountMLMgr fail " );
			return false;
		}
	   accountMap.put(account.getJid(), accountMgr);
		return true;
	}
	
	public AccountML getAccountML(String id)  //建立连接
	{
	
		AccountMLMgr accountMgr ;
		
		accountMgr = accountMap.get(id);
		if (accountMgr != null)
			return accountMgr.getAccount();
		else 
			return null;
		
		 
	}
/*the flowing is the interface for UI */
public boolean removeAccountML(AccountML account)
{
	AccountMLMgr accountMgr;
	accountMgr = accountMap.get(account.getJid());
	if (accountMgr == null) {
		Log.e(this.getClass().getName(), "removeAccount:"+account.getUsername()+": but not find this account");
		return false;
	}

	accountMap.remove(account.getJid());
	accountMgr.release();
	return true;
}

public boolean updateAccountML(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return false;
	}
	return accountMgr.updateAccountML(account);
}

public   List<AccountML > getAllAccountML( )
{
	List <AccountML> list = new ArrayList <AccountML> ();
	AccountML value;
	
	for (String key : accountMap.keySet()) {

	    value = accountMap.get(key).getAccount();  //AccountMgr should supply getAccount interface
	    list.add(value);

	}
	return list;
}

public List<ContactML> getAllContactML(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getAllContactML();
}

public List<ContactML> getAllRecentContactML(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getAllRecentContactML();
}


public List<ContactML> getAddedContactML(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getAddedContactML();	
}

public List<ContactML> getAddedRecentContactML(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getAddedRecentContactML();	
}

public List<ContactML> getUpdatedContactML(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getUpdatedContactML();		
}

public List<ContactML> getRemovedContactML(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getRemovedContactML();			
}
public ContactML getContact(AccountML account, String contact)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getContact(contact);		
}

public ContactML getRecentContact(AccountML account, String contact)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getRecentContact(contact);		
}




public boolean requestRandContact(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return false;
	}
	return accountMgr.requestRandContact();		
}

public boolean requestAddContact(AccountML account, ContactML contact)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return false;
	}
	return accountMgr.requestAddContact(contact);		
}

public boolean requestRemoveContact(AccountML account, ContactML contact)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return false;
	}
	return accountMgr.requestRemoveContact(contact);			
}

public List<MessageML> getAllMessageML (AccountML account, String contact)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getAllMessageML(contact);			
}

public  int  getAllMessageMLSize (AccountML account, String contact)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return 0;
	}
	return accountMgr.getAllMessageMLSize(contact);			
}

public  MessageML getLastMessageML(AccountML account,String contactJid)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getLastMessageML(contactJid);				
}

public List<MessageML> getAddedMessageML(AccountML account,String contactJid)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getAddedMessageML(contactJid);				
}

public int getAddedMessageMLSize(AccountML account,String contactJid)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return 0;
	}
	
	return accountMgr.getAddedMessageMLSize(contactJid);				
}

public boolean sendMessageML(AccountML account,String contact, MessageML msg)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return false;
	}
	
	return accountMgr.sendMessageML(contact, msg);		
}

public  AccountML getDefaultAccount(){
	 
	AccountML account; 
	TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	String username = tm.getDeviceId();
	String password = "12345678";
	Log.d("milian", "Default Account, username: " + username +", password: "  + password);
	account = new AccountML(username, password ,"fangxinchihe.cn",  "mobile",  5222);
	account.setAliasName( AccountML.getRandAliasName());
	//String SimSerialNumber = tm.getSimSerialNumber();
	//String IMSI = android.os.SystemProperties.get(android.telephony.TelephonyProperties.PROPERTY_IMSI);
	//String IMSI = tm.getSubscriberId();

	//NotifyBroadcast.notify(account.getJid(),  NotifyBroadcast.ACCOUNT_ADD_MSG);
	return account;

}

public	Form getPersonalInfo(AccountML account)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return null;
	}
	return accountMgr.getPersonalInfo( account.getJid());		
}

public	boolean setPersonalInfo(AccountML account, String name)
{
	AccountMLMgr  accountMgr = accountMap.get(account.getJid());
	if( accountMgr == null) {
		return false;
	}
		return accountMgr.setPersonalInfo(account.getJid(), name);	
}

 
 

}
