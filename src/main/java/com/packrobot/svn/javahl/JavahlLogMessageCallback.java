package com.packrobot.svn.javahl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.subversion.javahl.callback.LogMessageCallback;
import org.apache.subversion.javahl.types.ChangePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavahlLogMessageCallback implements LogMessageCallback {
	private static final Logger _logger = LoggerFactory.getLogger(JavahlLogMessageCallback.class);
	@Override
	public void singleMessage(Set<ChangePath> arg0, long arg1, Map<String, byte[]> arg2, boolean arg3) {
		_logger.info(""+arg0);
		
		
		Iterator<Entry<String, byte[]>>  iter=arg2.entrySet().iterator();
		 while(iter.hasNext()) {
			 Entry<String, byte[]> e=iter.next();
			 _logger.info(""+e.getKey()+" , "+new String(e.getValue()));
		 }
	}

}
