package com.milian.im.ui;

import com.milian.im.service.NotifyBroadcast;
import com.milian.im.service.XmppMgrService;
import com.milian.im.ui.ContactsFragment.NotifyReceiver;
import com.milian.im.entry.AccountML;
import com.milian.im.entry.ContactML;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.packet.Message;




//import org.jivesoftware.smack.SmackAndroid;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

//import android.app.LocalActivityManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;  
import android.support.v4.app.FragmentManager; 
import android.support.v4.app.FragmentPagerAdapter;  
import android.support.v4.app.FragmentActivity;  
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


/**
 * Tab页面手势滑动切换以及动画效果
 * 
 * @author D.Winter
 * 
 */
public class MainActivity extends FragmentActivity    {
	// ViewPager是google SDk中自带的一个附加包的一个类，可以用来实现屏幕间的切换。
	// android-support-v4.jar
	private ViewPager mPager;//页卡内容
	//private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView mainTab[];// 页卡头标
	//private Intent intent[] ;
	private ArrayList<Fragment> fragmentList; 
	private Fragment fragments[] ;
	private MeFragment meFragment;
	//LocalActivityManager manager = null;
	Context context = null;
	private int screenWidth;
	private int offset = 0;// 动画图片偏移量
	private int curIndex = 0;// 当前页卡编号
	private int bmpWidth;// 动画图片宽度
	static private  int MAIN_TAB_NUMBERS = 5 ;
	private XmppMgrService.XmppMgrBinder  binder;
	private XmppMgrService xmppMgrService;
	private AccountML account;
	private ContactML contact;
	private NotifyReceiver notifyReceiver;
	public AccountML getAccount(){
		return account;
	}
	
	public XmppMgrService getXmppMgrService(){
		return xmppMgrService;
	}
	
	public void initAccount()
	{
		account = new AccountML("" , "", "fangxinchihe.cn", "mobile", 5222  );

	}
	public void connectAccountML()
	{
		
		Log.e ("milian" ,  "before xmppMgrService.addAccountML  ");
		account = xmppMgrService.getDefaultAccount();
		account.setAccountCreated(false);
		meFragment.updateAccount();
		xmppMgrService.addAccountML(account);
		Log.e ("milian" ,  "after xmppMgrService.addAccountML  ");
	}
	
	public void updateAccountML(AccountML newAccount)
	{
		if ( xmppMgrService == null) {
			Log.e ("milian" ,  "Error! the   xmppMgrService is null !  can't updateAccountML now");
			return ;
		}
		Log.e ("milian" ,  "before xmppMgrService.removeAccountML  ");
		xmppMgrService.removeAccountML(account);
		Log.e ("milian" ,  "after xmppMgrService.removeAccountML  ");
		account = newAccount;
		Log.e ("milian" ,  "start xmppMgrService.addAccountML(account) ");
		xmppMgrService.addAccountML(account);
		Log.e ("milian" ,  "end xmppMgrService.addAccountML(account) ");
	}
	
	public boolean setAccountMLName(AccountML newAccount, String name)
	{
		if ( xmppMgrService == null) {
			Log.e ("milian" ,  "Error! the   xmppMgrService is null !  can't updateAccountName now");
			return false;
		}
		Log.e ("milian" ,  "before xmppMgrService.setPersonalInfo, name:   " + name);
		xmppMgrService.setPersonalInfo(account, name);
		Log.e ("milian" ,  "start xmppMgrService.setPersonalInfo(account) ");

		return true;
	}
	
