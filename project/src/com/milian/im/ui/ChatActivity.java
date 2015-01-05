package com.milian.im.ui;


import java.io.File;

import com.milian.im.entry.*;

import android.content.BroadcastReceiver;  
import android.content.ComponentName;
import android.content.Context;  
import android.content.Intent;  
import android.content.IntentFilter;
import android.content.ServiceConnection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.util.Log; 
import android.app.Activity;
import android.app.Service;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
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

import com.milian.im.entry.MessageML;
import com.milian.im.service.NotifyBroadcast;
import com.milian.im.service.XmppMgrService;
import com.milian.im.ui.ChatMsgViewAdapter;
import com.milian.im.ui.ContactsFragment.NotifyReceiver;
import com.milian.im.audio.SoundMeter;
import com.milian.im.service.XmppMgrService;



public class ChatActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private Button mBtnSend;
	private TextView mBtnRcd;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private RelativeLayout mBottom;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<MessageML> msgList = new ArrayList<MessageML>();
	private boolean isShosrt = false;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	private ImageView img1, sc_img1;
	private SoundMeter mSensor;
	private View rcChat_popup;
	private LinearLayout del_re;
	private ImageView chatting_mode_btn, volume;
	private boolean btn_vocie = false;
	private int flag = 1;
	private Handler mHandler = new Handler();
	private String voiceName;
	private long startVoiceT, endVoiceT;
	private XmppMgrService.XmppMgrBinder  binder;
	private XmppMgrService xmppMgrService;
	private AccountML account;
	private ContactML contact;
	private Activity activity;
	private NotifyReceiver notifyReceiver;
 
 
	private ServiceConnection conn = new ServiceConnection() {

		public void onServiceConnected(ComponentName name , IBinder service)
		{
			 binder = (XmppMgrService.XmppMgrBinder) service;
			 Log.d("milian", "chatActivity onServiceConnected");
			 xmppMgrService =  binder.getService();
			 Log.d("milian", "chatActivity getService finished ");
			 xmppMgrService.getAddedMessageML(account, contact.getUser());
			 List <MessageML> list = xmppMgrService.getAllMessageML(account, contact.getUser());
			 
			 if ( list != null) {
				 Log.d("milian", " getMessageML list is not null ");
				 for (MessageML msg: list) {
	        			msgList.add(msg);
	        		}
				 
			 }	 else {
				 Log.d("milian", "get MessageML list is null ");
			 }
			 Log.d("milian", "chatActivity getAllMessageML finished ");
			mAdapter = new ChatMsgViewAdapter(activity, msgList);
			if ( mAdapter == null)  {
				
				Log.d("milian", "mAdapter is null");
			}   else {
				Log.d("milian", "mAdapter is not null");
			}
			Log.d("milian", "chatActivity ChatMsgViewAdapter finished ");
			if ( mListView == null) {
				Log.d("milian", "mListView is null");
			} else {
				Log.d("milian", "start setAdapter");
				
				//mListView.setAdapter(mAdapter);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(msgList.size()  - 1);
				Log.d("milian", "start setAdapter finished");
			}
			
			Log.d("milian", "start freshContact ");
			
			freshContact();
			Log.d(this.getClass().getName(), "ChatActivity Service freshcontact finished ");
		}
		

		public void onServiceDisconnected(ComponentName name )
		{
			Log.v("milian", "service disconnect");
			xmppMgrService = null;
		}
				
		
		
	};
 
	public void initService()
	{
		
		//startService(new Intent("com.milian.im.service.XmppMgrService"));
		xmppMgrService = null;
		Intent intent;
		intent = new Intent();
		Log.e ("milian" ,  "set action ");
		intent.setAction("com.milian.im.service.XmppMgrService");
		Log.e ("milian" ,   "before bind service ");
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
	public void freshContact()
	{
		if ( xmppMgrService == null) {
			Log.e(this.getClass().getName(), "xmppMgrService is null");
			return;
		}
		account = xmppMgrService.getAccountML(account.getJid());
		ContactML tmpContact;
		tmpContact = xmppMgrService.getContact(account, contact.getJid());
		if (tmpContact == null)
			tmpContact = xmppMgrService.getRecentContact(account, contact.getJid());
		
		if (tmpContact == null) {
			Log.d("milian",  this.getClass().getName() +  "fresh Contact " +  contact.getName());
			return;
		}
		contact = tmpContact;
		Log.d("milian" , this.getClass().getName() +  "fresh Contact " +  contact.getName());
		TextView title = (TextView) findViewById(R.id.chat_titile);
	    title.setText( contact.getName());;;
		
	}

	
	public class MsgReceiver extends BroadcastReceiver {  
	      
	    private static final String TAG = "MyReceiver";  
	      
	    @Override  
	    public void onReceive(Context context, Intent intent) {  
	        String msg = intent.getStringExtra("msg");  
	        Log.i(TAG, msg);  
	    }  
	  
	}  
    
	public class NotifyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) { 
			 
			if ( intent.getAction().equals(NotifyBroadcast.NOTIFY_TYPE_MESSAGE)) {
		        int msgID = intent.getIntExtra(NotifyBroadcast.MSG_ID,  0);
        
		        switch ( msgID) {
			        case NotifyBroadcast.CONNECT_SUCCESS_MSG:
			        	
			        	break;
			        case NotifyBroadcast.CONNECT_FAIL_MSG:
			        	
			        	break;
			        case NotifyBroadcast.ACCOUNT_REMOVE_MSG:
			        	activity.finish();
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
			        	
			        	String  accountJid = intent.getStringExtra(NotifyBroadcast.ACCOUNT_JID);
			        	String contactJid = intent.getStringExtra(NotifyBroadcast.CONTACT_JID);
			        	Log.d("milian", "ChatActivity: receive a new Message: accountJid: " +  accountJid + ",  contactJid: " + contactJid);
			        	if ( contactJid == null)  {
			        		Log.d("milian", "contactJid is null");
			        		return ;
			        	}
			        	if (contact == null) {
			        		Log.d("milian", "contact is null");
			        		return;
			        	}
			        	Log.d("milian", "contactJid:"  + contactJid + ", and  current contact: " + contact.getUser());
			        	if( !contactJid.equals(contact.getUser())) {
			        		Log.d("milian", "not equal");
			        		return ;
			        	}
			        	List <MessageML> list = xmppMgrService.getAddedMessageML(account, contact.getUser()) ;
			        	if ( list != null) {
			        		Log.d("milian", "getAddedMessageML is not  null");
                            //boolean msgEmpty=msgList.isEmpty();
			        		for (MessageML msg: list) {
			        			msgList.add(msg);
			        		}
				        	//mAdapter.notifyDataSetInvalidated();
				        	mAdapter.notifyDataSetChanged();
				        	/*
                            if(msgEmpty){
									//mListView.setVisibility(View.GONE);
									//mListView.setVisibility(View.VISIBLE);
                            } */
				        	//mListView.refreshDrawableState();
				        	mListView.setSelection(msgList.size()  - 1);
			        	} else {
			        		Log.d("milian", "getAddedMessageML is null");
			        	}
			        	
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
    
	public void onCreate(Bundle savedInstanceState) {
		 
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		activity = this;
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		initView();

		initData();
		Bundle bundle=this.getIntent().getExtras();
		account = new AccountML(bundle.getString("account"));
		contact = new ContactML(bundle.getString("contact"));
		initBroadCast();
		initService();
		
	}
	
	public void initBroadCast()
	{
		notifyReceiver = new NotifyReceiver();
		IntentFilter intentFilter = new IntentFilter(NotifyBroadcast.NOTIFY_TYPE_MESSAGE );
		registerReceiver( notifyReceiver , intentFilter);
	}

	public void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnRcd = (TextView) findViewById(R.id.btn_rcd);
		mBtnSend.setOnClickListener(this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBottom = (RelativeLayout) findViewById(R.id.btn_bottom);
		mBtnBack.setOnClickListener(this);
		chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp);
		volume = (ImageView) this.findViewById(R.id.volume);
		rcChat_popup = this.findViewById(R.id.rcChat_popup);
		img1 = (ImageView) this.findViewById(R.id.img1);
		sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		del_re = (LinearLayout) this.findViewById(R.id.del_re);
		voice_rcd_hint_rcding = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_tooshort);
		mSensor = new SoundMeter();
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		

		chatting_mode_btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (btn_vocie) {
					mBtnRcd.setVisibility(View.GONE);
					mBottom.setVisibility(View.VISIBLE);
					btn_vocie = false;
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_msg_btn);

				} else {
					mBtnRcd.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.GONE);
					chatting_mode_btn
							.setImageResource(R.drawable.chatting_setmode_voice_btn);
					btn_vocie = true;
				}
			}
		});
		mBtnRcd.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				return false;
			}
		});
	}

	//private String[] msgArray = new String[] { "你好","非常好","来聊聊天","聊什么东西呢 ","聊聊中国好生意","最近股市很赚钱"};
