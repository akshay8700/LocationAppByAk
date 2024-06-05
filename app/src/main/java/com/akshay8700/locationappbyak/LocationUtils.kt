package com.akshay8700.locationappbyak

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationUtils(val context: Context) {

    // Getting FusedLocationProviderClient it helps to access any network like wifi, mobile operator, bluetooth
    private val _fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(viewModel: LocationViewModel) {
        /* Basically Accessing location data this function is able to access location data because its
        an special function of android "com.google.android.gms:play-services-location:21.0.1@aar"

         We want location updates so here created an inline anonymous class with the help
     of keyword "object" and this anonymous class is sub class of LocationCallback() class
     So that we can access his function onLocationResult and locationCallback variable is holding
     this anonymous object or we should say class */
        // Part 1
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let {
                    val location = LocationData(latitude = it.latitude, longtitude = it.longitude)
                    // After accessing last location we gived this to viewModel updateLocation and
                    // updateLocation is sending this data to private variable
                    // So that any screen on our phone can access this data through viewModel
                    viewModel.updateLocation(location)
                }
            }
        }

        // Now we are LocationRequest builder that will helps to set accuracy and time interval for
        // updates of network information this network information can be anything but
        // this time its location latitude and longtitude
        // Part 2
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()

//        This is the actual base provider that actually gives us the location information, And this is
//        the reason we are creating everything in this function "requestLocationUpdates", He needs
        // Part 1. For which location data and where to send
        // Part 2. For Priority setup and interval setup and more maybe
//        The FusedLocationProviderClient is part of Google Play services, so you need to make sure
//        that Google Play services is installed on the device before you can use it.
        _fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // Returning boolean if we got location permission if got location true or not than false
    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    // This function takes a location (with latitude and longitude) and returns the address as a string.
    // If it can't find the address, it returns "Address not found".
    fun reverseGeocodeLocation(location: LocationData) : String {

        // Geocoder Initialization:
        // A Geocoder object is created. The Geocoder is a class that helps convert geographic coordinates (latitude and longitude)
        // into a human-readable address. 'context' is typically an application or activity context, and 'Locale.getDefault()'
        // sets the language and region settings for the results.
        val geocoder = Geocoder(context, Locale.getDefault())

        // Coordinate Creation:
        // A LatLng object named 'coordinate' is created using the latitude and longitude from the 'location' parameter.
        val coordinate = LatLng(location.latitude, location.longtitude)

        // Fetching Addresses:
        // The getFromLocation method of the Geocoder object is called to get a list of addresses corresponding to the latitude and longitude.
        // The number '1' means it will return at most one address. This list is stored in 'addresses'.
        val addresses: MutableList<Address>? = geocoder.getFromLocation(coordinate.latitude, coordinate.longitude, 1)

        // Return Address or Default Message:
        // This part checks if the 'addresses' list is not null and not empty.
        return if (addresses?.isNotEmpty() == true) {
            // If true, it returns the first address line of the first address in the list.
            addresses[0].getAddressLine(0)
        } else {
            // If false, it returns "Address not found".
            "Address not found"
        }
    }
}