package com.milian.im.service;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterListener;

import java.util.concurrent.ConcurrentHashMap;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import com.milian.im.entry.*;
import com.milian.im.service.*;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.RosterPacket.Item;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock;

public class ContactMgr{
    private XMPPConnection connection = null;
    private Roster roster = null;
    private ConcurrentHashMap<String, ContactML> contacts = new ConcurrentHashMap<String, ContactML>();
    private ArrayList < ContactML>  addedList = new  ArrayList < ContactML>();
    private ArrayList < ContactML>  updatedList = new  ArrayList < ContactML>();
    private ArrayList < ContactML>  removedList = new  ArrayList < ContactML>();
    
    //recent Contacts: include unfamiliar contacts, readonly
    private ConcurrentHashMap<String, ContactML> recentContacts = new ConcurrentHashMap<String, ContactML>();
    private ArrayList < ContactML>  addedRecentList = new  ArrayList < ContactML>();
    
    
    private String version = null;
    private RosterListener mRosterListener;
    AccountMLMgr accountMLMgr;

    public ContactMgr(AccountMLMgr accountMLMgr) {
        
        this.accountMLMgr = accountMLMgr;
        resetRoster();
    }
    public boolean hasContact(String jid) {
    	    String cleanJid = getCleanJid(jid);
            return contacts.containsKey(cleanJid);
    }
    
    public String getCleanJid (String jid){
    	if (jid.contains("/") )  {
    		return jid.split("/")[0];
    	}
    	return jid;
    	
    }
    
    public boolean hasRecentContact(String jid) {
        String cleanJid = getCleanJid(jid);
        return recentContacts.containsKey(cleanJid);
}

    public Roster resetRoster(){
        connection = accountMLMgr.getXMPPConnection();
        if ( connection ==null )
            return null;
        roster = connection.getRoster();
        return roster;
    }
    public ContactML getContact(String jid) {
            String cleanJid = getCleanJid(jid);
            if (contacts.containsKey(cleanJid)) {
                    return contacts.get(cleanJid);
            } 
            return null;
    }
    
    public ContactML getRecentContact(String jid) {
        String cleanJid = getCleanJid(jid);
        return recentContacts.get(cleanJid);
 
}
    
    public List<ContactML> getContacts() {
            return new ArrayList<ContactML>(this.contacts.values());
    }
    
    public ContactMgr(AccountMLMgr accountMLMgr , XMPPConnection connection){
        this.accountMLMgr  = accountMLMgr;
        this.roster = connection.getRoster();
        this.connection=connection;
        addSelfRosterListener();
    }
    
    public void addSelfRosterListener(){
            if(null != mRosterListener) {
            	return;
            }
            mRosterListener = new RosterListener() {
                
                    public void entriesAdded(Collection<String> addresses) {
                            for (String entry : addresses) {
                                    RosterEntry rosterEntry = roster.getEntry(entry);
                                    addFamiliarContact(rosterEntry);   //Familiar is fixed contact
                            }
                            
                            NotifyBroadcast.notify(accountMLMgr.getAccount().getJid(),  NotifyBroadcast.CONTACT_ADD__MSG);;
                    } 
                    /*
                    public void entriesAdded(Collection<String> addresses) {
                        
                    }*/

                    public void entriesDeleted(Collection<String> entries) {
                            for (String entry : entries) {
                                removeContact(entry);
                            }
                            NotifyBroadcast.notify(accountMLMgr.getAccount().getJid(),  NotifyBroadcast.CONTACT_REMOVE__MSG);;
                    }

                    public void entriesUpdated(Collection<String> entries) {
                            for (String entry : entries) {
                                    RosterEntry rosterEntry = roster.getEntry(entry);
                                    updateContact(rosterEntry);
                            }
                            NotifyBroadcast.notify(accountMLMgr.getAccount().getJid(),  NotifyBroadcast.CONTACT_UPDATE__MSG);;
                    }
                    public void presenceChanged(Presence presence) {
                    }
            };
            addRosterListener(mRosterListener);
    }
    
    public boolean setConnection(XMPPConnection conn ) {
    	this.connection = conn;
        this.roster = conn.getRoster();
        addSelfRosterListener();
    	return true;
    }

    public void addRosterListener(RosterListener rosterListener) {
        if(null!=this.roster){
            this.roster.addRosterListener(rosterListener);
        }
    }
    private void addToAddedList(ContactML newContact){
        synchronized(addedList){
            addedList.add(newContact);
        }
    }
    
    private void addToAddedRecentList(ContactML newContact) {
        synchronized(addedRecentList){
        	addedRecentList.add(newContact);
        }
    }
    
    private void addToUpdatedList(ContactML newContact){
        synchronized(updatedList){
            updatedList.add(newContact);
        }
    }
    
    private void addToRemovedList(ContactML newContact){
        synchronized(removedList){
            removedList.add(newContact);
        }
    }
    
    //Familiar only add to fixed contact
    public void addFamiliarContact(RosterEntry entry){
        if(null==entry){return;}
        String cleanJid = entry.getUser().split("/")[0];
        if(!hasContact(cleanJid)){
            ContactML newContact=new ContactML(entry,this, ContactML.CONTACT_TYPE_FAMILIAR);
            contacts.put(cleanJid,newContact);
            addToAddedList(newContact);
        }
    }
    
    //
    public void addRecentContact(ContactML contact) {
    	if (recentContacts.get( contact.getJid() ) != null) {
    		Log.e("milian", "recentContacts already cotain : " + contact.getJid());
    		return;
    	}
       recentContacts.put(contact.getJid(),   contact);
       addToAddedRecentList(contact);
    }
    
