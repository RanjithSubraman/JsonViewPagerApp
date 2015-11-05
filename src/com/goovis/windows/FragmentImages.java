package com.goovis.windows;

import java.util.ArrayList;

import com.goovis.adapter.ViewPagerAdapter;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentImages extends Fragment {
	private ViewPager viewPager;
	private ArrayList<String> imageList;
	private ViewPagerAdapter adapter;

	public FragmentImages() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View rootView = inflater.inflate(R.layout.viewpager_main, container,
				false);

		imageList = new ArrayList<String>();

		// Locate the ViewPager in viewpager_main.xml
		viewPager = (ViewPager) rootView.findViewById(R.id.pager);
		Bundle args = getArguments();
		if (args != null && args.containsKey("imageList")) {
			imageList = args.getStringArrayList("imageList");
		}
		adapter = new ViewPagerAdapter(getActivity(), imageList,
				imageList.size());
		viewPager.setAdapter(adapter);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		return rootView;
	}
}