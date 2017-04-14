package org.mooc.controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mooc.main.InitializeMooc;
import org.mooc.main.UpdateManager;

/**
 * 
 * @author wuke
 * @date  : 2017年04月14日 下午5:06:46
 * Title  : InitListener
 * Description : Initialization and start timer listener.
 */
public class InitListener implements ServletContextListener {

	UpdateManager updatemanager = null;
	
	public InitListener() {
		super();
	}

	/**
	 * Start when the Web Services start
	 */
	public void contextInitialized(ServletContextEvent e) {
		InitializeMooc.initMooc(); // Initialization of the whole service
		
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