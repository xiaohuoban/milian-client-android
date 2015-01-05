package com.milian.im.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.app.AlertDialog;  
import android.content.DialogInterface;  

import com.milian.im.entry.AccountML;
import com.milian.im.entry.ContactML;
import com.milian.im.service.*;

import java.util.Iterator;  



public class ContactsFragment extends Fragment {

	private ListView listView;
	private ArrayAdapter<ContactML> listAdapter;	

	LayoutInflater inflater ;
	View  view;
	
	private List<ContactML> contactMLList = new ArrayList<ContactML>();
	private HashMap <String , ContactML > contactMap = new HashMap <String, ContactML > ();
	private AccountML account;
	private XmppMgrService xmppMgrService;

	private NotifyReceiver notifyReceiver;
	private MainActivity activity;
	  class ViewHolder {  
			
		    public TextView lastMsgTime;  
		    public TextView name;  
		    public ImageView header_img;
		    public ViewHolder() { };
		}  
	  
		public class NotifyReceiver extends BroadcastReceiver {
			/*
			public NotifyReceiver()
			{
				
			} */
			public void onReceive(Context context, Intent intent) { 
				if ( intent.getAction().equals(NotifyBroadcast.NOTIFY_TYPE_MESSAGE)) {
			        int msgID = intent.getIntExtra(NotifyBroadcast.MSG_ID,  0);
			        
			        account = ((MainActivity) getActivity() ).getAccount();
					if (account != null) {
						xmppMgrService = ((MainActivity) getActivity() ).getXmppMgrService();
					}
					if (xmppMgrService == null) {
						Log.v("ContactsFragment",  "receive broadcast msg, but xmppMgrService is null");
						return;
					}
					
			        switch ( msgID) {
			        	
				        case NotifyBroadcast.ACCOUNT_REMOVE_MSG:
				        	contactMLList.clear();
				        	contactMap.clear();
				        	listAdapter.notifyDataSetChanged();
				        	break;
			        	
				        case NotifyBroadcast.CONTACT_ADD__MSG:
				        	List<ContactML> addList =  xmppMgrService.getAddedContactML(account);
				        	if (addList == null){
				        		Log.v("ContactsFragment",  "receive broadcast msg, but addList is null");
				        		return;
				        	}
				        	for(int i = 0; i <addList.size(); i++)   {  
				        		
				        		addContact(addList.get(i) );  
				            }  
				        	Collections.sort(contactMLList,  ContactML.sortByNameComparator() );
				        	listAdapter.notifyDataSetChanged();
				        	break;
				        case NotifyBroadcast.CONTACT_UPDATE__MSG:
				        	List<ContactML> updateList =  xmppMgrService.getUpdatedContactML(account);
				        	if (updateList == null){
				        		Log.v("ContactsFragment",  "receive broadcast msg, but addList is null");
				        		return;
				        	}
				        	for(int i = 0; i <updateList.size(); i++)   {  
				        		/*
				        		for(int j= 0; j < contactMLList.size(); j++ ) {
				        			if ( contactMLList.get(j).getUser().equals(updateList.get(i).getUser()) )
				        				contactMLList.remove(j);
				        				contactMLList.add(updateList.get(i) );  
				        				break;
				        		} */
				        		addContact(updateList.get(i));
				        		
				        		
				            }  
				        	Collections.sort(contactMLList,  ContactML.sortByNameComparator() );
				        	listAdapter.notifyDataSetChanged();
				        	
				        	break;      
				        case NotifyBroadcast.CONTACT_REMOVE__MSG:
				        	List<ContactML> removeList =  xmppMgrService.getRemovedContactML(account);
				        	if (removeList == null){
				        		Log.v("ContactsFragment",  "receive broadcast msg, but addList is null");
				        		return;
				        	}
				        	for(int i = 0; i <removeList.size(); i++)   {  
				        		 
				        			 removeContact(contactMLList.get(i) );
				        				 
				        		
				            }  
				        	Collections.sort(contactMLList,  ContactML.sortByNameComparator() );
				        	listAdapter.notifyDataSetChanged();
				        	break;      

				        	default:
				        		Log.e("chatActivity", "receive a message: " + msgID);
				        		break;
			
			        }
			        
				}
		    } 
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
    
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		activity = (MainActivity) getActivity();
		initData();
		initBroadCast();
	}
	public void initBroadCast()
	{
		notifyReceiver = new NotifyReceiver();
		Log.d("milian", "ContactsFragment registerReceiver");
		IntentFilter intentFilter = new IntentFilter(NotifyBroadcast.NOTIFY_TYPE_MESSAGE );
		getActivity().registerReceiver( notifyReceiver , intentFilter);
	}
	
