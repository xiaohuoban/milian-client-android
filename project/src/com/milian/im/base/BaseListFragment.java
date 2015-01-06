package com.milian.im.base;

import java.io.Serializable;

import android.R;
import android.app.Activity;
import android.support.v4.app.Fragment;  
import android.support.v4.app.FragmentManager;  
import android.support.v4.app.FragmentPagerAdapter;  
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;


public class BaseListFragment  extends BaseFragment {
	ListView listView;
	int  list_resource;
	
	public void setListResource(int list_resource) {
		this.list_resource = list_resource;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(com.milian.baselib.R.layout.layout_demo, container,false);  
         return view;
        
    }  
	
	
	
	
	
	
	
	
}
