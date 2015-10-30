package com.daanendaron.mp3;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {

	private Connection sqliteConnection = null;

	public SQLite(File file) {
		try {
			Class.forName("org.sqlite.JDBC");
			sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return sqliteConnection;
	}

	public int executeUpdate(String update) {
		try {
			Statement statement = sqliteConnection.createStatement();
			return statement.executeUpdate(update);
		} catch (SQLException e) {
			System.err.println("Error while executing update: " + e.getMessage());
		}
		return -1;
	}

	public ResultSet executeQuery(String query) {
		try {
			Statement statement = sqliteConnection.createStatement();
			return statement.executeQuery(query);
		} catch (SQLException e) {
			System.err.println("Error while executing query: " + e.getMessage());
		}
		return null;
	}
}