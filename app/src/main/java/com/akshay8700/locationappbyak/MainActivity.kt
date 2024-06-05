package com.akshay8700.locationappbyak

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akshay8700.locationappbyak.ui.theme.LocationAppByAkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LocationAppByAkTheme {
                MyApp(viewModel)
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils, viewModel,context = context)
}

@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context
) {

    // This variable is holding longtitude and latitude data so that we can access it later in this code
    val location = viewModel.location.value

    // here locationUtils.reverseGeocodeLocation is taking lat and alt

    val address = location?.let {
        locationUtils.reverseGeocodeLocation(location)
    }

    // We will use this var to request permission
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        // Requesting multiple permissions
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        // callback function is giving us all the result of permissions if user ranted or not lets see the result is in Map(String, Boolean)
        onResult = { permissions ->
            // Check if both location permissions are granted
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                // Afrer permission acces now we are sending location data to viewModel with the help of locationUtils
                // Basically updating location data
                locationUtils.requestLocationUpdates(viewModel)
            }
            // If uses deny location permission
            else {
                // Check if a rationale is required for either permission
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if(rationaleRequired){
                    Toast.makeText(context,
                        "Location Permission is required for this feature to work",
                        Toast.LENGTH_LONG).show()
                // If user completely deny location and Android will refuse to show permission
                // request on display, We will request user to enable permission from settings
                }else{
                    Toast.makeText(context,
                        "Location Permission is required Please enable it in the Android Settings",
                        Toast.LENGTH_LONG).show()
                }
            }
        })


    // Screen
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        // If location data that viewModel is giving us is not empty than we will show that data into screen text
        if(location != null){
            Text("Address: ${location.latitude} ${location.longtitude} n/${address}")
        }else{
            Text(text = "Location not available")
        }


        Button(onClick = { 
            if(locationUtils.hasLocationPermission(context)){
                // Permission already granted update the location
                locationUtils.requestLocationUpdates(viewModel)
                Toast.makeText(context,
                    "You have already access of location",
                    Toast.LENGTH_LONG).show()
            }else{
                // Request location permission
                requestPermissionLauncher.launch(
                    // Sending all permissions in the rememberLauncherForActivityResult that will show on display to user
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "Get Location")
        }
    }
    // Screen
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    LocationAppByAkTheme() {
        
    }
}