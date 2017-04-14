package org.mooc.controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mooc.main.UpdateManager;

public class StartTimerListener implements ServletContextListener {

	UpdateManager updatemanager = null;

	/**
	 * 创建一个初始化监听器对象，一般由容器调用
	 */
	public StartTimerListener() {
		super();
	}

	/**
	 * 让 Web 程序运行的时候自动加载 Timer
	 */
	public void contextInitialized(ServletContextEvent e) {
		System.out.println("-------------StartTimerListener.init-------------");
		updatemanager = new UpdateManager();
		updatemanager.executeUpdateTimer();
	}

	/**
	 * 该方法由容器调用 空实现
	 */
	public void contextDestroyed(ServletContextEvent e) {
	}
}