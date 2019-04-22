package com.packrobot.svn.javahl;

import org.apache.subversion.javahl.CommitInfo;
import org.apache.subversion.javahl.callback.CommitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavahlCommitCallback implements CommitCallback {
	private static final Logger _logger = LoggerFactory.getLogger(JavahlCommitCallback.class);
	public JavahlCommitCallback() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void commitInfo(CommitInfo commitInfo) {
		_logger.info("JavahlCommitCallback "+commitInfo.getReposRoot()+" "+commitInfo.getAuthor()+" "+commitInfo.getRevision()+" "+commitInfo.getDate());
		
	}

}
