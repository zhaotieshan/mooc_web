package org.mooc.controller.timer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** 
 * @author wuke
 * @date  : 20170411 20:55:46
 * Title  : UpdateManager
 * Description : 
 */
public class UpdateManager {
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private static final long Rate = 24 * 60 * 60 * 1000;
	
	/**
	 * Update at 23:50:00 everyday
	 */
	public void executeUpdateTimer() {
		long initDelay = getTimeMillis("23:50:00") - System.currentTimeMillis();
		initDelay = initDelay > 0 ? initDelay : Rate + initDelay;
		if (scheduler.isShutdown()) {
			scheduler = Executors.newScheduledThreadPool(1);
			scheduler.scheduleAtFixedRate(new Update(), initDelay, Rate, TimeUnit.MILLISECONDS);
		} else {
			scheduler.scheduleAtFixedRate(new Update(), initDelay, Rate, TimeUnit.MILLISECONDS);
		}
	}
	
	public void stop() {
		scheduler.shutdown();
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	private static long getTimeMillis(String time) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
			Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
			return curDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
