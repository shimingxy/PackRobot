package com.packrobot.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import com.packrobot.BusinessTask;
import com.packrobot.svn.db.SvnNodes;

public class PackMavenProject    implements BusinessTask{
	private static final Logger _logger = LoggerFactory.getLogger(PackMavenProject.class);
	/**
	 * jar
	 * war
	 * bat
	 * class
	 */
	String projectTyppe="jar";
	String projectPath;
	String projectName;
	String targetPath;
	String modifyListFilePath;
	String packDate;
	String relPath;
	String webappPath;
	String version;
	
	ArrayList<SvnNodes> nodesList=new ArrayList<SvnNodes>();
	

	public PackMavenProject() {

	}

	public static void main(String[] args) throws IOException {
		PackMavenProject pp=new PackMavenProject();
		pp.copy("D:/svn11/packparent/mavenpackage.bat", "d:/svnpp/mavenpackage.bat");
	}



	@Override
	public void execute() throws Exception {
		if(packDate==null||packDate.equals("")) {
			packDate=new SimpleDateFormat("yyyyMMdd").format(new Date());
		}
		
		readChangeList();
		
		_logger.info("project path "+targetPath);
		
		if(projectTyppe.equalsIgnoreCase("war")) {
			 packWar();
		}else	if(projectTyppe.equalsIgnoreCase("jar")) {
			 packJar();
		}
		
	}
	
	public void readChangeList() throws Exception {
		
		modifyListFilePath=modifyListFilePath+"svn_changelist_"+packDate+".txt";
		FileInputStream in=new FileInputStream(modifyListFilePath);
		BufferedReader br=new BufferedReader(new InputStreamReader(in));

		String line=null;
		while ((line=br.readLine())!=null){
			String []fields=line.split(",");
			if(fields[0].equalsIgnoreCase("DELETE")) {//删除数据需要手动处理
				continue;
			}
			if(fields[0].startsWith("--")) {//跳过注释内容
				continue;
			}
			
			SvnNodes node=new SvnNodes();
			node.setChangedType(fields[0]);
			node.setChangedDate(fields[1]);
			node.setKind(fields[2]);
			node.setLocalRelpath(fields[3]);
			node.setChangedAuthor(fields[4]);
			node.setChangedRevision(Integer.parseInt(fields[5]));
			nodesList.add(node);
			_logger.info(""+node);
		}
		
		br.close();
		in.close();
	}
	
	public void packWar() throws Exception {
		for( SvnNodes node : nodesList) {
			if(node.getLocalRelpath().startsWith(relPath)) {
				String copyFileName="";
				String relFileName="";
				_logger.info("getLocalRelpath "+node.getLocalRelpath());
				
				if(webappPath!=null&&node.getLocalRelpath().startsWith(webappPath)) {
					relFileName=node.getLocalRelpath().substring(webappPath.length(), node.getLocalRelpath().length());
					copyFileName=projectPath+relPath+"target/"+projectName+"/"+relFileName;
				}
				
				if(node.getLocalRelpath().startsWith(relPath+"src/main/java/")) {
					if(node.getLocalRelpath().endsWith(".java")) {
						relFileName=node.getLocalRelpath().substring((relPath+"src/main/java/").length(), node.getLocalRelpath().length()-5)+".class";
						copyFileName=projectPath+relPath+"target/classes/"+relFileName;
					}else {
						relFileName=node.getLocalRelpath().substring((relPath+"src/main/java/").length(), node.getLocalRelpath().length());
						copyFileName=projectPath+relPath+"target/classes/"+relFileName;
					}
				}
				
				if(node.getLocalRelpath().startsWith(relPath+"src/main/resources/")) {
					if(node.getLocalRelpath().endsWith(".java")) {
						relFileName=node.getLocalRelpath().substring((relPath+"src/main/resources/").length(), node.getLocalRelpath().length()-5)+".class";
						copyFileName=projectPath+relPath+"target/classes/"+relFileName;
					}else {
						relFileName=node.getLocalRelpath().substring((relPath+"src/main/resources/").length(), node.getLocalRelpath().length());
						copyFileName=projectPath+relPath+"target/classes/"+relFileName;
					}
				}
				
				_logger.info("copyFileName "+copyFileName);
				String targetFileName="";
				if(webappPath!=null&&node.getLocalRelpath().startsWith(webappPath)) {
					 targetFileName=targetPath+relFileName;
				}else {
					 targetFileName=targetPath+"WEB-INF/classes/"+relFileName;
				}
				
				_logger.info("targetFileName "+targetFileName);
				
				if(!copyFileName.equals("")) {
					copy(copyFileName,targetFileName);
				}
			}
		}
	}
	
	public void packJar() throws Exception {
		for( SvnNodes node : nodesList) {
			if(node.getLocalRelpath().startsWith(relPath)) {
				String copyFileName="";
				String relFileName="";
				_logger.info("getLocalRelpath "+node.getLocalRelpath());
				
				if(node.getLocalRelpath().startsWith(relPath+"src/main/java/")) {
					if(node.getLocalRelpath().endsWith(".java")) {
						relFileName=node.getLocalRelpath().substring((relPath+"src/main/java/").length(), node.getLocalRelpath().length()-5)+".class";
						copyFileName=projectPath+relPath+"target/classes/"+relFileName;
					}else {
						relFileName=node.getLocalRelpath().substring((relPath+"src/main/java/").length(), node.getLocalRelpath().length());
						copyFileName=projectPath+relPath+"target/classes/"+relFileName;
					}
				}
				
				if(node.getLocalRelpath().startsWith(relPath+"src/main/resources/")) {
					if(node.getLocalRelpath().endsWith(".java")) {
						relFileName=node.getLocalRelpath().substring((relPath+"src/main/resources/").length(), node.getLocalRelpath().length()-5)+".class";
						copyFileName=projectPath+relPath+"target/classes/"+relFileName;
					}else {
						relFileName=node.getLocalRelpath().substring((relPath+"src/main/resources/").length(), node.getLocalRelpath().length());
						copyFileName=projectPath+relPath+"target/classes/"+relFileName;
					}
				}
				
				_logger.info("copyFileName "+copyFileName);
				_logger.info("targetFileName "+targetPath+relFileName);
				
				if(!copyFileName.equals("")) {
					copy(copyFileName,targetPath+relFileName);
				}
			}
		}
	}
	
	public void copy(String inFileName,String outFileName) throws IOException {
		File f=new File(outFileName.substring(0,outFileName.lastIndexOf("/")));
		f.mkdirs();
		FileInputStream in=new FileInputStream(inFileName);
		FileOutputStream out=new FileOutputStream(outFileName);
		
		FileCopyUtils.copy(in, out);
		
	}

	public String getProjectTyppe() {
		return projectTyppe;
	}

	public void setProjectTyppe(String projectTyppe) {
		this.projectTyppe = projectTyppe;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getModifyListFilePath() {
		return modifyListFilePath;
	}

	public void setModifyListFilePath(String modifyListFilePath) {
		this.modifyListFilePath = modifyListFilePath;
	}

	public String getPackDate() {
		return packDate;
	}

	public void setPackDate(String packDate) {
		this.packDate = packDate;
	}

	public String getRelPath() {
		return relPath;
	}

	public void setRelPath(String relPath) {
		this.relPath = relPath;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getWebappPath() {
		return webappPath;
	}

	public void setWebappPath(String webappPath) {
		this.webappPath = webappPath;
	}

	
	
}
