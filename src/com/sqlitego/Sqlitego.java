package com.sqlitego;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sqlitego {
	
	private String table = null;
	private String tableurl = null;
	private String primkey = null;
	
	//THIS REPRESENTS THE DATA FROM THE TABLE
	class data{
		
		String id;
		String name;
		
		data(String id, String name){
			this.id = id;
			this.name = name;
		}
	}
	
   
    //GET CONNECTION
    private  Connection GetConn() {
    	
    	try {
    		return DriverManager.getConnection(tableurl);
    	} catch (SQLException e) {
    		return null;
    	}
    	
    }
    
    
    //SETUP SQLITEGO
    public  boolean setup(String db, String tb, String pmk) {
    	
        String url = "jdbc:sqlite:" + db + ".db"; // Creates file if it doesn't exist
     
        try (Connection con = DriverManager.getConnection(url)) {
        	
            
            tableurl = url;
            table = tb;
            primkey = pmk;
            return true;
            
        } catch (SQLException e) {
        	
           return false;
            
        }
    	
    	
    }
    
    
    //GET ALL DATA IN THE DATA BASE RETURNS ARRAY OF OBJECT
    public  data[]  readAll() {
    	
    	
        try(Connection conn = GetConn();
            Statement smt = conn.createStatement();
            ResultSet result = smt.executeQuery( "SELECT * FROM " + table +";" );) {
        	
        	int count = count();
        	
        	data[] datas = new data[count];
        	
        	for(int x = 0; x < count; x++) {
        		result.next();
        		datas[x] = new data(result.getString(1), result.getString(2));
        		
        	}

            
            return datas;
            
        } catch (SQLException e) {
        	System.out.println(e);
            return null;
            
        }
    	
    	
    }
    
  //UPDATE DATA IN THE DATA BASE (!REMINDER! YOURE PRIMARY COLOUMN IS USED FOR CONDITION) RETURNS OBJECT
    public  data  Read(String condition) {
    	

        try (Connection conn = GetConn();
        	 Statement smt = conn.createStatement();
        	 ResultSet result = smt.executeQuery( "SELECT * FROM " + table + " where " + primkey + "="+condition);){
        	
            data res = new data(result.getString(1), result.getString(2));
            return res;
            
        } catch (SQLException e) {
        	System.out.println(e);
            return null;
            
        }
    	
    	
    }
    
    //CREATE/INSERT  DATA IN THE DATA BASE
    public  boolean  Insert( String row, String values) {
    	
       
        try {
        	String sql = "INSERT INTO " + table + " (" + row + ") VALUES (" + values + ")";
        	Connection conn = GetConn();
        	Statement smt = conn.createStatement();
            smt.executeUpdate(sql);
            conn.close();
            smt.close();
            return true;
            
        } catch (SQLException e) {
        	
            return false;
            
        }
    	
    	
    }
    
    
    //UPDATE DATA IN THE DATA BASE (!REMINDER! YOURE PRIMARY COLOUMN IS USED FOR CONDITION)
    public  boolean  Update( String col, String values, String condition) {
    	
        
        try {
        	String sql = "UPDATE " + table + " set " + col + " = " + values + " where " + primkey + "="+condition ;
        	Connection conn = GetConn();
        	Statement smt = conn.createStatement();
            smt.executeUpdate(sql);
            conn.close();
            smt.close();
            return true;
            
        } catch (SQLException e) {
        	System.out.print(e);
            return false;
            
        }
    	
    	
    }
    
    //DELETE SPECICIFC DATA IN THE DATA BASE (!REMINDER! YOURE PRIMARY COLOUMN IS USED FOR CONDITION)
    public  boolean  Delete( String condition) {
    	
        
        try {
        	String sql = "DELETE " + "from "+ table + " where " + primkey + "="+condition ;
        	Connection conn = GetConn();
        	Statement smt = conn.createStatement();
            smt.executeUpdate(sql);
            conn.close();
            smt.close();
            return true;
            
        } catch (SQLException e) {
        	System.out.print(e);
            return false;
            
        }
    	
    	
    }
    
    //CREATE TABLE USING SCHEMA
    public  boolean createTable(String schema) {
        try (Connection conn = GetConn();
             Statement smt = conn.createStatement()) {

            smt.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (" + schema + ")");
            return true;

        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }
    
    
    public  boolean exists(String condition) {
        try (Connection conn = GetConn();
             Statement smt = conn.createStatement()) {

            ResultSet rs = smt.executeQuery(
                "SELECT 1 FROM " + table + " WHERE " + primkey + "=" + condition + " LIMIT 1"
            );

            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }
    
    
    public  int count() {
        try (Connection conn = GetConn();
             Statement smt = conn.createStatement()) {

            ResultSet rs = smt.executeQuery("SELECT COUNT(*) FROM " + table);
            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            return 0;
        }
    }
    

   

    
}
