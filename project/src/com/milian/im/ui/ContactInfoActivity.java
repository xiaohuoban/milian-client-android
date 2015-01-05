package com.milian.im.ui;


import android.app.Activity;
import android.app.Service;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.milian.im.entry.AccountML;
import com.milian.im.entry.ContactML;
import com.milian.im.service.NotifyBroadcast;
import com.milian.im.service.XmppMgrService;
import com.milian.im.ui.ChatActivity;
import com.milian.im.ui.ChatActivity.NotifyReceiver;

public class ContactInfoActivity extends Activity {
	ImageView headerImage;
	Activity activity;
	private AccountML account;
	private ContactML contact;
	private NotifyReceiver notifyReceiver;
	private XmppMgrService.XmppMgrBinder  binder;
	private XmppMgrService xmppMgrService;
	private ServiceConnection conn = new ServiceConnection() {

		public void onServiceConnected(ComponentName name , IBinder service)
		{
			 binder = (XmppMgrService.XmppMgrBinder) service;
			 xmppMgrService =  binder.getService();
			 Log.d(this.getClass().getName(), " ContactInfoActivity :service connectted ");
			 freshContact();
			
		}
		

		public void onServiceDisconnected(ComponentName name )
		{
			Log.v("milian", "service disconnect");
			xmppMgrService = null;
		}
				
		
		
	};
	
	public void freshContact()
	{
		if ( xmppMgrService == null) {
			Log.e(this.getClass().getName(), "xmppMgrService is null");
			return;
		}
		account = xmppMgrService.getAccountML(account.getJid());
		String jid = contact.getJid();
		ContactML tmpContact ;
		tmpContact = xmppMgrService.getContact(account, jid);
		if (tmpContact == null)
			tmpContact = xmppMgrService.getRecentContact(account, jid);
		if (tmpContact == null) {
			Log.d("milian",  this.getClass().getName() +  "fresh Contact " +  jid);
			return;
		}
		contact = tmpContact;
		 Log.d(this.getClass().getName(), "ContactInfoActivity fresh Contact " +  contact.getName());
		TextView name = (TextView) findViewById(R.id.contact_info_name);
	    name.setText( contact.getName());
		
	}

	public void initService()
	{
		
		//startService(new Intent("com.milian.im.service.XmppMgrService"));
		xmppMgrService = null;
		Intent intent;
		intent = new Intent();
		Log.e ("milian" ,  "set action ");
		intent.setAction("com.milian.im.service.XmppMgrService");
		Log.e ("milian" ,  "befor bind service ");
		try {
			Intent startIntent = new Intent(this, XmppMgrService.class);
			//startService(startIntent);
			bindService(intent, conn, Service.BIND_AUTO_CREATE);
			
			 
			//Log.e ("milian" ,  "after bind service ");
			
			Log.e ("milian" ,  "get service  ");
			//xmppMgrService.setMessageReciever(this);
			
			
		} catch (NullPointerException e) {
			Log.e ("milian" ,  "bindService to XmppMgrService error ！");
			e.printStackTrace();
		}
		
	}
 
	public class NotifyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) { 
			if ( intent.getAction().equals(NotifyBroadcast.NOTIFY_TYPE_MESSAGE)) {
		        int msgID = intent.getIntExtra(NotifyBroadcast.MSG_ID,  0);
        
		        switch ( msgID) {
			        case NotifyBroadcast.CONNECT_SUCCESS_MSG:
			        	
			        	break;
			        case NotifyBroadcast.ACCOUNT_REMOVE_MSG:
			        	activity.finish();
			        	break;
			        case NotifyBroadcast.CONNECT_FAIL_MSG:
			        	
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_SUCCESS_MSG:
       	
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_FAIL_MSG:
			        	
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_FAIL_PASSWORD_ERROR_MSG:
			        	
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_RESOURCE_CONFLICT_MSG:
			        	
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_USER_NOT_EXIST_MSG:
			        	
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_XMPP_ERROR_MSG:
			        	
			        	break;
			        case NotifyBroadcast.MESSAGE_NEW_RECEIVE__MSG:
			        	
			        	break;
			        	
			        case NotifyBroadcast.CONTACT_ADD__MSG:
			        	break;
			        case NotifyBroadcast.CONTACT_UPDATE__MSG:
			        	break;      
			        case NotifyBroadcast.CONTACT_REMOVE__MSG:
			        	break;      
			        case NotifyBroadcast.GROUP_JOIN_SUCCESS__MSG:
			        	break;      
			        case NotifyBroadcast.GROUP_JOIN_FAIL__MSG:
			        	break;      
			        case NotifyBroadcast.GROUP_LEAVE_SUCCESS__MSG:
			        	break;      
			        case NotifyBroadcast.GROUP_LEAVE_FAIL__MSG:
			        	break;      
			        	
			        	default:
			        		Log.e("chatActivity", "receive a message: " + msgID);
			        		break;
		
		        }
		        
			}
	    } 
	}
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_info);
        activity = this;
        Bundle bundle=this.getIntent().getExtras();
        Log.d("milian", "onCreate contact: " + bundle.getString("contact") + ", account: " + bundle.getString("account"));
		account = new AccountML(bundle.getString("account"));
		contact = new ContactML(bundle.getString("contact"));
		Log.d("milian", "onCreate contact: " + contact.getJid() + ", account: " + account.getJid());
        initService();
        initBroadCast();
        initView();
    }
	
	public void initView()
	{
		ImageView header_img = (ImageView) findViewById(R.id.contact_info_header_image);
		header_img.setImageResource(contact.getHeaderImgRes() );
		
		TextView name = (TextView) findViewById(R.id.contact_info_name);
		name.setText(contact.getName());
		
	       Button chat_btn = (Button) findViewById(R.id.contact_info_chat_btn);
	       
	        chat_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) 
				{
					//int i;
					//ViewGroup vg = (ViewGroup) v.getParent().getParent(); 
	                // i = vg.indexOfChild((View)v.getParent()); 
					//i = (Integer) v.getTag();
					//showDialog("position: " + i);
					Intent intent=new Intent(ContactInfoActivity.this,  ChatActivity.class);  
					
		              Bundle bundle=new Bundle();  
		              //bundle.putString("arg", "position is :"+i);  
		              bundle.putString("account",  account.getJid());  
		              bundle.putString("contact",  contact.getUser());  
		              intent.putExtras(bundle);  
		               Log.d("milian", " to start ChatActivity ");
		              startActivity(intent); 
		              Log.d("milian", "end  start ChatActivity" );
					
				}
				});
	        
	        Button back_btn = (Button) findViewById(R.id.contact_info_back_btn);
	        back_btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) 
				{
					finish();
					
				}
				});	
		
	}
	
	public void initBroadCast()
	{
		notifyReceiver = new NotifyReceiver();
		IntentFilter intentFilter = new IntentFilter(NotifyBroadcast.NOTIFY_TYPE_MESSAGE );
		registerReceiver( notifyReceiver , intentFilter);
	}
	
	protected void onDestroy() {
        
        unbindService(conn);
        Log.d("milian", "ContactInfoActivity: onDestroy,  activityCreated = false" );
        super.onDestroy();
   }

 
	
}
