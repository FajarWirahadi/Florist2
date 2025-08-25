package com.example.florist.views.seller.createshop;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.florist.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;

public class MapsActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSION_REQUEST_CODE = 1;
    private MapView map = null;
    private MyLocationNewOverlay mMyLocationOverlay;
    private LocationManager locationManager;

    FloatingActionButton floatingActionButton;
    Geocoder geocoder;

    EditText etSearchLocation;
    TextView tvKnownName, tvAddress;
    Button btnNext;
    private TextInputEditText searchET;
    private boolean ignoreNextQueryUpdate = false;
    boolean focusLocation = true;


//    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
//        @Override
//        public void onActivityResult(Boolean result) {
//            if(result) {
//                Toast.makeText(MapsActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(MapsActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    });
//
//    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
//        @Override
//        public void onIndicatorBearingChanged(double v) {
//            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
//        }
//    };
//
//    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
//        @Override
//        public void onIndicatorPositionChanged(@NonNull Point point) {
//            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(20.0).build());
//            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
//            MapsActivity.this.point = point;
//        }
//    };
//
//    private final OnMoveListener onMoveListener = new OnMoveListener() {
//        @Override
//        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
//            focusLocation = false;
//            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
//            getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
//            getGestures(mapView).removeOnMoveListener(onMoveListener);
//            floatingActionButton.show();
//        }
//
//        @Override
//        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
//            return false;
//        }
//
//        @Override
//        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {
//
//        }
//    };
//
//    private void updateCamera(Point point, Double bearing) {
//        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
//        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(18.0).bearing(bearing).pitch(45.0)
//                .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();
//
//        getCamera(mapView).easeTo(cameraOptions, animationOptions);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_maps);

        tvKnownName = findViewById(R.id.tvKnownName);
        tvAddress = findViewById(R.id.tvAddress);
        floatingActionButton = findViewById(R.id.focusLocation);
        btnNext = findViewById(R.id.btnNext);
        map = (MapView) findViewById(R.id.map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.lokasi_peta);
        setSupportActionBar(toolbar);

        tvKnownName.setVisibility(View.GONE);
        tvAddress.setVisibility(View.GONE);
        btnNext.setEnabled(false);



        needsPermissions();
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE});

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        GeoPoint startPoint = new GeoPoint(-6.200000, 106.816666);
        mapController.setZoom(10.0);
        Marker startMarker = new Marker(map);
        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        mapController.setCenter(startPoint);
        MyLocationNewOverlay myLocationoverlay = new MyLocationNewOverlay(map);

        final MapEventsReceiver mReceive = new MapEventsReceiver(){
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                map.getOverlays().remove(myLocationoverlay);
                map.invalidate();
//                Toast.makeText(getBaseContext(),p.getLatitude() + " - "+p.getLongitude(), Toast.LENGTH_LONG).show();
                startMarker.setPosition(p);
                startMarker.closeInfoWindow();
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                startMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.location, null));
                map.getOverlays().add(startMarker);

                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(p.getLatitude(), p.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String knownName = addresses.get(0).getThoroughfare() + " " +addresses.get(0).getSubThoroughfare();// Only if available else return NULL
                knownName.replaceAll("\\null\\b", "");
                tvAddress.setText(address);
                tvKnownName.setText(knownName);
                tvAddress.setVisibility(View.VISIBLE);
                tvKnownName.setVisibility(View.VISIBLE);
                return false;
            }
            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        map.getOverlays().add(new MapEventsOverlay(mReceive));
        map.invalidate();


        if (!tvAddress.getText().equals("")) {
                    btnNext.setEnabled(true);
                    btnNext.setBackground(AppCompatResources.getDrawable(MapsActivity.this, R.drawable.rounded_success_button));
                }
                tvAddress.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String text = editable.toString();
                        if (text.length() > 0) {
                            btnNext.setEnabled(true);
                            btnNext.setBackground(AppCompatResources.getDrawable(MapsActivity.this, R.drawable.rounded_success_button));
                        } else {
                            btnNext.setEnabled(false);
                            btnNext.setBackground(AppCompatResources.getDrawable(MapsActivity.this, R.drawable.rounded_normal_edittext));
                        }
                    }
                });


        Drawable currentDraw = ResourcesCompat.getDrawable(getResources(), R.drawable.location, null);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.getOverlays().remove(mReceive);
                map.invalidate();
                myLocationoverlay.enableFollowLocation();
                myLocationoverlay.setPersonIcon(drawMarker(currentDraw));
                myLocationoverlay.enableMyLocation();
                map.getOverlays().add(myLocationoverlay);
                mapController.setZoom(19.0);

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MapsActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String knownName = addresses.get(0).getThoroughfare()
                        + " " +addresses.get(0).getSubThoroughfare();// Only if available else return NULL
                knownName.replaceAll("null", "");
                tvAddress.setText(address);
                tvKnownName.setText(knownName);
                tvAddress.setVisibility(View.VISIBLE);
                tvKnownName.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSION_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean needsPermissions() {

        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
        if (permissions.isEmpty()) {
            return false;
        } // else: We already have permissions, so handle as normal
        return true;

    }

    public String convertPointToLocation(GeoPoint point) {
        String address = "";
        Geocoder geoCoder = new Geocoder( getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                    point.getLatitude(),
                    point.getLongitude(), 1);

            if (addresses.size() > 0) {
                for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
                    address += addresses.get(0).getAddressLine(index) + " ";
            }


            Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public Bitmap drawMarker(Drawable currentDraw) {
        Bitmap currentIcon = null;
        if (currentDraw != null) {
            currentIcon = Bitmap.createBitmap(currentDraw.getIntrinsicWidth(), currentDraw.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(currentIcon);
            currentDraw.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            currentDraw.draw(canvas);
        }
        return currentIcon;
    }


}