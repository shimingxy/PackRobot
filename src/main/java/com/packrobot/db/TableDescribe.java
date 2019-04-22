/**
 * 
 */
package com.packrobot.db;

/**
 * @author user
 *
 */
public class TableDescribe {
	String owner;
	String tableName;
	String tableComments;
	
	
	public TableDescribe(){}


	public TableDescribe(String owner, String tableName, String tableComments) {
		super();
		this.owner = owner;
		this.tableName = tableName;
		this.tableComments = tableComments;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public String getTableComments() {
		return tableComments;
	}


	public void setTableComments(String tableComments) {
		this.tableComments = tableComments;
	}

	
}
