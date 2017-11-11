package googleplaydeveloper.jdotspacegdot.expolog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "expolog";
    public static final int DATABASE_VERSION = 1;

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        update(db, 0, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        update(db, oldVersion, newVersion);
    }

    private void update(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on creation of database
        if (oldVersion < 1) {
            StringBuilder statement;

            // create the table for fear and avoidance hierarchy
            statement = new StringBuilder();
            statement.append("CREATE TABLE HIERARCHY (")
                    .append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append("DISCOMFORT INTEGER NOT NULL, ")
                    .append("AVOIDANCE INTEGER NOT NULL, ")
                    .append("ACTIVITY TEXT NOT NULL);");
            db.execSQL(statement.toString());

            // create the table for planned exposures
            statement = new StringBuilder();
            statement.append("CREATE TABLE PLAN (")
                    .append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append("ACTIVITY_ID INTEGER NOT NULL, ")
                    .append("INSTANCE TEXT, ")
                    .append("PREDICTION TEXT, ")
                    .append("CHALLENGE TEXT);");
            db.execSQL(statement.toString());

            // create the table for completed exposures
            statement = new StringBuilder();
            statement.append("CREATE TABLE EXPOSURE (")
                    .append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ")
                    .append("ACTIVITY_ID INTEGER NOT NULL, ")
                    .append("INSTANCE TEXT, ")
                    .append("PREDICTION TEXT, ")
                    .append("CHALLENGE TEXT, ")
                    .append("EXPERIENCE TEXT, ")
                    .append("REFLECTION TEXT, ")
                    .append("NOTES TEXT, ")
                    .append("TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP);");
            db.execSQL(statement.toString());
        }
    }

}
