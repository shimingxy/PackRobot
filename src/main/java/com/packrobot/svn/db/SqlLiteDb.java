package com.packrobot.svn.db;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

public class SqlLiteDb {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String DB_NAME="d:/mhshi.db";
		 File dbFile = new File(DB_NAME);
		   dbFile.delete();
		         
		   SqlJetDb db = SqlJetDb.open(dbFile, true);
		   db.getOptions().setAutovacuum(true);
		   db.beginTransaction(SqlJetTransactionMode.WRITE);
		   try {
		     db.getOptions().setUserVersion(1);
		   } finally {
		     db.commit();
		   }
		   
		   
		String createTableQuery="CREATE TABLE employees (second_name TEXT NOT NULL PRIMARY KEY , first_name TEXT NOT NULL,    date_of_birth INTEGER NOT NULL)";

		String createFirstNameIndexQuery="CREATE INDEX full_name_index ON employees(first_name,second_name)";

		String createDateIndexQuery="CREATE INDEX dob_index ON employees(date_of_birth)";
	
		db.beginTransaction(SqlJetTransactionMode.WRITE);
		  try {            
		    db.createTable(createTableQuery);
		    db.createIndex(createFirstNameIndexQuery);
		    db.createIndex(createDateIndexQuery);
		  } finally {
		    db.commit();
		  }
		  
		  
		  Calendar calendar = Calendar.getInstance();
		  calendar.clear();
		 
		  db.beginTransaction(SqlJetTransactionMode.WRITE);
		  try {
		    ISqlJetTable table = db.getTable("employees");
		    calendar.set(1991, 4, 19);
		    table.insert("Prochaskova", "Elena", calendar.getTimeInMillis());
		    calendar.set(1967, 5, 19);
		    table.insert("Scherbina", "Sergei", calendar.getTimeInMillis());
		    calendar.set(1987, 6, 19);
		    table.insert("Vadishev", "Semen", calendar.getTimeInMillis());
		    calendar.set(1982, 7, 19);
		    table.insert("Sinjushkin", "Alexander", calendar.getTimeInMillis());
		    calendar.set(1979, 8, 19);
		    table.insert("Stadnik", "Dmitry", calendar.getTimeInMillis());
		    calendar.set(1977, 9, 19);
		    table.insert("Kitaev", "Alexander", calendar.getTimeInMillis());
		  } finally {
		    db.commit();
		  }
		  
		  
		  db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
		  try {
			ISqlJetTable table = db.getTable("employees");
		    printRecords(table.open());
		  } finally {
		    db.commit();
		  }
		  
	}
	
	
	  private static void printRecords(ISqlJetCursor cursor) throws SqlJetException {
		    try {
		      if (!cursor.eof()) {
		        do {
		          System.out.println(cursor.getRowId() + " : " + 
		                             cursor.getString("second_name") + " " + 
		                             cursor.getString("first_name") + " was born on " + 
		                             formatDate(cursor.getInteger("date_of_birth")));
		         } while(cursor.next());
		      }
		    } finally {
		      cursor.close();
		    }
		  }
	  private static String formatDate(long time) {
	        return SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(new Date(time));
	    }
}
