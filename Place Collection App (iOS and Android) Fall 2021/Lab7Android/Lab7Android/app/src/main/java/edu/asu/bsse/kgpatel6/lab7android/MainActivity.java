package edu.asu.bsse.kgpatel6.lab7android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


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

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private ArrayList<forListViewC> PlaceNAL;
    private SimpleAdapter sa;

    private ListView PlacesLV;
    private String colLabelPLV[];
    private int colIDsPLV[];
    private List<HashMap<String,String>> MapforLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlacesLV = findViewById(R.id.ListViewInActivityMain);

        PlaceNAL = new ArrayList<>();

        setTitle("Places");

        getDataforLV();
        prepareAdapterforPlacesLV();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(getClass().getSimpleName(), "onRestart()");

        getDataforLV();
        prepareAdapterforPlacesLV();
    }

    // create the menu items for this activity, placed in the action bar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                android.util.Log.d(this.getClass().getSimpleName(),"onOptionsItemSelected -> add");
                Intent AddNewPlace = new Intent(MainActivity.this, AddNewPlaceActivity.class);
                startActivityForResult(AddNewPlace, 11);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getDataforLV() {
        PlaceNAL.clear();
        SQLiteDatabase placeDBAM = null;
        Cursor cur = null;
        try {
            placeDBAM = new PlacesDB(this).openDB();
            cur = placeDBAM.rawQuery("select name, category from places;", new String[]{});
            while (cur.moveToNext()) {
                try {
                    android.util.Log.d(this.getClass().getSimpleName(),"cur [0] = " + cur.getString(0) + "/ncur [1] = " + cur.getString(1));
                    //forListViewC tempforLV = new forListViewC(cur.getString(0), cur.getString(1));
                    PlaceNAL.add(new forListViewC(cur.getString(0), cur.getString(1)));
                } catch (Exception e) {
                    android.util.Log.w(this.getClass().getSimpleName(),"unable to write data to AL");
                }
            }
        } catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(),"unable to get data for LV in AM");
            //android.util.Log.w(this.getClass().getSimpleName(), e.getMessage());
        } finally {
            if (cur != null ) {
                cur.close();
            }
            try {
                if (placeDBAM != null) {
                    placeDBAM.close();
                }
            } catch (Exception e) {
                android.util.Log.w(this.getClass().getSimpleName(),"unable to close SQLDB");
            }

        }
    }

    public void prepareAdapterforPlacesLV() {
        colLabelPLV = this.getResources().getStringArray(R.array.col_header_LV_Places);
        //Can't work as @id does not seem to work in the array.xml file in a int array.
        //colIDsPLV =  this.getResources().getIntArray(R.array.col_ids_LV_Places);
        colIDsPLV =  new int[] {R.id.PlaceNamePL, R.id.CategoryPL};
        Log.d(this.getClass().getSimpleName(), String.valueOf(colLabelPLV.length));
        Log.d(this.getClass().getSimpleName(), String.valueOf(colIDsPLV.length));

        Collections.sort(PlaceNAL);

        MapforLV = new ArrayList<>();
        HashMap<String, String> PLVColheaders = new HashMap<>();
        PLVColheaders.put("PlaceName", "PlaceName");
        PLVColheaders.put("Category", "Category");
        MapforLV.add(PLVColheaders);

        for (forListViewC keyt : PlaceNAL) {
            HashMap<String, String> tempHM = new HashMap<>();
            tempHM.put("PlaceName", keyt.PlaceNinner);
            tempHM.put("Category", keyt.Catagory);
            android.util.Log.w(this.getClass().getSimpleName(),"Place Name: " + keyt.PlaceNinner + "\nCategory: " + keyt.Catagory);

            MapforLV.add(tempHM);
        }

        sa = new SimpleAdapter(this, MapforLV, R.layout.place_list_in_activity_main, colLabelPLV, colIDsPLV);
        PlacesLV.setAdapter(sa);
        PlacesLV.setOnItemClickListener(this);
        Log.d(this.getClass().getSimpleName(), String.valueOf(MapforLV.size()));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0 && position <= PlaceNAL.size()) {
            android.util.Log.d(this.getClass().getSimpleName(), "in method onItemClick. selected: " + PlaceNAL.get(position-1).PlaceNinner);
            Intent displayStud = new Intent(this, Display_Edit_Spinner_PD_Entry.class);
            displayStud.putExtra("selectedPD", PlaceNAL.get(position - 1).PlaceNinner);
            this.startActivityForResult(displayStud, 21);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //android.util.Log.d(this.getClass().getSimpleName(), "requestCode: " + requestCode);
        if (requestCode == 11 && resultCode == RESULT_OK) {
            android.util.Log.d(this.getClass().getSimpleName(), "Returning from Add Place Activity with success");
        }
        else if (requestCode == 21 && resultCode == RESULT_OK) {
            if (data!= null && data.getStringExtra("EditedPD") != null) {
                android.util.Log.d(this.getClass().getSimpleName(), "Returning from Display and Edit Place Activity due to edit with success");
            }
            if (data!= null && data.getStringExtra("DeletedPD") != null) {
                android.util.Log.d(this.getClass().getSimpleName(), "Returning from Display and Edit Place Activity due to delete with success");
            }
        }
    }

    private class forListViewC implements Comparable<forListViewC> {
        public String PlaceNinner;
        public String Catagory;

        forListViewC(String tempPN, String tempC) {
            this.PlaceNinner = tempPN;
            if (tempC.isEmpty()) {
                this.Catagory = "None";
            } else {
                this.Catagory = tempC;
            }
        }

        @Override
        public int compareTo(forListViewC o) {
            return this.PlaceNinner.compareTo(o.PlaceNinner);
        }
    }

}