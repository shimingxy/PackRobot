package com.packrobot.svn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import com.packrobot.BusinessTask;
import com.packrobot.svn.db.SqlLiteSvnWcDb;
import com.packrobot.svn.javahl.JavahlAdapter;

public class SvnBaseLine2Tag   implements BusinessTask{
	private static final Logger _logger = LoggerFactory.getLogger(SvnBaseLine2Tag.class);
	JavahlAdapter svn;
	String repositoryDir;
	String baseLineFilePath;
	
	public SvnBaseLine2Tag() {

	}

	@Override
	public void execute() throws Exception {
		
		//copy dir to tag with date dir
		svn.connect();
		String tagDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
		String tagPath="tag/"+tagDate;
		//System.out.println("getRemoteRevision "+svn.getRemoteRevision(tagPath));
		try{
			svn.remove(tagPath);
		}catch(Exception e) {
			
		}
		svn.mkdir(tagPath);
		svn.copy(svn.getSvnUrl()+repositoryDir, svn.getSvnUrl()+tagPath);
		svn.close();
		
		//base Line wc db
		SqlLiteSvnWcDb wc=new SqlLiteSvnWcDb(svn.getLocalSVNPath());
		FileInputStream in=new FileInputStream(wc.getDbFile());
		String basewcFile=baseLineFilePath+"wc_baseline_"+tagDate+".db";
		
		_logger.info("wc "+wc.getDbFile().getAbsolutePath());
		_logger.info("basewcFile "+basewcFile);
		
		File basewc=new File(basewcFile);
		
		if(basewc.exists()) {
			basewc.delete();
		}
		
		FileOutputStream out=new FileOutputStream(basewcFile);
		
		FileCopyUtils.copy(in, out);
		
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

	public String getBaseLineFilePath() {
		return baseLineFilePath;
	}

	public void setBaseLineFilePath(String baseLineFilePath) {
		this.baseLineFilePath = baseLineFilePath;
	}
	
	

}
