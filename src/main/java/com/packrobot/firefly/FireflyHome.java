package com.packrobot.firefly;

import java.io.File;

import com.hansky.core.util.Props;

public class FireflyHome {

	public FireflyHome() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File ffProp = new File(System.getProperty("user.home") + File.separatorChar + "Hansky" + File.separatorChar + "Firefly" + File.separatorChar + "Client" + File.separatorChar + "global.dat");
		System.out.println(ffProp.toString());
		 if (ffProp.exists()) {
	          Props p = new Props(ffProp);
	          boolean DEBUG = false;
	          if ("true".equals(p.getProperty("WSADDEBUG"))) {
	            DEBUG = true;
	          }


	          String ffhome = p.getProperty("firefly.dir");
	          if (ffhome != null) {
	            System.setProperty("firefly.dir", ffhome);
	          }
	          
	          System.out.println("firefly "+ffhome);
	          System.out.println("start firefly ==================");

	        }
	}

}
