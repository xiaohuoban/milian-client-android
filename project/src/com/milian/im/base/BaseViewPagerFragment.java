package com.milian.base;

import java.util.ArrayList;
 
import java.util.List;

import android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;  
import android.support.v4.app.FragmentManager;  
import android.support.v4.app.FragmentPagerAdapter;  
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.TabPageIndicator;

public class BaseViewPagerFragment  extends BaseFragment {
		
	int layout_resource, resource_view_pager, resource_indicator;
	TabPageIndicator tabPageIndicator;
	FragmentPagerAdapter adapter;
	ArrayList< BaseFragment> fragmentList = new ArrayList <BaseFragment> ();
	
	public void addPage(BaseFragment fragment) {
		fragmentList.add(fragment);
		//adapter.notifyDataSetChanged();
	}
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		adapter = new BasePagerAdapter(getActivity().getSupportFragmentManager());
	}
 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		

        
        //layout_resource = R.layout.layout_base_view_pager_tab;
        View view = inflater.inflate(com.milian.baselib.R.layout.layout_base_view_pager_tab, container,false);
        ViewPager pager = (ViewPager) view.findViewById(com.milian.baselib.R.id.pager);
        pager.setAdapter(adapter);
    
        TabPageIndicator indicator = (TabPageIndicator)view.findViewById(com.milian.baselib.R.id.indicator);
        indicator.setViewPager(pager);
        
		return view;
	}

    class BasePagerAdapter extends FragmentPagerAdapter {
        public BasePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return  fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentList.get(position).getTitle();
        }

        @Override
        public int getCount() {
          return fragmentList.size();
        }
    }

	

}
