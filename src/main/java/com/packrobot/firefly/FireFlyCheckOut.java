package com.packrobot.firefly;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hansky.apps.firefly.client.Arguments;
import com.hansky.apps.firefly.client.cmdline.cmd.Bringover;
import com.hansky.apps.firefly.client.cmdline.cmd.Create;
import com.hansky.apps.firefly.client.cmdline.cmd.FireflyBringover;
import com.hansky.apps.firefly.client.core.ClientUtil;
import com.hansky.apps.firefly.intf.client.ILocalWorkspace;

public class FireFlyCheckOut  implements Runnable{
	private static final Logger _logger = LoggerFactory.getLogger(FireFlyCheckOut.class);
	 ILocalWorkspace lw;
	
	public FireFlyCheckOut( ILocalWorkspace lw) {
		this.lw=lw;
	}

	@Override
	public void run() {
		try {
		    
		    FireflyArguments args=new FireflyArguments();
		    String []remains=new String[] {
		    		"/code/RWMS"
		    		//,"/code/rwmsapp"
		    		//,"/code/rwmsbatch"
		    };		    
			 args.setArgument("-h", "10.1.32.7");
			 args.setArgument("-port", "4859");
			 args.setArgument("-u", "shiminghai");
			 args.setArgument("-pwd", "ceb123456");
			 
			 //args.setArgument("-i", "true");
			 args.setArgument("-proj", "RWMS");
			 args.setArgument("-ssl", "false");
			 args.setArgument("-b", "develop");
			 args.setArgument("-d", "D:/ff_rwmsweb2");
			 
			 //args.setArgument("-cs", "rwms_");

			args.setArgument("remains", remains);
			
		    String localHost = null;
		    try {
		      localHost = InetAddress.getLocalHost().getHostName();
		    } catch (IOException ie) {
		      localHost = "UnknownHost";
		    }
			
			 String name = "shiminghai" + "@"+localHost+":/" + ClientUtil.formalizePath("D:/ff_rwmsweb2");

			 args.setArgument("-name", name);
			
			//String ws="D:\\ff_rwmsweb2";
			
			//Create.create(lw, args);
		    //Bringover.clearEnv();
		    _logger.info("CheckOut "+Bringover.isLocal);
		    FireflyBringover bringover=new FireflyBringover(false);
		   // bringover.remoteCmdInit(args, ws);
		    FireflyBringover.bringover(lw, args);
		    _logger.info("CheckOut Finished.");
		    
		    System.exit(1);
		}catch(Exception e) {
			 _logger.error("Exception ",e);
		}
		
	}

}
