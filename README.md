# SqliteGoSimp

```
 ____   ___  _ _ _         ____
/ ___| / _ \| (_) |_ ___  / ___| ___
\___ \| | | | | | __/ _ \| |  _ / _ \
 ___) | |_| | | | ||  __/| |_| | (_) |
|____/ \__\_\_|_|\__\___| \____|\___/
  Simple SQLite wrapper for Java.
```

[![GitHub release](https://img.shields.io/github/v/release/cajx-it/SqliteGoSimp?color=blue&label=release)](https://github.com/cajx-it/SqliteGoSimp/releases/latest)
[![GitHub downloads](https://img.shields.io/github/downloads/cajx-it/SqliteGoSimp/total?color=brightgreen)](https://github.com/cajx-it/SqliteGoSimp/releases)
[![GitHub license](https://img.shields.io/github/license/cajx-it/SqliteGoSimp)](https://github.com/cajx-it/SqliteGoSimp/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.java.com)
[![SQLite](https://img.shields.io/badge/SQLite-JDBC-lightblue)](https://github.com/xerial/sqlite-jdbc)

A lightweight, no-fuss Java utility class for working with SQLite databases. No ORMs, no annotations — just plain and simple database operations you can drop into any Java project.

> **v2 Update:** `readAll()` and `Read()` now return a `Map<Integer, ArrayList<String>>` instead of a fixed `data` object, making the library work with **any table schema** regardless of how many columns you have. A new `Print()` helper method is also included for quick console output. `setup()` now requires you to pass the number of columns in your table.

---

## Installation


> **Prerequisite:** The **SQLite JDBC driver** must also be added. See [Setting Up JARs in Eclipse](#setting-up-jars-in-eclipse) below.

1. Download `sqlitego.jar` from the [releases page](../../releases).
2. Add it to your Eclipse project alongside the SQLite JDBC JAR. See [Setting Up JARs in Eclipse](#setting-up-jars-in-eclipse) for step-by-step instructions.
3. Import the class in your code:

```java
import com.sqlitego.Sqlitego;
```


---

## Quick Start

```java
import com.sqlitego.Sqlitego;
import java.util.ArrayList;
import java.util.Map;

Sqlitego db = new Sqlitego();

// Connect to (or create) "mydb.db", target table "users",
// primary key column "id", and this table has 2 columns
db.setup("mydb", "users", "id", 2);

// Create the table
db.createTable("id INTEGER PRIMARY KEY, name TEXT");

// Insert rows
db.Insert("id, name", "1, 'Alice'");
db.Insert("id, name", "2, 'Bob'");

// Print all rows to console
Map<Integer, ArrayList<String>> rows = db.readAll();
db.Print(rows, -1);  // prints all columns of every row
```

---

## ⚠️ String Column Constraint

When passing a **string value** into any method, you **must** wrap it in single quotes **inside** the Java string, like this:

```java
"'your text here'"
```

This applies to `Insert`, `Update`, and any condition value that is a string type.

```java
// ✅ Correct
db.Insert("id, name", "1, 'Alice'");
db.Update("name", "'Charlie'", "1");

// ❌ Wrong — will cause a SQL error
db.Insert("id, name", "1, Alice");
db.Update("name", "Charlie", "1");
```

Numeric values do **not** need the extra quotes:

```java
// ✅ Numerics — no single quotes needed
db.Insert("id, score", "1, 99");
db.Delete("1");
db.exists("1");
```

---

## Methods

### `setup(String db, String table, String primaryKey, int columns)`

Connects to a SQLite database file. Creates the `.db` file if it doesn't exist. **Must be called before anything else.**

The `columns` parameter tells the library how many columns your table has — this is used internally by `readAll()`, `Read()`, and `Print()` to fetch every column correctly.

```java
Sqlitego db = new Sqlitego();

// "sample.db", table "users", primary key "id", table has 2 columns
db.setup("sample", "users", "id", 2);

// A table with 4 columns
db.setup("school", "students", "student_id", 4);
```

---

### `createTable(String schema)`

Creates the table if it does not already exist, using the SQL schema you provide.

```java
db.createTable("id INTEGER PRIMARY KEY, name TEXT");

// Multi-column example
db.createTable("id INTEGER PRIMARY KEY, name TEXT, age INTEGER, email TEXT");
```

---

### `Insert(String columns, String values)`

Inserts a new row into the table. Returns `true` on success, `false` on failure.

```java
db.Insert("id, name", "1, 'Alice'");
db.Insert("id, name", "2, 'Bob'");

// Multi-column insert
db.Insert("id, name, age, email", "3, 'Carol', 25, 'carol@email.com'");
```

---

### `Read(String condition)`

Reads a **single row** where the primary key matches the condition. Returns a `Map<Integer, ArrayList<String>>` where the data is stored at key `0`.

Each column value is accessible by its index inside the `ArrayList`, starting at `0` (first column), `1` (second column), and so on.

```java
Map<Integer, ArrayList<String>> result = db.Read("1");

// Access columns by index inside key 0
String id   = result.get(0).get(0);  // first column
String name = result.get(0).get(1);  // second column

System.out.println("ID: " + id + " | Name: " + name);

// If the primary key is a string column:
Map<Integer, ArrayList<String>> result = db.Read("'Alice'");
```

---

### `readAll()`

Returns **all rows** in the table as a `Map<Integer, ArrayList<String>>`. Rows are stored with keys starting at `1` (row 1, row 2, ...). Each row is an `ArrayList<String>` where values are ordered by column position.

```java
Map<Integer, ArrayList<String>> rows = db.readAll();

// Access a specific row and column
String firstRowName = rows.get(1).get(1);  // row 1, second column

// Loop through all rows manually
for (int i = 1; i <= db.count(); i++) {
    System.out.println(rows.get(i));  // prints the full row as a list
}
```

---

### `Print(Map<Integer, ArrayList<String>> data, int index)`

A convenience method that prints the result of `readAll()` or `Read()` directly to the console.

- Pass `index = -1` to print **all columns** of every row as a full list.
- Pass a specific column index (e.g., `0`, `1`, `2`...) to print **only that column** for every row.

The method automatically detects whether the map came from `Read()` (single row, key `0`) or `readAll()` (multiple rows, keys `1`..`n`).

```java
Map<Integer, ArrayList<String>> rows = db.readAll();

// Print all columns of every row
db.Print(rows, -1);
// Output example:
// [1, Alice]
// [2, Bob]

// Print only the second column (index 1) of every row
db.Print(rows, 1);
// Output example:
// Alice
// Bob

// Works with Read() too
Map<Integer, ArrayList<String>> single = db.Read("1");
db.Print(single, -1);   // prints full row: [1, Alice]
db.Print(single, 0);    // prints only: 1
db.Print(single, 1);    // prints only: Alice

// Invalid index (less than -1) prints an error message
db.Print(rows, -5);
// Output: Invalid index!
```

---

### `Update(String column, String value, String condition)`

Updates a column's value on the row where the primary key matches the condition. Returns `true` on success.

```java
// Update name where id = 1
db.Update("name", "'Charlie'", "1");

// Update a numeric column
db.Update("age", "30", "2");

// Update where primary key is a string
db.Update("name", "'Dave'", "'Alice'");
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

Returns `true` if a row with the given primary key value exists, `false` otherwise.

```java
if (db.exists("1")) {
    System.out.println("Row found.");
} else {
    System.out.println("Not found.");
}

// String primary key
if (db.exists("'Alice'")) {
    System.out.println("Alice exists.");
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

## Setting Up JARs in Eclipse

SqliteGoSimp requires **two JAR files** to be added to your Eclipse project:

| JAR | Purpose |
|-----|---------|
| `sqlitego.jar` | The SqliteGoSimp library itself |
| `sqlite-jdbc-x.x.x.jar` | The Xerial SQLite JDBC driver that connects Java to SQLite |

### Step 1 — Get the JARs

- **SqliteGoSimp JAR:** Download `sqlitego.jar` from the [releases page](../../releases) of this repository.
- **SQLite JDBC JAR:** Download the latest `sqlite-jdbc-x.x.x.jar` from the [Xerial releases page](https://github.com/xerial/sqlite-jdbc/releases).

### Step 2 — Add both JARs to your Eclipse project

1. Create a `lib/` folder inside your Eclipse project root and place **both** `.jar` files there (optional but keeps things organized).
2. Right-click your project in the **Package Explorer** → **Properties**.
3. Go to **Java Build Path** → **Libraries** tab.
4. Click **Add JARs...** if both files are inside the project, or **Add External JARs...** if they are stored elsewhere.
5. Select **both** `sqlitego.jar` and `sqlite-jdbc-x.x.x.jar`, then click **Open**.
6. Click **Apply and Close**.

### Step 3 — Verify the setup

Add this to your `main` and run it. If no errors are thrown, both JARs are correctly configured:

```java
import com.sqlitego.Sqlitego;

Sqlitego db = new Sqlitego();
boolean ok = db.setup("test", "verify", "id", 1);
System.out.println("SqliteGoSimp ready: " + ok);
```

---

## Notes

- `setup()` must always be the **first method called** before any other operation.
- The `columns` argument in `setup()` must match the **exact number of columns** in your table. A mismatch will cause columns to be missing or throw an error during reads.
- The primary key set in `setup()` is the condition column used by `Read()`, `Update()`, `Delete()`, and `exists()`.
- `readAll()` row keys start at `1`. `Read()` row key is always `0`. Keep this in mind when accessing results directly without `Print()`.
- All column values returned by `readAll()` and `Read()` are `String` type regardless of the original SQLite column type. Convert as needed (e.g., `Integer.parseInt(...)`).

---

## How to Create a GitHub Release & Tag

A GitHub Release is how you publish a versioned snapshot of your project. When you attach `sqlitego.jar` to it, users can download it directly from the releases page and the download badge on this README will count it automatically.

### Step 1 — Go to your repository's Releases page

On GitHub, go to your repo and click **Releases** on the right sidebar, then click **Draft a new release**.

### Step 2 — Create a new tag

In the **Choose a tag** dropdown, type your version number — use the format `v1.0.0` (semantic versioning). Since this is the first stable release, use:

```
v2.0.0
```

Select **Create new tag on publish**. This creates the tag automatically when you publish.

### Step 3 — Fill in the release details

- **Release title:** `v2.0.0 — Initial Release`
- **Description:** Describe what's in the release. Example:

```
## SqliteGoSimp v2.0.0

First stable release of SqliteGoSimp — a simple SQLite wrapper for Java.

### What's included
- `sqlitego.jar` — drop-in library for your Java project
- Supports: setup, createTable, Insert, Read, readAll, Update, Delete, exists, count, Print
- readAll() and Read() return Map<Integer, ArrayList<String>> for flexible multi-column support

### Requirements
- Java 8+
- sqlite-jdbc (Xerial) — see README for setup instructions
```

### Step 4 — Attach the JAR file

Scroll down to **Attach binaries by dropping them here or selecting them**. Upload your `sqlitego.jar` file here. This is what users will download when they click the releases page.

### Step 5 — Publish

Click **Publish release**. Your tag `v2.0.0` is now live and the release badge on this README will automatically update to show `v2.0.0`.

---

### Version Naming Guide

Follow this pattern for future releases:

| Version | When to use |
|---------|-------------|
| `v2.0.0` | Major rewrite or breaking change (e.g. changed method signatures) |
| `v2.1.0` | New method or feature added, backwards compatible |
| `v2.1.1` | Bug fix, no new features |
