package com.ridecell.app.ridecell.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.ridecell.app.ridecell.DataModels.SpotData;
import com.ridecell.app.ridecell.Interfaces.ReserveInterface;
import com.ridecell.app.ridecell.Interfaces.ReserveInterfacePATCH;
import com.ridecell.app.ridecell.R;
import com.ridecell.app.ridecell.Utilities.Constants;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SpotInfoActivity extends AppCompatActivity {

    //================================================================================
    // PROPERTIES
    //================================================================================
    private SeekBar seekBar;
    private int minutes;
    private double howMuch;
    private double costPrMin;
    private TextView seekValTxt;
    private TextView price;
    //private SpotData spotData;
    private TextView dateField;
    private ImageView dateIcon;
    private TextView timeField;
    private ImageView timeIcon;
    private Calendar calendar;
    private Context ctx;
    private Intent intent;
    private SharedPreferences sharedPrefs;
    private ProgressBar spinner;


    //================================================================================
    // ACTIVITY METHODS
    //================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_info);

        if (getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(Html.fromHtml("<b>PARKING SPOT INFO</b>"));
        }
        ctx = this;
        intent = getIntent();
        initSetViews();
        startSeparatorAnim();
    }



    //================================================================================
    // INITIALIZATIONS AND PROPERTIES SETUPS
    //================================================================================
    private void initSetViews(){

        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);

        spinner = (ProgressBar) findViewById(R.id.spinner);

        final ImageView streetImg = (ImageView) findViewById(R.id.streetImg);
        Picasso.with(this).load(getStreetViewUrl()).into(streetImg);

        final TextView locName = (TextView) findViewById(R.id.locationName);
        locName.setText(intent.getStringExtra(Constants.ADDR_NAME));

        final TextView address = (TextView) findViewById(R.id.address);
        address.setText(intent.getStringExtra(Constants.THE_ADDRESS));

        final TextView open = (TextView) findViewById(R.id.open);
        if(Constants.SPOTDATA.getIsReserved())
        open.setText("No");
        else
        open.setText(("Yes"));

        costPrMin = Double.valueOf(Constants.SPOTDATA.getCostPerMinute());

        final  TextView costTv = (TextView) findViewById(R.id.cost);
        costTv.setText(String.valueOf(costPrMin));

        final  TextView distance = (TextView) findViewById(R.id.distance);
        distance.setText(intent.getStringExtra(Constants.DISTANCE));

        // set the calendar object
        setCalendarFromExtra();

        dateIcon = (ImageView) findViewById(R.id.dateIcn);
        dateField = (TextView) findViewById(R.id.dateEdit);
        dateField.setText(getDateString());
        setDateFieldListener();

        timeIcon = (ImageView) findViewById(R.id.timeIcn);
        timeField = (TextView) findViewById(R.id.timeEdit);
        timeField.setText(getTimeString());
        setTimeFieldListener();

        // Get minutes extra to set the seek bar
        minutes = sharedPrefs.getInt(Constants.MINUTES_KEY, 10);

        seekValTxt = (TextView) findViewById(R.id.seekval);
        seekValTxt.setText(String.valueOf(minutes));

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setProgress(minutes - 10);
        setSeekBarListeners();

        price = (TextView) findViewById(R.id.price);
        double cost =  minutes * costPrMin;
        price.setText(String.valueOf(cost));

        final Button pay = (Button) findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showSpinner();
                resetSpotReservation();
                // Save the minutes value selected
                saveSelectedMinutesVale();
            }
        });

    }// End initViews
    //================================================================================
    // END INITIALIZATIONS AND PROPERTIES SETUPS
    //================================================================================



    //================================================================================
    // SET PROPERTIES AND LISTENERS
    //================================================================================
    private void startSeparatorAnim(){
        final ImageView sepGreen = (ImageView) findViewById(R.id.sepGreen);
        final ImageView sepRed = (ImageView) findViewById(R.id.sepRed);
        final Animation fadeIn = AnimationUtils.loadAnimation(ctx, R.anim.fade_in);
        final Animation fadeOut = AnimationUtils.loadAnimation(ctx, R.anim.fade_out);
        sepGreen.startAnimation(fadeIn);
        sepRed.startAnimation(fadeOut);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sepGreen.startAnimation(fadeOut);
                sepRed.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sepGreen.startAnimation(fadeIn);
                sepRed.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    // Setting up the date field
    private void setDateFieldListener() {
        // After date gets set listener
        final DatePickerDialog.OnDateSetListener date =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        dateField.setText(getDateString());
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



    // Time field listeners
    private void setTimeFieldListener() {
        // After time gets set listener
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                timeField.setText(getTimeString());
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



    // SeekBar listeners
    private void setSeekBarListeners(){
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        minutes = i + 10;
                        seekValTxt.setText("" + minutes);
                        howMuch = costPrMin * minutes;
                        price.setText("" + howMuch);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        seekValTxt.setText("" + minutes);
                        howMuch = costPrMin * minutes;
                        price.setText("" + howMuch);
                    }
                });
    }// End seekBar listeners
    //================================================================================
    // END SET PROPERTIES AND LISTENERS
    //================================================================================


    //================================================================================
    // SHOW AND HIDE VIEWS AND DIALOGS
    //================================================================================

    // when Reserve button clicked
    private void showReserveWindow(String title, String msg) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.window_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.0f);

        final TextView titleTv = (TextView) dialog.findViewById(R.id.title);
        titleTv.setText(title);

        final TextView msgTv = (TextView) dialog.findViewById(R.id.message);
        msgTv.setText(msg);

        final Button checkBtn = (Button) dialog.findViewById(R.id.checkbtn);
        checkBtn.setVisibility(View.GONE);

        final Button closeBtn = (Button) dialog.findViewById(R.id.closebtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (title.equals("Sorry!")) {
            final ImageView iv = (ImageView) dialog.findViewById(R.id.imageView);
            iv.setImageResource(R.drawable.exmark);
        }

        dialog.show();
    }

    // To show progress spinner
    private void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }

    private void showToast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

    }
    //================================================================================
    // END SHOW AND HIDE VIEWS AND DIALOGS
    //================================================================================



    //================================================================================
    // GET VALUES AND SETUP  METHODS
    //================================================================================
    // Get Google street view image url
    private String getStreetViewUrl(){
        final String location = intent.getStringExtra(Constants.LOCATION);
        // The Google url for google image view api
        final String imgUrl = "https://maps.googleapis.com/maps/api/streetview?size=600x400&location="
                + location + "&fov=120&pitch=0&key=" + Constants.GOOGLE_STREET_VIEW_API_KEY;

        return imgUrl;
    }


    private void setCalendarFromExtra(){
        calendar = Calendar.getInstance();
        String dateTimeString = intent.getStringExtra(Constants.DATE_TIME_STRING);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mma", Locale.US);
        try {
            calendar.setTime(sdf.parse(dateTimeString));
        }catch(Exception e) {
            showToast("Error parsing the date");
        }

    }

    // return date as a string
    private String getDateString(){
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String date = sdf.format(calendar.getTime());

        return date;
    }

    // Retrun time as a string
    private String getTimeString(){
        String format = "hh:mma";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String timeStr = sdf.format(calendar.getTime()).replace("AM", "am").replace("PM", "pm");

        return timeStr;
    }

    // Use SharedPreferences to save how many minutes selected so it can be used through the app
    private void saveSelectedMinutesVale(){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(Constants.MINUTES_KEY, minutes);
        editor.apply();
    }

    // Get time as a string
    private String getTimeDateString(){
        String format = "yyyy-MM-dd hh:mma";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        String timeDateStr = sdf.format(calendar.getTime()).replace("AM", "am").replace("PM", "pm");

        return timeDateStr;
    }
    //================================================================================
    // GET VALUES AND SETUP METHODS
    //================================================================================



    //================================================================================
    // API REQUESTS
    //================================================================================
    // Reset a reserved spot using @PATCH api request from Retrofit2
    private void resetSpotReservation() {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ReserveInterfacePATCH  apiRequest = retrofit.create(ReserveInterfacePATCH.class);

        Constants.SPOTDATA.setReservedUntil(null);
        Constants.SPOTDATA.setIsReserved(false);
        //spid = id;
        Call<SpotData> call = apiRequest.reserveSpot(Constants.SPOTDATA.getId(), Constants.SPOTDATA); // pass query to the endpoint
        call.enqueue(new Callback<SpotData>() {
            @Override
            public void onResponse(Call<SpotData> call, Response<SpotData> response) {

                if (response.isSuccessful()) {
                    reserveSpotAPIRequest(); // Start a new request after resetting the spot
                }
                else {
                    if (response.body() == null) {
                        showReserveWindow("Sorry!", "not changed.");
                    }
                }
            }

            @Override
            public void onFailure(Call<SpotData> call, Throwable t) {
                hideSpinner();
                showToast("fail");
                showReserveWindow("Sorry!", t.getMessage());
            }
        });
    }// End Reset reservation()


    // Reserve a spot Retrofit2 api request
    private void reserveSpotAPIRequest() {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ReserveInterface apiRequest = retrofit.create(ReserveInterface.class);

        calendar.add(Calendar.MINUTE, minutes); // add seekbar value to the selected time

        Constants.SPOTDATA.setReservedUntil(getTimeDateString());
        Constants.SPOTDATA.setIsReserved(true);

        Call<SpotData> call = apiRequest.reserveSpot(Constants.SPOTDATA.getId(), Constants.SPOTDATA); // pass query to the endpoint
        call.enqueue(new Callback<SpotData>() {
            @Override
            public void onResponse(Call<SpotData> call, Response<SpotData> response) {

                hideSpinner();
                if (response.isSuccessful()) {
                    showReserveWindow("Great!", "Your reservation has been confirmed.");
                } else {
                    if (response.body() == null) {
                        showReserveWindow("Sorry!", "spot is reserved.");
                    }
                }
            }

            @Override
            public void onFailure(Call<SpotData> call, Throwable t) {

                hideSpinner();
                showReserveWindow("Sorry!", t.getMessage());
            }
        });

    }// reserveRequest()
    //================================================================================
    // END API REQUESTS
    //================================================================================




}// End activity
