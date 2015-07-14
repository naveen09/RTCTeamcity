package com.naveen.rtcteamcity.service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SchedulerBroadCastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startServiceIntent = new Intent(context, SchedulerService.class);
		context.startService(startServiceIntent);
	}

}
