package com.ridecell.app.ridecell.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ridecell.app.ridecell.DataModels.SpotData;
import com.ridecell.app.ridecell.Interfaces.ReserveInterface;
import com.ridecell.app.ridecell.Interfaces.SearchInterface;
import com.ridecell.app.ridecell.R;
import com.ridecell.app.ridecell.Utilities.Constants;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ParkingSpotsActivity extends FragmentActivity implements OnMapReadyCallback, GestureDetector.OnGestureListener {

    //================================================================================
    // PROPERTIES
    //================================================================================
    private GoogleMap gmap;
    private HashMap<String, SpotData> foundSpotsMap = new HashMap<>();
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private RelativeLayout srchLayout;
    private EditText srchField;
    private RelativeLayout green_srchLayout;
    private Animation slide_down;
    private Animation slide_up;
    private Animation slide_down_short;
    private Animation slide_up_short;
    private Context ctx;
    private LatLng srchPoint;
    private ProgressBar spinner;
    private String srchTerm;
    private String addrName = "";
    private String theAddress = "";
    private String distance;
    private SeekBar seekBar;
    private int minutes = 10;
    private TextView seekVal;
    private EditText dateField;
    private ImageView dateIcon;
    private EditText timeField;
    private ImageView timeIcon;
    private Calendar calendar;
    private String selectedDateTime;
    private Marker selectedMarker;
    private ArrayList<Marker> reservedMarkers;
    private SharedPreferences sharedPrefs;
    private GestureDetector gDetector;
    private Dialog dialog ;


    //================================================================================
    // ACTIVITY METHODS
    //================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_spots);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ctx = this;

        iniSettViews();
        showToast("Enter a street in San Francisco");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        // Move the map camera on San Francisco
        LatLng sanFranciscoCord = new LatLng(37.7749, -122.4194);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(sanFranciscoCord, 10);
        gmap.animateCamera(location);

        // Marker click listener
        gmap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                selectedMarker = marker;

                Constants.SLECTED_SPOT = foundSpotsMap.get(marker.getId());
                int orientation = getResources().getConfiguration().orientation;
                moveMapCameraToSpot(orientation);
                return true;
            }
        });

    }// onMapReady


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(dialog != null) {
            dialog.dismiss();
            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                moveMapCameraToSpot(Configuration.ORIENTATION_LANDSCAPE);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                moveMapCameraToSpot(Configuration.ORIENTATION_PORTRAIT);
            }
        }
    }


    //================================================================================
    // END ACTIVITY METHODS
    //================================================================================


    //================================================================================
    // INIT VIEWS AND SETUPS
    //================================================================================
    private void iniSettViews() {

        gDetector = new GestureDetector(this, this);
        srchLayout = (RelativeLayout) findViewById(R.id.srchLayout);
        green_srchLayout = (RelativeLayout) findViewById(R.id.srchLayoutGreen);
        srchField = (EditText) findViewById(R.id.locationEdit);
        setSrchFieldListeners();

        spinner = (ProgressBar) findViewById(R.id.spinner);

        seekVal = (TextView) findViewById(R.id.seekval);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        setSeekBarListeners();

        calendar = Calendar.getInstance();

        dateField = (EditText) findViewById(R.id.dateEdit);
        dateIcon = (ImageView) findViewById(R.id.dateIcn);
        setDateFieldListener();

        timeField = (EditText) findViewById(R.id.timeEdit);
        timeIcon = (ImageView) findViewById(R.id.timeIcn);
        setTimeFieldListener();

        loadAnim();

        reservedMarkers = new ArrayList<>();
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);

        showSearchWindow();

        // The search icon shown at the bottom of search window
        final Button srchIcn = (Button) findViewById(R.id.srchIcn);
        srchIcn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSearchWindow();
                return gDetector.onTouchEvent(motionEvent);
            }
        });


        // The green search icon shown when search window hidden
        final Button srchIcn_green = (Button) findViewById(R.id.srchIcnGreen);
        srchIcn_green.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showSearchWindow();
                return gDetector.onTouchEvent(motionEvent);
            }
        });

        // Search Button
        final Button srchBtn = (Button) findViewById(R.id.srchBtn);
        srchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setParkingSearch();
            }
        });

        setAnimationListeners();

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.0f);

    }// End initSetViews

    // Set animation variables
    private void loadAnim() {
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide_down_short = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down_short);
        slide_up_short = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_short);
    }
    //================================================================================
    // END INIT VIEWS AND SETUPS
    //================================================================================


    //================================================================================
    // HIDE AND SHOW VIEWS AND DIALOGS METHODS
    //================================================================================
    private void showSearchWindow() {
        green_srchLayout.startAnimation(slide_up_short);
        green_srchLayout.setVisibility(View.GONE);
        srchLayout.startAnimation(slide_down);
        srchLayout.setVisibility(View.VISIBLE);
    }

    private void hideSearchWindow() {
        hideKeyboard();
        srchLayout.startAnimation(slide_up);
        srchLayout.setVisibility(View.GONE);
    }

    // To show progress spinner
    private void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // End hideKeyboard


    // When tapping a spot, focus the map camera on it, then call show marker window method
    private void moveMapCameraToSpot(final int orientation){
        if (Constants.SLECTED_SPOT != null) {
            Double lat = Double.valueOf(Constants.SLECTED_SPOT.getLat());
            Double lng = Double.valueOf(Constants.SLECTED_SPOT.getLng());
            LatLng spot = new LatLng(lat, lng);
            gmap.animateCamera(CameraUpdateFactory.newLatLng(spot), 200, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    setDialogPosition(orientation);
                    showMarkerInfoWindow();
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }


    // When a marker clicked
    private void showMarkerInfoWindow() {

        dialog.setContentView(R.layout.window_marker);
        int orientation = this.getResources().getConfiguration().orientation;
        setDialogPosition(orientation);



        final double lat = Double.valueOf(Constants.SLECTED_SPOT.getLat());
        final double lng = Double.valueOf(Constants.SLECTED_SPOT.getLng());
        latLngToAddress(lat, lng);


        distance = getDistance();

        final TextView location = (TextView) dialog.findViewById(R.id.locationName);
        if (addrName != null)
            location.setText(addrName);

        final TextView address = (TextView) dialog.findViewById(R.id.address);
        if (theAddress != null)
            address.setText(theAddress);

        final TextView open = (TextView) dialog.findViewById(R.id.open);
        open.setText( String.valueOf(getNumOfOpenSpots()));

        final TextView cost = (TextView) dialog.findViewById(R.id.cost);
        final String costStr = Constants.SLECTED_SPOT.getCostPerMinute();
        cost.setText(costStr);

        final TextView distanceTv = (TextView) dialog.findViewById(R.id.distance);
        distanceTv.setText(distance);

        final Button payBtn = (Button) dialog.findViewById(R.id.pay);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reserveSpotAPIRequest(Constants.SLECTED_SPOT.getId());
            }
        });

        final Button moreBtn = (Button) dialog.findViewById(R.id.more);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreWindow();
            }
        });

        dialog.show();

    } // End showMarkerInfoWindow


    // when Reserve button clicked
    private void showReserveWindow(String title, String msg) {

        dialog.setContentView(R.layout.window_dialog);


        final TextView titleTv = (TextView) dialog.findViewById(R.id.title);
        titleTv.setText(title);

        final TextView msgTv = (TextView) dialog.findViewById(R.id.message);
        msgTv.setText(msg);

        final Button checkBtn = (Button) dialog.findViewById(R.id.checkbtn);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInfoActivityWithExtras();
                dialog.dismiss();
            }
        });

        final Button closeBtn = (Button) dialog.findViewById(R.id.closebtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (title.equals("Sorry!")) {
            // checkBtn.setVisibility(View.GONE);
            final ImageView iv = (ImageView) dialog.findViewById(R.id.imageView);
            iv.setImageResource(R.drawable.exmark);
        }

        dialog.show();

    } // End showReserveWindow


    // When More button clicked
    private void showMoreWindow() {

        dialog.setContentView(R.layout.window_more);


        // Get data
        final String name = Constants.SLECTED_SPOT.getName();
        String costPrMin = Constants.SLECTED_SPOT.getCostPerMinute();
        String maxVal = String.valueOf(Constants.SLECTED_SPOT.getMaxReserveTimeMins());
        String minVal = String.valueOf(Constants.SLECTED_SPOT.getMinReserveTimeMins());

        // Set values to views
        final TextView address = (TextView) dialog.findViewById(R.id.address);
        address.setText(theAddress);

        final TextView nameTv = (TextView) dialog.findViewById(R.id.name);
        nameTv.setText(name);

        final TextView costTv = (TextView) dialog.findViewById(R.id.cost);
        costTv.setText(costPrMin);

        final TextView maxTime = (TextView) dialog.findViewById(R.id.maxtime);
        maxTime.setText(maxVal);

        final TextView minTime = (TextView) dialog.findViewById(R.id.mintime);
        minTime.setText(minVal);

        final TextView reservdUntl = (TextView) dialog.findViewById(R.id.reservdUntl);
        if (Constants.SLECTED_SPOT.getReservedUntil() != null) {
            final String date =
                    Constants.SLECTED_SPOT.getReservedUntil().toString().replace("T", "  ").replace("Z", "");
            reservdUntl.setText(date);
        }

        final TextView isReservd = (TextView) dialog.findViewById(R.id.isReserved);
        if (Constants.SLECTED_SPOT.getIsReserved())
            isReservd.setText("Reserved");
        else
            isReservd.setText("Not reserved");

        final Button closeBtn = (Button) dialog.findViewById(R.id.closebtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    } // End showMoreWindow


    // Toast for messages
    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

    }

    // Send spot data to SpotInfoActivity and open it
    private void openInfoActivityWithExtras() {
        Intent i = new Intent(ctx, SpotInfoActivity.class);
        String location = Constants.SLECTED_SPOT.getLat() + "," + Constants.SLECTED_SPOT.getLng();
        i.putExtra(Constants.LOCATION, location);
        i.putExtra(Constants.ADDR_NAME, addrName);
        i.putExtra(Constants.THE_ADDRESS, theAddress);
        i.putExtra(Constants.DISTANCE, distance);
        i.putExtra(Constants.SEEKBAR_MINUTES, minutes);
        i.putExtra(Constants.DATE_TIME_STRING, selectedDateTime);
        i.putExtra(Constants.OPEN_SPOTS, getNumOfOpenSpots());

        startActivity(i);

    }
    //================================================================================
    // END HIDE AND SHOW VIEWS AND DIALOGS METHODS
    //================================================================================


    //================================================================================
    // LISTENERS AND SET PROPERTIES
    //================================================================================

    // Set listeners to the search field
    private void setSrchFieldListeners() {
        // When done button clicked
        srchField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    setParkingSearch();
                }
                return false;
            }
        });

        // Detect typing activities
        srchField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }// End srchFieldListeners


    // Set seek bar control
    private void setSeekBarListeners() {

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        minutes = i + 10;
                        seekVal.setText("" + minutes);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seekVal.setText("" + minutes);

                    }
                });
    }// End seekBar listeners


    // Setting up the date field
    private void setDateFieldListener() {

        // After date set listener
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDateField();
            }
        };

        // Show the date dialog when date views clicked
        dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ctx, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ctx, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    // Set the date EditText
    private void setDateField() {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        dateField.setText(sdf.format(calendar.getTime()));
        selectedDateTime = getDateTimeString();
    }


    // Time field listeners
    private void setTimeFieldListener() {
        // After time gets set listener
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                setTimeField();
            }
        };

        // Show the time dialog when time views clicked
        timeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(ctx, time,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        });

        timeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new TimePickerDialog(ctx, time,
                        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        });
    }

    // set the time EditText
    private void setTimeField() {
        String format = "hh:mma";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String timeStr = sdf.format(calendar.getTime()).replace("AM", "am").replace("PM", "pm");
        timeField.setText(timeStr);
        selectedDateTime = getDateTimeString();
    }


    // Return date & time as a string
    private String getDateTimeString() {
        String format = "yyyy-MM-dd hh:mma";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String timeStr = sdf.format(calendar.getTime()).replace("AM", "am").replace("PM", "pm");

        return timeStr;
    }


    // set animation listeners
    private void setAnimationListeners() {
        slide_up.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                green_srchLayout.startAnimation(slide_down_short);
                green_srchLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }// End animation listeners
    //================================================================================
    // END LISTENERS AND SET PROPERTIES
    //================================================================================


    //================================================================================
    // SETUPS AND CONVERSION METHODS
    //================================================================================
    private void setDialogPosition(int orientation){
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        // Set info widnow position based on screen orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            wmlp.gravity = Gravity.TOP | Gravity.CENTER;
            wmlp.x = 0;   //x position
            wmlp.y = 290;   //y position
        } else {
            wmlp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            wmlp.x = 0;   //x position
            wmlp.y = 0;     //y position
        }
    }


    //Perform parking search based on search term and AsyncTask to get its geo point
    private void setParkingSearch() {
        if (!srchField.getText().toString().equals("")
                && !dateField.getText().toString().equals("")
                && !timeField.getText().toString().equals("")) {
            srchTerm = srchField.getText().toString() + ", San Francisco, California";
            hideSearchWindow();
            //Execute a task to convert address to geo point and request the api
            new getGeoPointTask().execute();
        } else {
            showToast("Please complete all fields");
        }
    }


    // AsyncTask to get geo point from the search string, then starts Json api request
    private class getGeoPointTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSpinner();
        }

        @Override
        protected Void doInBackground(Void... params) {
            srchPoint = getLocationFromAddress(ctx, srchTerm);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //Get data from back end server
            if (srchPoint != null) {
                getSpotsAPIRequest(srchPoint);
            } else {
                hideSpinner();
                Toast.makeText(ctx, "Can't find location", Toast.LENGTH_LONG).show();
            }
        }
    }// End AsyncTask


    // Get the distance between the searched point and the clicked marker
    private String getDistance() {

        // Get searched location as a Location object
        final Location srchLocation = new Location("Search Location");
        srchLocation.setLatitude(srchPoint.latitude);
        srchLocation.setLongitude(srchPoint.longitude);

        // Get clicked spot's location as a Location object
        final Location spotLocation = new Location("Marker Location");
        double spotLat = Double.valueOf(Constants.SLECTED_SPOT.getLat());
        double spotLng = Double.valueOf(Constants.SLECTED_SPOT.getLng());
        spotLocation.setLatitude(spotLat);
        spotLocation.setLongitude(spotLng);

        final Float disInMeters = srchLocation.distanceTo(spotLocation);

        final double disInMiles = disInMeters * Constants.MILES_IN_METER;

        DecimalFormat df = new DecimalFormat("#.##");

        String distance = String.valueOf(df.format(disInMiles));

        return distance;
    }


    // Get address info based on lat and lng
    private void latLngToAddress(Double lat, Double lng) {

        final double latitude = lat;
        final double longitude = lng;

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    final Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
                    final List<android.location.Address> addresses;
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea().trim();
                    String postalCode = addresses.get(0).getPostalCode();
                    //String featuredName = addresses.get(0).getFeatureName();
                    addrName = addresses.get(0).getPremises();
                    if (addrName == null || addrName.equals("")) {
                        addrName = addresses.get(0).getThoroughfare();
                    }
                    theAddress = address + ", " + city + ", " + Constants.getStateAbbr(state) + " " + postalCode;

                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showToast("Error getting address info");
                        }
                    });
                }
            }
        };
        thread.start();

    }// End getAddress


    /// Get lat and lng point from an address
    private LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<android.location.Address> address;
        LatLng point = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            android.location.Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            point = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return point;

    } // End getLocationFromAddress


    // Return the number of spots share the same initial name
    private int getNumOfOpenSpots(){
        final String spotName = Constants.SLECTED_SPOT.getName();
        String spotInitial = "";
        int numOfSpots = 0;
        for(int i=0; i<3; i++){
            spotInitial += spotName.charAt(i);
        }
        // Go through each found spot and check if there are spots with similar name initial
        for(SpotData spotData : foundSpotsMap.values()){
            if(spotData.getName().contains(spotInitial)){
                numOfSpots++;
            }
        }
        return numOfSpots ;
    }


    // Add the desired minutes to the spot based on its id
    private void addMinutesToSpot() {
        // check if marker already reserved, so we don't add minutes twice
        if (!reservedMarkers.contains(selectedMarker)) {
            // first save the minutes
            saveSelectedMinutesVale();
            calendar.add(Calendar.MINUTE, minutes); // add seekbar value to the selected time
            Constants.SLECTED_SPOT.setReservedUntil(getDateTimeString());
            Constants.SLECTED_SPOT.setIsReserved(true);
        }
    }


    // Use SharedPreferences to save the number of minutes selected to be accessed in other classes
    private void saveSelectedMinutesVale() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.MINUTES_KEY, minutes);
        editor.apply();
    }


    // Create location point marker
    private void addSrchLocationMarker() {

        latLngToAddress(srchPoint.latitude, srchPoint.longitude);

        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(srchPoint)
                .zoom(15)
                .bearing(90)
                .tilt(30)
                .build();
        final Marker marker = gmap.addMarker(new MarkerOptions().position(srchPoint).title(addrName));
        marker.showInfoWindow();
        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }// End addSrchLocationMarker
    //================================================================================
    // END SETUPS AND CONVERSION METHODS
    //================================================================================


    //================================================================================
    // API REQUESTS
    //================================================================================

    // Use Retrofit2 to send a GET request to the back end API
    private void getSpotsAPIRequest(LatLng latLng) {

        final SearchInterface apiRequest = retrofit.create(SearchInterface.class);

        String latStr = String.valueOf(latLng.latitude);
        String lngStr = String.valueOf(latLng.longitude);
        Call<List<SpotData>> call = apiRequest.getSpots(latStr, lngStr); // Pass lat lng queries to the endpoint
        call.enqueue(new Callback<List<SpotData>>() {
            @Override
            public void onResponse(Call<List<SpotData>> call, Response<List<SpotData>> response) {
                if (response.isSuccessful()) {

                    hideSpinner();

                    final List<SpotData> spots = response.body(); // Get SpotData-type spots from the response
                    for (int i = 0; i < spots.size(); i++) {
                        LatLng spot = new LatLng(Double.valueOf(spots.get(i).getLat()), Double.valueOf(spots.get(i).getLng()));

                        Marker marker = gmap.addMarker(new MarkerOptions()
                                .position(spot).icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.marker)));
                        foundSpotsMap.put(marker.getId(), spots.get(i)); // Add spots to a hash map and let marker's id as a key
                    }
                    addSrchLocationMarker(); // To show the searched location area also
                }
            }

            @Override
            public void onFailure(Call<List<SpotData>> call, Throwable t) {

                hideSpinner();
                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
            }
        });

    }// End getSpotsAPIRequest


    // Reserve a spot api request using Retrofit2
    private void reserveSpotAPIRequest(int id) {

        final ReserveInterface apiRequest = retrofit.create(ReserveInterface.class);

        addMinutesToSpot(); // Add desired minutes to the spot

        Call<SpotData> call = apiRequest.reserveSpot(id, Constants.SLECTED_SPOT); // pass query to the endpoint
        call.enqueue(new Callback<SpotData>() {
            @Override
            public void onResponse(Call<SpotData> call, Response<SpotData> response) {

                if (response.isSuccessful()) {
                    showReserveWindow("Great!", "Your reservation has been confirmed.");
                    reservedMarkers.add(selectedMarker);
                    selectedMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_reserved));

                } else {
                    if (response.body() == null) {
                        showReserveWindow("Sorry!", "spot is reserved.");
                    }
                }
            }

            @Override
            public void onFailure(Call<SpotData> call, Throwable t) {

                showReserveWindow("Sorry!", t.getMessage());
            }
        });

    }// End reserveSpotAPIRequest()
    //================================================================================
    // END API REQUESTS
    //================================================================================


    //================================================================================
    // GESTURES LISTENERS
    //================================================================================
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return gDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {

        return false;

    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {


    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }


}// End ParkingSpotsActivity
