package edu.asu.bsse.kgpatel6.lab7android;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;


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


public class Display_Edit_Spinner_PD_Entry extends AppCompatActivity implements DialogInterface.OnClickListener, AdapterView.OnItemSelectedListener {

    private Spinner PlacesSpinner;
    private TextView GreatCircleDis;
    private TextView BearingDis;


    private TextView Pname;
    private EditText Des;
    private EditText Cat;
    private EditText At;
    private EditText As;
    private EditText El;
    private EditText Lat;
    private EditText Long;

    private TextView WarningMessageEDPA;

    private String selectedPlace;
    private ArrayList<String> PlaceNtemp;
    private double Latt;
    private double Longt;
    //private Place_Description ToDisplayherePD;

    private Context DEDCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_edit_spinner_pd_entry);

        DEDCon = getApplicationContext();

        GreatCircleDis = findViewById(R.id.GDDistanceDisplay);
        BearingDis = findViewById(R.id.BearingDisplay);

        Pname = findViewById(R.id.editTTNameOfPlace);
        Des = findViewById(R.id.editTTDescription);
        Cat = findViewById(R.id.editTTCategory);
        At = findViewById(R.id.editTTAddressTitle);
        As = findViewById(R.id.editTTAddressStreet);
        El = findViewById(R.id.editTTElevation);
        Lat = findViewById(R.id.editTTLatitude);
        Long = findViewById(R.id.editTTLongitude);

        WarningMessageEDPA = findViewById((R.id.WarningMessage2));

        PlaceNtemp = new ArrayList<>();

        Intent FromMainA = getIntent();

        selectedPlace = FromMainA.getStringExtra("selectedPD")!=null ? FromMainA.getStringExtra("selectedPD") : "unknown";
        if (selectedPlace.equals("unknown")) {
            Intent bti = new Intent();
            this.setResult(RESULT_CANCELED, bti);
            finish();
        }

        PlacesSpinner = findViewById(R.id.PlacesSpinner);

        PlacesSpinner.setOnItemSelectedListener(this);



        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"exception action bar: "+ex.getLocalizedMessage());
        }

        setTitle("Display Place Description Entry");

        displayPlace();

        setupPlacesSpin();
    }

    public void displayPlace() {

        SQLiteDatabase placeDBAM = null;
        Cursor cur = null;
        try {
            placeDBAM = new PlacesDB(this).openDB();
            cur = placeDBAM.rawQuery("select * from places where name=?;", new String[]{selectedPlace});
            while (cur.moveToNext()) {
                try {
                    Pname.setText(cur.getString(0));
                    Des.setText(cur.getString(6));
                    Cat.setText(cur.getString(7));
                    At.setText(cur.getString(1));
                    As.setText(cur.getString(2));
                    El.setText(String.format(Locale.getDefault(), "%f", cur.getDouble(3)));
                    Lat.setText(String.format(Locale.getDefault(), "%f", cur.getDouble(4)));
                    Long.setText(String.format(Locale.getDefault(), "%f", cur.getDouble(5)));
                    Latt = cur.getDouble(4);
                    Longt = cur.getDouble(5);
                } catch (Exception e) {
                    android.util.Log.d(this.getClass().getSimpleName(),"Failed to set selected place data.");
                }
            }
        } catch (Exception e) {
            android.util.Log.d(this.getClass().getSimpleName(),"Failed to get selected place data.");
        } finally {
            if (cur != null) {
                cur.close();
            }
            if (placeDBAM != null) {
                placeDBAM.close();
            }
        }

    }

    public void setupPlacesSpin() {

        SQLiteDatabase placeDBAM = null;
        Cursor cur = null;
        try {
            placeDBAM = new PlacesDB(this).openDB();
            cur = placeDBAM.rawQuery("select name from places;", new String[]{});
            while (cur.moveToNext()) {
                try {
                    PlaceNtemp.add(cur.getString(0));
                } catch (Exception e) {
                    android.util.Log.d(this.getClass().getSimpleName(),"Failed to set selected place data.");
                }
            }
        } catch (Exception e) {
            android.util.Log.d(this.getClass().getSimpleName(),"Failed to get selected place data.");
        } finally {
            if (cur != null) {
                cur.close();
            }
            if (placeDBAM != null) {
                placeDBAM.close();
            }
        }
        if (PlaceNtemp.isEmpty()) {
            PlaceNtemp.add("Unknown");
        }
        Collections.sort(PlaceNtemp);
        ArrayAdapter<String> anAA = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, PlaceNtemp );
        PlacesSpinner.setAdapter(anAA);
    }

    public void SaveData(View v) {
        android.util.Log.w(this.getClass().getSimpleName(),"Save button clicked");
        if (!Pname.getText().toString().isEmpty() && !El.getText().toString().isEmpty() && !Lat.getText().toString().isEmpty() && !Long.getText().toString().isEmpty()) {
            if (Double.parseDouble(Lat.getText().toString()) < -90 || Double.parseDouble(Lat.getText().toString()) > 90) {
                WarningMessageEDPA.setText("Latitude must be between -90 and 90. ");
                displayPlace();
                return;
            }
            if (Double.parseDouble(Long.getText().toString()) < -180 || Double.parseDouble(Long.getText().toString()) > 180) {
                WarningMessageEDPA.setText("Longitude must be between -180 and 180. ");
                displayPlace();
                return;
            }

            //ToDisplayherePD.setPlaceName(Pname.getText().toString());
            //ToDisplayherePD.setDescriptionofP(Des.getText().toString());
            //ToDisplayherePD.setCategory(Cat.getText().toString());
            //ToDisplayherePD.setAddressTitle(At.getText().toString());
            //ToDisplayherePD.setAddressStreet(As.getText().toString());
            //ToDisplayherePD.setElevation(Double.parseDouble(El.getText().toString()));
            //ToDisplayherePD.setLatitude(Double.parseDouble(Lat.getText().toString()));
            //ToDisplayherePD.setLongitude(Double.parseDouble(Long.getText().toString()));
            EditPlaceAlert();

        }
        else {
            WarningMessageEDPA.setText("Warning, a required field is missing: Place Name, Elevation, Latitude, Longitude");
            displayPlace();

        }
        //WarningMessageANPA.setText("Save clicked");
    }

    // create the menu items for this activity, placed in the action bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.places_display_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // AdapterView.OnItemSelectedListener method. Called when spinner selection Changes
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        android.util.Log.d(this.getClass().getSimpleName(),"Spinner item "+
                PlacesSpinner.getSelectedItem().toString() + " selected.");

        String selectedPlacespin = PlacesSpinner.getSelectedItem().toString();

        SQLiteDatabase placeDBAM = null;
        Cursor cur = null;
        try {
            placeDBAM = new PlacesDB(this).openDB();
            cur = placeDBAM.rawQuery("select latitude, longitude from places where name=?;", new String[]{selectedPlacespin});
            while (cur.moveToNext()) {
                try {
                    GreatCircleDis.setText(Double.toString(Place_Description.getGreatCircleDistance(Latt, cur.getDouble(0), Longt, cur.getDouble(1))) + " KM");
                    BearingDis.setText(Double.toString(Place_Description.getBearing(Latt, cur.getDouble(0), Longt, cur.getDouble(1))) + " Degrees");
                } catch (Exception e) {
                    android.util.Log.d(this.getClass().getSimpleName(),"Failed to set GCD and Bearing data.");
                }
            }
        } catch (Exception e) {
            android.util.Log.d(this.getClass().getSimpleName(),"Failed to get spinner selected place lat and long data.");
        } finally {
            if (cur != null) {
                cur.close();
            }
            if (placeDBAM != null) {
                placeDBAM.close();
            }
        }

        //android.util.Log.d(this.getClass().getSimpleName(),"GCD: " + Double.toString(ToDisplayherePD.getGreatCircleDistance(tempPL.get(selectedPlace).getLatitude(), tempPL.get(selectedPlace).getLongitude())));
        //android.util.Log.d(this.getClass().getSimpleName(),"Bearing: " + Double.toString(ToDisplayherePD.getBearing(tempPL.get(selectedPlace).getLatitude(), tempPL.get(selectedPlace).getLongitude())));

        //GreatCircleDis.setText(Double.toString(ToDisplayherePD.getGreatCircleDistance(tempPL.get(selectedPlace).getLatitude(), tempPL.get(selectedPlace).getLongitude())) + " KM");
        //BearingDis.setText(Double.toString(ToDisplayherePD.getBearing(tempPL.get(selectedPlace).getLatitude(), tempPL.get(selectedPlace).getLongitude())) + " Degrees");


    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        android.util.Log.d(this.getClass().getSimpleName(),"In onNothingSelected: No item selected");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // the user selected the up/home button (left arrow at left of action bar)
            case android.R.id.home:
                android.util.Log.d(this.getClass().getSimpleName(),"onOptionsItemSelected -> home");
                Intent i = new Intent();
                this.setResult(RESULT_OK);
                finish();
                return true;
            // the user selected the action (garbage can) to remove the student
            case R.id.action_remove:
                android.util.Log.d(this.getClass().getSimpleName(),"onOptionsItemSelected -> remove");
                this.removeStudentAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // show an alert view for the user to confirm removing the selected student
    private void removeStudentAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Remove: "+Pname.getText()+"?");
        dialog.setNegativeButton("Cancel", this);
        dialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                android.util.Log.d(this.getClass().getSimpleName(), "onClick positive button for Remove Place.");
                SQLiteDatabase placeDBAM = null;
                Cursor cur = null;
                try {
                    placeDBAM = new PlacesDB(DEDCon).openDB();
                    placeDBAM.delete(getResources().getString(R.string.DBPlaces), "name=?", new String[] {selectedPlace});
                    Intent ti = new Intent();
                    ti.putExtra("DeletedPD", "Place should be deleted now");
                    setResult(RESULT_OK, ti);
                    finish();
                } catch (Exception e) {
                    android.util.Log.d(this.getClass().getSimpleName(),"Failed to Edit place data.");
                } finally {
                    if (cur != null) {
                        cur.close();
                    }
                    if (placeDBAM != null) {
                        placeDBAM.close();
                    }
                }

            }
        });
        dialog.show();
    }

    private void EditPlaceAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Saves Changes to: "+Pname.getText()+"?");
        dialog.setNegativeButton("Cancel", this);

        dialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.util.Log.d(this.getClass().getSimpleName(), "onClick positive button for Edit Place.");
                SQLiteDatabase placeDBAM = null;
                Cursor cur = null;
                try {
                    placeDBAM = new PlacesDB(DEDCon).openDB();
                    ContentValues CVU = new ContentValues();
                    CVU.put(getResources().getString(R.string.name), Pname.getText().toString());
                    CVU.put(getResources().getString(R.string.AT), At.getText().toString());
                    CVU.put(getResources().getString(R.string.AS), As.getText().toString());
                    CVU.put(getResources().getString(R.string.E), Double.parseDouble(El.getText().toString()));
                    CVU.put(getResources().getString(R.string.La), Double.parseDouble(Lat.getText().toString()));
                    CVU.put(getResources().getString(R.string.Lo), Double.parseDouble(Long.getText().toString()));
                    CVU.put(getResources().getString(R.string.D), Des.getText().toString());
                    CVU.put(getResources().getString(R.string.Cat), Cat.getText().toString());
                    placeDBAM.update(getResources().getString(R.string.DBPlaces), CVU, "name=?", new String[] {selectedPlace});
                    Intent ti = new Intent();
                    ti.putExtra("EditedPD", "ShouldBeEdited");
                    setResult(RESULT_OK, ti);
                    finish();
                } catch (Exception e) {
                    android.util.Log.d(this.getClass().getSimpleName(),"Failed to Edit place data.");
                } finally {
                    if (cur != null) {
                        cur.close();
                    }
                    if (placeDBAM != null) {
                        placeDBAM.close();
                    }
                }

            }
        });

        dialog.show();
    }

    // DialogInterface.onClickListener method. Gets called when negative or positive button is clicked
    // in the Alert Dialog created by the newStudentAlert method.
    //Not needed if overriding the method when creating the dialog.
    @Override
    public void onClick(DialogInterface dialog, int whichButton) {
        android.util.Log.d(this.getClass().getSimpleName(),"onClick positive button? "+
                (whichButton==DialogInterface.BUTTON_POSITIVE));
        if(whichButton == DialogInterface.BUTTON_POSITIVE) {
            // ok, so remove the student and return the modified model to main activity
            //students.remove(this.selectedStud);
            //Intent ti = new Intent();
            //ti.putExtra("DeletedPD", ToDisplayherePD.getPlaceName());
            //this.setResult(RESULT_OK, ti);
            //finish();
        }
    }
}