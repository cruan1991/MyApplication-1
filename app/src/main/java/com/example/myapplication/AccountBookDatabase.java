package com.example.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AccountBookDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database
    static final String DATABASE_NAME = "AccountBook";
    // Tables
    static final String Account_table = "Account";
    static final String Member_table = "Member";
    static final String Item_table = "Item";
    static final String ItemMember_table = "ItemMember";

    // Account Table Columns names
    static final String KEY_ACCTID = "acctid";
    static final String KEY_ACCTNAME = "acctname";
    static final String KEY_DATE_ACCT = "acctdate";
    static final String KEY_LASTUPDATE = "acctlastupdate";
    static final String KEY_PHOTO_ACCT = "acctphoto";

    // Member Table Columns names
    static final String KEY_MEMBERID = "memberid";
    static final String KEY_MEMBERNAME = "membername";
    static final String KEY_ACCTID_MEMBER = "acctid";
    static final String KEY_BALANCE = "balance";

    // Item Table Columns names
    static final String KEY_ITEMID = "itemid";
    static final String KEY_ITEMNAME = "itemname";
    static final String KEY_ACCTID_ITEM = "acctid";
    static final String KEY_VALUE = "itemvalue";
    static final String KEY_DATE_ITEM = "itemdate";
    static final String KEY_BUYER = "buyer";
    static final String KEY_PHOTO_ITEM = "itemphoto";
    static final String KEY_LAT = "latitude";
    static final String KEY_LONG = "longitude";

    // Item/Member Table
    static final String KEY_ITEMID_IM = "itemid";
    static final String KEY_MEMBERID_IM = "memberid";

    SQLiteDatabase db;

    public AccountBookDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Category table create query
        String CREATE_ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS " +
                Account_table + " ("
                + KEY_ACCTID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ACCTNAME + " TEXT,"
                + KEY_DATE_ACCT + " TEXT,"
                + KEY_LASTUPDATE + " TEXT,"
                + KEY_PHOTO_ACCT + " TEXT)";
        String CREATE_MEMBER_TABLE = "CREATE TABLE IF NOT EXISTS " +
                Member_table + " ("
                + KEY_MEMBERID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_MEMBERNAME + " TEXT,"
                + KEY_ACCTID_MEMBER + " INTEGER,"
                + KEY_BALANCE + " DOUBLE,"
                + " CONSTRAINT fk_member FOREIGN KEY (" + KEY_ACCTID_MEMBER + ") "
                + "REFERENCES " + Account_table + "(" + KEY_ACCTID + ") ON DELETE CASCADE)";
        String CREATE_ITEM_TABLE = "CREATE TABLE IF NOT EXISTS " +
                Item_table + " ("
                + KEY_ITEMID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_ITEMNAME + " TEXT,"
                + KEY_ACCTID_ITEM + " INTEGER,"
                + KEY_VALUE + " DOUBLE,"
                + KEY_DATE_ITEM + " TEXT,"
                + KEY_BUYER + " TEXT,"
                + KEY_PHOTO_ITEM + " TEXT,"
                + KEY_LAT + " DOUBLE,"
                + KEY_LONG + " DOUBLE,"
                + " CONSTRAINT fk_item FOREIGN KEY (" + KEY_ACCTID_ITEM + ") "
                + "REFERENCES " + Account_table + "(" + KEY_ACCTID + ") ON DELETE CASCADE)";
        String CREATE_ITEMMEMBER_TABLE = "CREATE TABLE IF NOT EXISTS " +
                ItemMember_table + " ("
                + KEY_ITEMID_IM + " INTEGER,"
                + KEY_MEMBERID_IM + " INTEGER,"
                + " CONSTRAINT pk_itemmember PRIMARY KEY (" + KEY_ITEMID_IM + ", " + KEY_MEMBERID_IM + "), "
                + " CONSTRAINT fk1_itemmember FOREIGN KEY (" + KEY_ITEMID_IM + ") "
                + "REFERENCES " + Item_table + "(" + KEY_ITEMID + ") ON DELETE CASCADE, "
                + " CONSTRAINT fk2_itemmember FOREIGN KEY (" + KEY_MEMBERID_IM + ") "
                + "REFERENCES " + Member_table + "(" + KEY_MEMBERID + ") ON DELETE CASCADE)";

        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_MEMBER_TABLE);
        db.execSQL(CREATE_ITEM_TABLE);
        db.execSQL(CREATE_ITEMMEMBER_TABLE);
    }

    // Use this insert, instead of SQLiteDatabase.insert
    public long insert(String table, ContentValues cv) {
        return db.insert(table, null, cv);
    }

    // Pass query string here
    public Cursor query(String query) {
        Cursor csr = db.rawQuery(query, null);
        return csr;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
