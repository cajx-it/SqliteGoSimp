# SqliteGoSimp

```
 ____   ___  _ _ _         ____
/ ___| / _ \| (_) |_ ___  / ___| ___
\___ \| | | | | | __/ _ \| |  _ / _ \
 ___) | |_| | | | ||  __/| |_| | (_) |
|____/ \__\_\_|_|\__\___| \____|\___/
  Simple SQLite wrapper for Java.
```

A lightweight, no-fuss Java utility class for working with SQLite databases. No ORMs, no annotations — just plain and simple database operations you can drop into any Java project.

---

## Installation

> **Prerequisite:** The **SQLite JDBC driver** must be added to your project first. See [SQLite-JDBC Setup in Eclipse](#sqlite-jdbc-setup-in-eclipse) below.

1. Clone or download this repository.
2. Copy `SqliteGo.java` from the `src/` folder into your own project's `src/` directory.
3. Done.

To see working examples of every method, check `Main.java` in `src/`.

---

## Quick Start

```java
Sqlitego db = new Sqlitego();

// Connect to (or create) a database file called "mydb.db",
// target table "users", with "id" as the primary key column
db.setup("mydb", "users", "id");

// Create the table
db.createTable("id INTEGER PRIMARY KEY, name TEXT");

// Insert a row
db.Insert("id, name", "1, 'Alice'");

// Read all rows
Sqlitego.data[] rows = db.readAll();
for (Sqlitego.data row : rows) {
    System.out.println("ID: " + row.id + " | Name: " + row.name);
}
```

---

## ⚠️ String Column Constraint

When passing a **string value** into any method, you **must** wrap it in single quotes **inside** the Java string, like this:

```java
"'your text here'"
```

This applies to `Insert`, `Update`, and any condition value that is a string type.

**Examples:**

```java
// ✅ Correct
db.Insert("id, name", "1, 'Alice'");
db.Update("name", "'Bob'", "1");

// ❌ Wrong — will cause a SQL error
db.Insert("id, name", "1, Alice");
db.Update("name", "Bob", "1");
```

Numeric values do **not** need the extra quotes:

```java
// ✅ Numeric values — no single quotes needed
db.Insert("id, name", "1, 'Alice'");
db.Delete("1");
db.exists("1");
```

---

## Methods

### `setup(String db, String table, String primaryKey)`
Connects to a SQLite database file. Creates the `.db` file if it doesn't exist. **Must be called before anything else.**

```java
Sqlitego db = new Sqlitego();
db.setup("sample", "users", "id");
// Opens or creates "sample.db", targets table "users", primary key is "id"
```

---

### `createTable(String schema)`
Creates the table if it does not already exist, using the schema you provide.

```java
db.createTable("id INTEGER PRIMARY KEY, name TEXT");
```

---

### `Insert(String columns, String values)`
Inserts a new row into the table. Returns `true` on success, `false` on failure.

```java
db.Insert("id, name", "1, 'Alice'");
db.Insert("id, name", "2, 'Bob'");
```

---

### `Read(String condition)`
Reads a single row where the primary key matches the condition. Returns a `data` object with `id` and `name` fields.

```java
Sqlitego.data row = db.Read("1");
System.out.println(row.id + " | " + row.name);

// If the primary key is a string column:
Sqlitego.data row = db.Read("'Alice'");
```

---

### `readAll()`
Returns all rows in the table as an array of `data` objects.

```java
Sqlitego.data[] rows = db.readAll();
for (Sqlitego.data row : rows) {
    System.out.println("ID: " + row.id + " | Name: " + row.name);
}
```

---

### `Update(String column, String value, String condition)`
Updates a column's value on the row where the primary key matches the condition. Returns `true` on success.

```java
// Update name where id = 1
db.Update("name", "'Charlie'", "1");

// Update with a numeric value
db.Update("score", "99", "1");
```

---

### `Delete(String condition)`
Deletes the row where the primary key matches the condition. Returns `true` on success.

```java
// Delete where id = 2
db.Delete("2");

// Delete where primary key is a string
db.Delete("'Alice'");
```

---

### `exists(String condition)`
Returns `true` if a row exists with a primary key matching the condition, `false` otherwise.

```java
if (db.exists("1")) {
    System.out.println("Row found.");
} else {
    System.out.println("Not found.");
}
```

---

### `count()`
Returns the total number of rows in the table as an `int`.

```java
int total = db.count();
System.out.println("Total rows: " + total);
```

---

## SQLite-JDBC Setup in Eclipse

SqliteGoSimp uses the [SQLite JDBC driver by Xerial](https://github.com/xerial/sqlite-jdbc) to connect Java to SQLite. You need to manually add this `.jar` to your Eclipse project.

### Step 1 — Download the JAR

Go to the [Xerial releases page](https://github.com/xerial/sqlite-jdbc/releases) and download the latest `sqlite-jdbc-x.x.x.jar`.

### Step 2 — Add it to your Eclipse project

1. Create a `lib/` folder inside your Eclipse project root and paste the `.jar` there (optional but recommended).
2. Right-click your project in the **Package Explorer** → **Properties**.
3. Navigate to **Java Build Path** → **Libraries** tab.
4. Click **Add JARs...** if the file is inside your project, or **Add External JARs...** if it's stored elsewhere.
5. Browse to the `sqlite-jdbc-x.x.x.jar` file and click **Open**.
6. Click **Apply and Close**.

### Step 3 — Verify the setup

Add this snippet to your `main` and run it. If no error is thrown, the driver is working correctly:

```java
import java.sql.DriverManager;

DriverManager.getConnection("jdbc:sqlite:test.db");
System.out.println("SQLite JDBC is working!");
```

---

## Notes

- The `data` inner class currently holds two fields: `id` and `name`. If your table has different or more columns, modify `Sqlitego.java` to match your schema.
- The primary key set in `setup()` is the condition column used by `Read`, `Update`, `Delete`, and `exists`.
- `setup()` must always be the first method called before any database operation.
