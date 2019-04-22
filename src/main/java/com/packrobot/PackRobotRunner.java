package com.packrobot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.beust.jcommander.JCommander;

public class PackRobotRunner {
	
	private static final Logger _logger = LoggerFactory.getLogger(PackRobotRunner.class);
	
	public static ApplicationContext context;
	PackRobotRunnerArgs runnerArgs = new PackRobotRunnerArgs();
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args)  {
		try{
			PathUtils.getInstance();
			PackRobotRunner runner=new PackRobotRunner();
			new JCommander(runner.runnerArgs, args);
			//List Environment Variables
			runner.listEnvVars();
			runner.init(args);
			runner.execute(args);
		}catch(Exception e){
			e.printStackTrace();
		}
		return;
		
	}
	
	//Initialization ApplicationContext for Project
	public void init(String[] args){
		_logger.info("Application dir "+System.getProperty("user.dir"));

		_logger.info("Application Context Configuration XML File  "+runnerArgs.config);
		if(runnerArgs.config == null) {
			context = new FileSystemXmlApplicationContext(new String[] {"/assembly/applicationContext.xml"});
		}else {
			context = new FileSystemXmlApplicationContext(new String[] {"/assembly/"+runnerArgs.config});
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public int execute(String[] args) throws Exception{
		_logger.info("execute...");
		
		ArrayList<BusinessTask> businessTaskList= (ArrayList<BusinessTask>)context.getBean("businessTask", ArrayList.class);
		
		for(BusinessTask bt : businessTaskList){
			bt.execute();
		}
		
		return 1;
	}
	
	   public void listEnvVars(){
	      _logger.info("----------------------------------------------------------------------------------------------------");
	      _logger.info("List Environment Variables ");
	      Map<String, String> map = System.getenv();
	        for(Iterator<String> itr = map.keySet().iterator();itr.hasNext();){
	            String key = itr.next();
	            _logger.info(String.format("%-24s", key) + "   =" + map.get(key));
	        }   
	       // _logger.info("APP_HOME"+"   =   " + PathUtils.getInstance().getWebRoot());
	        _logger.info("----------------------------------------------------------------------------------------------------");
	   }
	   
}
