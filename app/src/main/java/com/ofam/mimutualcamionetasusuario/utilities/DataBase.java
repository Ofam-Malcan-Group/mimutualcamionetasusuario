package com.ofam.mimutualcamionetasusuario.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ofam.mimutualcamionetasusuario.entities.User;

import java.util.Date;

/**
 * **************************************************************************
 * NAME: DataBase.java
 * DESCRIPTION: clase que controla la base de datos interna de la app.
 */

public class DataBase {

    //region Variables
    //Version data base
    private static final int VERSION_BD = 1;
    //Name data base
    private static final String NAME_BD = "BDMIMUTUALCONDUCTOR";
    //Sentence Create Table
    private static final String CREATE_TABLE_IF_EXISTS = "CREATE TABLE IF NOT EXISTS";
    //Sentence Delete Data
    private static final String DELETE_FROM = "DELETE FROM 'main'.'%s'";
    //region Columns Tables
    //Table USER
    private static final String USER_ID = "userID";
    private static final String PASS = "Pass";
    private static final String DATE_USER = "FechaUsuario";
    //endregion
    //region Name Tables
    private static final String TABLE_USER = "TB_USER";
    //region Sentence Create Tables
    private static final String BD_USER = String.format("%s %s(%s TEXT NOT NULL, %s TEXT NOT NULL, %s TEXT NOT NULL)", CREATE_TABLE_IF_EXISTS, TABLE_USER, USER_ID, PASS, DATE_USER);
    //endregion
    //Helper
    private final DatabaseHelper mBdHelper;
    //endregion
    //Name instance BD
    private SQLiteDatabase sqLiteDatabase;
    //endregion

    //region Internal
    public DataBase(Context ctx) {
        mBdHelper = new DatabaseHelper(ctx);
    }

    private void open() {
        sqLiteDatabase = mBdHelper.getWritableDatabase();
    }

    private void close() {
        mBdHelper.close();
    }

    //endregion

    //region Table User
    //insert a new session in the table session
    public void insertUser(User user) {
        try {
            open();
            ContentValues values = new ContentValues();
            values.put(USER_ID, user.getUserID());
            values.put(PASS, user.getPassID());
            values.put(DATE_USER, Utilities.getDateFormat().format(new Date()));
            sqLiteDatabase.insert(TABLE_USER, null, values);
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        } finally {
            close();
        }
    }

    //Read if exist a session in the table Session
    public User getUser() {
        try {
            open();
            Cursor cursorSession = sqLiteDatabase.query(TABLE_USER, new String[]{USER_ID, PASS}, null, null, null, null, null);
            User resultUser = null;
            if (cursorSession != null) {
                cursorSession.moveToFirst();
                if (cursorSession.getCount() > 0)
                    resultUser = new User(cursorSession.getString(0), cursorSession.getString(1));
                cursorSession.close();
            }
            return resultUser;
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
            return null;
        } finally {
            close();
        }
    }

    //Delete all table Session
    public void deleteUser() {
        open();
        try {
            sqLiteDatabase.execSQL(String.format(DELETE_FROM, TABLE_USER));
        } catch (Exception e) {
            Message.logMessageException(getClass(), e);
        } finally {
            close();
        }
    }

    //endregion

    //region Private Class DatabaseHelper
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, NAME_BD, null, VERSION_BD);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //Create Tables
            try {
                db.execSQL(BD_USER);
            } catch (Exception e) {
                Message.logMessageException(getClass(), e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int previousVersion, int newVersion) {
            //Use version 2
        }
    }

    //endregion
}