package com.milian.im.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;  
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.AlertDialog;  
import android.content.DialogInterface;  

import com.milian.im.entry.*;
import com.milian.im.service.NotifyBroadcast;
import com.milian.im.service.XmppMgrService;
import com.milian.im.ui.ChatActivity;
import com.milian.im.ui.ContactInfoActivity;
import com.milian.im.ui.ChatActivity.NotifyReceiver;




public class QuikChatFragment extends Fragment {

	private ListView listView;
	private ArrayAdapter<ContactML> listAdapter;	
 
	LayoutInflater inflater ;
	View  view;
	 
	private List<ContactML> contactMLList = new ArrayList<ContactML>();  //all is recent ContactML: unfimiliar and recent chat friend
	private HashMap <String , ContactML > contactMap = new HashMap <String, ContactML > ();
	private AccountML account;
	private XmppMgrService xmppMgrService;

	private NotifyReceiver notifyReceiver;

	  class ViewHolder {  
		
	    public TextView lastMsgTime;  
	    public TextView name;  
	    public ImageView header_img;
	    public TextView unreadMsgNumbers;
	    public TextView lastMsg;
	    public View unfamiliarBlock;
	    public Button addContact;
	    public ViewHolder() { };
	}  
	  
		public class NotifyReceiver extends BroadcastReceiver {
			public void onReceive(Context context, Intent intent) { 
				String log = null;
				account = ((MainActivity) getActivity() ).getAccount();
				if (account != null) {
					xmppMgrService = ((MainActivity) getActivity() ).getXmppMgrService();
				}
				if (xmppMgrService == null) {
					Log.v("ContactsFragment",  "receive broadcast msg, but xmppMgrService is null");
					return;
				}
				
				
				if ( intent.getAction().equals(NotifyBroadcast.NOTIFY_TYPE_MESSAGE)) {
			        int msgID = intent.getIntExtra(NotifyBroadcast.MSG_ID,  0);
	        
			        switch ( msgID) {
				        case NotifyBroadcast.CONNECT_SUCCESS_MSG:
				        	
				        	break;
				        case NotifyBroadcast.CONNECT_FAIL_MSG:
				        	
				        	break;
				        	
				        case NotifyBroadcast.ACCOUNT_REMOVE_MSG:
				        	contactMLList.clear();
				        	contactMap.clear();
				        	listAdapter.notifyDataSetChanged();
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
				        	if (log == null)
				        		log = "MESSAGE_NEW_RECEIVE__MSG";
				        case NotifyBroadcast.MESSAGE_NEW_SEND__MSG:
				        	if (log == null)
				        		log = "MESSAGE_NEW_SEND__MSG";
				        case  NotifyBroadcast.MESSAGE_READED__MSG:
				        	if (log == null)
				        		log = "MESSAGE_READED__MSG";
				        	String  accountJid = intent.getStringExtra(NotifyBroadcast.ACCOUNT_JID);
				        	String contactJid = intent.getStringExtra(NotifyBroadcast.CONTACT_JID);
				        	Log.d("milian", "QuikChat Fragment: receive a new Message: accountJid: " +  accountJid + ",  contactJid: " + contactJid);
				        	if ( contactJid == null)  {
				        		Log.d("milian", "contactJid is null");
				        		return ;
				        	}
				        	Log.d("milian", "QuickFragment:  Recv Broadcast: " + log);
				        	Log.d("milian",  "QuikChatFragment: "+ contactJid +" 's unRead Message Number is: " + xmppMgrService.getAddedMessageMLSize(account, contactJid)     );
				        	
				        	
				        	Collections.sort(contactMLList,  ContactML.sortByTimeComparator() );
				        	 
				        	listAdapter.notifyDataSetChanged();
				        	listView.setVisibility(View.GONE);
				        	listView.setVisibility(View.VISIBLE);
				        	break;
				        	
				        case NotifyBroadcast.CONTACT_ADD__MSG:
				        	break;
				        case NotifyBroadcast.CONTACT_UPDATE__MSG:
				        	break;      
				        case NotifyBroadcast.CONTACT_REMOVE__MSG:
				        	break;      
				        	
				        case NotifyBroadcast.CONTACT_RECENT_ADD__MSG:
					        {
					        	Log.d("milian" , " QuikChat recv CONTACT_RECENT_ADD__MSG: " );
					        	List<ContactML> addList =  xmppMgrService.getAddedRecentContactML(account);
					        	if (addList == null){
					        		Log.v("ContactsFragment",  "receive broadcast msg, but addList is null");
					        		return;
					        	}
					        	for(int i = 0; i <addList.size(); i++)   {  
					        		Log.d("milian" , " QuikChat recv CONTACT_RECENT_ADD__MSG, jid: "  + addList.get(i).getJid() );
					        		addContact(addList.get(i) );  
					            }  
					        	Collections.sort(contactMLList,  ContactML.sortByTimeComparator() );
					        	listAdapter.notifyDataSetChanged();
					        }
				        	break;
				        case NotifyBroadcast.CONTACT_RECENT_UPDATE__MSG:
					        {
	
					        }
				        	break;      
				        case NotifyBroadcast.CONTACT_RECENT_REMOVE__MSG:
					        {

					        }
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
				        		Log.e("milian", "QuikChatFragment: receive a message: " + msgID);
				        		break;
			
			        }
			        
				}
		    } 
		}

	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initData();
		initBroadCast();
		
	}
	
	public void initData()
	{
		account = ((MainActivity) getActivity() ).getAccount();
		if (account != null) {
			xmppMgrService = ((MainActivity) getActivity() ).getXmppMgrService();
			if( xmppMgrService != null) {
				contactMLList.addAll(xmppMgrService.getAllRecentContactML(account));
				for(ContactML contactTmp: contactMLList) {
					contactMap.put(contactTmp.getJid(),  contactTmp);
				}
			}
			   Collections.sort(contactMLList,  ContactML.sortByTimeComparator() );
		}
	}
	
	public void initBroadCast()
	{
		notifyReceiver = new NotifyReceiver();
		IntentFilter intentFilter = new IntentFilter(NotifyBroadcast.NOTIFY_TYPE_MESSAGE );
		getActivity().registerReceiver( notifyReceiver , intentFilter);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{  
			
			view = inflater.inflate(R.layout.quikchat, container, false);
			this.inflater = inflater;
			
			 initListView();
			 return view;
	}

	private void initListView()
	{
        listView = (ListView) view.findViewById(R.id.quikchat_listview);

		this.listAdapter = new ArrayAdapter<ContactML>(this.getActivity().getApplicationContext(),
				R.layout.quikchat_list_row, contactMLList) {
			

			
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				ViewHolder holder  = null;
				if (view == null) {
					
					view = (View) inflater.inflate(
							R.layout.quikchat_list_row, null);
					holder = new ViewHolder();
					holder.name = (TextView) view.findViewById(R.id.quikchat_name);
					holder.lastMsgTime  = (TextView) view.findViewById(R.id.quikchat_lastupdate);
					holder.header_img =  (ImageView) view.findViewById(R.id.quikchat_head_image);
					holder.addContact = (Button) view.findViewById(R.id.quikchat_add_contact);
					holder.unfamiliarBlock  = (View) view.findViewById(R.id.quikchat_unfamiliar_block) ;
					holder.lastMsg  = (TextView) view.findViewById(R.id.quikchat_lastmsg) ;
					holder.unreadMsgNumbers =  (TextView) view.findViewById(R.id.quikchat_unread_msg_numbers);
					
					view.setTag(holder);
				} else {
					 holder = (ViewHolder)view.getTag(); ;  	
					
				}
				holder.header_img.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						int i;
						//ViewGroup vg = (ViewGroup) v.getParent().getParent(); 
                        // i = vg.indexOfChild((View)v.getParent()); 
						i = (Integer) v.getTag();
						//showDialog("position: " + i);
						Intent intent=new Intent(getActivity(),  ContactInfoActivity.class);  
						
			              Bundle bundle=new Bundle();  
			              bundle.putString("account",  account.getJid());  
			              bundle.putString("contact", contactMLList.get(i) .getUser());  
			             
			              intent.putExtras(bundle);  
			                
			              startActivity(intent); 
						
					}
					});
				
				holder.addContact.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						int i;
						//ViewGroup vg = (ViewGroup) v.getParent().getParent(); 
                        // i = vg.indexOfChild((View)v.getParent()); 
						i = (Integer) v.getTag();
						if (xmppMgrService != null) {
							Log.d("milian" , " try to add Contact " +  getItem(i).getJid() + "to Friend");
							getItem(i).setType(ContactML.CONTACT_TYPE_FAMILIAR);
							xmppMgrService.requestAddContact(account, getItem(i));
							listAdapter.notifyDataSetChanged();
							Toast.makeText(getActivity(), "添加固定好友: " + getItem(i).getName() , Toast.LENGTH_SHORT).show();
				        	
						}
					}
					});  
				
				holder.name.setText(  getItem(position).getName()  );
				Log.d("milian", "quikChat (" + getItem(position).getName()  + ")  display lastMsgTime: " + getItem(position).getLastMsgTimeStr() );
				if (xmppMgrService != null) {
					int unreadNumbers = 0;
					unreadNumbers = xmppMgrService.getAddedMessageMLSize(account, getItem(position).getJid());
					if (unreadNumbers > 0 ) {
						holder.unreadMsgNumbers.setText("" + unreadNumbers );	
						holder.unreadMsgNumbers.setVisibility(view.VISIBLE);
					} else {
						holder.unreadMsgNumbers.setVisibility(view.INVISIBLE);
					}
					
					MessageML msg = xmppMgrService.getLastMessageML(account, getItem(position).getJid());
					if ( msg != null) {
						if (msg.getType() == MessageML.MSG_TYPE_TEXT) {
							String msgText= msg.getText();
						   if( msgText.length() > 16 ) {
							   msgText = msgText.substring(0, 16) + " ...";
							 }
							holder.lastMsg.setText(msgText  );	
						}
					}
					if (getItem(position).getType() == ContactML.CONTACT_TYPE_UNFAMILIAR ) {
						//holder.unfamiliarBlock.setVisibility(view.VISIBLE);
						//holder.unfamiliarBlock.setFocusable(false);
						//view.setFocusable(true);
						 
						
						holder.unfamiliarBlock.setVisibility(view.VISIBLE);
						//view.setFocusable(true);
						
						//holder.unfamiliarBlock.setClickable(false);
						 
					} else {
						holder.unfamiliarBlock.setVisibility(view.GONE);
					}
					
				} else {
					holder.unreadMsgNumbers.setVisibility(view.GONE);
					
				}
				holder.lastMsgTime.setText(  getItem(position).getLastMsgTimeStr()  );
				holder.header_img.setImageResource(getItem(position).getHeaderImgRes() );
				holder.addContact.setTag(position);
				holder.header_img.setTag(position);
				
				return view;	
				}
			};
			
			listView.setAdapter(this.listAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {
			
						@Override
						public void onItemClick(AdapterView<?> arg0, View clickedView,
								int position, long arg3) {
								Log.d("milian", "quikchat listView item:" + position + "is clicked");
								Intent intent=new Intent(getActivity(), ChatActivity.class);  
	
						              Bundle bundle=new Bundle();  
						              bundle.putString("account",  account.getJid());  
						              bundle.putString("contact", contactMLList.get(position) .getUser());  
						              intent.putExtras(bundle);  
						                
						              startActivity(intent); 
								};
							
						}	);

    
		
	}
	
	/*
	private void showDialog(ContactML ContactML)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
        builder.setTitle( ContactML.getName() );  
        builder.setPositiveButton("OK" , new DialogInterface.OnClickListener()  {

        	   @Override
        	   public void onClick(DialogInterface dialog, int which) {
        	    
        	   }
        	  });
        AlertDialog ad = builder.create();  
   
        ad.show();  
		
	} */
	private void showDialog(String str)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
        builder.setTitle( str );  
        builder.setPositiveButton("OK" , new DialogInterface.OnClickListener()  {

        	   @Override
        	   public void onClick(DialogInterface dialog, int which) {
        	    
        	   }
        	  });
        AlertDialog ad = builder.create();  
   
        ad.show();  
		
	}
	
	public void addContact(ContactML contact) {
		if ( contactMap.get(contact.getJid()) != null) {
			
				    Log.d("milian", "ContactsFragment contain: "  + contact.getJid() + ",  when addContact, so remove it then add the new one !");
					contactMLList.remove(contactMap.get(contact.getJid()) );
					contactMap.remove(contact.getJid());
			}
		contactMap.put(contact.getJid(), contact);
		contactMLList.add(contact);
		
	}
	
	public void removeContact(ContactML contact) {
		if ( contactMap.get(contact.getJid()) != null) {
		 
				    Log.d("milian", "ContactsFragment contain: "  + contact.getJid() + ",  when addContact, so remove it then add the new one !");
					contactMLList.remove(contactMap.get(contact.getJid()));
					contactMap.remove(contact.getJid());
		}
	}
    
}
