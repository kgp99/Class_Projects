package edu.asu.bsse.kgpatel6.lab6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private final int camefroma = 1;

    private String PlaceN[];
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

        PlaceN = new String[] { "Unknown" };

        setTitle("Places");

        // initiate request to server to get the names of all places to be placed in the ListView
        try{
            MethodInformation mi = new MethodInformation(this, getString(R.string.defaulturl),"getNames",
                    new Object[]{}, camefroma);
            AsyncGetPlaceNamesConnect ac = (AsyncGetPlaceNamesConnect) new AsyncGetPlaceNamesConnect().execute(mi);
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception gettting place names in Main: "+
                    ex.getMessage());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(getClass().getSimpleName(), "onRestart()");

        try{
            MethodInformation mi = new MethodInformation(this, getString(R.string.defaulturl),"getNames",
                    new Object[]{}, camefroma);
            AsyncGetPlaceNamesConnect ac = (AsyncGetPlaceNamesConnect) new AsyncGetPlaceNamesConnect().execute(mi);
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception creating adapter: "+
                    ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreateOptionsMenu()");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void prepareAdapterforPlacesLV() {
        colLabelPLV = this.getResources().getStringArray(R.array.col_header_LV_Places);
        //Can't work as @id does not seem to work in the array.xml file in a int array.
        //colIDsPLV =  this.getResources().getIntArray(R.array.col_ids_LV_Places);
        colIDsPLV =  new int[] {R.id.PlaceNamePL};
        Log.d(this.getClass().getSimpleName(), String.valueOf(colLabelPLV.length));
        Log.d(this.getClass().getSimpleName(), String.valueOf(colIDsPLV.length));

        Arrays.sort(PlaceN);

        MapforLV = new ArrayList<>();
        HashMap<String, String> PLVColheaders = new HashMap<>();
        PLVColheaders.put("PlaceName", "PlaceName");
        //PLVColheaders.put("Category", "Category");
        MapforLV.add(PLVColheaders);

        for (String keyt : PlaceN) {
            HashMap<String, String> tempHM = new HashMap<>();
            tempHM.put("PlaceName", keyt);
            //tempHM.put("Category", PlacesL.get(keyt).getCategory());
            android.util.Log.w(this.getClass().getSimpleName(),"Place Name: " + keyt);

            MapforLV.add(tempHM);
        }

        sa = new SimpleAdapter(this, MapforLV, R.layout.place_list_in_activity_main, colLabelPLV, colIDsPLV);
        PlacesLV.setAdapter(sa);
        PlacesLV.setOnItemClickListener(this);
        Log.d(this.getClass().getSimpleName(), String.valueOf(MapforLV.size()));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position > 0 && position <= PlaceN.length) {
            android.util.Log.d(this.getClass().getSimpleName(), "in method onItemClick. selected: " + PlaceN[position-1]);
            Intent displayStud = new Intent(this, Display_Edit_Spinner_PD_Entry.class);
            displayStud.putExtra("selectedPD", PlaceN[position-1]);
            this.startActivityForResult(displayStud, 21);
        }
    }


    public void setPlaceN (String tempa[]) {

        PlaceN = tempa;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //android.util.Log.d(this.getClass().getSimpleName(), "requestCode: " + requestCode);
        if (requestCode == 11 && resultCode == RESULT_OK) {
                android.util.Log.d(this.getClass().getSimpleName(), "Returning from Add Place Activity");
        }
        else if (requestCode == 21 && resultCode == RESULT_OK) {
            if (data!= null && data.getStringExtra("EditedPD") != null) {
                android.util.Log.d(this.getClass().getSimpleName(), "Returning from Display and Edit Place Activity due to edit");
            }
            if (data!= null && data.getStringExtra("DeletedPD") != null) {
                android.util.Log.d(this.getClass().getSimpleName(), "Returning from Display and Edit Place Activity due to delete");
            }
        }
    }
}