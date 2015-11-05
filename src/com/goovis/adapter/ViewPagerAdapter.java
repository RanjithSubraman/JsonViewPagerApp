package com.goovis.adapter;

import java.util.ArrayList;

import com.goovis.imagecache.ImageLoader;
import com.goovis.windows.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ViewPagerAdapter extends PagerAdapter {
	private ImageLoader imageLoader;
	Context context;
	private ArrayList<String> imageList;
	LayoutInflater inflater;
	private int numberofimages;

	public ViewPagerAdapter(Context context, ArrayList<String> imageList,
			int numberofimages) {
		this.context = context;
		this.imageList = imageList;
		this.numberofimages = numberofimages;
		imageLoader = new ImageLoader(context);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((RelativeLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		ImageView imgflag;

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View itemView = inflater.inflate(R.layout.viewpager_item, container,
				false);
		imgflag = (ImageView) itemView.findViewById(R.id.flag);

		imageLoader.DisplayImage(imageList.get(position), imgflag);
		imageLoader.clearCache();
		
		//imgflag.setImageBitmap(imageLoader.getBitmap(imageList.get(position)));

		((ViewPager) container).addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}

	@Override
	public int getCount() {
		return numberofimages;
	}
}
