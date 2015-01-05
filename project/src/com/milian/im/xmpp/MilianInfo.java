package com.milian.im.xmpp;



import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.ThreadFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.xdata.Form;
//import org.jivesoftware.smack.test.SmackTestCase;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import android.util.Log;

/**
 * Tests the DataForms extensions.
 * 
 * @author Gaston Dombiak
 */
public class MilianInfo {

    public MilianInfo() {
       // super(arg0);
    }
    private static MilianInfo instance = new MilianInfo();
    public static  MilianInfo getInstance() {
    	return instance;
     }
    
    /**
     * 1. Create a form to fill out and send it to the other user
     * 2. Retrieve the form to fill out, complete it and return it to the requestor
     * 3. Retrieve the completed form and check that everything is OK
     */
     public boolean setPersonalInfo(XMPPConnection con, String jid, String name) {
        Form formToSend = new Form(Form.TYPE_SUBMIT);
        //Form formToSend = new Form(Form.TYPE_FORM);
        formToSend.setInstructions(
            "set personal info");
        formToSend.setTitle("set name ... info");
        // Add a hidden variable
        FormField field = new FormField("hidden_var");
        field.setType(FormField.TYPE_HIDDEN);
        field.addValue("Some value for the hidden variable");
        formToSend.addField(field);

        field = new FormField("username");
        //field.setLabel("Enter a username for the case");
        //field.setType(FormField.TYPE_TEXT_SINGLE);
	field.addValue(jid);

        field = new FormField("name");
        //field.setLabel("Enter a name for the case");
        //field.setType(FormField.TYPE_TEXT_SINGLE); 
	field.addValue(name);

        formToSend.addField(field);

	MilianInfoIQ iq= new MilianInfoIQ(); //iq.setBody("To enter a case please fill out this form and send it back to me");
	iq.setType(IQ.Type.SET);
    iq.addExtension(formToSend.getDataFormToSend());
    try {
		    PacketCollector collector = con.createPacketCollectorAndSend(iq);
			IQ answer = (IQ) collector.nextResult(3000);
			
			if (answer == null ) {
				return false;
			}
			if(answer.getType() == IQ.Type.RESULT) {
				Log.v("milian", "set name success");
				return true;
			} else {
				Log.v("milian", "set name fail");
				return false;
			}
	
    } catch ( NotConnectedException e) {
    	Log.e("milian",  " NotConnectedException :" + e.getMessage());
    	
    }
    return false;
    };

     public Form getPersonalInfo(XMPPConnection con, String jid) {
        Form formToSend = new Form(Form.TYPE_SUBMIT);
        //Form formToSend = new Form(Form.TYPE_FORM);
        formToSend.setInstructions(
            "get personal info.");
        formToSend.setTitle("get personal info");
        // Add a hidden variable
        FormField field = new FormField("hidden_var");
        field.setType(FormField.TYPE_HIDDEN);
        field.addValue("Some value for the hidden variable");
        formToSend.addField(field);

        field = new FormField("username");
        field.addValue(jid);

        formToSend.addField(field);

        MilianInfoIQ iq= new MilianInfoIQ();  
		iq.setType(IQ.Type.GET);
        iq.addExtension(formToSend.getDataFormToSend());
        try  {
		        PacketCollector collector = con.createPacketCollectorAndSend(iq);
		        IQ answer = (IQ)collector.nextResult(3000);
		        if (answer == null ) {
		    		return null;
		    	}
		    	if(answer.getType()  == IQ.Type.RESULT) {
		    		Log.v("milian", "get Peronal Info success\n");
		    		Form formRecv = Form.getFormFrom(iq);	
		    		Log.v("milian", "name: " + formRecv.getField("name").getValues().get(0) ) ;
		    		return formRecv;
		    	} else if (answer.getType()  == IQ.Type.ERROR){
		    		Log.v("milian", "name:  get personal info Error"   ) ;
		    		
		    	}
    	
        } catch ( NotConnectedException e) {
        	Log.e("milian",  " when get personal Info, NotConnectedException :" + e.getMessage());
        	
        }
	
	
	
	return null ;
    };
    
    
    static  class MilianInfoIQ extends IQ {
 
        public MilianInfoIQ() {
        }

        public String getChildElementXML() {
            StringBuilder buf = new StringBuilder();
            buf.append("<query xmlns=\"milian:iq:info\">");
            buf.append(getExtensionsXML());
            buf.append("</query>");
            return buf.toString();
        }

        /**
         * Returns the form for all search fields supported by the search service.
         *
         * @param con           the current XMPPConnection.
         * @param searchService the search service to use. (ex. search.jivesoftware.com)
         * @return the search form received by the server.
         * @throws XMPPErrorException 
         * @throws NoResponseException 
         * @throws NotConnectedException 
         */
        //public Form getForm(IQ packet) throws NoResponseException, XMPPErrorException, NotConnectedException {
        public Form getForm(IQ packet) {
            //UserSearch search = new UserSearch();
            //search.setType(IQ.Type.GET);
            //search.setTo(searchService);

            //IQ response = (IQ) con.createPacketCollectorAndSend(search).nextResultOrThrow();
            return Form.getFormFrom(packet);
        }
    }



}


