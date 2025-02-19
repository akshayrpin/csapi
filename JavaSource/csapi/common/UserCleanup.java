package csapi.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserCleanup {

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
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      
		    //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt1 = conn.createStatement();
		      // Now you can extract all the records
		      // to see the updated records
		      String sql1 = "select distinct(EMAIL) AS EMAIL from users where email !='' and email is not null order by email asc";
		      System.out.println(sql1);
		      rs = stmt1.executeQuery(sql1);
              String email="";
		      while(rs.next()){ 
		    	  String sql2 = "";
		    	  if(rs.getString("EMAIL").contains("'"))
		    	  {
		    	   sql2 = "select ID from users where upper(REPLACE(email,CHAR(39),'')) =upper('"+rs.getString("EMAIL").replaceAll("'", "")+"')";
		    	  }else{
		    		  sql2 = "select ID from users where upper(email) =upper('"+rs.getString("EMAIL")+"')"; 
		    	  }
			      System.out.println(sql2);
			      stmt2 = conn.createStatement();
			      
			      rs1 = stmt2.executeQuery(sql2);
			      String updateUserIds="";
			      String deleteUserIds="";
			      int i=0;
			      int userId=0;
			      while(rs1.next()){
			    	  stmt3= conn.createStatement();
			    	  if(i<=0)
			    	  {
			    		  userId=rs1.getInt("ID");
			    	  }
			    	  
			    	  updateUserIds= "UPDATE ACTIVITY SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	 // System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE ACTIVITY SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE LSO SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	 // System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE LSO SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE NOTES SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE NOTES SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE PEOPLE SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	 //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE PEOPLE SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE PEOPLE SET USERS_ID="+userId+" WHERE USERS_ID="+rs1.getInt("ID");
			    	  //ystem.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE RESOLUTION SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE RESOLUTION SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_USERS_GROUP SET USERS_ID="+userId+" WHERE USERS_ID="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_USERS_DEPOSIT SET USERS_ID="+userId+" WHERE USERS_ID="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_USERS SET USERS_ID="+userId+" WHERE USERS_ID="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	 // updateUserIds= "UPDATE REF_TEAM SET USERS_ID="+userId+" WHERE USERS_ID="+rs1.getInt("ID");
			    	//  stmt3.executeUpdate(updateUserIds);
			    	//  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE RESOLUTION_DETAIL SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE RESOLUTION_DETAIL SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_ACT_HOLDS SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_ACT_HOLDS SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_ACT_NOTES SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_ACT_NOTES SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_ACT_ATTACHMENTS SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_PROJECT_RESOLUTION SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_PROJECT_RESOLUTION SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_PROJECT_NOTES SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_PROJECT_NOTES SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_PROJECT_HOLDS SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_PROJECT_HOLDS SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_LSO_NOTES SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_LSO_NOTES SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_LSO_HOLDS SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_LSO_HOLDS SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  
			    	  updateUserIds= "UPDATE REF_LSO_ATTACHMENTS SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_LSO_HOLDS SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_LSO_HOLDS SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_COMBOREVIEW_TEAM SET REF_TEAM_ID="+userId+" WHERE REF_TEAM_ID="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE VEHICLE_INFORMATION SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE VEHICLE_INFORMATION SET UPDATED_BY="+userId+" WHERE UPDATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE ATTACHMENTS SET CREATED_BY="+userId+" WHERE CREATED_BY="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	  updateUserIds= "UPDATE REF_APPOINTMENT_USERS SET USERS_ID="+userId+" WHERE USERS_ID="+rs1.getInt("ID");
			    	  //System.out.println(updateUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(updateUserIds);
			    	  //if(stmt3!=null){stmt3.close();}
			    	  
			    	 // PAYMENT
			    	  //PAYMENT_DETAL
			    	 // PEOPLE
			    	 /// PROJECT
			    	//apn_OWNER_FIELD_VALUE	    	
			    	  
			    	  if(userId!=rs1.getInt("ID"))
			    	  {
			      
			    	  deleteUserIds= "DELETE FROM USERS WHERE ID="+rs1.getInt("ID") +"";
			    	  //System.out.println(deleteUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(deleteUserIds);
			    	  //if(stmt3!=null){stmt3.close();} 
			    	  
			    	  deleteUserIds= "DELETE FROM USERS_FIELD_VALUE WHERE USERS_ID="+rs1.getInt("ID");
			    	  //System.out.println(deleteUserIds);
			    	  //stmt3= conn.createStatement();
			    	  stmt3.executeUpdate(deleteUserIds);
			    	  //if(stmt3!=null){stmt3.close();} 
			    	  
			    	  }
			    		  
			    	  i++;
			    	  
			    	  if(stmt3!=null){stmt3.close();} 
			    	  
			      }
			      
			      if(rs1!=null){rs1.close();} 
			      if(stmt2!=null){stmt2.close();} 
			      
            }
		      if(rs!=null){rs.close();} 
		      if(conn!=null){conn.close();} 
		      if(stmt1!=null){stmt1.close();} 
		      if(stmt2!=null){stmt2.close();} 
		      if(stmt3!=null){stmt3.close();} 
		      System.out.println(" Completed successfully ");
		    
		   }catch(Exception e)
		   {
			 e.printStackTrace();  
		   }
	}
	
	 
}
