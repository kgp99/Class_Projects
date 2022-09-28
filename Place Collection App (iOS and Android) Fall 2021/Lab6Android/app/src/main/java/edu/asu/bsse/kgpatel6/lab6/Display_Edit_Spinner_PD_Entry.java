package edu.asu.bsse.kgpatel6.lab6;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.Arrays;
import java.util.Locale;

//Needs to be done fully, need to add methods for bearing and distance in Place library. 
public class Display_Edit_Spinner_PD_Entry extends AppCompatActivity implements DialogInterface.OnClickListener, AdapterView.OnItemSelectedListener {

    private final int camefroma = 2;
    private final int camefromspin = 3;

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
    private String PlaceNtemp[];
    private Place_Description ToDisplayherePD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_edit_spinner_pd_entry);

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

        PlaceNtemp = new String[] {"Unknown"};

        Intent FromMainA = getIntent();

        selectedPlace = FromMainA.getStringExtra("selectedPD")!=null ? FromMainA.getStringExtra("selectedPD") : "unknown";
        if (selectedPlace.equals("unknown")) {
            Intent bti = new Intent();
            this.setResult(RESULT_CANCELED, bti);
            finish();
        }

        PlacesSpinner = findViewById(R.id.PlacesSpinner);
        //setupPlacesSpin();
        PlacesSpinner.setOnItemSelectedListener(this);

        //displayPlace();

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"exception action bar: "+ex.getLocalizedMessage());
        }

        setTitle("Display Place Description Entry");

        // initiate request to server to get the names of all places to be placed in the ListView
        try{
            MethodInformation mi = new MethodInformation(this, getString(R.string.defaulturl),"get",
                    new Object[]{selectedPlace}, camefroma);
            AsyncGetPlaceInfo ac = (AsyncGetPlaceInfo) new AsyncGetPlaceInfo().execute(mi);
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception getting place info from it's key in DEPD: "+
                    ex.getMessage());
        }

        try{
            MethodInformation mi = new MethodInformation(this, getString(R.string.defaulturl),"getNames",
                    new Object[]{}, camefroma);
            AsyncGetPlaceNamesConnect ac = (AsyncGetPlaceNamesConnect) new AsyncGetPlaceNamesConnect().execute(mi);
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception gettting place names in DEPD: "+
                    ex.getMessage());
        }
    }

    public void setToDisplayherePD(Place_Description toDisplayherePDtemp) {
        ToDisplayherePD = toDisplayherePDtemp;
    }

    public void setPlaceNtemp (String tempplaceN[]) {
        PlaceNtemp = tempplaceN;
    }

    public void displayPlace() {
        Pname.setText(ToDisplayherePD.getPlaceName());
        Des.setText(ToDisplayherePD.getDescriptionofP());
        Cat.setText(ToDisplayherePD.getCategory());
        At.setText(ToDisplayherePD.getAddressTitle());
        As.setText(ToDisplayherePD.getAddressStreet());
        El.setText(String.format(Locale.getDefault(), "%f", ToDisplayherePD.getElevation()));
        Lat.setText(String.format(Locale.getDefault(), "%f", ToDisplayherePD.getLatitude()));
        Long.setText(String.format(Locale.getDefault(), "%f", ToDisplayherePD.getLongitude()));
    }

    public void setupPlacesSpin() {
        //String PlaceNtemp[] = tempPL.getKeys();
        Arrays.sort(PlaceNtemp);
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

            ToDisplayherePD.setPlaceName(Pname.getText().toString());
            ToDisplayherePD.setDescriptionofP(Des.getText().toString());
            ToDisplayherePD.setCategory(Cat.getText().toString());
            ToDisplayherePD.setAddressTitle(At.getText().toString());
            ToDisplayherePD.setAddressStreet(As.getText().toString());
            ToDisplayherePD.setElevation(Double.parseDouble(El.getText().toString()));
            ToDisplayherePD.setLatitude(Double.parseDouble(Lat.getText().toString()));
            ToDisplayherePD.setLongitude(Double.parseDouble(Long.getText().toString()));
            EditStudentAlert();

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

        String selectedPlace = PlacesSpinner.getSelectedItem().toString();

        try{
            MethodInformation mi = new MethodInformation(this, getString(R.string.defaulturl),"get",
                    new Object[]{selectedPlace}, camefromspin);
            AsyncGetPlaceInfo ac = (AsyncGetPlaceInfo) new AsyncGetPlaceInfo().execute(mi);
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception getting place info from it's key in DEPD: "+
                    ex.getMessage());
        }

        //android.util.Log.d(this.getClass().getSimpleName(),"GCD: " + Double.toString(ToDisplayherePD.getGreatCircleDistance(tempPL.get(selectedPlace).getLatitude(), tempPL.get(selectedPlace).getLongitude())));
        //android.util.Log.d(this.getClass().getSimpleName(),"Bearing: " + Double.toString(ToDisplayherePD.getBearing(tempPL.get(selectedPlace).getLatitude(), tempPL.get(selectedPlace).getLongitude())));

        //GreatCircleDis.setText(Double.toString(ToDisplayherePD.getGreatCircleDistance(tempPL.get(selectedPlace).getLatitude(), tempPL.get(selectedPlace).getLongitude())) + " KM");
        //BearingDis.setText(Double.toString(ToDisplayherePD.getBearing(tempPL.get(selectedPlace).getLatitude(), tempPL.get(selectedPlace).getLongitude())) + " Degrees");


    }

    public void setGCDandBearing (double latt, double longt) {
        GreatCircleDis.setText(Double.toString(ToDisplayherePD.getGreatCircleDistance(latt, longt)) + " KM");
        BearingDis.setText(Double.toString(ToDisplayherePD.getBearing(latt, longt)) + " Degrees");
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
        dialog.setTitle("Remove: "+this.ToDisplayherePD.getPlaceName()+"?");
        dialog.setNegativeButton("Cancel", this);
        dialog.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.util.Log.d(this.getClass().getSimpleName(),"onClick positive button for remove Place." );
                Intent ti = new Intent();
                ti.putExtra("DeletedPD", ToDisplayherePD.getPlaceName());
                setResult(RESULT_OK, ti);
                finish();
            }
        });
        dialog.show();
    }

    private void EditStudentAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Saves Changes to: "+this.ToDisplayherePD.getPlaceName()+"?");
        dialog.setNegativeButton("Cancel", this);
        dialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.util.Log.d(this.getClass().getSimpleName(),"onClick positive button for Edit Place." );
                Intent ti = new Intent();
                ti.putExtra("EditedPD", "ToDisplayherePD");
                setResult(RESULT_OK, ti);
                finish();
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