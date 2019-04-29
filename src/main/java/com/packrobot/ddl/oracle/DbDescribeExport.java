/**
 * 
 */
package com.packrobot.ddl.oracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.packrobot.BusinessTask;
import com.packrobot.db.TableColumns;

/**
 * @author user
 *
 */
public class DbDescribeExport  implements BusinessTask{
	private static final Logger _logger = LoggerFactory.getLogger(DbDescribeExport.class);
	FileOutputStream out;
	DataSource dataSource;
	String url;
	String user;
	String pass;
	String driverClass;
	
	String configFilePath;
	String exportFilePath;
	Connection conn;
	
	/**
	 * 
	 */
	public DbDescribeExport() {
		// TODO Auto-generated constructor stub
	}

	public void  execute() throws Exception{
		_logger.info("BACKUP_DATE "+System.getenv().get("BACKUP_DATE"));
		_logger.info("PUBLISH_DATE "+System.getenv().get("PUBLISH_DATE"));
		conn = dataSource.getConnection();
		_logger.info("---exportFilePath "+exportFilePath);
		File exportFile=new File(exportFilePath);
		if(exportFile.exists()){
			exportFile.delete();
		}
		exportFile.createNewFile();
		out=new FileOutputStream(exportFile);
		out.write(("-- --FROM SERVER " + this.url + "\n").getBytes());
		//out.write(("-- --FROM USER " + this.user + "\n").getBytes());
		out.write(("-- --TO FILE " + this.exportFilePath + "\n").getBytes());
		readExportData(configFilePath);
		out.flush();
		out.close();
		conn.close();
	}
	
	public void  readTable(String owner,String tableName) throws Exception{
		out.write("--Create Table \r\n".getBytes());
		Statement stmt=conn.createStatement();
		/*String sql="select t.owner,t.table_name,c.COMMENTS from sys.all_all_tables t,sys.all_tab_comments c "+
					" where t.table_name=c.TABLE_NAME "+
					" and t.table_name='"+tableName+"' and t.owner ='"+owner+"' "+
					" order by t.owner,t.table_name";*/
		String sql="select  t.owner,t.table_name,c.COMMENTS as TABLECOMMENTS,col.COLUMN_NAME,"+
			" col.DATA_TYPE,col.DATA_LENGTH,col.DATA_SCALE,col.DATA_PRECISION,colc.COMMENTS AS COLUMNCOMMENTS ,col.NULLABLE,col.DATA_DEFAULT"+
			" from sys.all_all_tables t,sys.all_tab_comments c,sys.all_tab_columns col,sys.all_col_comments colc "+
			" where t.table_name=c.TABLE_NAME "+
			" and col.TABLE_NAME=t.table_name "+
			" and colc.TABLE_NAME=t.table_name "+
			" and col.COLUMN_NAME=colc.COLUMN_NAME "+
			" and t.owner ='"+owner+"' "+
			" and COL.OWNER ='"+owner+"' "+
			" and C.OWNER ='"+owner+"' "+
			" and COLC.OWNER ='"+owner+"' "+
			" and upper(t.table_name)='"+tableName.toUpperCase()+"' "+
			" order by t.owner,t.table_name,col.COLUMN_ID";
		
		_logger.info(sql);
		ArrayList<TableColumns> tcArray=new ArrayList<TableColumns>();
		ResultSet rs=stmt.executeQuery(sql);
		String TABLECOMMENTS=null;
		while(rs.next()){
			_logger.info("COLUMN_NAME : "+rs.getString("COLUMN_NAME"));
			TableColumns tc=new TableColumns();
			tc.setOwner(rs.getString("owner"));
			tc.setTableName(rs.getString("table_name"));
			tc.setTableComments(rs.getString("TABLECOMMENTS"));
			tc.setColumnName(rs.getString("COLUMN_NAME"));
			tc.setColumnComments(rs.getString("COLUMNCOMMENTS"));
			tc.setDataType(rs.getString("DATA_TYPE"));
			tc.setDataLength(rs.getInt("DATA_LENGTH"));
			tc.setDataScale(rs.getInt("DATA_SCALE"));
			tc.setDataPrecision(rs.getInt("DATA_PRECISION"));
			tc.setNullAble(rs.getString("NULLABLE"));
			tc.setDataDefault(rs.getString("DATA_DEFAULT"));
			//DATA_DEFAULT
			//col.NULLABLE
			tcArray.add(tc);
		}
		out.write(("create table "+owner+"."+tableName.toUpperCase()).getBytes());
		out.write("(\r\n".getBytes());
		
		for (int i=0;i<tcArray.size();i++){
			TableColumns tc=tcArray.get(i);
			TABLECOMMENTS=tc.getTableComments();
			int defaultType=0;
			out.write((" \t"+String.format("%-20s", tc.getColumnName()) +"\t").getBytes());
			if(tc.getDataType().equalsIgnoreCase("VARCHAR2")||
					tc.getDataType().equalsIgnoreCase("VARCHAR")||
					tc.getDataType().equalsIgnoreCase("NVARCHAR2")||
					tc.getDataType().equalsIgnoreCase("CHAR")||
					tc.getDataType().equalsIgnoreCase("RAW")
					){
				defaultType=1;
				out.write((tc.getDataType()+"("+tc.getDataLength()+")").getBytes());
				
			}else if(tc.getDataType().equalsIgnoreCase("NUMBER")){
				defaultType=2;
				if(tc.getDataLength()==22&&tc.getDataScale()>0){
					out.write(("NUMBER").getBytes());
				}else if(tc.getDataLength()==22&&tc.getDataScale()==0){
					out.write(("NUMBER").getBytes());
				}else if(tc.getDataPrecision()==0||tc.getDataScale()==0||tc.getDataScale()==0){
					out.write((tc.getDataType()+"("+tc.getDataPrecision()+")").getBytes());
				}else{//Double
					out.write((tc.getDataType()+"("+tc.getDataPrecision()+","+tc.getDataScale()+")").getBytes());
				}
			}else if(tc.getDataType().equalsIgnoreCase("blob")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("clob")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("DATE")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("NCLOB")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("LONG")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("ROWID")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("BINARY_DOUBLE")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("BINARY_FLOAT")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().indexOf(")")>-1){
				out.write((tc.getDataType()+"").getBytes());
			}
			
			if(defaultType>0&&tc.getDataDefault()!=null&&!tc.getDataDefault().equals("")){
				if(defaultType==1){
					out.write((" default '"+tc.getDataDefault().replace("\n", "").replace("\r", "")+"'").getBytes());
				}else if(defaultType==2){
					out.write((" default "+tc.getDataDefault().replace("\n", "").replace("\r", "")+"").getBytes());
				}
			}
			
			if(!tc.getNullAble().equalsIgnoreCase("Y")){
				out.write(" NOT NULL ".getBytes());
			}
			
			if(i<tcArray.size()-1){
				out.write(",\r\n".getBytes());
			}else{
				out.write("\r\n".getBytes());
			}
		}
		//comment on column ALARMAPPEVALUATE.signalgrade1 is '信号建议等级';
		//comment on table ALARMAPPEVALUATE  is '预警信号应用评价表';
		out.write(");\r\n\r\n".getBytes());
		
		if(TABLECOMMENTS!=null&&!TABLECOMMENTS.equals("")){
			out.write(("comment on table  "+owner+"."+String.format("%-40s", tableName.toUpperCase())+" is '"+TABLECOMMENTS.replace("\r", "").replace("\n", "")+"';\r\n\r\n").getBytes());
		}
		for (int i=0;i<tcArray.size();i++){
			TableColumns tc=tcArray.get(i);
			if(tc.getColumnComments()!=null&&!tc.getColumnComments().equals("")){
				out.write(("comment on column "+owner+"."+String.format("%-40s", tableName.toUpperCase()+"."+tc.getColumnName())+" is '"+tc.getColumnComments().replace("\r", "").replace("\n", "")+"';\r\n").getBytes());
			}
		}
		out.write("\r\n".getBytes());
		rs.close();
		stmt.close();
		
	}
	
