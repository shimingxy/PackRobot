package com.packrobot.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnUtil
{


  public static Connection createConnection(String url, String user, String pwd, String dbType)
  {
	  Connection conn=null;
    try
    {
      if (dbType.equals("ORACLE")) {
        Class.forName("oracle.jdbc.OracleDriver");
      } else if (dbType.equals("MYSQL")) {
        Class.forName("com.mysql.jdbc.Driver");
      }else if (dbType.equals("DB2")) {
          Class.forName("com.ibm.db2.jcc.DB2Driver");
      }
      
      
      conn = java.sql.DriverManager.getConnection(url, user, pwd);
      return conn;
    } catch (ClassNotFoundException e) {
      System.out.println("ClassNotFoundException鍔犺浇鏁版嵁搴撻┍鍔ㄥけ璐�");
      return null;
    } catch (SQLException e) {
      System.out.println("SQLException鏁版嵁搴撹繛鎺ュけ璐ワ紝璇峰惎鍔ㄦ暟鎹簱鎴栬�妫�煡缃戠粶銆�"); }
    return null;
  }
  





 

  public void releaseConnection(Connection conn)
  {
    if (conn != null) {
      try {
    	  conn.close();
      } catch (SQLException e) {
        System.out.println("鏁版嵁搴撳叧闂け璐ャ�");
      }
    }
  }
  

  public static void releaseConnection(Connection conn,java.sql.Statement stmt, ResultSet rs)
  {
    if (rs != null)
      try {
        rs.close();
        rs = null;
      } catch (SQLException e) {
        System.out.println("鏁版嵁搴撳叧闂け璐ャ�");
      }
    if (stmt != null)
      try {
        stmt.close();
        stmt = null;
      } catch (SQLException e) {
        System.out.println("鏁版嵁搴撳叧闂け璐ャ�");
      }
    if (conn != null) {
      try {
    	  conn.close();
    	  conn = null;
      } catch (SQLException e) {
        System.out.println("鏁版嵁搴撳叧闂け璐ャ�");
      }
    }
  }
  
  public class DBTYPE
  {
    public static final String ORACLE = "ORACLE";
    public static final String MYSQL = "MYSQL";
    
    public DBTYPE() {}
  }
  
  public static void main(String[] args)  {
	  createConnection("jdbc:db2://127.0.0.1:50000/sample","db2admin","db2admin","DB2");
  }
}
