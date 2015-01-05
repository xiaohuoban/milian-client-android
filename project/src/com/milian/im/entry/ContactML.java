package com.milian.im.entry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.RosterPacket;
 
import java.util.ArrayList;
import    java.text.SimpleDateFormat; 
import java.util.Comparator;
import     java.util.Date; 
import java.util.List;

import android.R;

import com.milian.im.ui.*;
import com.milian.im.service.*;

import java.util.ArrayList;
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
import java.util.Comparator;
public  class ContactML  {
        private ContactMgr roster;
        private RosterEntry rosterEntry;
        private long  lastMsgTime;
        private String  lastMsgTimeStr;
        private String  header_img_file;
        private int head_img_res;
        private String userJid;
        private String name;
        private  List<String> groups;
        private int type;
        public final static int CONTACT_TYPE_FAMILIAR = 1;
        public final static int CONTACT_TYPE_UNFAMILIAR = 0;
        
        public ContactML(String userJid,String name, String header_img_file, long lastMsgTime,int head_img_res) {
         //super.RosterEntry(user);
                this.userJid = userJid;
                this.header_img_file = header_img_file;
                this.lastMsgTime = lastMsgTime;
                this.head_img_res = head_img_res;
                this.name = name;
                this.type = CONTACT_TYPE_UNFAMILIAR;
        }
        
        public ContactML(String userJid,String name) {
               // super(user,name,null,null,null,null);
               this.type = CONTACT_TYPE_UNFAMILIAR;
               this.userJid  = userJid;
               this.name = name;
               this.header_img_file = null;
               this.lastMsgTime = 0;
               this.head_img_res = 0;
        }
        static int  cnt  = 0;
        public ContactML(RosterEntry entry, ContactMgr roster, int type) {
               // super(entry.getUser(),entry.getName(),entry.getType(),entry.getStatus(),roster,connection);
                rosterEntry = entry;
                this.type = type;
                this.name = entry.getName();
                if (this.name == null)
                	this.name = entry.getUser().split("@")[0];
                this.userJid = entry.getUser();
                this.roster  = roster;
                
                switch (cnt++) {
                case 0 :
                	this.head_img_res = com.milian.im.ui.R.drawable.header_bg1 ;
                	break;
                	
                case 1:
                	this.head_img_res = com.milian.im.ui.R.drawable.header_bg2 ;
                	break;
                case 2:
                	this.head_img_res = com.milian.im.ui.R.drawable.header_bg3;
                	break;
                default :
                	this.head_img_res = com.milian.im.ui.R.drawable.header_bg4 ;
                	cnt = 0;
                   break;
                }
                
        }
        
        public ContactML(String userJid)
        {
                //super(null, name, null, null,null,null);
            this.type = CONTACT_TYPE_UNFAMILIAR;
            this.userJid = userJid;
            this.name = userJid.split("@")[0];
            switch (cnt++) {
            case 0 :
            	this.head_img_res = com.milian.im.ui.R.drawable.header_bg1 ;
            	break;
            	
            case 1:
            	this.head_img_res = com.milian.im.ui.R.drawable.header_bg2 ;
            	break;
            case 2:
            	this.head_img_res = com.milian.im.ui.R.drawable.header_bg3;
            	break;
            default :
            	this.head_img_res = com.milian.im.ui.R.drawable.header_bg4 ;
            	cnt = 0;
               break;
            }
        }
        
        public static Comparator<ContactML> timeCompare =  (new Comparator <ContactML> (){
    		public int compare(ContactML obj1,ContactML obj2){ 
  			  ContactML contact1=(ContactML)obj1;
  			  ContactML contact2=(ContactML)obj2;
  			  //Log.d("milian", contact1.getJid()  + " compare "  + contact2.getJid() );
  			  return  (int) ( contact2.getLastMsgTime() - contact1.getLastMsgTime());
  			    
  			} ;
  		}  );
        public static  Comparator sortByTimeComparator(){
        	return timeCompare; 
        }
        
        public static Comparator<ContactML> nameCompare =  (new Comparator<ContactML>  (){
    		public int compare(ContactML obj1,ContactML obj2){ 
  			  ContactML contact1=(ContactML)obj1;
  			  ContactML contact2=(ContactML)obj2;
  			  return (contact1.getName().compareTo(contact2.getName() ));
  			    
  			} ;
  		}  );
        public static  Comparator sortByNameComparator(){
            return nameCompare;
            }
        
        public  static String getCleanJid (String jid){
        	if (jid.contains("/") )  {
        		return jid.split("/")[0];
        	}
        	return jid;
        	
        }
        public String getName() {
                return name;
        }
        public void setName(String newName) {
                this.name = newName;
                
                
                try {
                        rosterEntry.setName(name);
                //super.setName(newName);
                } catch( NotConnectedException e) {
                        e.printStackTrace();
                } 
        }
        public String getUser(){
                return userJid;
        }
        public String getJid(){
            return getUser();
    }
        public long getLastMsgTime() {
                return lastMsgTime;
        }
        
        public long setLastMsgTime(long  newTime) {
            this.lastMsgTime = newTime;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");       
        	Date    tdate    =   new    Date(this.lastMsgTime );
        	String    lastMsgTimeStr    =    dateFormat.format(tdate); 
        	 this.lastMsgTimeStr = lastMsgTimeStr;
            return lastMsgTime;
    }
        
        public String getLastMsgTimeStr() {
              return lastMsgTimeStr; 
    }
        
        public String getHeaderImgFile() {
                return header_img_file;
        }
        public int getHeaderImgRes() {
                return head_img_res;
        }
        
        public int getType() {
        	
        	return type;
        }
 public void setType(int type) {
        	
        	this. type = type;
        }
}