	public HashMap<String,TableColumns>  readBackupTable(String owner,String tableName) throws Exception{
		Statement stmt=conn.createStatement();
		String sql="SELECT * FROM PUB_TABLE_COLUMNS  WHERE owner='"+owner+"'"+" and upper(table_name)='"+tableName.toUpperCase()+"' "+
			" order by owner,table_name";
		
		_logger.info(sql);
		HashMap<String,TableColumns> tcArray=new HashMap<String,TableColumns>();
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			_logger.info("COLUMN_NAME : "+rs.getString("COLUMN_NAME"));
			TableColumns tc=new TableColumns();
			tc.setOwner(rs.getString("owner"));
			tc.setTableName(rs.getString("table_name"));
			tc.setTableComments(rs.getString("TABLECOMMENTS")==null?"":rs.getString("TABLECOMMENTS"));
			tc.setColumnName(rs.getString("COLUMN_NAME"));
			tc.setColumnComments(rs.getString("COLUMNCOMMENTS")==null?"":rs.getString("COLUMNCOMMENTS"));
			tc.setDataType(rs.getString("DATA_TYPE"));
			tc.setDataLength(rs.getInt("DATA_LENGTH"));
			tc.setDataScale(rs.getInt("DATA_SCALE"));
			tc.setDataPrecision(rs.getInt("DATA_PRECISION"));
			tc.setNullAble(rs.getString("NULLABLE"));
			tc.setDataDefault(rs.getString("DATA_DEFAULT"));
			//DATA_DEFAULT
			//col.NULLABLE
			tcArray.put(tc.getColumnName(),tc);
		}
		
