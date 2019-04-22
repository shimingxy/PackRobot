/**
 * 
 */
package com.packrobot.db;

/**
 * @author user
 *
 */
public class TableColumns {
	String owner;
	String tableName;
	String tableComments;
	String columnName;
	String dataType;
	int dataLength;
	int dataScale;
	int dataPrecision; 
	String columnComments;
	String nullAble;
	//DATA_DEFAULT
	String dataDefault;
	
	
	public TableColumns(){}

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

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getDataScale() {
		return dataScale;
	}

	public void setDataScale(int dataScale) {
		this.dataScale = dataScale;
	}

	public int getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(int dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getColumnComments() {
		return columnComments;
	}

	public void setColumnComments(String columnComments) {
		this.columnComments = columnComments;
	}

	public String getNullAble() {
		return nullAble;
	}

	public void setNullAble(String nullAble) {
		this.nullAble = nullAble;
	}

	public String getDataDefault() {
		return dataDefault;
	}

	public void setDataDefault(String dataDefault) {
		this.dataDefault = dataDefault;
	}

	@Override
	public String toString() {
		return "TableColumns [owner=" + owner + ", tableName=" + tableName + ", tableComments=" + tableComments
				+ ", columnName=" + columnName + ", dataType=" + dataType + ", dataLength=" + dataLength
				+ ", dataScale=" + dataScale + ", dataPrecision=" + dataPrecision + ", columnComments=" + columnComments
				+ ", nullAble=" + nullAble + ", dataDefault=" + dataDefault + "]";
	}
	

	
}
