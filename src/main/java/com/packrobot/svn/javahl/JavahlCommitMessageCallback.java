package com.packrobot.svn.javahl;

import java.util.Iterator;
import java.util.Set;
import org.apache.subversion.javahl.CommitItem;
import org.apache.subversion.javahl.callback.CommitMessageCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavahlCommitMessageCallback implements CommitMessageCallback {
	private static final Logger _logger = LoggerFactory.getLogger(JavahlCommitMessageCallback.class);
	public JavahlCommitMessageCallback() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLogMessage(Set<CommitItem> setCommitItem) {
		Iterator<CommitItem>  iter=setCommitItem.iterator();
		 while(iter.hasNext()) {
			 CommitItem e=iter.next();
			 _logger.info("JavahlCommitMessageCallback "+
					 		e.getCopyUrl()+" "+
					 		e.getMovedFromPath()+" "+
					 		e.getRevision()+" "+
					 		e.getUrl()+" "+
					 		e.getNodeKind()+" "+
					 		e.getStateFlags());
		 }
		return "commit";
	}

}
