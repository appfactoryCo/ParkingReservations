# ParkingReservations
Reserve parking spots in the area of San Francisco


## Importing Instructions

Download the zip file, unzip it, and use Android Studio to import the project folder. I compiled it using Android 2.2 preview 3.

If you get an error like: Unsupported major.minor version 52.0,

Go to the porject's build.gradle and update the dependancy:

```javascript
dependencies {
        classpath 'com.android.tools.build:gradle:2.1.2' 
    }
```

The app uses two Google API kies- one for maps one for street view imagery. I left my kies in the project
and the app should work fine as dowloaded and imported to Android studio without any setups. 

In case you needed to add your own Google API kies, add them to these folders:

**1-** For Google maps

Add the key to the debug file 

res -> values -> [google_maps_api.xml (debug)](https://github.com/appfactoryCo/ParkingReservations/blob/master/app/src/debug/res/values/google_maps_api.xml) 
 
 
 **2-** For Google Street View Imagery API key, just add yours to this class:
 
 app -> java -> Utilities -> [Constants.java](https://github.com/appfactoryCo/ParkingReservations/blob/master/app/src/main/java/com/ridecell/app/ridecell/Utilities/Constants.java)
 
 
## About The App
 
 The app fetches available parking spots in San Francisco area based on a search term entered by the user. The user can enter
 an address or a street name, then the app will get the geo location of that address.
 
 After it gets the latitude and longitude, the app uses Retrofit2 to send a GET API request and receives a JSON response
 of a list contains available parking spots near that location. 
 
 Then, the app drwas markers on the map for each available spot and let the user reserve a spot in a certain date and time 
 that are picked by the user. The app sends a POST API request to reserve a parking spot by using the spot id as a parameter.
 
 The user can change the reservation to a different date or time, and the app will send a PATCH API request to cancel the first
 reservation by setting the objects **is_reserved** to false and **reserved_until** to null. Then it sends a POST request 
 again to reserve the spot with the new date and time. 
 
 The app uses Picasso to download and cache the image of the street view. The image is obtained by embedding Google Street
 View API into the app. The app constructs a Google Street View url by taking the spot's latitude longitude and passes
 them along with browsing API key to the url. Then we can use Picasso with the url to load the image.
 Here is the Google Street View Imagery api website [link](https://developers.google.com/maps/documentation/streetview/)
 
