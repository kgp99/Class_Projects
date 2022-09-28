package edu.asu.bsse.kgpatel6.lab6;

import android.os.AsyncTask;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class AsyncGetPlaceInfo extends AsyncTask<MethodInformation, Integer, MethodInformation> {

    @Override
    protected void onPreExecute(){
        android.util.Log.d(this.getClass().getSimpleName(),"in onPreExecute on "+
                (Looper.myLooper() == Looper.getMainLooper()?"Main thread":"Async Thread"));
    }

    @Override
    protected MethodInformation doInBackground(MethodInformation... aRequest){
        // array of methods to be called. Assume exactly one input, a single MethodInformation object
        android.util.Log.d(this.getClass().getSimpleName(),"in doInBackground on "+
                (Looper.myLooper() == Looper.getMainLooper()?"Main thread":"Async Thread"));
        try {
            JSONArray ja = new JSONArray(aRequest[0].params);
            android.util.Log.d(this.getClass().getSimpleName(),"params: "+ja.toString());
            String requestData = "{ \"jsonrpc\":\"2.0\", \"method\":\""+aRequest[0].method+"\", \"params\":"+ja.toString()+
                    ",\"id\":3}";
            android.util.Log.d(this.getClass().getSimpleName(),"requestData: "+requestData+" url: "+aRequest[0].urlString);
            JsonRPCRequestViaHttp conn = new JsonRPCRequestViaHttp((new URL(aRequest[0].urlString)), aRequest[0].parent);
            aRequest[0].resultAsJson = conn.call(requestData);
        }catch (Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"exception in remote call "+
                    ex.getMessage());
        }
        return aRequest[0];
    }

    @Override
    protected void onPostExecute(MethodInformation res){
        android.util.Log.d(this.getClass().getSimpleName(), "in onPostExecute on " +
                (Looper.myLooper() == Looper.getMainLooper() ? "Main thread" : "Async Thread"));
        android.util.Log.d(this.getClass().getSimpleName(), " resulting is: " + res.resultAsJson);
        try {
            if (res.method.equals("get")) {
                JSONObject jo = new JSONObject(res.resultAsJson);
                if (res.camefrom == 2) {
                    Display_Edit_Spinner_PD_Entry DEPD = (Display_Edit_Spinner_PD_Entry) res.parent;
                    DEPD.setToDisplayherePD(new Place_Description(jo.getJSONObject("result")));
                    DEPD.displayPlace();
                }
                else if (res.camefrom == 3) {
                    Display_Edit_Spinner_PD_Entry DEPD = (Display_Edit_Spinner_PD_Entry) res.parent;
                    Place_Description tempPD = new Place_Description(jo.getJSONObject("result"));
                    DEPD.setGCDandBearing(tempPD.getLatitude(), tempPD.getLongitude());
                }

            }
            else {
                android.util.Log.d(this.getClass().getSimpleName(),"Got back wrong method or called wrong method");
            }
        }catch (Exception ex){
            android.util.Log.d(this.getClass().getSimpleName(),"Exception: "+ex.getMessage());
        }
    }
}
