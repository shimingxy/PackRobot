package com.packrobot.svn.javahl;

import org.apache.subversion.javahl.callback.ListCallback;
import org.apache.subversion.javahl.types.DirEntry;
import org.apache.subversion.javahl.types.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavahlListCallback implements ListCallback {
	private static final Logger _logger = LoggerFactory.getLogger(JavahlListCallback.class);
	public static long revision=0;
	
	int callType=1;
	public static class CallbackType{
		public static  int ALL=1;
		public static  int ONE=2;
		public static  int REV=3;
	}
	
	public JavahlListCallback(int callType) {
		super();
		this.callType = callType;
	}
	
	
	@Override
	public void doEntry(DirEntry dirEntry, Lock lock) {
		revision=0;
		
		if(callType==CallbackType.ALL) {
			_logger.info(""+dirEntry.getAbsPath()+"/"+dirEntry.getPath()+" , "+dirEntry.getLastAuthor()+" , "+dirEntry.getLastChangedRevisionNumber());
			JavahlAdapter.listRemoteDirEntry.put(dirEntry.getPath(), dirEntry);
		}else if(callType==CallbackType.ONE) {
			_logger.info(""+dirEntry.getAbsPath()+" , "+dirEntry.getLastAuthor()+" , "+dirEntry.getLastChangedRevision());
			JavahlAdapter.listRemoteDirEntryRevision.put(dirEntry.getAbsPath(), dirEntry);
		}else if(callType==CallbackType.REV) {
			revision=dirEntry.getLastChangedRevisionNumber();
			_logger.info(""+dirEntry.getAbsPath()+" , "+dirEntry.getLastAuthor()+" , "+dirEntry.getLastChangedRevision());
		}
		
	}

}
