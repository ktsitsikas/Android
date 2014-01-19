package com.example.mrdummy;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private GoogleMap googleMap;
	private static final String TAG = "MainActivity";
	//private static String urlget = "http://192.168.2.6:3000/updateUserLocation";
	//private static String urlpost = "http://192.168.2.6:3000/updateVehicleLocation";
	private static EditText editTextIP, editTextPort, editTextFunction;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 90000;
    //add button that changes this
    boolean mUpdatesRequested = true;
    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editTextIP = (EditText) this.findViewById(R.id.editTextIP);
		editTextPort = (EditText) this.findViewById(R.id.editTextPort);
		editTextFunction = (EditText) this.findViewById(R.id.editTextFunction);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startSensing(View view) {
		Log.v(TAG,"Start Sensing!");
        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(0);
        mLocationRequest.setSmallestDisplacement(0);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling 
        mLocationRequest.setFastestInterval(0);

        // Note that location updates are off until the user turns them on
        mUpdatesRequested = true;

        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        
    	Log.v(TAG,"Start getting location!");
        mUpdatesRequested = true;

        if (servicesConnected()) {
        	Log.v(TAG,"Services connected!");
        	mLocationClient.connect();
        }
	}
	
	public void stopSensing(View view) {
	    // Do something in response to button	
		Log.v(TAG,"Stop Sensing!");
		mLocationClient.disconnect();
	}

	// Check if Google Play Services are available
	@SuppressLint("NewApi")
	public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
	
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            // Continue
        	Log.v(TAG,"Connected!");
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
            }
            return false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
        }
    }
	
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    @Override
    public void onConnected(Bundle bundle) {

        if (mUpdatesRequested) {
        	Log.v(TAG,"Start getting updates!");
            startPeriodicUpdates();
        }
        
        //TODO: Separate function
        setContentView(R.layout.activity_map);
        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Getting GoogleMap object from the fragment
        googleMap = fm.getMap();
        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
    }

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void onLocationChanged(Location location) {
    	double latitude, longitude;
    	
    	latitude = location.getLatitude();
    	longitude = location.getLongitude();
        String msg = "Updated Location: " +
                Double.toString(latitude) + "," +
                Double.toString(longitude);

        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng).title("cur_pos"));
        CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(latLng)      		// Sets the center of the map to Mountain View
        .zoom(17)                   // Sets the zoom
        .bearing(90)                // Sets the orientation of the camera to east
        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
        .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        
        JSONObject postData = new JSONObject();
        JSONObject connectionData = new JSONObject();
        try {
        	//Create json for postData
        	postData.put("id", 4);
        	postData.put("name", "220");
        	
        	JSONObject loc = new JSONObject();
        	loc.put("latitude", latitude);
        	loc.put("longitude", longitude);
	        postData.put("location", loc);
	        
	        //Create json for connectionData
	        connectionData.put("method", "POST");
	        connectionData.put("ip", editTextIP.getText().toString());
	        connectionData.put("port", editTextPort.getText().toString());
	        connectionData.put("function", editTextFunction.getText().toString());
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		Rester rester = new Rester();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			rester.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, connectionData, postData);
		else
			rester.execute(connectionData, postData);
        Log.v(TAG,"Location found:"+msg);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
    }
    
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }
    
}
