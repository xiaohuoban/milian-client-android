package com.milian.im.service;

import android.app.AlarmManager;

import org.jivesoftware.smack.SmackException;
import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.sasl.SASLMechanism.SASLFailure;
import org.jivesoftware.smack.sasl.SASLError;

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
import android.util.Log;
import org.jivesoftware.smack.packet.XMPPError.Condition;

import org.jivesoftware.smack.ConnectionCreationListener;

import java.util.ArrayList;
import java.util.List;

import com.milian.im.entry.AccountML;

import java.io.IOException;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;

import java.util.Map;
import java.util.HashMap; 
import java.util.Set; 
import java.util.HashSet; 
import java.util.Iterator; 


public class ConnectionMgr {
	AccountMLMgr accountMgr; //up layer pointer
	XMPPConnection conn;
	ConnectionListener connListener= new ConnectionListener() {
	    public void connected(XMPPConnection connection) {
	    	NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.CONNECT_SUCCESS_MSG);
	    	int i = 0;
	    	
	    	try {
	    		while (accountMgr.getAccount().isCreated() == false ) {
	    			if ( ++i  > 3) {
	    				Log.e("milian", "createAccount >= 3 times, so just login");
	    				break;
	    			}
	    			Log.e("milian", "try to createAccount, " + i + " time");
	    			createAccount();
	    			
	    		}
	    		if (accountMgr.getAccount().isCreated() ) {
	    			Log.e("milian", "Account was created success !,  start login");
	    		} else {
	    			Log.e("milian", "Account is created fail ! try to login");
	    		}
	    			 
	    		
	    		Log.d("milian", "Login,  username:" + accountMgr.getAccount().getUsername() +", password: "+ accountMgr.getAccount().getPassword() );
	    		conn.login(accountMgr.getAccount().getUsername(), accountMgr.getAccount().getPassword());
	    		 
	    	}  catch ( SASLErrorException e) {
	    		
	    		Log.e(this.getClass().getName(),  e.getSASLFailure().getError().toString());

	    		switch (e.getSASLFailure().getSASLError()) {

	    		case account_disabled:
	    			Log.e(this.getClass().getName(), "account is not exist");
	    			NotifyBroadcast.notify(accountMgr.getAccount().getJid(), NotifyBroadcast.LOGIN_FAIL_USER_NOT_EXIST_MSG);
	    			break;
	    		 
	    		
	    		case    invalid_authzid:
	    			NotifyBroadcast.notify(accountMgr.getAccount().getJid(), NotifyBroadcast.LOGIN_FAIL_PASSWORD_ERROR_MSG);
	    			break;
	    		case    invalid_mechanism:
	    			
	    		case    malformed_request:
	    			
	    		case    mechanism_too_weak:
	    			
	    		case    not_authorized:
	    			
	    		case    temporary_auth_failure:
	    			
	    		case     encryption_required:
	    			
	    		case    incorrect_encoding:
	    			
	    		case  aborted:
	    		case  credentials_expired:
	    	    default:
	    	    	Log.e(this.getClass().getName(), "login fail");
	    	    	NotifyBroadcast.notify(accountMgr.getAccount().getJid(), NotifyBroadcast.LOGIN_FAIL_MSG);
	    	    	break;
	    		}
	    	}
	    	catch (XMPPException e) {
	    		Log.e(this.getClass().getName() , e.getMessage() );
	    		e.printStackTrace();
	    		
	    		NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.LOGIN_FAIL_MSG);
	    	} catch (SmackException e) {
	    		Log.e(this.getClass().getName() , e.getMessage() );
	    		e.printStackTrace();
	    		NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.LOGIN_FAIL_MSG);
	    	} catch (SaslException e) {
	    		//Log.e(this.getClass().getName() , e.getMessage(), e.getCause().);
	    		e.printStackTrace();
	    		NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.LOGIN_FAIL_MSG);
	    	} catch (IOException e) {
	    		Log.e(this.getClass().getName() , e.getMessage() );
	    		e.printStackTrace();
	    		NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.LOGIN_FAIL_MSG);
	    	} 
	    	
	    }

	  
	    public void authenticated(XMPPConnection connection) {
	    	Log.e("milian" ,"Account is authenticated !");
	    	accountMgr.setConnection(connection);
	    	NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.LOGIN_SUCCESS_MSG);
	    }

	     
	    public void connectionClosed() {
	    	Log.e(this.getClass().getName() , " connectionClosed !");
	    	NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.CONNECT_FAIL_MSG);
	    }

	     
	    public void connectionClosedOnError(Exception e) {
	    	Log.e(this.getClass().getName() , " connectionClosedOnError : " + e.getMessage());
	    	NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.CONNECT_FAIL_MSG);
	    }
	    
	    
	    public void reconnectingIn(int seconds) {
	    	Log.v (this.getClass().getName() , "reconnect in " + seconds + " seconds later");
	    }
	    
	     
	    public void reconnectionSuccessful() {
	    	Log.e(this.getClass().getName() , " reconnectionSuccessful !");
	    	NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.CONNECT_SUCCESS_MSG);
	    }
	    
	 
	    public void reconnectionFailed(Exception e) {
	    	Log.e(this.getClass().getName() , " reconnectionFailed : " + e.getMessage());
	    	NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.CONNECT_FAIL_MSG);
   	
	    }
		
		
	};
	public  ConnectionMgr(AccountMLMgr accountMgr)
	{
		this.accountMgr = accountMgr;
		conn = null;
		
		Log.d("milian", "Connect,  domain:" + accountMgr.getAccount().getDomain() +", port: "+ accountMgr.getAccount().getPort() );

		ConnectionConfiguration config =  new ConnectionConfiguration(accountMgr.getAccount().getDomain(),  
				accountMgr.getAccount().getPort());
		config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		config.setReconnectionAllowed(true);
		conn = new XMPPTCPConnection(config);
		if ( conn == null)  {
			Log.e (this.getClass().getName(), " new XMPPTAccountMLMgrCPConnection(config)  failed ");		
			return ;
		}
		conn.addConnectionListener(  connListener  );
	}
	
	public XMPPConnection  getXMPPConnection()
	{
		return conn;
	}
	
	public void  closeConnection()
	{
		if (conn != null) {
			try {
				if (conn.isConnected()) {
					
					conn.removeConnectionListener(connListener);
					conn.disconnect();
					
					 
					Log.d ("milian",  "conn disconnect");
				}
			} catch (NotConnectedException e ) {
				Log.e (this.getClass().getName(), "to closeConnection, but it is disconnected  " );
				 e.printStackTrace();
			}
		}
	}
	
	public void startConnect()
	{
		
		
		Thread connectThread= new Thread( new Runnable() {
				public void run () {
						try {
							if ( conn == null) {
								Log.e(this.getClass().getName() , " connection is null !  just return, don't connect Server " );
								return ;
							}
							Log.e(this.getClass().getName() , " start to connect Server " );
							conn.connect();
						} catch (SmackException e){
							Log.e(this.getClass().getName() , " conn.connect error, SmackException : " + e.getMessage());
							e.printStackTrace();						
						} catch (IOException e) {
							Log.e(this.getClass().getName() , " conn.connect error, IOException : " + e.getMessage());
							e.printStackTrace();
						} catch (Exception e) {
							Log.e(this.getClass().getName() , " conn.connect error, Exception : " + e.getMessage());
							e.printStackTrace();
						}
				}
			
		} );
		connectThread.start();
		
	}
	
	public boolean isConnected() {
		if (conn == null)
			return false;
		return conn.isConnected();
		
	}
	
	public boolean isAuthed() {
		if (conn == null)
			return false;
		return conn.isAuthenticated();
	}
	
   public void release () {
	   
	   closeConnection();
	   NotifyBroadcast.notify(accountMgr.getAccount().getJid(),  NotifyBroadcast.ACCOUNT_REMOVE_MSG);
	   
   }
   
   public void createAccount() {
	   HashMap <String, String> attrMap = new HashMap <String, String> ();
	   attrMap.put("name", accountMgr.getAccount().getAliasName());
	   AccountManager accountManager = AccountManager.getInstance(conn);
	   if (accountMgr.getAccount().isCreated())
		   return ;
	   try {
		   accountManager.createAccount(accountMgr.getAccount().getUsername(), 
				   accountMgr.getAccount().getPassword(), 
				   attrMap );
		   accountMgr.getAccount().setAccountCreated(true);
	   } catch (  NoResponseException e ) {
		   Log.e("milian", "Server is NoResponseException");
		   
	   } catch (XMPPErrorException e) {
			   XMPPError error =   e.getXMPPError();
			  if (error.getCondition().equals(XMPPError.Condition.conflict.toString() ))  {
				  Log.e("milian", "createAccount Error:  Account conflict, it was created in the past");
				  accountMgr.getAccount().setAccountCreated(true);
			  }  else if (error.getCondition().equals(XMPPError.Condition.forbidden.toString() ))  {
				  Log.e("milian", "createAccount Error:  Account forbidden ");
			  }  else if (error.getCondition().equals(XMPPError.Condition.feature_not_implemented.toString() ))  {
				  Log.e("milian", "createAccount Error:  Account feature_not_implemented ");
			  }  else if (error.getCondition().equals(XMPPError.Condition.not_allowed.toString() ))  {
				  Log.e("milian", "createAccount Error:  Account not_allowed ");
			  }  else if (error.getCondition().equals(XMPPError.Condition.bad_request.toString() ))  {
				  Log.e("milian", "createAccount Error:  Account bad_request ");
			  }  else if (error.getCondition().equals(XMPPError.Condition.not_acceptable.toString() ))  {
				  Log.e("milian", "createAccount Error:  Account not_acceptable ");
			  }  else if (error.getCondition().equals(XMPPError.Condition.request_timeout.toString() ))  {
				  Log.e("milian", "createAccount Error:  Account request_timeout ");
			  }  
	   } catch ( NotConnectedException e) {
		   Log.e("milian", "Server is NotConnectedException");
	   }
	   
   }
}