	public void initData()
	{
	
		int hi =  R.drawable.header_bg1;
		/*	ContactML rf2 = new ContactML("LiSi", "/tmp", "2014-02-08 12:30", R.drawable.header_bg2);
		ContactML rf3 = new ContactML("LiuFei", "/tmp", "2014-03-08 12:30", R.drawable.header_bg3);
		ContactML rf4= new ContactML("GuFei", "/tmp", "2014-04-08 12:30", R.drawable.header_bg4);
		ContactML rf5 = new ContactML("XiaoMing", "/tmp", "2014-05-08 12:30", R.drawable.header_bg1);
		ContactML rf6 = new ContactML("PeiPei", "/tmp", "2014-06-08 12:30", R.drawable.header_bg2);
		ContactML rf7 = new ContactML("WangChang", "/tmp", "2014-07-08 12:30", R.drawable.header_bg3);
		ContactML rf8 = new ContactML("YuShi", "/tmp", "2014-08-08 12:30", R.drawable.header_bg4);
		contactMLList.add(rf1);
		contactMLList.add(rf2);
		contactMLList.add(rf3);
		contactMLList.add(rf4);
		contactMLList.add(rf5);
		contactMLList.add(rf6);
		contactMLList.add(rf7);
		contactMLList.add(rf8);	 */
		account = ((MainActivity) getActivity() ).getAccount();
		if (account != null) {
			xmppMgrService = ((MainActivity) getActivity() ).getXmppMgrService();
			if( xmppMgrService != null) {
				contactMLList.addAll(xmppMgrService.getAllContactML(account));
				for(ContactML contactTmp: contactMLList) {
					contactMap.put(contactTmp.getJid(),  contactTmp);
				}
			}
			Collections.sort(contactMLList,  ContactML.sortByNameComparator() );
		}
	}
	
 
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{  
			
			view = inflater.inflate(R.layout.contacts, container, false);
			this.inflater = inflater;
			
			 initListView();
			 initAddContactEvent();
			 return view;
	}
	
	public void initAddContactEvent()
	{
		Button button = (Button) view.findViewById(R.id.contacts_search_button);
		final EditText editText = (EditText)   view.findViewById(R.id.contacts_search_text);
		button.setOnClickListener(
				new View.OnClickListener() {

					String jid = "" ;
					public void onClick(View v) 
					{
						 
						jid = editText.getText().toString().trim();
						if (jid.contains("@") == false)
							jid =   editText.getText().toString() + "@" + account.getDomain() ;
						if (contactMap.containsKey(jid)) {
							Log.d("milian", jid + " is in Contact List, don't  add it again ");
							return ;
						}
						ContactML contact = new ContactML(jid, editText.getText().toString() );
						
						contact.setType(ContactML.CONTACT_TYPE_FAMILIAR);
						XmppMgrService service = activity.getXmppMgrService();
						if (service != null)
							service.requestAddContact(account, contact);
						else
							Log.e(this.getClass().getName(), "can't get XmppMgrService");
					}
					}
				);
		
	}

	private void initListView()
	{
        listView = (ListView) view.findViewById(R.id.contacts_listview);

		this.listAdapter = new ArrayAdapter<ContactML>(this.getActivity().getApplicationContext(),
				R.layout.contacts_list_row, contactMLList) {
			

			
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				ViewHolder holder  = null;
				if (view == null) {
					
					view = (View) inflater.inflate(
							R.layout.contacts_list_row, null);
					holder = new ViewHolder();
					holder.name = (TextView) view.findViewById(R.id.contacts_name);
					holder.lastMsgTime  = (TextView) view.findViewById(R.id.contacts_lastupdate);
					holder.header_img =  (ImageView) view.findViewById(R.id.contacts_head_image);
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
						Intent intent=new Intent(getActivity(), ContactInfoActivity.class);  
						
			              Bundle bundle=new Bundle();  
			              //bundle.putString("arg", "position is :");  
			              account = ((MainActivity) getActivity() ).getAccount();
			              bundle.putString("account",  account.getJid());  
			              bundle.putString("contact",  contactMLList.get(i).getUser() );  
			              intent.putExtras(bundle);  
			                
			              startActivity(intent); 
					}
					});
				holder.name.setText(  getItem(position).getName()  );
				holder.lastMsgTime.setText(  getItem(position).getLastMsgTimeStr()  );
				holder.header_img.setImageResource(getItem(position).getHeaderImgRes() );
				holder.header_img.setTag(position);
				
				return view;	
				}
			};
			listView.setAdapter(this.listAdapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {
			
						@Override
						public void onItemClick(AdapterView<?> arg0, View clickedView,
								int position, long arg3) {
								
								Intent intent=new Intent(getActivity(), ContactInfoActivity.class);  
	
						              Bundle bundle=new Bundle();  
						              account = ((MainActivity) getActivity() ).getAccount();
						              Log.d("milian",  "position: " + position + "AccountJid: " +  account.getJid());
						              bundle.putString("account",  account.getJid());  
						              Log.d("milian",  "position: " + position + "ContactJid: " +  contactMLList.get(position).getUser());
						              bundle.putString("contact",  contactMLList.get(position).getUser() );    
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
}
