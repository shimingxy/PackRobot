package com.packrobot.svn;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.packrobot.BusinessTask;
import com.packrobot.svn.db.SqlLiteSvnWcDb;
import com.packrobot.svn.db.SvnNodes;
import com.packrobot.svn.javahl.JavahlAdapter;

public class SvnCheckOut   implements BusinessTask{

	JavahlAdapter svn;
	
	String repositoryDir;
	String baseLineFilePath;
	String baseLineDate;
	
	ArrayList<String> skips;
	
	public SvnCheckOut() {
		
	}

	@Override
	public void execute() throws Exception {
		svn.connect();
		svn.checkout(this.repositoryDir);
		svn.close();
		doDiff();
	}
	
	public void doDiff() throws Exception {
		//new checkout 
		SqlLiteSvnWcDb wc=new SqlLiteSvnWcDb(svn.getLocalSVNPath());
		ArrayList<SvnNodes> wcList=wc.getAllNodes();
		
		//baseline checkout
		SqlLiteSvnWcDb baseLineWc=new SqlLiteSvnWcDb(new File (baseLineFilePath+"wc_baseline_"+baseLineDate+".db"));
		ArrayList<SvnNodes> baseLineList=baseLineWc.getAllNodes();
		
		ArrayList<SvnNodes> modifyList=new ArrayList<SvnNodes>();
		//add and update
		for(int wcCount=0;wcCount<wcList.size();wcCount++) {
			boolean isAdd=true;
			boolean isUpdate=false;
			if(isNeedSkip(wcList.get(wcCount).getLocalRelpath()))continue;
			
			for(int baseLineCount=0;baseLineCount<baseLineList.size();baseLineCount++) {
				if(wcList.get(wcCount).getLocalRelpath().equalsIgnoreCase(baseLineList.get(baseLineCount).getLocalRelpath())) {
					isAdd=false;
					if(wcList.get(wcCount).getChangedRevision()>baseLineList.get(baseLineCount).getChangedRevision()) {
						isUpdate=true;
					}
				}
			}
			SvnNodes node=wcList.get(wcCount);
			//跳过目录
			if(node.getKind().equals("dir"))continue;
			if(isAdd) {
				node.setChangedType("ADD   ");
				modifyList.add(node);
			}
			if(isUpdate) {
				node.setChangedType("UPDATE");
				modifyList.add(node);
			}
			
		}
		
		//Delete
		for(int baseLineCount=0;baseLineCount<baseLineList.size();baseLineCount++) {
			if(isNeedSkip(baseLineList.get(baseLineCount).getLocalRelpath()))continue;
			boolean isDelete=true;
			for(int wcCount=0;wcCount<wcList.size();wcCount++) {
				if(wcList.get(wcCount).getLocalRelpath().equalsIgnoreCase(baseLineList.get(baseLineCount).getLocalRelpath())) {
					isDelete=false;
				}
			}
			SvnNodes node=wcList.get(baseLineCount);
			//跳过目录
			//if(node.getKind().equals("dir"))continue;
			if(isDelete) {
				node.setChangedType("DELETE");
				modifyList.add(node);
			}
		}
		
		writeNode2File(baseLineList,"baseline");
		
		writeNode2File(wcList,"repository");
		
		writeNode2File(modifyList,"changelist");
	}

	public boolean isNeedSkip(String filePath) {
		for(String skip : skips) {
			if(filePath.indexOf(skip.trim())>-1)return true;
		}
		return false;
	}
	
	/**
	 * write Nodes to file
	 * @param nodeList
	 * @param fileName
	 * @throws Exception
	 */
	public void writeNode2File(ArrayList<SvnNodes> nodeList,String fileName) throws Exception {
		String modifyDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
		
		String modifyFile=baseLineFilePath+"svn_"+fileName+"_"+modifyDate+".txt";
		FileOutputStream out;
		File exportFile=new File(modifyFile);
		if(exportFile.exists()){
			exportFile.delete();
		}
		exportFile.createNewFile();
		out=new FileOutputStream(exportFile);
		for(SvnNodes node  : nodeList) {
			if(node.getLocalRelpath().equals(""))continue;
			if(fileName.equalsIgnoreCase("changelist")){
				out.write((node.getChangedType()+","+node.getChangedDate()+","+(node.getKind().equalsIgnoreCase("file")?"file":"ford")+","+node.getLocalRelpath()+","+node.getChangedAuthor()+","+node.getChangedRevision()+"\r\n").getBytes());
		
			}else {
				out.write((node.getChangedDate()+","+(node.getKind().equalsIgnoreCase("file")?"file":"ford")+","+node.getLocalRelpath()+","+node.getChangedAuthor()+","+node.getChangedRevision()+"\r\n").getBytes());
			}
		}
		out.close();
		
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

	public String getBaseLineDate() {
		return baseLineDate;
	}

	public void setBaseLineDate(String baseLineDate) {
		this.baseLineDate = baseLineDate;
	}

	public ArrayList<String> getSkips() {
		return skips;
	}

	public void setSkips(ArrayList<String> skips) {
		this.skips = skips;
	}
}
