package edu.asu.bsse.kgpatel6.lab6;

import org.json.JSONObject;

import java.io.Serializable;

public class Place_Description extends Object implements Serializable {

    private String PlaceName;
    private String DescriptionofP;
    private String Category;
    private String AddressTitle;
    private String AddressStreet;
    private double Elevation;
    private double Latitude;
    private double Longitude;

    public Place_Description(JSONObject PDJSONO) {
        try {
            PlaceName = PDJSONO.getString("name");
            DescriptionofP = PDJSONO.getString("description");
            Category = PDJSONO.getString("category");
            AddressTitle = PDJSONO.getString("address-title");
            AddressStreet = PDJSONO.getString("address-street");
            Elevation = PDJSONO.getDouble("elevation");
            Latitude = PDJSONO.getDouble("latitude");
            Longitude = PDJSONO.getDouble("longitude");

        }
        catch (Exception e) {
            e.printStackTrace();
            android.util.Log.w(this.getClass().getSimpleName(),"error converting from json");
        }
    }



    public JSONObject getasJSON() {
        try {
            JSONObject PDJSONobj = new JSONObject();
            PDJSONobj.put("name", PlaceName);
            PDJSONobj.put("description", DescriptionofP);
            PDJSONobj.put("category", Category);
            PDJSONobj.put("address-title", AddressTitle);
            PDJSONobj.put("address-street", AddressStreet);
            PDJSONobj.put("elevation", Elevation);
            PDJSONobj.put("latitude", Latitude);
            PDJSONobj.put("longitude", Longitude);

            return PDJSONobj;
        }
        catch (Exception e) {
            android.util.Log.w(this.getClass().getSimpleName(),"error converting to json");
        }

        return null;

    }

    public double getGreatCircleDistance (double lat2, double long2) {
        double lat1rad = getLatitude() * Math.PI / 180;
        double lat2rad = lat2 * Math.PI / 180;
        double deltalat = (lat2 - getLatitude()) * Math.PI / 180;
        double deltaLong = (long2 - getLongitude()) * Math.PI / 180;

        //System.out.println(getLatitude());
        //System.out.println(getLongitude());
        //System.out.println(lat2);
        //System.out.println(long2);

        double a = Math.pow(Math.sin(deltalat / 2), 2) + Math.cos(lat1rad) * Math.cos(lat2rad) * Math.pow(Math.sin( deltaLong / 2 ), 2);

        //System.out.println(Math.pow(Math.sin(Math.toRadians(lat2 - this.getLatitude()) / 2), 2));
        //System.out.println(Math.cos(Math.toRadians(this.getLongitude())));
        //System.out.println(Math.cos(Math.toRadians(long2)));
        //System.out.println(Math.pow(Math.sin( Math.toRadians(long2 - this.getLongitude()) / 2 ), 2));
        //System.out.println(Math.pow(Math.sin(Math.toRadians(lat2 - this.getLatitude()) / 2), 2) + Math.cos(Math.toRadians(this.getLongitude())) + Math.cos(Math.toRadians(long2)) + Math.pow(Math.sin( Math.toRadians(long2 - this.getLongitude()) / 2 ), 2));
        //System.out.println(2 * Math.atan2(Math.sqrt(Math.abs(a)), Math.sqrt(Math.abs(1-a))));
        return 6371.0 * 2.0 * Math.atan2(Math.sqrt(Math.abs(a)), Math.sqrt(Math.abs(1-a)));
    }

    public double getBearing(double lat2b, double long2b) {
        return Math.toDegrees(Math.atan2(Math.sin(Math.toRadians(long2b - this.getLongitude())) * Math.cos(Math.toRadians(lat2b)), (Math.cos(Math.toRadians(this.getLatitude())) * Math.sin(Math.toRadians(lat2b))) - (Math.sin(Math.toRadians(this.getLatitude())) * Math.cos(Math.toRadians(lat2b)) * Math.cos(Math.toRadians(long2b - this.getLongitude())))));
    }



    public void setPlaceName(String placeName) {
        PlaceName = placeName;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public void setDescriptionofP(String descriptionofP) {
        DescriptionofP = descriptionofP;
    }

    public String getDescriptionofP() {
        return DescriptionofP;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getCategory() {
        return Category;
    }

    public void setAddressTitle(String addressTitle) {
        AddressTitle = addressTitle;
    }

    public String getAddressTitle() {
        return AddressTitle;
    }

    public void setAddressStreet(String addressStreet) {
        AddressStreet = addressStreet;
    }

    public String getAddressStreet() {
        return AddressStreet;
    }

    public void setElevation(double elevation) {
        Elevation = elevation;
    }

    public double getElevation() {
        return Elevation;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

}
