package com.osdma.milestones;


import com.example.osdma.R;
import com.osdma.milestones.adapters.ImageAdapter;
import com.osdma.milestones.utils.Dataloader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class ImageViewer extends Activity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_viewer);
		
		ViewPager pager = (ViewPager) this.findViewById(R.id.imageViewer);
		PagerAdapter imageViewerAdapter = new ImageAdapter(Dataloader.arrayList,this);
		int position = getIntent().getExtras().getInt("POSITION", 0);
		pager.setAdapter(imageViewerAdapter);
		pager.setCurrentItem(position);
	}
}
