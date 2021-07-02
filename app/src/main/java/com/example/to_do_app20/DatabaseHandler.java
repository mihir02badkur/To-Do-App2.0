package com.example.to_do_app20;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "tokenDatabase";
    private static final String TOKEN_TABLE = "tokens";
    private static final String ID = "id";
    private static final String TOKEN = "token";

    private static final String CREATE_TOKEN_TABLE = "CREATE TABLE " + TOKEN_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TOKEN + " TEXT)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TOKEN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TOKEN_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertToken(TokenModel token){
        ContentValues cv = new ContentValues();
        cv.put(TOKEN, token.getToken());

        db.insert(TOKEN_TABLE, null, cv);
    }

    public List<TokenModel> getAllTokens(){
        List<TokenModel> tokenList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TOKEN_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        TokenModel token = new TokenModel();
                        token.setId(cur.getInt(cur.getColumnIndex(ID)));
                        token.setToken(cur.getString(cur.getColumnIndex(TOKEN)));

                        tokenList.add(token);
                    }
                    while(cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return tokenList;
    }



    public void updateToken(int id, String token) {
        ContentValues cv = new ContentValues();
        cv.put(TOKEN, token);
        db.update(TOKEN_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }



    public void delete() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting rows
        sqLiteDatabase.delete(TOKEN_TABLE, null, null);
        sqLiteDatabase.close();
    }

    public boolean isDBEmpty() {
        boolean chk = false;
        @SuppressLint("Recycle") Cursor mCursor = db.rawQuery("SELECT * FROM " + TOKEN_TABLE, null);
        if (mCursor != null) {
            while (mCursor.moveToFirst()) {
                if (mCursor.getInt(0) == 0) {
                    chk = false;
                }
            }
        } else {
            chk = true;
        }


        return chk;
    }

}