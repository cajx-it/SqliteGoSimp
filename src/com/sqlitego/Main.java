package test;
import java.util.ArrayList;
import java.util.Map;

import com.sqlitego.Sqlitego;

public class Main {

    public static void main(String[] args) {

        Sqlitego db = new Sqlitego();

        // =========================================================
        // 1. setup(String db, String table, String primaryKey, int columns)
        //    - Connects to or creates "mydb.db"
        //    - Targets the "users" table
        //    - Primary key column is "id"
        //    - Table has 2 columns
        // =========================================================
        db.setup("mydb", "users", "id", 2);


        // =========================================================
        // 2. createTable(String schema)
        //    - Creates the "users" table if it doesn't exist yet
        //    - Safe to call every run — won't overwrite existing data
        // =========================================================
        db.createTable("id INTEGER PRIMARY KEY, name TEXT");


        // =========================================================
        // 3. Insert(String columns, String values)
        //    - Inserts a new row into the table
        //    - String values MUST be wrapped in single quotes: "'value'"
        //    - Numeric values do NOT need single quotes
        //    - Returns true on success, false on failure
        // =========================================================
        db.Insert("id, name", "1, 'Alice'");
        db.Insert("id, name", "2, 'Bob'");
        db.Insert("id, name", "3, 'Carol'");
        db.Insert("id, name", "4, 'Dave'");
        db.Insert("id, name", "5, 'Eve'");


        // =========================================================
        // 4. count()
        //    - Returns the total number of rows in the table as int
        // =========================================================
        int total = db.count();
        System.out.println("=== count() ===");
        System.out.println("Total rows: " + total);
        // Output: Total rows: 5


        // =========================================================
        // 5. exists(String condition)
        //    - Returns true if a row with that primary key exists
        //    - Use "'value'" for string primary keys
        // =========================================================
        System.out.println("\n=== exists() ===");
        System.out.println("ID 2 exists: " + db.exists("2"));   // true
        System.out.println("ID 99 exists: " + db.exists("99")); // false


        // =========================================================
        // 6. Read(String condition)
        //    - Reads a single row where primary key matches condition
        //    - Returns Map<Integer, ArrayList<String>>
        //    - The row is always stored at key 0
        //    - Columns are accessed by index inside the ArrayList:
        //        index 0 = first column (id)
        //        index 1 = second column (name)
        // =========================================================
        System.out.println("\n=== Read() ===");
        Map<Integer, ArrayList<String>> single = db.Read("2");

        // Access columns manually
        String readId   = single.get(0).get(0); // first column
        String readName = single.get(0).get(1); // second column
        System.out.println("ID: " + readId + " | Name: " + readName);
        // Output: ID: 2 | Name: Bob


        // =========================================================
        // 7. readAll()
        //    - Returns all rows as Map<Integer, ArrayList<String>>
        //    - Rows are stored at keys 1, 2, 3 ... n
        //    - Columns are accessed by index inside each ArrayList
        // =========================================================
        System.out.println("\n=== readAll() ===");
        Map<Integer, ArrayList<String>> all = db.readAll();

        // Access a specific row and column manually
        System.out.println("Row 1, Name: " + all.get(1).get(1)); // Alice
        System.out.println("Row 3, Name: " + all.get(3).get(1)); // Carol

        // Loop through all rows manually
        System.out.println("--- All rows (manual loop) ---");
        for (int i = 1; i <= db.count(); i++) {
            System.out.println("ID: " + all.get(i).get(0) + " | Name: " + all.get(i).get(1));
        }


        // =========================================================
        // 8. Print(Map<Integer, ArrayList<String>> data, int index)
        //    - Quick console print helper
        //    - index = -1  → prints all columns of every row as a list
        //    - index =  0  → prints only the first column of every row
        //    - index =  1  → prints only the second column of every row
        //    - Works automatically with both Read() and readAll() results
        //    - Passing index < -1 prints "Invalid index!"
        // =========================================================
        System.out.println("\n=== Print() with readAll() — index -1 (all columns) ===");
        db.Print(all, -1);
        // Output:
        // [1, Alice]
        // [2, Bob]
        // [3, Carol]
        // [4, Dave]
        // [5, Eve]

        System.out.println("\n=== Print() with readAll() — index 0 (id column only) ===");
        db.Print(all, 0);
        // Output:
        // 1
        // 2
        // 3
        // 4
        // 5

        System.out.println("\n=== Print() with readAll() — index 1 (name column only) ===");
        db.Print(all, 1);
        // Output:
        // Alice
        // Bob
        // Carol
        // Dave
        // Eve

        System.out.println("\n=== Print() with Read() — index -1 (full single row) ===");
        db.Print(single, -1);
        // Output: [2, Bob]

        System.out.println("\n=== Print() with Read() — index 1 (name only) ===");
        db.Print(single, 1);
        // Output: Bob

        System.out.println("\n=== Print() — invalid index ===");
        db.Print(all, -5);
        // Output: Invalid index!


        // =========================================================
        // 9. Update(String column, String value, String condition)
        //    - Updates a column on the row where primary key matches
        //    - String values MUST use "'value'" format
        //    - Numeric values do NOT need single quotes
        //    - Returns true on success, false on failure
        // =========================================================
        System.out.println("\n=== Update() ===");
        db.Update("name", "'Alice Updated'", "1");
        db.Update("name", "'Bob Updated'", "2");

        Map<Integer, ArrayList<String>> afterUpdate = db.readAll();
        System.out.println("After Update:");
        db.Print(afterUpdate, -1);
        // Output:
        // [1, Alice Updated]
        // [2, Bob Updated]
        // [3, Carol]
        // [4, Dave]
        // [5, Eve]


        // =========================================================
        // 10. Delete(String condition)
        //     - Deletes the row where primary key matches condition
        //     - Use "'value'" for string primary keys
        //     - Returns true on success, false on failure
        // =========================================================
        System.out.println("\n=== Delete() ===");
        db.Delete("5"); // Delete Eve

        Map<Integer, ArrayList<String>> afterDelete = db.readAll();
        System.out.println("After Delete (Eve removed):");
        db.Print(afterDelete, -1);
        // Output:
        // [1, Alice Updated]
        // [2, Bob Updated]
        // [3, Carol]
        // [4, Dave]

        System.out.println("\nFinal row count: " + db.count());
        // Output: Final row count: 4
    }
}
