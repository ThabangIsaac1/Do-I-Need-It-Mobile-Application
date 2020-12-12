package com.example.do_i_need_it;
/**
 * The java class MapSelection Extends AppCompatActivity
 * This class is used to access a map and upload details of a product to firebase.
 * Note application runs on a Nexus 5X API 30
 *
 * @author Thabang Fenge Isaka
 * @version 1.0
 * @since 2020-11-16
 */

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.do_i_need_it.utils.SimplePlacePicker;
import com.example.do_i_need_it.utils.CustomButton;
import com.example.do_i_need_it.utils.CustomTextView;
import com.example.do_i_need_it.utils.FetchAddressIntentService;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapSelectLocation extends AppCompatActivity implements OnMapReadyCallback {
    //location
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLAstKnownLocation;
    private LocationCallback locationCallback;
    private final float DEFAULT_ZOOM = 17;

    //places
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    //views
    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private RippleBackground rippleBg;
    private CustomTextView mDisplayAddressTextView;
    private ProgressBar mProgressBar;
    private ImageView mSmallPinIv;

    //variables
    private String addressOutput;
    private int addressResultCode;
    private boolean isSupportedArea;
    private LatLng currentMarkerPosition;

    //receiving
    private String mApiKey = String.valueOf(R.string.google_api_key );
    private String[] mSupportedArea = new String[]{};
    private String mCountry = "";
    private String mLanguage = "en";

    //firebase
    private StorageReference mStorageRef;
    private FirebaseStorage storage;
    FirebaseAuth fireAuth;
    FirebaseFirestore fireStore;
    String userId;

    //image
    Uri imageuri;
    String url;


    private static final String TAG = MapSelectLocation.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_content_main);

        //FirebaseStorage, FirebaseAuth and FirebaseFirestore instance.
        mStorageRef = FirebaseStorage.getInstance().getReference();
        fireAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = fireAuth.getCurrentUser().getUid();

        initViews();
        receiveIntent();
        initMapsAndPlaces();
    }
        //initialise views or components
    private void initViews(){
        materialSearchBar = findViewById(R.id.searchBar);
        CustomButton submitLocationButton = findViewById(R.id.submit_location_button);
        rippleBg = findViewById(R.id.ripple_bg);
        mDisplayAddressTextView = findViewById(R.id.tv_display_marker_location);
        mProgressBar = findViewById(R.id.progress_bar);
        mSmallPinIv = findViewById(R.id.small_pin);


        final View icPin = findViewById(R.id.ic_pin);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                revealView(icPin);
            }
        }, 1000);


        //OnClickListener to open location builder and upload products
        submitLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLocationButton.setText(R.string.processing);
                submitResultLocation();
            }
        });

    }


    //Inetent to receive API keys and other variables from FetchPlace intent Service
    private void receiveIntent(){
        Intent intent = getIntent();

        if (intent.hasExtra(SimplePlacePicker.API_KEY)){
            mApiKey = intent.getStringExtra(SimplePlacePicker.API_KEY);
        }

        if (intent.hasExtra(SimplePlacePicker.COUNTRY)){
            mCountry = intent.getStringExtra(SimplePlacePicker.COUNTRY);
        }

        if (intent.hasExtra(SimplePlacePicker.LANGUAGE)){
            mLanguage = intent.getStringExtra(SimplePlacePicker.LANGUAGE);
        }

        if (intent.hasExtra(SimplePlacePicker.SUPPORTED_AREAS)){
            mSupportedArea = intent.getStringArrayExtra(SimplePlacePicker.SUPPORTED_AREAS);
        }
    }


    //Initialise map and current location on the map
    private void initMapsAndPlaces() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(this, mApiKey);
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.disableSearch();
                    materialSearchBar.clearSuggestions();
                }
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setCountry(mCountry)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!materialSearchBar.isSuggestionsVisible()) {
                                            materialSearchBar.showSuggestionsList();
                                        }
                                    }
                                }, 1000);
                            }
                        } else {
                            Log.i(TAG, "prediction fetching task unSuccessful");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //Search bar to find locations by name
        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                }

                String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i(TAG, "place found " + place.getName() + place.getAddress());
                        LatLng latLng = place.getLatLng();
                        if (latLng != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                        }

                        rippleBg.startRippleAnimation();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rippleBg.stopRippleAnimation();
                            }
                        }, 2000);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e instanceof ApiException) {
                                    ApiException apiException = (ApiException) e;
                                    apiException.printStackTrace();
                                    int statusCode = apiException.getStatusCode();
                                    Log.i(TAG, "place not found" + e.getMessage());
                                    Log.i(TAG, "status code : " + statusCode);
                                }
                            }
                        });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
            }
        });
    }

    private void submitResultLocation(){
        // if the process of getting address failed or this is not supported area , don't submit
        if (addressResultCode == SimplePlacePicker.FAILURE_RESULT || !isSupportedArea) {
            Toast.makeText(MapSelectLocation.this, "Failed", Toast.LENGTH_SHORT).show();
        } else {

            //Get Product Details from Intent data
            Intent intent = getIntent();
            String productName = intent.getStringExtra("product_name");
            String productDescription = intent.getStringExtra("product_description");
            String productPrice = intent.getStringExtra("product_price");
            String productSite = intent.getStringExtra("product_site");
            String productImage = intent.getStringExtra("image-uri");
            imageuri = Uri.parse(productImage);
            double latitude = currentMarkerPosition.latitude;
            double longitude = currentMarkerPosition.longitude;
            String address = addressOutput;






            final String randomId = UUID.randomUUID().toString();

            StorageReference productImages = mStorageRef.child("productImages/" +randomId);

            productImages.putFile(imageuri).addOnCompleteListener(task -> productImages.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            url = uri.toString();
                                String status ="Pending";

                            //Store Product Details To Firestore

                            //Add product document to firestore for user
                            userId = fireAuth.getCurrentUser().getUid();
                            DocumentReference product = fireStore.collection("products").document();
                            Map<String, Object> productinformation = new HashMap<>();
                            productinformation.put("date_added", new Date().toString());
                            productinformation.put("product_name", productName);
                            productinformation.put("product_owner", Objects.requireNonNull(fireAuth.getCurrentUser()).getEmail());
                            productinformation.put("product_description", productDescription);
                            productinformation.put("product_site", productSite);
                            productinformation.put("product_price", productPrice);
                            productinformation.put("user_id", userId);
                            productinformation.put("product_address", address);
                            productinformation.put("latitude", latitude);
                            productinformation.put("longitude", longitude);
                            productinformation.put("image_url",url);
                            productinformation.put("status",status);



                            product.set(productinformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                        //  Confirm success
                                    new SweetAlertDialog(MapSelectLocation.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Do you really need this item?")
                                            .setContentText("Take time and ponder about it Later")
                                            .setConfirmText("OK!")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog
                                                            .setTitleText("Product Added!")
                                                            .setContentText("Your List Is Updated")
                                                            .setConfirmText("Check It Out")
                                                            .setConfirmClickListener(null)
                                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);




                                                    Intent intent = new Intent(MapSelectLocation.this, MainActivity2.class);
                                                    startActivity(intent);
                                                }

                                            })
                                            .show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MapSelectLocation.this, "Product Failed To Add", Toast.LENGTH_SHORT).show();

                                }
                            });







                        }
                    }));

        }
    }

    @SuppressLint("MissingPermission")

    /*
      is triggered when the map is loaded and ready to display
      @param GoogleMap
     *
     * */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        //enable location button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);

        //move location button to the required position and adjust params such margin
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 60, 500);
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        //if task is successful means the gps is enabled so go and get device location amd move the camera to that location
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        //if task failed means gps is disabled so ask user to enable gps
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapSelectLocation.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible()) {
                    materialSearchBar.clearSuggestions();
                }
                if (materialSearchBar.isSearchEnabled()) {
                    materialSearchBar.disableSearch();
                }
                return false;
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mSmallPinIv.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                Log.i(TAG, "changing address");
//                ToDo : you can use retrofit for this network call instead of using services
                //hint: services is just for doing background tasks when the app is closed no need to use services to update ui
                //best way to do network calls and then update user ui is Retrofit .. consider it
                startIntentService();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    /**
     * is triggered whenever we want to fetch device location
     * in order to get device's location we use FusedLocationProviderClient object that gives us the last location
     * if the task of getting last location is successful and not equal to null ,
     * apply this location to mLastLocation instance and move the camera to this location
     * if the task is not successful create new LocationRequest and LocationCallback instances and update lastKnownLocation with location result
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLAstKnownLocation = task.getResult();
                            if (mLAstKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLAstKnownLocation.getLatitude(), mLAstKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                            else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(1000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLAstKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLAstKnownLocation.getLatitude(), mLAstKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        //remove location updates in order not to continues check location unnecessarily
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, null);
                            }
                        } else {
                            Toast.makeText(MapSelectLocation.this, "Unable to get last location ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    protected void startIntentService() {
        currentMarkerPosition = mMap.getCameraPosition().target;

        AddressResultReceiver resultReceiver = new AddressResultReceiver(new Handler());

        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(SimplePlacePicker.RECEIVER, resultReceiver);
        intent.putExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition.latitude);
        intent.putExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition.longitude);
        intent.putExtra(SimplePlacePicker.LANGUAGE, mLanguage);

        startService(intent);
    }

    private void updateUi() {
        mDisplayAddressTextView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mMap.clear();
        if (addressResultCode == SimplePlacePicker.SUCCESS_RESULT) {
            //check for supported area
            if (isSupportedArea(mSupportedArea)) {
                //supported
                addressOutput = addressOutput.replace("Unnamed Road,", "");
                addressOutput = addressOutput.replace("Unnamed Road،", "");
                addressOutput = addressOutput.replace("Unnamed Road New,", "");
                mSmallPinIv.setVisibility(View.VISIBLE);
                isSupportedArea = true;
                mDisplayAddressTextView.setText(addressOutput);
            } else {
                //not supported
                mSmallPinIv.setVisibility(View.GONE);
                isSupportedArea = false;
                mDisplayAddressTextView.setText("Area not supported");
            }
        } else if (addressResultCode == SimplePlacePicker.FAILURE_RESULT) {
            mSmallPinIv.setVisibility(View.GONE);
            mDisplayAddressTextView.setText(addressOutput);
        }
    }

    private boolean isSupportedArea(String[] supportedAreas) {
        if (supportedAreas.length==0)
            return true;

        boolean isSupported = false;
        for (String area : supportedAreas) {
            if (addressOutput.contains(area)) {
                isSupported = true;
                break;
            }
        }
        return isSupported;
    }

    private void revealView(View view) {
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        Animator anim = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        }
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressResultCode = resultCode;
            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(SimplePlacePicker.RESULT_DATA_KEY);
            if (addressOutput == null) {
                addressOutput = "";
            }
            updateUi();
        }
    }
}

