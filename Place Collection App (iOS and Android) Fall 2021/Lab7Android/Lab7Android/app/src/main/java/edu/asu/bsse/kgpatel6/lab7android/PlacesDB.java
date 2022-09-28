package edu.asu.bsse.kgpatel6.lab7android;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Copyright (c) 2019 Tim Lindquist,
 * Copyright (c) 2021 Kunal Patel,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Purpose: To be graded for SER423 so the grader/ASU has the rights to grade this app, build this app and anything relating to those two.
 *
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, Arizona State University Polytechnic
 *         Kunal Patel kgpatel@asu.edu
 * @version Nov 26, 2021
 */


public class PlacesDB  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static String dbName = "place";
    private String dbPath;
    private SQLiteDatabase crsDB;
    private final Context context;

    public PlacesDB(Context context){
        super(context,dbName, null, DATABASE_VERSION);
        this.context = context;
        // place the database in the files directory. Could also place it in the databases directory
        // with dbPath = context.getDatabasePath("dbName"+".db").getPath();
        dbPath = context.getFilesDir().getPath()+"/";
        android.util.Log.d(this.getClass().getSimpleName(),"db path is: "+
                context.getDatabasePath("place"));
        android.util.Log.d(this.getClass().getSimpleName(),"dbpath: "+dbPath);
    }

    public SQLiteDatabase openDB() throws SQLException {
        String myPath = dbPath + dbName + ".db";
        if(checkDB()) {
            crsDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            android.util.Log.d("CourseDB --> openDB", "opened db at path: " + crsDB.getPath());
        }else{
            try {
                this.copyDB();
                crsDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            }catch(Exception ex) {
                android.util.Log.w(this.getClass().getSimpleName(),"unable to copy and open db: "+ex.getMessage());
            }
        }
        return crsDB;
    }

    private boolean checkDB(){    //does the database exist and is it initialized?
        SQLiteDatabase checkDB = null;
        boolean crsTabExists = false;
        try{
            String path = dbPath + dbName + ".db";
            android.util.Log.d("PlaceDB --> checkDB: path to db is", path);
            File aFile = new File(path);
            if(aFile.exists()){
                checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
                if (checkDB!=null) {
                    android.util.Log.d("PlaceDB --> checkDB","opened db at: "+checkDB.getPath());
                    Cursor tabChk = checkDB.rawQuery("SELECT name FROM sqlite_master where type='table' and name='places';", null);
                    if(tabChk == null){
                        android.util.Log.d("PlaceDB --> checkDB","check for course table result set is null");
                    }else{
                        tabChk.moveToNext();
                        android.util.Log.d("PlaceDB --> checkDB","check for course table result set is: " +
                                ((tabChk.isAfterLast() ? "empty" : (String) tabChk.getString(0))));
                        crsTabExists = !tabChk.isAfterLast();
                    }
                    if(crsTabExists){
                        Cursor c= checkDB.rawQuery("SELECT * FROM places", null);
                        c.moveToFirst();
                        while(!c.isAfterLast()) {
                            String crsName = c.getString(0);
                            android.util.Log.d("PlaceDB --> checkDB","Place table has PlaceName: "+
                                    crsName);
                            c.moveToNext();
                        }
                        crsTabExists = true;
                    }
                }
            }
        }catch(SQLiteException e){
            android.util.Log.w("PlaceDB->checkDB",e.getMessage());
        }
        finally {
            if(checkDB != null){
                checkDB.close();
            }
        }
        return crsTabExists;
    }

    public void copyDB() throws IOException {
        try {
            if(!checkDB()){
                // only copy the database if it doesn't already exist in my database directory
                android.util.Log.d("PlaceDB --> copyDB", "checkDB returned false, starting copy");
                InputStream ip =  context.getResources().openRawResource(R.raw.place);
                // make sure the database path exists. if not, create it.
                File aFile = new File(dbPath);
                if(!aFile.exists()){
                    aFile.mkdirs();
                }
                String op=  dbPath  +  dbName +".db";
                OutputStream output = new FileOutputStream(op);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = ip.read(buffer))>0){
                    output.write(buffer, 0, length);
                }
                output.flush();
                output.close();
                ip.close();
            }
        } catch (IOException e) {
            android.util.Log.w("PlaceDB --> copyDB", "IOException: "+e.getMessage());
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