	public class NotifyReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) { 
			if ( intent.getAction().equals(NotifyBroadcast.NOTIFY_TYPE_MESSAGE)) {
		        int msgID = intent.getIntExtra(NotifyBroadcast.MSG_ID,  0);
		        Toast toast;
		        switch ( msgID) {
			        case NotifyBroadcast.CONNECT_SUCCESS_MSG:
			        	
			        	break;
			        case NotifyBroadcast.CONNECT_FAIL_MSG:
			        	
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_SUCCESS_MSG:
			        	toast = Toast.makeText(context, "登录成功 ！", Toast.LENGTH_SHORT);
			        	toast.show();
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_FAIL_MSG:
			        	toast = Toast.makeText(context, "LOGIN_FAIL_MSG", Toast.LENGTH_SHORT);
			        	toast.show();
			        	break;
			        	
			        case NotifyBroadcast.LOGIN_FAIL_PASSWORD_ERROR_MSG:
			        	toast = Toast.makeText(context, "LOGIN_FAIL_PASSWORD_ERROR_MSG", Toast.LENGTH_SHORT);
			        	toast.show();
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_RESOURCE_CONFLICT_MSG:
			        	toast = Toast.makeText(context, "LOGIN_FAIL_RESOURCE_CONFLICT_MSG", Toast.LENGTH_SHORT);
			        	toast.show();
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_USER_NOT_EXIST_MSG:
			        	toast = Toast.makeText(context, "LOGIN_FAIL_USER_NOT_EXIST_MSG", Toast.LENGTH_SHORT);
			        	toast.show();
			        	break;
			        case NotifyBroadcast.LOGIN_FAIL_XMPP_ERROR_MSG:
			        	toast = Toast.makeText(context, "LOGIN_FAIL_XMPP_ERROR_MSG", Toast.LENGTH_SHORT);
			        	toast.show();
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
	
	public void initBroadCast()
	{
		notifyReceiver = new NotifyReceiver();
		IntentFilter intentFilter = new IntentFilter(NotifyBroadcast.NOTIFY_TYPE_MESSAGE );
		registerReceiver( notifyReceiver , intentFilter);
	}
	
	
	private ServiceConnection conn = new ServiceConnection() {

		public void onServiceConnected(ComponentName name , IBinder service)
		{
			Log.e("milian", "onServiceConnected onBind" );
			 binder = (XmppMgrService.XmppMgrBinder) service;
				Log.e("milian", "after onServiceConnected onBind" );
				xmppMgrService =  binder.getService();
				Log.e("milian", "start to connectAccountML()" );
				//SmackAndroid.init(this);
				connectAccountML();
		}
		

		public void onServiceDisconnected(ComponentName name )
		{
			Log.v("milian", "service disconnect");
			
		}
				
		
		
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		context = MainActivity.this;
		
		//don't use activity in viewpager
/*
		 manager = new LocalActivityManager(this , true);
	    manager.dispatchCreate(savedInstanceState);
*/	    
		initAccount();
		InitImageView();
		initMainTabTextView();
		InitViewPagerFragment();
		initBroadCast();
		initService();
		
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


	
	/**
	 * 初始化头标
	 */
	private void initMainTabTextView() {
		int i;
		mainTab =  new TextView[MAIN_TAB_NUMBERS];
		mainTab[0] =   (TextView) findViewById(R.id.tab_main_chat);
		mainTab[1] =   (TextView) findViewById(R.id.tab_main_say);
		mainTab[2] =   (TextView) findViewById(R.id.tab_main_contacts);
		mainTab[3] =   (TextView) findViewById(R.id.tab_main_together);
		mainTab[4] =   (TextView) findViewById(R.id.tab_main_me);
		for ( i = 0; i < MAIN_TAB_NUMBERS; i ++ ) {
			mainTab[i].setOnClickListener(new MainTabItemOnClickListener(i));
		}
		setMainTabTextBgColor(0);

	}
	
	// don't use activtity method in main tab viewpager
/*
	private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }
	
	private void InitViewPagerActivity() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		
		Intent intent[] = new Intent[MAIN_TAB_NUMBERS];
		intent[0] = new Intent(context, QuikChatActivity.class);
		listViews.add(getView("ChatActivity", intent[0]));
        intent[1]= new Intent(context, SayActivity.class);
        listViews.add(getView("SayActivity", intent[1]));
        intent[2] = new Intent(context, ContactsActivity.class);
        listViews.add(getView("ContactsActivity", intent[2]));
        intent[3] = new Intent(context, TogetherActivity.class);
        listViews.add(getView("TogetherActivity", intent[3]));
        intent[4] = new Intent(context, MeActivity.class);
        listViews.add(getView("MeActivity",  intent[4]));
		

		
		mPager.setAdapter(new MainTabPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MainTabOnPageChangeListener());
	}
	*/
	private void InitViewPagerFragment() {
	
		mPager = (ViewPager) findViewById(R.id.vPager);
		fragmentList = new ArrayList<Fragment>();
		
		fragments = new Fragment[MAIN_TAB_NUMBERS];
		fragments[0] = new QuikChatFragment();
		fragmentList.add(fragments[0]);
		fragments[1] = new SayFragment();
		fragmentList.add(fragments[1]);
		fragments[2] = new ContactsFragment();
		fragmentList.add(fragments[2]);
		fragments[3] = new TogetherFragment();
		fragmentList.add(fragments[3]);
		fragments[4] = new MeFragment();
		meFragment = (MeFragment)fragments[4];
		fragmentList.add(fragments[4]);
       
		MainTabFragmentPagerAdapter mPagerAdapter =  new MainTabFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList);
		mPager.setAdapter(mPagerAdapter);
		
		mPager.setCurrentItem(0);
		mPager.setOffscreenPageLimit(5);
		//mPagerAdapter.notifyDataSetChanged();
		mPager.setOnPageChangeListener(new MainTabOnPageChangeListener());
	}


	private void setMainTabTextBgColor(int cur)
	{
		int i = 0;
		
		
		for (i = 0; i < MAIN_TAB_NUMBERS; i ++ ) {
			if( cur == i ) {
				mainTab[i].setBackgroundColor(Color.GREEN);
			} else {
				mainTab[i].setBackgroundColor(Color.WHITE);
			}
			
		}
	}
	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpWidth = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;// 获取分辨率宽度
		//offset = (screenWidth / MAIN_TAB_NUMBERS - bmpWidth) / 2;// 计算偏移量
		offset = getAnimationPosition(0);// 计算偏移量  , default use 0 index;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * ViewPager适配器
	 */
	public class MainTabPagerAdapter extends PagerAdapter  {
		public List<View> mListViews;

		public MainTabPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
	
	
	/**
	 * ViewPager适配器
	 */
	public class MainTabFragmentPagerAdapter extends FragmentPagerAdapter {
		ArrayList<Fragment> list;

		public  MainTabFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
			super(fm);  
	        this.list = list; 
		}

		public MainTabFragmentPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
	 
	  
	 
	    @Override
	    public int getCount() {
	        return list.size();
	    }
	 
	    @Override
	    public Fragment getItem(int arg0) {
	        return list.get(arg0);
	    }
	 
	    @Override
	    public int getItemPosition(Object object) {
	        return super.getItemPosition(object);
	    }
	 
	}
	
	

	/**
	 * 头标点击监听
	 */
	public class MainTabItemOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MainTabItemOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

private int getAnimationPosition(int index )
{
	 return   (screenWidth / MAIN_TAB_NUMBERS) >  bmpWidth  ?  
			 (    index * (screenWidth / MAIN_TAB_NUMBERS) + ((screenWidth / MAIN_TAB_NUMBERS) -  bmpWidth  )/2  )     :   
				 (   (  index * (screenWidth / MAIN_TAB_NUMBERS)  >  ( bmpWidth - (screenWidth / MAIN_TAB_NUMBERS)   )/2)     ?
						   (  index * (screenWidth / MAIN_TAB_NUMBERS) - ( bmpWidth - (screenWidth / MAIN_TAB_NUMBERS)   )/2)   :   0 ) ;

}
	
	/**
	 * 页卡切换监听
	 */
	public class MainTabOnPageChangeListener implements OnPageChangeListener {

		

		@Override
		
		public void onPageSelected(int arg0) {
			//int old_place = curIndex * offset + bmpWidth;
			int old_place =  getAnimationPosition(curIndex);
			
			int new_place = getAnimationPosition(arg0);
			Animation animation = null;
			
			animation = new TranslateAnimation(old_place, new_place, 0, 0);
			setMainTabTextBgColor(arg0);
			curIndex = arg0;
	
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	public void receiveMsg(Message msg)  // to receive Msg
	{
		
		
	}
	
	protected void onDestroy() {

        unbindService(conn);
        Log.d("milian", "MainActivity: onDestroy" );
        super.onDestroy();
   }
}