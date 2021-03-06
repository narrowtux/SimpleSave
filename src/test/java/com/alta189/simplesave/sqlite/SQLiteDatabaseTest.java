package com.alta189.simplesave.sqlite;

import com.alta189.simplesave.DatabaseFactory;
import com.alta189.simplesave.Field;
import com.alta189.simplesave.Id;
import com.alta189.simplesave.Table;
import com.alta189.simplesave.exceptions.ConnectionException;
import com.alta189.simplesave.exceptions.TableRegistrationException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class SQLiteDatabaseTest {

	@Test
	public void test() {
		SQLiteConfiguration config = new SQLiteConfiguration();
		File tmpfile = null;
		try {
			tmpfile = File.createTempFile("h2test_", ".db");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException occured: " + e.toString());
		}
		assertNotNull(tmpfile);
		config.setPath(tmpfile.getAbsolutePath().substring(0, tmpfile.getAbsolutePath().indexOf(".db")));
		tmpfile.deleteOnExit();
		SQLiteDatabase db = (SQLiteDatabase) DatabaseFactory.createNewDatabase(config);
		try {
			db.registerTable(TestClass.class);
		} catch (TableRegistrationException e) {
			e.printStackTrace();
			fail("Exception occured too early! " + e.toString());
		}
		try {
			db.registerTable(TestClass2.class);
		} catch (TableRegistrationException e) {
			e.printStackTrace();
			fail("Exception occured too early! " + e.toString());
		}
		try {
			db.connect();
		} catch (ConnectionException e) {
			fail("Failed to connect to database! " + e.toString());
		}
		TestClass one = new TestClass();
		one.setName("Hello World");
		db.save(TestClass.class, one);
		TestClass two = new TestClass();
		two.setName("Cruel World");
		db.save(TestClass.class, two);
		assertEquals(db.getTableRegistration(TestClass.class).getTableClass(), TestClass.class);
		assertEquals(db.select(TestClass.class).execute().find().size(), 2);
		assertEquals(db.select(TestClass.class).where().equal("name", "Hello World").execute().findOne().name, "Hello World");
		try {
			db.close();
		} catch (ConnectionException e) {
			fail("Failed to close database! " + e.toString());
		}
		tmpfile.delete();
	}

	@Table("test")
	public static class TestClass {
		@Id
		private long id;

		@Field
		private String name;

		protected long getId() {
			return id;
		}

		protected void setId(long id) {
			this.id = id;
		}

		protected String getName() {
			return name;
		}

		protected void setName(String name) {
			this.name = name;
		}
	}

	@Table("tEsT")
	public static class TestClass2 {
		@Id
		private long id;

		@Field
		private String name;
	}
}