    public int getRecentContactSize() {
         return recentContacts.size();
     }
    public int getAddedRecentContactSize() {
    	int size;
    	synchronized(addedRecentList){
        	size = addedRecentList.size();
        }
    	return size;
    }
    
    
    
    // unfamiliarContact only add to Recent Contact
    public void addUnfamiliarContact(RosterEntry entry){
        if(null==entry){return;}
        String cleanJid = entry.getUser().split("/")[0];
        if(!hasContact(cleanJid)){
            ContactML newContact=new ContactML(entry,this, ContactML.CONTACT_TYPE_UNFAMILIAR);
            //contacts.put(cleanJid,newContact);
            addRecentContact(newContact);
        }
    }
    
    //this function will process by FAMILIAR OR UNFAMILIAR
    public void addContact(String jid, int type ){
        if(null==jid)   {
            return;
        }
        String cleanJid = getCleanJid(jid);
        switch (type) {
        case  ContactML.CONTACT_TYPE_FAMILIAR:
        if(!hasContact(cleanJid)) {
            ContactML newContact=new ContactML(cleanJid);
            newContact.setType(ContactML.CONTACT_TYPE_FAMILIAR);
            contacts.put(cleanJid, newContact);
            addToAddedList(newContact);
        }
        break;
        case  ContactML.CONTACT_TYPE_UNFAMILIAR:
        	if(!hasRecentContact(cleanJid)) {
                ContactML newContact=new ContactML(cleanJid);
                newContact.setType(ContactML.CONTACT_TYPE_UNFAMILIAR);
                recentContacts.put(cleanJid, newContact);
                addToAddedRecentList(newContact);
            }
        	break;
        }
    }
    
    public	boolean addRecentContactFromFixedContact(String contactJid)
    {
    	String cleanJid = ContactML.getCleanJid(contactJid);
    	ContactML contact = contacts.get(cleanJid) ;
    	if (contact == null)
    		return false;
    	addRecentContact(contact);
    	
    	return true;
    }
    
    public void removeContact(String userjid){
        String cleanJid = getCleanJid(userjid);
        contacts.remove(cleanJid); 
        
        ContactML newContact=new ContactML(userjid, cleanJid);
        addToRemovedList(newContact);
    }
    
    public void removeRecentContact(String userjid){
    	 String cleanJid = getCleanJid(userjid);
        recentContacts.remove(cleanJid); 
        
    }
    
    
    public void updateContact(RosterEntry entry){
        
        
    }
    public void createContact(String user, String name, String[] groups) throws Exception{
        if(null!=this.roster){
            this.roster.createEntry(user,name,groups);
        }
    }
    
    public void createContact(String user, String name) throws Exception{
        if(null!=this.roster){
            this.roster.createEntry(user,name,null);
        }
    }
    
    public boolean requestAddContact(ContactML contact){
    	if (contacts.get( contact.getUser() ) != null) {
    		Log.w(this.getClass().getName(),contact.getUser() +  " is  in contactsMap, we remove it  ");
    		contacts.remove( contact.getUser() );
    	}
        try{
        	
            createContact(contact.getUser(),contact.getName());
            contacts.put(contact.getUser(),  contact);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean requestRandContact(){
        return true;
    }
    public boolean requestRemoveContact(ContactML contact){
    	if(null!=this.roster){
    		try {
    			this.roster.removeEntry(this.roster.getEntry(contact.getUser()));
    		} catch (NotLoggedInException e) {
    			Log.e(this.getClass().getName(),  "NotLoggedInException: " + e.getMessage());
    			e.printStackTrace();
    		} catch (NoResponseException e) {
    			Log.e(this.getClass().getName(),  "NoResponseException: " + e.getMessage());
    			e.printStackTrace();   			
    		} catch (XMPPErrorException e) {
    			Log.e(this.getClass().getName(),  "XMPPErrorException: " + e.getMessage());
    			e.printStackTrace();   		
     		} catch (NotConnectedException e ) {
     			Log.e(this.getClass().getName(),  "NotConnectedException: " + e.getMessage());
    			e.printStackTrace();   		
     		} finally {
     			contacts.remove(contact.getUser());
     		}
        } 
        return true;
    }
    public List<ContactML> getAllContactML(){
        return getContacts();
    }
    
    public List<ContactML> getAllRecentContactML(){
    	return new ArrayList<ContactML>(this.recentContacts.values());
    }
    
    public List<ContactML> getAddedRecentContactML( ){
        List < ContactML> list;
        synchronized(addedRecentList) {
            list =new ArrayList<ContactML>(this.addedRecentList);
            addedRecentList.clear();
        }
        return list;
        
    }
    
    public List<ContactML> getAddedContactML( ){
        List < ContactML> list;
        synchronized(addedList){
            list =new ArrayList<ContactML>(this.addedList);
            addedList.clear();
        }
        return list;
        
    }
    public List<ContactML> getUpdatedContactML( ){
        List < ContactML> list;
        synchronized(updatedList){
            list =new ArrayList<ContactML>(this.updatedList);
            this.updatedList.clear();
        }
        return list;
    }
    public List<ContactML> getRemovedContactML( ){
        List < ContactML> list;
        synchronized(removedList){
            list = new ArrayList<ContactML>(this.removedList);
            this.removedList.clear();
        }
        return list;
    }
    public ContactML getContact(){
        return null;
    }
    
    public void release () {
    	if(null != this.roster){
            this.roster.removeRosterListener(mRosterListener);
        }
    	contacts.clear();
    	 synchronized(removedList){
    		 removedList.clear();
    	 }
    	 synchronized(addedList){
    		 addedList.clear();
    	 }
    	 synchronized(removedList){
    		 removedList.clear();
    	 }
    	 
    }
}
