package com.partyanimal.clockwidget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.util.TypedValue;
import android.widget.RemoteViews;

/**
 * This widget display an analogic clock with two hands for hours and
 * minutes.
 */

public class PartyClock extends Service {
    
	private Time mCalendar;
    private float mMinutes;
    private float mHour;
    private float mSeconds;
    
    private float fauxSeconds;
	private float fauxMinutes;
    private float SECONDS_DIVIDED = 1.2289485662266728f; //86400/70304
	private boolean mChanged;
	private Handler mHandler = new Handler();
	private Resources resources;
    Bitmap appWidgetBitmap;
    boolean boolSecond=false;
	
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
           
        buildUpdate();
           
        return super.onStartCommand(intent, flags, startId);
    }
	
	private void buildUpdate() {
        //if (mChanged) {
            mChanged = false;
        //}
		boolean sec = boolSecond; 
		if (sec) {
			boolSecond = false;
		}
		
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.party_clock_widget);
			
		mCalendar = new Time();
		mCalendar.setToNow();
		onTimemChanged();
					
		resources = getApplicationContext().getResources();
		onDraw();
        remoteViews.setImageViewBitmap(R.id.imageView1, appWidgetBitmap);
        
        // Pushing widget update to home screen
        ComponentName widgetComponentName = new ComponentName(this, PartyClockWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetManager.updateAppWidget(widgetComponentName, remoteViews);        
	}
    
	private void onDraw(){
		final int APP_WIDGET_WIDTH_DP = 210;
		final int APP_WIDGET_HEIGHT_DP = 210;
	 
		final int availableWidth = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, APP_WIDGET_WIDTH_DP, resources.getDisplayMetrics()) + 0.5f);
		final int availableHeight = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, APP_WIDGET_HEIGHT_DP, resources.getDisplayMetrics()) + 0.5f);

		int x = availableWidth / 2;
		int y = availableHeight / 2;
		
		appWidgetBitmap = Bitmap.createBitmap(availableWidth, availableHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(appWidgetBitmap);		
		Resources r = getResources();
		
		final Drawable dial = r.getDrawable(R.drawable.clock_background);
			int w = dial.getIntrinsicWidth();
			int h = dial.getIntrinsicHeight();

			boolean scaled = false;


			if (availableWidth < w || availableHeight < h) {
				scaled = true;
				float scale = Math.min((float) availableWidth / (float) w,
						(float) availableHeight / (float) h);
				canvas.save();
				canvas.scale(scale, scale, x, y);
			}
			if (mChanged) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
			}
			dial.draw(canvas);
			
		canvas.save();
		canvas.rotate((mHour / 13.0f) * 360.0f, x, y);
		
		final Drawable hourHand = r.getDrawable(R.drawable.clock_hour);
			if (mChanged) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
			}
			hourHand.draw(canvas);
			canvas.restore();
			
		canvas.save();
		canvas.rotate((mMinutes / 52.0f) * 360.0f, x, y);

		final Drawable minuteHand = r.getDrawable(R.drawable.clock_minute);
			if (mChanged) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
			}
			minuteHand.draw(canvas);
			canvas.restore();
			
		canvas.save();
		
		//canvas.rotate((mSeconds / 52.0f) * 360.0f, x, y);
		canvas.rotate(mSeconds, x, y);
		final Drawable secondHand = r.getDrawable(R.drawable.clock_second);
			//if (mChanged) {
			if (mChanged) {
			w = secondHand.getIntrinsicWidth();
			h = secondHand.getIntrinsicHeight();
			secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
			}
			secondHand.draw(canvas);
			canvas.restore();
		if (scaled) {
			canvas.restore();
		}
	}
	
	
	MyCount counter = new MyCount(10000, 1000);
	public class MyCount extends CountDownTimer{
		public MyCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			counter.start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mCalendar.setToNow();

			int hour = mCalendar.hour;
			int minute = mCalendar.minute;
			int second = mCalendar.second;
			
			mSeconds=6.0f*second;
			boolSecond=true;
			 //mmChanged = true;
			 //PartyClock.this.invalidate();
			 //Toast.makeText(mContext, "text", Toast.LENGTH_LONG).show();
		 }
	}

	
		
	private void onTimemChanged() {
		
		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		hour *= 60;
		minute += hour;
		second += (minute*60);
		
		fauxSeconds = second / SECONDS_DIVIDED;
		fauxMinutes = fauxSeconds / 52.0f;
		
		//mmSeconds = fauxSeconds % 52.0f;
		mMinutes = fauxMinutes / 13.0f;
		mHour = fauxSeconds / 52.0f;
		
		mChanged = true;
	}


	@Override
	public IBinder onBind(Intent intent) {
			return null;
	}

}
