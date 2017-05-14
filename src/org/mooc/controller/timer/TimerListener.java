package org.mooc.controller.timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author wuke
 * @date  : 20170414 17:06:46
 * Title  : TimerListener
 * Description : Start timer listener.
 */
public class TimerListener implements ServletContextListener {

	UpdateManager updatemanager = null;
	
	public TimerListener() {
		super();
	}

	/**
	 * Start when the Web Services start
	 */
	public void contextInitialized(ServletContextEvent e) {		
		System.out.println("-------------Start Timer Listener-------------");
		updatemanager = new UpdateManager();
		updatemanager.executeUpdateTimer();
	}

	/**
	 * Call by the container
	 */
	public void contextDestroyed(ServletContextEvent e) {
	}
}