		_logger.info(""+tcArray);
		rs.close();
		stmt.close();
		return tcArray;
	}
	//ALERT 对应的 add drop modify三种情况
	public void  readAlterTable(String owner,String tableName) throws Exception{
		out.write("--Alter Table \r\n".getBytes());
		Statement stmt=conn.createStatement();
		/*String sql="select t.owner,t.table_name,c.COMMENTS from sys.all_all_tables t,sys.all_tab_comments c "+
					" where t.table_name=c.TABLE_NAME "+
					" and t.table_name='"+tableName+"' and t.owner ='"+owner+"' "+
					" order by t.owner,t.table_name";*/
		String sql="select  t.owner,t.table_name,c.COMMENTS as TABLECOMMENTS,col.COLUMN_NAME,"+
			" col.DATA_TYPE,col.DATA_LENGTH,col.DATA_SCALE,col.DATA_PRECISION,colc.COMMENTS AS COLUMNCOMMENTS ,col.NULLABLE,col.DATA_DEFAULT"+
			" from sys.all_all_tables t,sys.all_tab_comments c,sys.all_tab_columns col,sys.all_col_comments colc "+
			" where t.table_name=c.TABLE_NAME "+
			" and col.TABLE_NAME=t.table_name "+
			" and colc.TABLE_NAME=t.table_name "+
			" and col.COLUMN_NAME=colc.COLUMN_NAME "+
			" and t.owner ='"+owner+"' "+
			" and COL.OWNER ='"+owner+"' "+
			" and C.OWNER ='"+owner+"' "+
			" and COLC.OWNER ='"+owner+"' "+
			" and upper(t.table_name)='"+tableName.toUpperCase()+"' "+
			" order by t.owner,t.table_name,col.COLUMN_ID";
		
		_logger.info(sql);
		ArrayList<TableColumns> tcArray=new ArrayList<TableColumns>();
		HashMap<String,TableColumns> tcMap=new HashMap<String,TableColumns>();
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			_logger.info("COLUMN_NAME : "+rs.getString("COLUMN_NAME"));
			TableColumns tc=new TableColumns();
			tc.setOwner(rs.getString("owner"));
			tc.setTableName(rs.getString("table_name"));
			tc.setTableComments(rs.getString("TABLECOMMENTS")==null?"":rs.getString("TABLECOMMENTS"));
			tc.setColumnName(rs.getString("COLUMN_NAME"));
			tc.setColumnComments(rs.getString("COLUMNCOMMENTS")==null?"":rs.getString("COLUMNCOMMENTS"));
			tc.setDataType(rs.getString("DATA_TYPE"));
			tc.setDataLength(rs.getInt("DATA_LENGTH"));
			tc.setDataScale(rs.getInt("DATA_SCALE"));
			tc.setDataPrecision(rs.getInt("DATA_PRECISION"));
			tc.setNullAble(rs.getString("NULLABLE"));
			tc.setDataDefault(rs.getString("DATA_DEFAULT"));
			//DATA_DEFAULT
			//col.NULLABLE
			//sql查询的对应的所有的内容放在数组中，既tableName表对应的数据
			tcArray.add(tc);
			//sql查询的对应的所有的内容放在map集合中，键就是 tableName表对应的所有的列
			tcMap.put(tc.getColumnName(), tc);
		}
		rs.close();
		stmt.close();
		
		/**
		 * PUB_TABLE_COLUMNS表 对应的所有的数据（该表是旧版的所有表以及对应的列）
		 * 这个函数返回的map集合中，String类型代表的是tableName表对应的所有的列
		*/
		HashMap<String,TableColumns> backupTcMap=readBackupTable( owner, tableName);
		
		alertTableSql(tcArray,backupTcMap,owner,tableName);
		
		
	}
	
	public void alertTableSql(ArrayList<TableColumns> tcArray,HashMap<String,TableColumns> backupTcMap,String owner,String tableName) throws IOException {
		String NEW_TABLECOMMENTS=null;
		String OLD_TABLECOMMENTS=null;
		String COLUMNCOMMENTS="";
		//TODO
		//迭代map集合
		Iterator<Entry<String, TableColumns>> iter=backupTcMap.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, TableColumns> entry=iter.next();
			boolean isDelete=true;
			OLD_TABLECOMMENTS=entry.getValue().getTableComments();
			_logger.info("entry "+entry.getValue());
			//循环遍历找出tcArray数组中是否存在对应的列，如果存在则继续循环，不存在证明被删除
			for (int i=0;i<tcArray.size();i++){
				TableColumns tc=tcArray.get(i);
				
				//对应的列相同则退出for循环
				if(tc.getColumnName().equalsIgnoreCase(entry.getKey())) {
					isDelete=false;
					break;
				}
			}
			
			if(isDelete) {
				out.write(("alter table "+owner+"."+tableName.toUpperCase()+" drop column "+entry.getKey()+";\r\n").getBytes());
			}
		}
		
		//&&tc.getTableComments().equalsIgnoreCase(backuptc.getTableComments())
		//感觉这段逻辑有问题
		for (int i=0;i<tcArray.size();i++){
			TableColumns tc=tcArray.get(i);
			int defaultType=0;
			String  alterType="add";
			TableColumns backuptc=null;
			NEW_TABLECOMMENTS=tc.getTableComments();
			if(backupTcMap.containsKey(tc.getColumnName())) {
				alterType="modify";
				backuptc=backupTcMap.get(tc.getColumnName());
				if(tc.getDataLength()==backuptc.getDataLength()
					&&tc.getDataType().equalsIgnoreCase(backuptc.getDataType())
					&&tc.getDataPrecision()==(backuptc.getDataPrecision())
					&&tc.getDataScale()==(backuptc.getDataScale())
					&&tc.getColumnComments().equalsIgnoreCase(backuptc.getColumnComments())
					
					) {
						continue;
				}else {
					
				}
				
			}else {
				alterType="add";
			}
			
			_logger.info("tc "+tc);
			_logger.info("backuptc "+backuptc);
			
			out.write(("alter table "+owner+"."+tableName.toUpperCase()+" "+alterType).getBytes());
			
			out.write((" \t"+String.format("%-20s", tc.getColumnName()) +"\t").getBytes());
			if(tc.getDataType().equalsIgnoreCase("VARCHAR2")||
					tc.getDataType().equalsIgnoreCase("VARCHAR")||
					tc.getDataType().equalsIgnoreCase("NVARCHAR2")||
					tc.getDataType().equalsIgnoreCase("CHAR")||
					tc.getDataType().equalsIgnoreCase("RAW")
					){
				defaultType=1;
				out.write((tc.getDataType()+"("+tc.getDataLength()+")").getBytes());
				
			}else if(tc.getDataType().equalsIgnoreCase("NUMBER")){
				defaultType=2;
				if(tc.getDataLength()==22&&tc.getDataScale()>0){
					out.write(("NUMBER").getBytes());
				}else if(tc.getDataLength()==22&&tc.getDataScale()==0){
					out.write(("INTEGER").getBytes());
				}else if(tc.getDataPrecision()==0||tc.getDataScale()==0||tc.getDataScale()==0){
					out.write((tc.getDataType()+"("+tc.getDataPrecision()+")").getBytes());
				}else{//Double
					out.write((tc.getDataType()+"("+tc.getDataPrecision()+","+tc.getDataScale()+")").getBytes());
				}
			}else if(tc.getDataType().equalsIgnoreCase("blob")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("clob")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("DATE")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("NCLOB")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("LONG")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("ROWID")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("BINARY_DOUBLE")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().equalsIgnoreCase("BINARY_FLOAT")){
				out.write((tc.getDataType()+"").getBytes());
			}else if(tc.getDataType().indexOf(")")>-1){
				out.write((tc.getDataType()+"").getBytes());
			}
			
			if(defaultType>0&&tc.getDataDefault()!=null&&!tc.getDataDefault().equals("")){
				if(defaultType==1){
					out.write((" default '"+tc.getDataDefault().replace("\n", "").replace("\r", "")+"'").getBytes());
				}else if(defaultType==2){
					out.write((" default "+tc.getDataDefault().replace("\n", "").replace("\r", "")+"").getBytes());
				}
			}
			
			if(!tc.getNullAble().equalsIgnoreCase("Y")){
				out.write(" NOT NULL ".getBytes());
			}
			out.write(";\r\n".getBytes());
			if(tc!=null&&backuptc!=null&&!tc.getColumnComments().equals(backuptc.getColumnComments())) {
				COLUMNCOMMENTS+=("comment on column "+owner+"."+String.format("%-40s", tableName.toUpperCase()+"."+tc.getColumnName())+" is '"+tc.getColumnComments().replace("\r", "").replace("\n", "")+"';\r\n");
			}
		}
		//comment on column ALARMAPPEVALUATE.signalgrade1 is '信号建议等级';
		//comment on table ALARMAPPEVALUATE  is '预警信号应用评价表';
		out.write("\r\n\r\n".getBytes());
		
		if(!NEW_TABLECOMMENTS.equals(OLD_TABLECOMMENTS)){
			out.write(("comment on table  "+owner+"."+String.format("%-40s", tableName.toUpperCase())+" is '"+NEW_TABLECOMMENTS.replace("\r", "").replace("\n", "")+"';\r\n\r\n").getBytes());
		}
		
		if(!COLUMNCOMMENTS.equalsIgnoreCase("")) {
			out.write(COLUMNCOMMENTS.getBytes());
		}
		
		out.write("\r\n".getBytes());
	}
	
	public void  readTableCons(String owner,String tableName) throws Exception{
		out.write("--Create Table Constraints \r\n".getBytes());
		Statement stmt=conn.createStatement();
		/*String sql="select t.owner,t.table_name,c.COMMENTS from sys.all_all_tables t,sys.all_tab_comments c "+
					" where t.table_name=c.TABLE_NAME "+
					" and t.table_name='"+tableName+"' and t.owner ='"+owner+"' "+
					" order by t.owner,t.table_name";*/
		String sql="select ac.OWNER,ac.CONSTRAINT_NAME,ac.CONSTRAINT_TYPE,ac.TABLE_NAME,acc.COLUMN_NAME,acc.POSITION " +
				" from sys.all_constraints ac ,sys.all_cons_columns acc "  +
				" where ac.OWNER=acc.OWNER  "  +
				" and ac.CONSTRAINT_NAME=acc.CONSTRAINT_NAME "  +
				" and ac.TABLE_NAME='"+tableName.toUpperCase()+"' "  +
				" and ac.owner ='"+owner+"'"+
				" and acc.POSITION IS NOT NULL "+
				" order by ac.CONSTRAINT_NAME, acc.POSITION";
		
		_logger.info(sql);
		HashMap<String,ArrayList<String>> propMap=new HashMap<String,ArrayList<String>>();
		HashMap<String,String> keyTypeMap=new HashMap<String,String>();
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			_logger.info("CONSTRAINT_NAME : "+rs.getString("CONSTRAINT_NAME"));
			if(!propMap.containsKey(rs.getString("CONSTRAINT_NAME"))){
				propMap.put(rs.getString("CONSTRAINT_NAME"),new ArrayList<String>());
			}
			keyTypeMap.put(rs.getString("CONSTRAINT_NAME"), rs.getString("CONSTRAINT_TYPE"));
			propMap.get(rs.getString("CONSTRAINT_NAME")).add(rs.getString("COLUMN_NAME"));
		}
		
		for (String key :propMap.keySet()){
			String indStr="alter table "+owner+"."+tableName+" add constraint "+key;
			if(!keyTypeMap.containsKey(key))continue;
			//primary, unique and foreign
			if(keyTypeMap.get(key).equalsIgnoreCase("U")){
				indStr=indStr+" unique key ";
			}
			if(keyTypeMap.get(key).equalsIgnoreCase("P")){
				indStr=indStr+" primary key ";
			}
			indStr=indStr+"(";
			int i=0;
			for(String c :propMap.get(key)){
				if(i++==0){
					indStr=indStr+""+c;
				}else{
					indStr=indStr+","+c;
				}
			}
			indStr=indStr+");\r\n";
			out.write(indStr.getBytes());
			
		}
		out.write("\r\n".getBytes());
		rs.close();
		stmt.close();
	}
		
	public void  readTableIndex(String owner,String tableName) throws Exception{
		out.write("--Create Table Indexs \r\n".getBytes());
		Statement stmt=conn.createStatement();
		String sql="select ai.OWNER,ai.INDEX_NAME,aic.COLUMN_NAME,aic.COLUMN_POSITION " +
				" from sys.all_indexes ai,sys.all_ind_columns aic "+
				" where ai.OWNER=aic.INDEX_OWNER "+
				" and ai.INDEX_NAME=aic.INDEX_NAME "+
				" and ai.TABLE_NAME=aic.TABLE_NAME "+
				" and ai.TABLE_NAME='"+tableName.toUpperCase()+"' "+
				" and ai.owner ='"+owner+"'"+
				" order by ai.INDEX_NAME,aic.COLUMN_POSITION ";
		
		_logger.info(sql);
		HashMap<String,ArrayList<String>> propMap=new HashMap<String,ArrayList<String>>();
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			_logger.info("INDEX_NAME : "+rs.getString("INDEX_NAME"));
			if(!propMap.containsKey(rs.getString("INDEX_NAME"))){
				propMap.put(rs.getString("INDEX_NAME"),new ArrayList<String>());
			}
			propMap.get(rs.getString("INDEX_NAME")).add(rs.getString("COLUMN_NAME"));
		}
		
		for (String key :propMap.keySet()){
			String indStr="create index "+owner+"."+key+" on "+owner+"."+tableName;
			indStr=indStr+"(";
			int i=0;
			for(String c :propMap.get(key)){
				if(i++==0){
					indStr=indStr+""+c;
				}else{
					indStr=indStr+","+c;
				}
			}
			indStr=indStr+");\r\n";
			out.write(indStr.getBytes());
			out.write("\r\n".getBytes());
		}
		
		rs.close();
		stmt.close();
		
	}	
	
	
	public void  readTableConsPrimaryKey(String opType,String owner,String primaryKey) throws Exception{
		out.write("--Create Table Constraints \r\n".getBytes());
		String tableName="";
		Statement stmt=conn.createStatement();
		/*String sql="select t.owner,t.table_name,c.COMMENTS from sys.all_all_tables t,sys.all_tab_comments c "+
					" where t.table_name=c.TABLE_NAME "+
					" and t.table_name='"+tableName+"' and t.owner ='"+owner+"' "+
					" order by t.owner,t.table_name";*/
		String sql="select ac.OWNER,ac.CONSTRAINT_NAME,ac.CONSTRAINT_TYPE,ac.TABLE_NAME,acc.COLUMN_NAME,acc.POSITION " +
				" from sys.all_constraints ac ,sys.all_cons_columns acc "  +
				" where ac.OWNER=acc.OWNER  "  +
				" and ac.CONSTRAINT_NAME=acc.CONSTRAINT_NAME "  +
				" and ac.CONSTRAINT_NAME='"+primaryKey.toUpperCase()+"' "  +
				" and ac.owner ='"+owner+"'"+
				" and acc.POSITION IS NOT NULL "+
				" order by ac.CONSTRAINT_NAME, acc.POSITION";
		
		_logger.info(sql);
		HashMap<String,ArrayList<String>> propMap=new HashMap<String,ArrayList<String>>();
		HashMap<String,String> keyTypeMap=new HashMap<String,String>();
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			_logger.info("CONSTRAINT_NAME : "+rs.getString("CONSTRAINT_NAME"));
			if(!propMap.containsKey(rs.getString("CONSTRAINT_NAME"))){
				propMap.put(rs.getString("CONSTRAINT_NAME"),new ArrayList<String>());
			}
			keyTypeMap.put(rs.getString("CONSTRAINT_NAME"), rs.getString("CONSTRAINT_TYPE"));
			propMap.get(rs.getString("CONSTRAINT_NAME")).add(rs.getString("COLUMN_NAME"));
			tableName=rs.getString("TABLE_NAME");
		}
		
		for (String key :propMap.keySet()){
			if(opType.toUpperCase().equals("ALTER")) {
				out.write(("alter table "+owner+"."+tableName+"  drop constraint "+owner+"."+key+" cascade;\r\n").getBytes());
			}
			
			String indStr="alter table "+owner+"."+tableName+" add constraint "+owner+"."+key;
			if(!keyTypeMap.containsKey(key))continue;
			//primary, unique and foreign
			if(keyTypeMap.get(key).equalsIgnoreCase("U")){
				indStr=indStr+" unique key ";
			}
			if(keyTypeMap.get(key).equalsIgnoreCase("P")){
				indStr=indStr+" primary key ";
			}
			indStr=indStr+"(";
			int i=0;
			for(String c :propMap.get(key)){
				if(i++==0){
					indStr=indStr+""+c;
				}else{
					indStr=indStr+","+c;
				}
			}
			indStr=indStr+");\r\n";
			out.write(indStr.getBytes());
			
		}
		out.write("\r\n".getBytes());
		rs.close();
		stmt.close();
	}
		
	public void  readTableIndexUnique(String opType,String owner,String unique) throws Exception{
		out.write("--Create Table Indexs \r\n".getBytes());
		String tableName="";
		Statement stmt=conn.createStatement();
		String sql="select ai.OWNER,ai.INDEX_NAME,aic.COLUMN_NAME,aic.COLUMN_POSITION,ai.TABLE_NAME " +
				" from sys.all_indexes ai,sys.all_ind_columns aic "+
				" where ai.OWNER=aic.INDEX_OWNER "+
				" and ai.INDEX_NAME=aic.INDEX_NAME "+
				" and ai.TABLE_NAME=aic.TABLE_NAME "+
				" and ai.INDEX_NAME='"+unique.toUpperCase()+"' "+
				" and ai.owner ='"+owner+"'"+
				" order by ai.INDEX_NAME,aic.COLUMN_POSITION ";
		
		_logger.info(sql);
		HashMap<String,ArrayList<String>> propMap=new HashMap<String,ArrayList<String>>();
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			_logger.info("INDEX_NAME : "+rs.getString("INDEX_NAME"));
			if(!propMap.containsKey(rs.getString("INDEX_NAME"))){
				propMap.put(rs.getString("INDEX_NAME"),new ArrayList<String>());
			}
			propMap.get(rs.getString("INDEX_NAME")).add(rs.getString("COLUMN_NAME"));
			tableName=rs.getString("TABLE_NAME");
		}
		
		for (String key :propMap.keySet()){
			if(opType.toUpperCase().equals("ALTER")) {
				out.write(("drop index "+owner+"."+key+";\r\n").getBytes());
			}
			
			String indStr="create index "+owner+"."+key+" on "+owner+"."+tableName;
			indStr=indStr+"(";
			int i=0;
			for(String c :propMap.get(key)){
				if(i++==0){
					indStr=indStr+""+c;
				}else{
					indStr=indStr+","+c;
				}
			}
			indStr=indStr+");\r\n";
			out.write(indStr.getBytes());
			out.write("\r\n".getBytes());
		}
		
		rs.close();
		stmt.close();
		
	}	
	public void  readTableGrant(String tableName) throws Exception{
		out.write("--Grant Privilege \r\n".getBytes());
		Statement stmt=conn.createStatement();
		String sql="select atp.GRANTOR,atp.GRANTEE,atp.TABLE_SCHEMA,atp.TABLE_NAME,atp.privilege,atp.GRANTABLE " +
				" from sys.all_tab_privs atp  where  TABLE_NAME='"+tableName.toUpperCase()+"'";
		
		_logger.info(sql);
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			out.write(("GRANT "+String.format("%-10s", rs.getString("privilege")) +" ON "+rs.getString("TABLE_SCHEMA")+"."+tableName.toUpperCase()+" to "+rs.getString("GRANTEE")+";\r\n").getBytes());
			//grant select, insert, update, delete, references, alter, index on RWMSUSER.CONTROLTHING to RWUSER;
		}
		out.write("\r\n".getBytes());
		rs.close();
		stmt.close();
		
	}
	
	public void  readSource(String owner,String name,String type) throws Exception{
		Statement stmt=conn.createStatement();
		String sql="select DISTINCT SC.OWNER,SC.name,SC.TYPE,SC.line,SC.TEXT  from sys.All_Source SC WHERE  owner='"+owner+"' and upper(name)='"+name+"' and  type='"+type+"' order by SC.OWNER,SC.name,SC.line";
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			//_logger.info("OWNER "+rs.getString("OWNER")+" name :"+rs.getString("name")+" TYPE :"+rs.getString("TYPE")+"\t "+rs.getString("TEXT"));
			//_logger.info(rs.getString("TEXT"));
			out.write(rs.getString("TEXT").getBytes());
		}
		out.write("/\r\n".getBytes());
		rs.close();
		stmt.close();
	}

	public void  readView(String owner,String view) throws Exception{
		out.write("\r\n--Create Or Replace View  \r\n".getBytes());
		Statement stmt=conn.createStatement();
		String sql="select AV.OWNER,AV.VIEW_NAME,  AV.text  from sys.All_Views AV WHERE  owner='"+owner+"' and upper(VIEW_NAME)='"+view+"'";
		ResultSet rs=stmt.executeQuery(sql);
		while(rs.next()){
			//_logger.info("OWNER "+rs.getString("OWNER")+" name :"+rs.getString("name")+" TYPE :"+rs.getString("TYPE")+"\t "+rs.getString("TEXT"));
			//_logger.info(rs.getString("TEXT"));
			out.write(("\r\ncreate or replace view "+view+" as \r\n"+rs.getString("TEXT")+";\r\n").getBytes());
			out.write("\r\n".getBytes());
		}
		rs.close();
		stmt.close();
	}
	
	public ArrayList<TableColumns > buildMetaData(ResultSet rs) throws SQLException{
		ArrayList<TableColumns > listTableColumns=new ArrayList<TableColumns > ();
		ResultSetMetaData metaData = rs.getMetaData();
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			TableColumns tc=new TableColumns();
			tc.setColumnName(metaData.getColumnName(i));
			tc.setDataType(metaData.getColumnTypeName(i));
			tc.setTableName(metaData.getTableName(i));
			tc.setDataPrecision(metaData.getPrecision(i));
			tc.setDataScale(metaData.getScale(i));
			_logger.info("--No. "+i+" , Column "+tc.getColumnName()+" , DataType "+tc.getDataType() );
			listTableColumns.add(tc);
		}
		return listTableColumns;
	}
	
	
	public void batchInsert(String schema ,String tableName,String whereSql) throws Exception{
		Connection targetConn=this.dataSource.getConnection();
		targetConn.setAutoCommit(false);
		Statement stmt = targetConn.createStatement();
		ResultSet rs=stmt.executeQuery("SELECT * FROM "+schema+"."+tableName+" "+whereSql);
		ArrayList<TableColumns > listTableColumns=buildMetaData(rs);
		String rowColumns=null;
		for(TableColumns tc : listTableColumns) {
			if(rowColumns==null) {
				rowColumns=tc.getColumnName();
			}else {
				rowColumns+=rowColumns + " , " + tc.getColumnName();
			}
		}
		long insertNum=0;
		while(rs.next()){
			int pos=1;
			StringBuffer rowValues= new StringBuffer();
			for(TableColumns tc : listTableColumns){
				
				String value=" NULL ";
				//_logger.info("--column "+tc.getColumnName()+" , "+tc.getDataType() );
				if(tc.getDataType().equalsIgnoreCase("VARCHAR2")||
						tc.getDataType().equalsIgnoreCase("VARCHAR")||
						tc.getDataType().equalsIgnoreCase("NVARCHAR2")||
						tc.getDataType().equalsIgnoreCase("CHAR")||
						tc.getDataType().equalsIgnoreCase("RAW")||
						tc.getDataType().equalsIgnoreCase("ROWID")
						){
					if(rs.getString(tc.getColumnName())!=null){
						value="'"+ rs.getString(tc.getColumnName())+"'";
					}
				}else if(tc.getDataType().equalsIgnoreCase("NUMBER")){
					if(rs.getString(tc.getColumnName())!=null){
						if(tc.getDataLength()==22&&tc.getDataScale()>0){//NUMBER
							value=rs.getLong(tc.getColumnName())+"";
						}else if(tc.getDataLength()==22&&tc.getDataScale()==0){//INTEGER
							value=rs.getInt(tc.getColumnName())+"";
						}else if(tc.getDataPrecision()==0||tc.getDataScale()==0||tc.getDataScale()==0){//LONG
							value=rs.getLong(tc.getColumnName())+"";
						}else{//DOUBLE
							value=rs.getDouble(tc.getColumnName())+"";
						}
					}
				}else if(tc.getDataType().equalsIgnoreCase("DATE")||
						tc.getDataType().toUpperCase().startsWith("TIMESTAMP")
						){
					value="TO_DATE('"+(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(rs.getTimestamp(tc.getColumnName())))+"','dd-mm-yyyy hh24:mi:ss')";
				}else if(	tc.getDataType().equalsIgnoreCase("BLOB")||
						tc.getDataType().equalsIgnoreCase("CLOB")||
						tc.getDataType().equalsIgnoreCase("NCLOB")||
						tc.getDataType().equalsIgnoreCase("LONG")||
						tc.getDataType().equalsIgnoreCase("BINARY_DOUBLE")||
						tc.getDataType().equalsIgnoreCase("BINARY_FLOAT")
					){
				
				}
				
				if(pos==1) {
					rowValues.append(value);
				}else {
					rowValues.append(" , ").append(value);
				}
				pos++;
			}
			out.write(("INSERT INTO "+schema+"."+tableName+" ( "+rowColumns+" ) VALUES ( "+rowValues+" ) ;\r\n").getBytes());
			insertNum++;
		}
		out.write(("COMMIT ;\r\n").getBytes());
		out.write(("-- "+schema+"."+tableName+" Data Export Count +"+insertNum+" \r\n").getBytes());
		stmt.close();
		targetConn.close();
	}
	
	public void  readExportData(String filePath) throws Exception{
		File txtFile=new File(filePath);
		if(txtFile.exists()){
			InputStreamReader read=new InputStreamReader(new FileInputStream(filePath));
			BufferedReader bReader=new BufferedReader(read);
			String lineText;
			int sourceCount=0;
			while((lineText=bReader.readLine())!=null){
				if(lineText.startsWith("#")||lineText.trim().equals("")){
					//
				}else if(lineText.startsWith("--")){
					out.write(("SELECT '"+lineText.substring(2)+"' FROM DUAL;\r\n").getBytes());
				}else if(lineText.startsWith("++")){
					out.write((lineText.substring(2)+"\r\n").getBytes());
				}else{
					_logger.info(lineText);
					sourceCount++;
					out.write(("-- --No." + sourceCount + " , " + lineText + "\n").getBytes());
					String []param=lineText.split(",");
					//"TYPE", "TYPE BODY", "PROCEDURE", "FUNCTION","PACKAGE", "PACKAGE BODY", "LIBRARY", "ASSEMBLY" or "JAVA SOURCE"
					if(	param[3].toUpperCase().equals("TYPE")
						||param[3].toUpperCase().equals("TYPE BODY")
						||param[3].toUpperCase().equals("PROCEDURE")
						||param[3].toUpperCase().equals("FUNCTION")
						||param[3].toUpperCase().equals("PACKAGE")
						||param[3].toUpperCase().equals("PACKAGE BODY")
						||param[3].toUpperCase().equals("LIBRARY")
						||param[3].toUpperCase().equals("ASSEMBLY")
						||param[3].toUpperCase().equals("JAVA SOURCE")
							) {
						readSource(param[1],param[2].toUpperCase(),param[3]);
					}else if(	(!param[0].toUpperCase().equals("BACKUP"))&&param[3].toUpperCase().equals("TABLE")) {
						if(param[0].toUpperCase().equals("ALTER")){
							readAlterTable(param[1],param[2]);
						}else {
							readTable(param[1],param[2]);
						}
					}else if(	param[3].toUpperCase().equals("VIEW")) {
						readView(param[1],param[2].toUpperCase());
					}else if(	param[3].toUpperCase().equals("PRIMARY")) {
						readTableConsPrimaryKey(param[0],param[1],param[2]);
					}else if(	param[3].toUpperCase().equals("UNIQUE")) {
						readTableIndexUnique(param[0],param[1],param[2]);
					}else if(	param[3].toUpperCase().equals("FOREIGN")) {
						//TODO
					}else if(	param[0].toUpperCase().equals("BACKUP")) {
						out.write(("SELECT '***************备份表 "+param[1]+"."+param[2]+"*****************' FROM DUAL;\n").getBytes());
						out.write(("CREATE TABLE "+param[1]+"."+param[2]+"_"+System.getenv().get("PUBLISH_DATE")+" AS SELECT * FROM "+param[1]+"."+param[2]+";\n\n").getBytes());
					}else if(	param[0].toUpperCase().equals("DATA")) {
						out.write(("SELECT '***************备份表 "+param[1]+"."+param[2]+"*****************' FROM DUAL;\n").getBytes());
						out.write(("CREATE TABLE "+param[1]+"."+param[2]+"_"+System.getenv().get("PUBLISH_DATE")+" AS SELECT * FROM "+param[1]+"."+param[2]+";\n\n").getBytes());
					}else if(	param[0].toUpperCase().equals("FILE")) {
						_logger.info(System.getProperty("APPRUNNER_PATH")+"/conf/"+System.getenv().get("PUBLISH_DATE"));
						File f=new File(System.getProperty("APPRUNNER_PATH")+"/conf/"+System.getenv().get("PUBLISH_DATE"));
						File [] flist=f.listFiles();
						if(flist==null)continue;
						
						for(File fi :flist) {
							if(fi.isDirectory()) {
								
								File sqlFile=new File(System.getProperty("APPRUNNER_PATH")+"/conf/"+System.getenv().get("PUBLISH_DATE")+"/"+fi.getName()+"/"+param[1]);
								_logger.info("read file "+System.getProperty("APPRUNNER_PATH")+"/conf/"+System.getenv().get("PUBLISH_DATE")+"/"+fi.getName()+"/"+param[1]);
								if(sqlFile.exists()) {
									BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("APPRUNNER_PATH")+"/conf/"+System.getenv().get("PUBLISH_DATE")+"/"+fi.getName()+"/"+param[1])));
									String lineData=null;
									out.write(("\r\n").getBytes());
									while((lineData=br.readLine())!=null) {
										out.write(lineData.getBytes());
										out.write(("\r\n").getBytes());
									}
									out.write(("\r\n").getBytes());
									br.close();
								}
							}
							
						}
						
						//out.write(("SELECT '***************备份表 "+param[1]+"."+param[2]+"*****************' FROM DUAL;\n").getBytes());
						//out.write(("CREATE TABLE "+param[1]+"."+param[2]+"_"+System.getenv().get("PUBLISH_DATE")+" AS SELECT * FROM "+param[1]+"."+param[2]+";\n\n").getBytes());
					}
				}
			}
			bReader.close();
			read.close();
		}else{
			_logger.info("");
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public String getExportFilePath() {
		return exportFilePath;
	}

	public void setExportFilePath(String exportFilePath) {
		this.exportFilePath = exportFilePath;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
