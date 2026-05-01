package com.sqlitego;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sqlitego.Sqlitego.data;


public class Main {

	public static void main(String[] args) {

		Sqlitego my_sql = new Sqlitego();
		my_sql.setup("sample", "users", "id");
		int count = my_sql.count();
		System.out.println("number of row: " + count);
		//my_sql.Update("name", "'Ara Mae'", "3");
		//my_sql.Insert("id,name", "20,'Amaw'");
		//my_sql.Delete("20");
		data[] res = my_sql.readAll();
		
		for(data a : res)
		
			System.out.print("\nID: " + a.id + " NAME: " + a.name);
	}
}