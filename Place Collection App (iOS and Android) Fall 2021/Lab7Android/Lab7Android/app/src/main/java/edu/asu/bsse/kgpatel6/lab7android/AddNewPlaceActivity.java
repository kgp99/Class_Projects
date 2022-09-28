package edu.asu.bsse.kgpatel6.lab7android;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;


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

public class AddNewPlaceActivity extends AppCompatActivity {

    private EditText Pname;
    private EditText Des;
    private EditText Cat;
    private EditText At;
    private EditText As;
    private EditText El;
    private EditText Lat;
    private EditText Long;

    private TextView WarningMessageANPA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);

        Pname = findViewById(R.id.editTTNameOfPlace);
        Des = findViewById(R.id.editTTDescription);
        Cat = findViewById(R.id.editTTCategory);
        At = findViewById(R.id.editTTAddressTitle);
        As = findViewById(R.id.editTTAddressStreet);
        El = findViewById(R.id.editTTElevation);
        Lat = findViewById(R.id.editTTLatitude);
        Long = findViewById(R.id.editTTLongitude);

        WarningMessageANPA = findViewById((R.id.WarningMessage1));

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"exception action bar: "+ex.getLocalizedMessage());
        }
        setTitle("Add New Place");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                android.util.Log.d(this.getClass().getSimpleName(),"onOptionsItemSelected -> home");
                Intent i = new Intent();
                this.setResult(RESULT_OK);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void AddData(android.view.View v) {
        if (!Pname.getText().toString().isEmpty() && !El.getText().toString().isEmpty() && !Lat.getText().toString().isEmpty() && !Long.getText().toString().isEmpty()) {
            try {
                if (Double.parseDouble(Lat.getText().toString()) < -90 || Double.parseDouble(Lat.getText().toString()) > 90) {
                    WarningMessageANPA.setText("Latitude must be between -90 and 90. ");
                    return;
                }
                if (Double.parseDouble(Long.getText().toString()) < -180 || Double.parseDouble(Long.getText().toString()) > 180) {
                    WarningMessageANPA.setText("Longitude must be between -180 and 180. ");
                    return;
                }
            } catch (Exception e) {
                    android.util.Log.d(this.getClass().getSimpleName(),"Error in making a JSON object from input");
                    WarningMessageANPA.setText("Error in parsing Doubles");
                }
            SQLiteDatabase placeDBAM = null;
            Cursor cur = null;
            try {
                placeDBAM = new PlacesDB(this).openDB();
                cur = placeDBAM.rawQuery("select name from places where name=?;", new String[]{Pname.getText().toString()});
                while (cur.moveToNext()) {
                    if (cur.getString(0).equals(Pname.getText().toString())) {
                        android.util.Log.d(this.getClass().getSimpleName(),"cur [0] = " + cur.getString(0));
                        WarningMessageANPA.setText("This Place Name already exits, try putting in another place name. ");
                        return;
                    }
                }
            } catch (Exception e) {
                android.util.Log.w(this.getClass().getSimpleName(), "Error in finding out if the same Name already exists");
                android.util.Log.w(this.getClass().getSimpleName(), e.getMessage());
            } finally {
                if (cur != null) {
                    cur.close();
                }
                if (placeDBAM != null) {
                    placeDBAM.close();
                }
            }

            try {
                placeDBAM = new PlacesDB(this).openDB();
                ContentValues cvnp = new ContentValues();
                cvnp.put(getResources().getString(R.string.name), Pname.getText().toString());
                cvnp.put(getResources().getString(R.string.AT), At.getText().toString());
                cvnp.put(getResources().getString(R.string.AS), As.getText().toString());
                cvnp.put(getResources().getString(R.string.E), Double.parseDouble(El.getText().toString()));
                cvnp.put(getResources().getString(R.string.La), Double.parseDouble(Lat.getText().toString()));
                cvnp.put(getResources().getString(R.string.Lo), Double.parseDouble(Long.getText().toString()));
                cvnp.put(getResources().getString(R.string.D), Des.getText().toString());
                cvnp.put(getResources().getString(R.string.Cat), Cat.getText().toString());

                placeDBAM.insert("places", null, cvnp);

                Intent ti = new Intent();
                this.setResult(RESULT_OK, ti);
                finish();
            } catch (Exception e) {
                android.util.Log.w(this.getClass().getSimpleName(), "Error in adding new Place");
                WarningMessageANPA.setText("Failed to add new Place for some reason");
                return;
            } finally {
                if (cur != null) {
                    cur.close();
                }
                if (placeDBAM != null) {
                    placeDBAM.close();
                }
            }
        }
        else {
            WarningMessageANPA.setText("Warning, a required field is missing: Place Name, Elevation, Latitude, Longitude");
        }
    }
}