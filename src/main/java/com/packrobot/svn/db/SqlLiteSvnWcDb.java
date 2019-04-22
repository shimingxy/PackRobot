package com.packrobot.svn.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;
/*
 * 
	wc_id            INTEGER NOT NULL
	         REFERENCES WCROOT (id),
	local_relpath    TEXT    NOT NULL,
	op_depth         INTEGER NOT NULL,
	parent_relpath   TEXT,
	repos_id         INTEGER REFERENCES REPOSITORY (id),
	repos_path       TEXT,
	revision         INTEGER,
	presence         TEXT    NOT NULL,
	moved_here       INTEGER,
	moved_to         TEXT,
	kind             TEXT    NOT NULL,
	properties       BLOB,
	depth            TEXT,
	checksum         TEXT,
	symlink_target   TEXT,
	changed_revision INTEGER,
	changed_date     INTEGER,
	changed_author   TEXT,
	translated_size  INTEGER,
	last_mod_time    INTEGER,
	dav_cache        BLOB,
	file_external    INTEGER,
	inherited_props  BLOB,
 */

public class SqlLiteSvnWcDb {
	private static final Logger _logger = LoggerFactory.getLogger(SqlLiteSvnWcDb.class);
	String wcPath;
	File dbFile;
	
	public SqlLiteSvnWcDb(String wcPath) {
		this.wcPath = wcPath;
		this.dbFile = new File(wcPath+".svn/wc.db");
	}
	public SqlLiteSvnWcDb(File wcFile) {
		this.dbFile = wcFile;
	}

	public ArrayList<SvnNodes> getAllNodes() throws Exception {
		 ArrayList<SvnNodes> svnNodesList=new ArrayList<SvnNodes>();
		 _logger.info(""+dbFile+"  exists  "+dbFile.exists());
		 if(dbFile.exists()) {
			 _logger.info("SqlJetVersion "+org.tmatesoft.sqljet.core.SqlJetVersion.getBuildNumberString());
			 SqlJetDb db = SqlJetDb.open(dbFile, true);
			 db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			  try {
				ISqlJetTable table = db.getTable("NODES");
				ISqlJetCursor cursor=table.open();
				 if (!cursor.eof()) {
				        do {
				        	SvnNodes node=new SvnNodes();
				        	node.setRowId(cursor.getRowId());
				        	node.setLocalRelpath(cursor.getString("local_relpath"));
				        	node.setRevision(cursor.getInteger("revision"));
				        	node.setKind(cursor.getString("kind"));
				        	node.setChangedAuthor(cursor.getString("changed_author"));
				        	node.setChangedDate(formatDate(cursor.getInteger("changed_date")));
				        	node.setChangedRevision(cursor.getInteger("changed_revision"));
				        	node.setReposPath(cursor.getString("repos_path"));
				        	_logger.info(""+ node);
				        	svnNodesList.add(node);
	
				         } while(cursor.next());
				}
			  } finally {
			    db.commit();
			  }
			  db.close();
		 }
		 return svnNodesList;
	}

	private static String formatDate(long time) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time / 1000L));
	}
	

	public File getDbFile() {
		return dbFile;
	}

	public void setDbFile(File dbFile) {
		this.dbFile = dbFile;
	}

	public static void main(String[] args) throws Exception {
		SqlLiteSvnWcDb wc=new SqlLiteSvnWcDb("D:/svn1/");
		wc.getAllNodes();
	}

}
