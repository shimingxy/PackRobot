package com.packrobot.firefly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hansky.apps.firefly.base.Delta;
import com.hansky.apps.firefly.base.DirectoryNode;
import com.hansky.apps.firefly.base.Index;
import com.hansky.apps.firefly.base.Node;
import com.hansky.apps.firefly.client.Arguments;
import com.hansky.apps.firefly.client.cmdline.MessageProcessor;
import com.hansky.apps.firefly.client.cmdline.cmd.Login;
import com.hansky.apps.firefly.client.core.ClientUtil;
import com.hansky.apps.firefly.client.core.LocalWorkspace;
import com.hansky.apps.firefly.client.core.ResourceUtil;
import com.hansky.apps.firefly.intf.FireflyConstants;
import com.hansky.apps.firefly.intf.FireflyException;
import com.hansky.apps.firefly.intf.IResourceUtil;
import com.hansky.apps.firefly.intf.client.IDeltaManager;
import com.hansky.apps.firefly.intf.client.IIndexManager;
import com.hansky.apps.firefly.intf.client.ILocalWorkspace;
import com.hansky.apps.firefly.intf.client.IMessageProcessor;
import com.hansky.core.net.UUID;
import com.hansky.core.str.Str;
import com.hansky.core.util.NotificationProducer;
import com.hansky.intf.rmc.session.IRMCClientSession;
import com.hansky.intf.util.IProducer;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class FireflyAdapter {
	private static final Logger _logger = LoggerFactory.getLogger(FireflyAdapter.class);
	ArrayList<FireflyNode> nodeList=new ArrayList<FireflyNode>();
	
	  private Hashtable pathNodeCache = new Hashtable();
	public FireflyAdapter() {
		
	}

	public static void main(String[] args) throws Exception {
		FireflyAdapter ffa=new FireflyAdapter();
		String ws="D:\\ff_rwmsweb2";
		//String ws="E:/ss";
		ffa.checkOut(ws);
		//ffa.listNodeVersions(ws);
		_logger.info("==========================");
	}

	
	public void checkOut(String wsPath) throws Exception{
		 ILocalWorkspace lw= getLocalWorkspace(wsPath);
		   
		 FireFlyCheckOut ffCheckOut=new FireFlyCheckOut(lw);
		 Thread tt=new Thread(ffCheckOut);
		 tt.start();
	}
	
	 public void listNodeVersions(String wsPath) throws Exception{
		ILocalWorkspace lw= getLocalWorkspace(wsPath);
	    
	    IIndexManager idxMgr = lw.getIndexManager();
	    IDeltaManager dm = lw.getDeltaManager();
	    //IFileManager fm = lw.getFileManager();
	    lw.clearChangesList();
	    Index index=idxMgr.getIndex();
	    //DirectoryNode  dn=index.getRoot();
	
	    listNodes((Node)index.getRoot(),"",lw);
	    DateTime baseLineDate=DateTime.parse("2018-07-26");
	    _logger.info("baseLineDate , "+baseLineDate.toString("yyyy-MM-dd hh:mm"));
	    //dt.toDate()
	    //DateTime startTime=Dat
	    for(FireflyNode fnode:nodeList) {
	    	// _logger.info("getNodePath "+fnode.getNodePath());
		    Node node = idxMgr.lookup(Str.create(fnode.getNodePath()));
		    
		    UUID uuttversion = node.getVersion();
	        Delta d = dm.getDelta(uuttversion, true);
	        DateTime changeDate=new DateTime(d.date);
	        if(changeDate.isAfter(baseLineDate)) {
	        String optype="";	
			switch (d.type) { 
	        	case 0:
	        		optype = "CREATE  ";break;
	        	case 1:
	        		optype = "UPDATE  ";break;
	        	case 2:
	        		optype = "MOVE    ";break;
	        	case 3:
	        		optype = "MERGE   ";break;
	        	case 4:
	        		optype = "ROLLBACK";break;
	            
	        }
	        _logger.info(optype+","+fnode.getNodePath()+", "+changeDate.toString("yyyy-MM-dd hh:mm")+" ,"+d.user);
	        }
	    }
        
	  }
	 
	 
	 
	 public void listNodes(Node  dn,String path,ILocalWorkspace lw) throws Exception {
		 for(Node n: dn.getChildren()) {
		    	if(n.getName().equalsIgnoreCase(Node.DELETED_DIR))continue;
		    	if(n.isFile()&&(path+"/"+n.getName()).startsWith("/code/RWMS/")) {
		    		nodeList.add(new FireflyNode(path+"/"+n.getName(),n));
		    	}
		    	if(n.countChildren()>0) {
		    		listNodes(n,path+"/"+n.getName(),lw);
		    	}
		    }
	 }
	 
	 public  ILocalWorkspace getLocalWorkspace(String wsPath) {
		 try {
			 ILocalWorkspace lw =null;
			 _logger.info("validateFireflyDir "+ClientUtil.validateFireflyDir(wsPath));
			 System.setProperty("user.dir", wsPath);
			 _logger.info( "user.dir "+System.getProperty("user.dir"));
			if(ClientUtil.validateFireflyDir(wsPath)==false) {
				
				 IResourceUtil  resUtil =ResourceUtil.getResourceUtil(2);
				 
			     IMessageProcessor mp=MessageProcessor.getMessageProcessor(resUtil);
			       
			     lw = LocalWorkspace.getLocalWorkspace(new File(wsPath), mp, resUtil, null, true);
			     lw.setIsLocal(false);
			     
			    
			     lw.setProperty("username", "shiminghai");
				 lw.setProperty("password", "ceb123456");
				 
				 IRMCClientSession cs=lw.connect("10.1.32.7", 4859, "shiminghai", "ceb123456");
				 //csMgr
				
				 _logger.info("getChangesetManager "+lw.getChangesetManager());
				 _logger.info("getWsDir "+    lw.getWsDir());
				
			}else {
			
		        IResourceUtil  resUtil =ResourceUtil.getResourceUtil(2);
		       
		        IMessageProcessor mp=MessageProcessor.getMessageProcessor(resUtil);
		        
			    lw = LocalWorkspace.getLocalWorkspace(new File(wsPath), mp, resUtil, null);
			    lw.save();
			   
			}
			 _logger.info("getFlyDir "+   lw.getFlyDir());
			 _logger.info("getWorkspaceRoot "+  lw.getWorkspaceRoot());
			    
			 return lw;
		 }catch(Exception e) {
			 _logger.error("Exception ",e);
		}
		 return null;
	 }
	 
	
}
