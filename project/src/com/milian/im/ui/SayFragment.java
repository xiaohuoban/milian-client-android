package com.milian.im.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.app.Fragment;  
import android.view.ViewGroup;  


public class SayFragment extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{  
     
        return inflater.inflate(R.layout.say, container, false);
    }
}
