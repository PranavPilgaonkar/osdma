package com.osdma.milestones.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.osdma.R;
import com.osdma.milestones.ImageItem;

public class ImageAdapter extends PagerAdapter{

	private ArrayList<ImageItem> arrayList;
	private Context context;

	
	
	public ImageAdapter(ArrayList<ImageItem> list,Context context) {
		// TODO Auto-generated constructor stub
		this.arrayList = list;
		this.context = context;
	}
	
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.setPrimaryItem(container, position, object);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		// TODO Auto-generated method stub
		return view == ((ImageView) obj);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
//		LayoutInflater layoutInflater = LayoutInflater.from(context);
//		View imager = layoutInflater.inflate(R.layout.image,null);
		ImageView imageView = new ImageView(context);
		
//		ImageView imageView = (ImageView) imager.findViewById(R.id.imager);
//		int padding = context.getResources().getDimensionPixelSize(R.dimen.)
//		imageView.setPadding(5, 5,5, 5);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setImageBitmap(arrayList.get(position).getImage());
		((ViewPager) container).addView(imageView,0);
		
		return imageView;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView((ImageView) object);
	}

}
