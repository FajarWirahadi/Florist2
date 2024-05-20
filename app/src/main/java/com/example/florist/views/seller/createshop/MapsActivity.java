package com.example.florist.views.seller.createshop;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import com.example.florist.model.Location;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;

public class MapsActivity extends AppCompatActivity {

    MapView mapView;
    FloatingActionButton floatingActionButton;
    Point point;
    EditText etSearchLocation;
    TextView tvKnownName, tvAddress;
    Button btnNext;
    private PlaceAutocomplete placeAutocomplete;
    private SearchResultsView searchResultsView;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private TextInputEditText searchET;
    private boolean ignoreNextQueryUpdate = false;
    boolean focusLocation = true;


    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if(result) {
                Toast.makeText(MapsActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapsActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    });

    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
        }
    };

    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(20.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
            MapsActivity.this.point = point;
        }
    };

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            focusLocation = false;
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);
            floatingActionButton.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };

    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(18.0).bearing(bearing).pitch(45.0)
                .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.mapView);
        floatingActionButton = findViewById(R.id.focusLocation);
        tvKnownName = findViewById(R.id.tvKnownName);
        tvAddress = findViewById(R.id.tvAddress);
        btnNext = findViewById(R.id.btnNext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.lokasi_peta);
        setSupportActionBar(toolbar);

        tvKnownName.setVisibility(View.GONE);
        tvAddress.setVisibility(View.GONE);
        btnNext.setEnabled(false);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        mapView.getMapboxMap().loadStyleUri("mapbox://styles/fajarwira1403/clvw9m3iz01rf01o0c1117u9y", new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());
                LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
                locationComponentPlugin.setEnabled(true);
                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(AppCompatResources.getDrawable(MapsActivity.this, R.drawable.location));
                locationComponentPlugin.setLocationPuck(locationPuck2D);
                locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                getGestures(mapView).addOnMoveListener(onMoveListener);

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location);
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);

                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
                addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        pointAnnotationManager.deleteAll();
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER)
                                .withPoint(point);
                        pointAnnotationManager.create(pointAnnotationOptions);
                        return true;
                    }
                });

                placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
                searchET = findViewById(R.id.searchET);

                searchResultsView = findViewById(R.id.search_result_view);
                searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
                placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(MapsActivity.this));

                searchET.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (ignoreNextQueryUpdate) {
                            ignoreNextQueryUpdate = false;
                        } else {
                            placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
                                @NonNull
                                @Override
                                public CoroutineContext getContext() {
                                    return EmptyCoroutineContext.INSTANCE;
                                }

                                @Override
                                public void resumeWith(@NonNull Object o) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            searchResultsView.setVisibility(View.VISIBLE);
                                            btnNext.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
                    @Override
                    public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

                    }

                    @Override
                    public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        ignoreNextQueryUpdate = true;
                        focusLocation = false;
                        searchET.setText(placeAutocompleteSuggestion.getName());
                        searchResultsView.setVisibility(View.GONE);
                        Point b = placeAutocompleteSuggestion.getCoordinate();
                        String place = placeAutocompleteSuggestion.getFormattedAddress();

                        point = placeAutocompleteSuggestion.getCoordinate();


                        Geocoder geocoder;

                        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                        Log.d("place", place);
                        pointAnnotationManager.deleteAll();
//                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER)
//                                .withPoint(placeAutocompleteSuggestion.getCoordinate());
//                        pointAnnotationManager.create(pointAnnotationOptions);
//                        updateCamera(placeAutocompleteSuggestion.getCoordinate(), 0.0);
                    }

                    @Override
                    public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {

                    }

                    @Override
                    public void onError(@NonNull Exception e) {

                    }
                });

//                if (!tvAddress.getText().equals("")) {
//                    btnNext.setEnabled(true);
//                    btnNext.setBackground(AppCompatResources.getDrawable(MapsActivity.this, R.drawable.rounded_success_button));
//                }
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

                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MapsActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                Location location= new Location();
                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocation = true;
                        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                        getGestures(mapView).addOnMoveListener(onMoveListener);


//                            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
//                                    .withTextAnchor(TextAnchor.CENTER)
//                                    .withIconImage(bitmap)
//                                    .withPoint(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
//                            pointAnnotationManager.create(pointAnnotationOptions);

//                        ReverseGeoOptions reverseGeoOptions = new ReverseGeoOptions.Builder(Point.fromLngLat(point.longitude(), point.latitude()))
//                                .limit(1)
//                                        .build();
                        double latitude = point.latitude();
                        double longitude = point.longitude();
                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();

                        tvKnownName.setText(knownName);
                        tvAddress.setText(address);
                        tvKnownName.setVisibility(View.VISIBLE);
                        tvAddress.setVisibility(View.VISIBLE);

                        Log.d("currentLongitude1", String.valueOf(point.longitude()));
                        Log.d("currentLatitude1", String.valueOf(point.latitude()));
                        Log.d("currentcoordinates", String.valueOf(point.coordinates()));
//                        Log.d("currentCountries", reverseGeoOptions.getCountries().toString());

                        Log.d("currentAddress", address);
                        Log.d("currentCity", city);
                        Log.d("currentState", state);
                        Log.d("currentCountry", country);
                        Log.d("currentPostalCode", postalCode);
                        Log.d("currentKnownName", knownName);


//                        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
//                            @Override
//                            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
//
//                                Log.d("currentLongitude2", String.valueOf(point.longitude()));
//                                Log.d("currentLatitude2", String.valueOf(point.latitude()));
//                                return true;
//                            }
//                        });
                        floatingActionButton.hide();


                    }
                });
            }
        });
    }
}