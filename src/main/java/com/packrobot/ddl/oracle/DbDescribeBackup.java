/**
 * 
 */
package com.packrobot.ddl.oracle;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.packrobot.BusinessTask;

/**
 * @author user
 *
 */
public class DbDescribeBackup   implements BusinessTask{
	private static final Logger _logger = LoggerFactory.getLogger(DbDescribeBackup.class);
	public String sqlBackupSource;
	DataSource dataSource;
	/**
	 * 
	 */
	public DbDescribeBackup() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		
		_logger.info("BACKUP_DATE "+System.getenv().get("BACKUP_DATE"));
		
		Connection conn=dataSource.getConnection();
		Statement stmt=conn.createStatement();
		sqlBackupSource=sqlBackupSource.replaceAll("_BACKUP_DATE_", System.getenv().get("BACKUP_DATE"));
		String []sqls=sqlBackupSource.split(";");
		for(String sql : sqls) {
			if(!sql.trim().equals("")) {
				stmt.addBatch(sql);
				_logger.info("Execute Batch SQL \r\n"+sql+"");
			}
		}
		stmt.executeBatch();
		
		stmt.close();
		conn.close();
	}

	public String getSqlBackupSource() {
		return sqlBackupSource;
	}

	public void setSqlBackupSource(String sqlBackupSource) {
		this.sqlBackupSource = sqlBackupSource;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
