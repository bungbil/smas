package de.forsthaus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

// @WebListener --> no need for declaration in web.xml
public class InitApplicationWorkspace implements ServletContextListener {

	// Scheduler for periodically starts a db cleaning job
	private ScheduledExecutorService scheduler;

	@Override
	public void contextInitialized(ServletContextEvent sce) {

		// init the ApplicationWorkspace
		String s = sce.getServletContext().getRealPath("/");
		ApplicationWorkspace.getInstance().setApplicationRealPath(s);
		System.out.println("###### ==> InitApplicationWorkspace -> RealPath=" + s);

		// scheduler period
		//int period = new Integer(1);

		//scheduler = Executors.newSingleThreadScheduledExecutor();
		//scheduler.scheduleAtFixedRate(new CleanDemoDataParsingJob(), 0, period, TimeUnit.HOURS);
		//System.out.println("###### ==> Scheduler for db cleaing jobs started every " + period + " hour.");

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		scheduler.shutdownNow();
	}

}
