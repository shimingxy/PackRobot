package com.packrobot.db;

public class TableConst {
	String tableName;
	String constName;
	String colNames;
	public TableConst(String tableName, String constName, String colNames) {
		super();
		this.tableName = tableName;
		this.constName = constName;
		this.colNames = colNames;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getConstName() {
		return constName;
	}
	public void setConstName(String constName) {
		this.constName = constName;
	}
	public String getColNames() {
		return colNames;
	}
	public void setColNames(String colNames) {
		this.colNames = colNames;
	}
	
	
}
