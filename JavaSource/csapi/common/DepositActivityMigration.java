package csapi.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DepositActivityMigration {

	static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";  
	   

	   //  Database credentials
	   static final String USER = "sqlinst1";
	   static final String PASS = "Sqlk0rma"; 
	   
	public static void main(String[] args) {
		String DB_URL = "jdbc:sqlserver://csdb01:1433;databaseName="+args[0];
		 Connection conn = null;
		   Statement stmt1 = null;
		   Statement stmt2 = null;
		   Statement stmt3 = null;
		   String sql="";
		   ResultSet rs =null;
		   ResultSet rs1 =null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

		      //STEP 3: Open a connection
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      int primaryId=0;
		      double currentAmnt=0;
		      int i=0;
		      
		      
		      //STEP 4: Execute a query
		      stmt1 = conn.createStatement();
		      // Now you can extract all the records
		      // to see the updated records
		      String sql1 = "SELECT distinct activity_id from ref_act_deposit order by activity_id asc";
		      rs = stmt1.executeQuery(sql1);

		      while(rs.next()){
		    	  primaryId=0;
		    	  currentAmnt=0;
		    	  String sql2 = "SELECT ID , LKUP_DEPOSIT_TYPE_ID , AMOUNT from deposit where activity_id="+rs.getInt("activity_id")+" order by id asc";
		    	  stmt2 = conn.createStatement();
			      rs1 = stmt2.executeQuery(sql2);
			      while(rs1.next()){
			    	  
			    	  if(rs1.getInt("LKUP_DEPOSIT_TYPE_ID")==1)
			    	  {
			    		  if(i==0)
			    		  {
			    		  primaryId=rs1.getInt("ID");
			    		  }
			    		  currentAmnt = currentAmnt + rs1.getDouble("AMOUNT");
			    		  i=i+1;
			    	  }
			    	  
			    	  if(rs1.getInt("LKUP_DEPOSIT_TYPE_ID")==3)
			    	  {
			    		  i=0;
			    		  currentAmnt = currentAmnt - rs1.getDouble("AMOUNT");
			    	  }
			    	  
			    	  
			    	  String sql3 ="";
			    	  if(rs1.getInt("LKUP_DEPOSIT_TYPE_ID")==1)
			    	  {
			    	  sql3="UPDATE DEPOSIT SET CURRENT_AMOUNT= " +currentAmnt+" , PARENT_ID=0 WHERE ID = "+rs1.getInt("ID");
			    	  } 
			    	  
			    	  if(rs1.getInt("LKUP_DEPOSIT_TYPE_ID")==3)
			    	  {
			    		  sql3="UPDATE DEPOSIT SET CURRENT_AMOUNT= " +currentAmnt+" , PARENT_ID="+primaryId+" WHERE ID = "+rs1.getInt("ID");
			    	  }
			    	  
			    	  stmt3 = conn.createStatement();
			          stmt3.executeUpdate(sql3); 
			    	  
			      }
			      
			      rs1.close();
		        
		      
		      }
		     
		      
           rs.close();
	
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		    	  
		    	  rs.close();
			      rs1.close();
		         if(stmt1!=null || stmt2!=null || stmt3!=null)
		            conn.close();
		      }catch(SQLException se){
		      }// do nothing
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try

	}

}
