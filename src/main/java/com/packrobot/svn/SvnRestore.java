package com.packrobot.svn;

import com.packrobot.BusinessTask;
import com.packrobot.svn.javahl.JavahlAdapter;

public class SvnRestore   implements BusinessTask{

	JavahlAdapter svn;
	
	String repositoryDir;
	long revision;
	
	public SvnRestore() {
		
	}

	@Override
	public void execute() throws Exception {
		svn.connect();
		svn.restore(repositoryDir, revision);
		svn.close();
		
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public JavahlAdapter getSvn() {
		return svn;
	}

	public void setSvn(JavahlAdapter svn) {
		this.svn = svn;
	}

	public String getRepositoryDir() {
		return repositoryDir;
	}

	public void setRepositoryDir(String repositoryDir) {
		this.repositoryDir = repositoryDir;
	}

}
