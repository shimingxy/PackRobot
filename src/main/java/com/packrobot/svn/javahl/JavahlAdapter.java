package com.packrobot.svn.javahl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.subversion.javahl.ClientException;
import org.apache.subversion.javahl.SVNClient;
import org.apache.subversion.javahl.types.CopySource;
import org.apache.subversion.javahl.types.Depth;
import org.apache.subversion.javahl.types.DirEntry;
import org.apache.subversion.javahl.types.Revision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//http://subclipse.tigris.org/archive/

public class JavahlAdapter   {
	
	private static final Logger _logger = LoggerFactory.getLogger(JavahlAdapter.class);
	public static HashMap<String,DirEntry> listBaseLineDirEntry=new HashMap<String,DirEntry>();
	public static HashMap<String,DirEntry> listRemoteDirEntry=new HashMap<String,DirEntry>();
	public static HashMap<String,DirEntry> listRemoteDirEntryRevision=new HashMap<String,DirEntry>();
	
	String svnUrl="svn://localhost";
	String svnUser="mhshi";
	String svnPass="mhshi123456";
	
	String relPath="";

	String localSVNPath="D:\\svn11\\";
	SVNClient client;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		JavahlAdapter adapter=new JavahlAdapter();
		adapter.connect();
		
		//adapter.checkout();
		//adapter.mkdir("shimh");
		adapter.remove("shimh");
		//adapter.copy("svn://localhost/trunk/","svn://localhost/tag/");
		
		//adapter.listRemoteEntries("");
		
		//_logger.info("getRemoteRevision "+adapter.getRemoteRevision(""));
		//_logger.info("getRevision "+adapter.getRevision(""));
		
		//adapter.logMsg("");
	}
	
	public void connect () throws Exception {
		client=new SVNClient();
		client.username(svnUser);
		client.password(svnPass);
		_logger.info("getVersion "+client.getVersion());
		relPath=svnUrl.substring("svn://".length(), svnUrl.length());
		relPath=relPath.substring(relPath.indexOf("/"), relPath.length());
		_logger.info("relPath "+relPath);
		
	}
	public void close() {
		client.dispose();
	}
	
	
	public void mkdir(String dir) throws ClientException {
		Set<String> s=new HashSet<String>();
		s.add(svnUrl+dir);
		_logger.info("mkdir "+svnUrl+dir);
		client.mkdir(s, true, null, new JavahlCommitMessageCallback(), new JavahlCommitCallback());
	}
	
	public void remove(String dir) throws ClientException {
		Set<String> s=new HashSet<String>();
		s.add(svnUrl+dir);
		_logger.info("remove "+svnUrl+dir);
		client.remove(s, true, false,null, new JavahlCommitMessageCallback(), new JavahlCommitCallback());
	}
	
	public void copy(String sourceUrl,String targetUrl) throws ClientException {
		_logger.info("copy "+sourceUrl +" to "+targetUrl);
		CopySource cs=new CopySource(sourceUrl, Revision.HEAD, Revision.HEAD);
		ArrayList<CopySource> listCopySource=new ArrayList<CopySource>();
		listCopySource.add(cs);
		client.copy(listCopySource, targetUrl, true, true, true, null, new JavahlCommitMessageCallback(), new JavahlCommitCallback());
	}
	
	public void checkout(String dir) throws ClientException {
		_logger.info("checkout " +svnUrl+dir);
		client.checkout(svnUrl+dir, localSVNPath, Revision.HEAD, Revision.HEAD, Depth.infinity, false, true);
	}
	
	public void restore(String dir,long revision) throws ClientException {
		_logger.info("restore " +svnUrl+dir +" , revision "+revision);
		client.checkout(svnUrl+dir, localSVNPath, Revision.getInstance(revision), Revision.getInstance(revision), Depth.infinity, false, true);
	}
	
	public  long getRemoteRevision( String path ) throws Exception {  
		JavahlListCallback callback=new JavahlListCallback(3);
	    client.list(this.svnUrl+path, Revision.HEAD, Revision.HEAD, Depth.empty, 1, true, callback);
	    return JavahlListCallback.revision;
	}
	
	//RemoteEntries
	public  void listRemoteEntries( String path ) throws Exception {  
		JavahlListCallback callback=new JavahlListCallback(1);
	    client.list(this.svnUrl+path, Revision.HEAD, Revision.HEAD, Depth.infinity, 1, true, callback);
	    
	    Iterator<Entry<String, DirEntry>>  iter=listRemoteDirEntry.entrySet().iterator();
		 while(iter.hasNext()) {
			 Entry<String, DirEntry> e=iter.next();
			 listRemoteOneEntrie(e.getKey(),path);			 
		 }
	}
	
	//RemoteEntrieOne
	public  void listRemoteOneEntrie( String path ,String basePath) throws Exception {  
		//_logger.info("listRemoteOneEntrie "+path);
		if(path.equalsIgnoreCase(basePath))return;
		JavahlListCallback callback=new JavahlListCallback(2);
	    client.list(this.svnUrl+path, Revision.HEAD, Revision.HEAD, Depth.empty, 1, true, callback);
	}

	public String getSvnUrl() {
		return svnUrl;
	}

	public void setSvnUrl(String svnUrl) {
		this.svnUrl = svnUrl;
	}

	public String getSvnUser() {
		return svnUser;
	}

	public void setSvnUser(String svnUser) {
		this.svnUser = svnUser;
	}

	public String getSvnPass() {
		return svnPass;
	}

	public void setSvnPass(String svnPass) {
		this.svnPass = svnPass;
	}

	public String getLocalSVNPath() {
		return localSVNPath;
	}

	public void setLocalSVNPath(String localSVNPath) {
		this.localSVNPath = localSVNPath;
	}

	

}
