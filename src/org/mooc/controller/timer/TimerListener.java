package org.mooc.controller.timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author wuke
 * @date  : 2017年04月14日 下午5:06:46
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