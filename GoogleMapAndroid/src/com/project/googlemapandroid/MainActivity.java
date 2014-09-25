package com.project.googlemapandroid;

import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends ActionBarActivity implements LocationListener {
	private LocationManager locationManager;
	private Marker marker;
	private static GoogleMap googleMap;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// init. google map
		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
	}
	
	@Override
	  public void onResume() {
	      super.onResume();

	      // Obtaining the service reference
	      locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

	      //If GPS is available, subscribes
	      if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	          GPSsubscription();
	      }
	  }
	  @Override
	  public void onPause() {
	      super.onPause();

	      // Invoke the method to unsubscribe
	      GPSunsubscribe();
	  }

	  /**
	  * Method to subscribe to the GPS location.
	  */
	  public void GPSsubscription() {
	      // subscribes
	      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
	  }

	  /**
	  * Method to unsubscribe from the GPS location.
	  */
	  public void GPSunsubscribe() {
	     
	      locationManager.removeUpdates(this);
	  }

	  @Override
	  public void onLocationChanged(final Location location) {
	      //On affiche dans un Toast la nouvelle Localisation
	      /*final StringBuilder msg = new StringBuilder("lat : ");
	      msg.append(location.getLatitude());
	      msg.append( "; lng : ");
	      msg.append(location.getLongitude());

	      Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show(); */
	      
	      //Mise à jour des coordonnées
	      /*final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());      
	      googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
	      marker.setPosition(latLng);*/
	  }

	  @Override
	  public void onProviderDisabled(final String provider) {
	      //If GPS is disable => unsubscribe
	      if("gps".equals(provider)) {
	          GPSunsubscribe();
	      }        
	  }

	  @Override
	  public void onProviderEnabled(final String provider) {
	   
	      if("gps".equals(provider)) {
	          GPSsubscription();
	      }
	  }

	  @Override
	  public void onStatusChanged(final String provider, final int status, final Bundle extras) { }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions, menu);

        //Here my search widget.
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        
        // Manage search event on click device's keyboard
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
	    	@Override
			public boolean onQueryTextSubmit(String query) {
	    		
	    		onSearchPlace(query);
	    		searchView.clearFocus(); // Dismiss virtual keyboard
	    		
				return true;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
        
        return true;
	}
	
     /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_delete_markers:
        	// delete all markers
        	deleteAllMarkers();
        	return true;
        case R.id.action_current_location:
            // set current location
        	getCurrentLocation();
            return true;
        case R.id.action_normal_map:
            // set normal map
        	googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        case R.id.action_satellite_map:
            // set satellite map
        	googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;
        case R.id.action_terrain_map:
            // set terrain map
        	googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;
        case R.id.action_hybrid_map:
            // set hybrid map
        	googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /*
     * Search requested location and move camera on it
     */
    public void onSearchPlace(String place){
    	Geocoder gc = new Geocoder(MainActivity.this);
    	
    	try{
			List<Address> list = gc.getFromLocationName(place, 1);
			Address add = list.get(0);
			double latitude = add.getLatitude();
			double longitude = add.getLongitude();
			
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
			CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

			googleMap.moveCamera(center);
			googleMap.animateCamera(zoom); 
	    	googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(place));
	    	
	    	displayLocation(latitude, longitude);
	    	
    	} catch(Exception ex){
    		System.out.println(ex);
    	}
    }
    
    /*
     * Display the coordinates of the place searched
     */
    public void displayLocation(double latitude, double longitude){
    	
    	// Set new location
    	Location location = null;
		location.setLatitude(latitude);
		location.setLongitude(longitude);
    	
    	StringBuilder msg = new StringBuilder("lat : ");
	    msg.append(location.getLatitude());
	    msg.append( "; lng : ");
	    msg.append(location.getLongitude());
	
	    Toast.makeText(this, msg.toString(), Toast.LENGTH_SHORT).show();
    }
    
    /*
     * Move to the current location and set the UI button
     */
    public void getCurrentLocation(){
    	googleMap.setMyLocationEnabled(true);   	
    }
    
    /*
     * Delete all markers on the map
     */
    public void deleteAllMarkers(){
    	googleMap.clear();
    	googleMap.setMyLocationEnabled(false);
    }
}