/*
	private String[] dataArray = new String[] { "2012-10-31 18:00",
			"2012-10-31 18:10", "2012-10-31 18:11", "2012-10-31 18:20",
			"2012-10-31 18:30", "2012-10-31 18:35"};
	*/
	//private final static int COUNT = 6;

	public void initData() {
		 
		mAdapter = new ChatMsgViewAdapter(this, msgList);
		mListView.setAdapter(mAdapter);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			 
			finish();
			break;
		}
	}

	private void send() {
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0) {
			MessageML msg = new MessageML();
			Log.d("milian", "after new msg, display msg time: " +  msg.getTimeStr());
			//msg.setDate(getDate());
			//msg.setName(entity.getName());
			msg.setMsgDir(false);
			msg.setText(contString);
			msg.setType(MessageML.MSG_TYPE_TEXT);

			msgList.add(msg);
			Log.d("milian", "befor notifyDataSetChanged, display msg time: " +  msg.getTimeStr());
			mAdapter.notifyDataSetChanged();

			mEditTextContent.setText("");

			mListView.setSelection(msgList.size()  - 1);
			//mListView.setVisibility(View.GONE);
			//mListView.setVisibility(View.VISIBLE);
			if(xmppMgrService != null)
				xmppMgrService.sendMessageML(account, contact.getUser(), msg);
			else
				Log.e(this.getClass().getSimpleName(), "xmppMgrService is null !");
			//xmppMgrService.sendMsg("lisi@fangxinchihe.cn" , entity  );
		}
	}

	private String getDate() {
		Calendar c = Calendar.getInstance();

		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
			return false;
		}

		if (btn_vocie) {
			System.out.println("1");
			int[] location = new int[2];
			mBtnRcd.getLocationInWindow(location);  
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			int[] del_location = new int[2];
			del_re.getLocationInWindow(del_location);
			int del_Y = del_location[1];
			int del_x = del_location[0];
			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				if (!Environment.getExternalStorageDirectory().exists()) {
					Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
					return false;
				}
				System.out.println("2");
				if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) { 
					System.out.println("3");
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
					rcChat_popup.setVisibility(View.VISIBLE);
					voice_rcd_hint_loading.setVisibility(View.VISIBLE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.GONE);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							if (!isShosrt) {
								voice_rcd_hint_loading.setVisibility(View.GONE);
								voice_rcd_hint_rcding
										.setVisibility(View.VISIBLE);
							}
						}
					}, 300);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					startVoiceT = SystemClock.currentThreadTimeMillis();
					voiceName = startVoiceT + ".amr";
					start(voiceName);
					flag = 2;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) { 
				System.out.println("4");
				mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
				if (event.getY() >= del_Y
						&& event.getY() <= del_Y + del_re.getHeight()
						&& event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					rcChat_popup.setVisibility(View.GONE);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					stop();
					flag = 1;
					File file = new File(android.os.Environment.getExternalStorageDirectory()+"/"
									+ voiceName);
					if (file.exists()) {
						file.delete();
					}
				} else {

					voice_rcd_hint_rcding.setVisibility(View.GONE);
					stop();
					endVoiceT = SystemClock.currentThreadTimeMillis();
					flag = 1;
					int time = (int) ((endVoiceT - startVoiceT) / 500);
					if (time < 1) {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort
										.setVisibility(View.GONE);
								rcChat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						return false;
					}
					MessageML entity = new MessageML();
					Log.d("milian", "after new msg, display msg time: " +  entity.getTimeStr());
					//entity.setDate(getDate());
					//entity.setName("张无忌");
					entity.setMsgDir(false);
					//entity.setTimeStr(time+"\"");
					entity.setText(voiceName);
					msgList.add(entity);
					Log.d("milian", "before NotifyDataSetChanged,  new msg, display msg time: " +  entity.getTimeStr());
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(mListView.getCount() - 1);
					rcChat_popup.setVisibility(View.GONE);

				}
			}
			if (event.getY() < btn_rc_Y) { 
				System.out.println("5");
				Animation mLitteAnimation = AnimationUtils.loadAnimation(this,
						R.anim.cancel_rc);
				Animation mBigAnimation = AnimationUtils.loadAnimation(this,
						R.anim.cancel_rc2);
				img1.setVisibility(View.GONE);
				del_re.setVisibility(View.VISIBLE);
				del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
				if (event.getY() >= del_Y
						&& event.getY() <= del_Y + del_re.getHeight()
						&& event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
					sc_img1.startAnimation(mLitteAnimation);
					sc_img1.startAnimation(mBigAnimation);
				}
			} else {

				img1.setVisibility(View.VISIBLE);
				del_re.setVisibility(View.GONE);
				del_re.setBackgroundResource(0);
			}
		}
		return super.onTouchEvent(event);
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {
		
		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);
			
			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}
	protected void onDestroy() {

        unbindService(conn);
        Log.d("milian", "ChatActivity: onDestroy" );
        super.onDestroy();
   }

	public void head_xiaohei(View v) { // 

	}
}
