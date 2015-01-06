package com.milian.im.base;

import java.io.Serializable;
 



import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v4.app.Fragment;  
import android.support.v4.app.FragmentManager;  
import android.support.v4.app.FragmentPagerAdapter;  
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.lidroid.xutils.*;;

public class BaseFragment  extends Fragment {
	int layoutResource;
	int menuResource;
	String title;
	private Activity activity;
	public  BaseFragment() {
		
	}
	public static BaseFragment newInstance(int layoutResource, int menuResource) {
		BaseFragment fragment = new BaseFragment();
		//fragment.setLayout(layoutResource);
		//fragment.setMenuResource(menuResource);
		return fragment;
	}
	
	public void setLayout(int layoutResource){
		this.layoutResource = layoutResource;
	}
public void setMenuResource(int menuResource){
	this.menuResource = menuResource;
	}
	
public void setTitle(String title) {
	
	this.title = title;
}
public String getTitle() {
	
	return this.title;
}
	 
	 
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	//onCreateView will be implicated in child class
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(layoutResource, container, false); // 加载fragment布局      
		ViewUtils.inject(this, view);
		return view;
	}

	/*
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
 
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onResume() {
		super.onResume();

	}
	*/
/*
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	      // inflater.inflate(menuResource, menu);
	        super.onCreateOptionsMenu(menu,inflater);
	} */



 

	
	
}
