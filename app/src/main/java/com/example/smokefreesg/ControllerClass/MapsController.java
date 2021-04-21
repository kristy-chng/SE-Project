package com.example.smokefreesg.ControllerClass;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smokefreesg.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class MapsController {

    public static void writeToDb(FirebaseFirestore db, HashMap<String, Object> smokingPoint, String id) {
        db.collection("SmokingPoints").document(id)
                .set(smokingPoint)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    private static final String TAG = "write_to_db";

                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    private static final String TAG = "write_to_db";
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    public static void loadOfficialDsa(Context context, final GoogleMap mMap, ArrayList<LatLng> locations, ArrayList<String> loc_info) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.dsa);
        String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();
        JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
        JsonArray jarray = object.getAsJsonArray("features");
        for (int i = 0; i < jarray.size(); i++) {
            JsonObject tmp_obj = jarray.get(i).getAsJsonObject();
            JsonObject geom = tmp_obj.getAsJsonObject("geometry");
            JsonArray xy = geom.getAsJsonArray("coordinates");
            double x = xy.get(1).getAsDouble();
            double y = xy.get(0).getAsDouble();
            LatLng to_add = new LatLng(x, y);
            locations.add(to_add);
            JsonObject prop = tmp_obj.getAsJsonObject("properties");
            String html = prop.getAsJsonPrimitive("Description").getAsString();
            Document doc = Jsoup.parse(html);
            String micro = doc.select("td").get(0).text();
            String macro = doc.select("td").get(1).text();
            loc_info.add(micro + ", " + macro);
        }
        for (int i = 0; i < locations.size(); i++) {
            Log.d("loadPins", loc_info.get(i));
            mMap.addMarker(new MarkerOptions()
                    .position(locations.get(i))
                    .title(loc_info.get(i))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            );
        }

    }

    public static void loadUserDsa(final GoogleMap mMap, FirebaseFirestore db, ArrayList<LatLng> locations, ArrayList<String> loc_info) {
                db.collection("SmokingPoints")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                double x = document.getDouble("latitude");
                                double y = document.getDouble("longitude");
                                String name = document.getString("name");
                                String verified = document.getString("verified");
                                LatLng tmp = new LatLng(x, y);
                                if (verified.equals("yes")) {
                                    mMap.addMarker(new MarkerOptions().position(tmp)
                                            .title(name)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                                }
                                else {
                                    mMap.addMarker(new MarkerOptions().position(tmp)
                                            .title(name));
                                }
                            }
                        } else {
                            Log.d("MapsActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void search(Context context, GoogleMap mMap, List<Address> addressList, String location) {
        Geocoder geocoder = new Geocoder(context);
        try {
            addressList = geocoder.getFromLocationName(location, 1);
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        } catch (Exception e) {
            Toast.makeText(context, "No location found!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void lodgeReport(FirebaseFirestore db, String title, String report) {
        DocumentReference doc_ref = db.collection("SmokingPoints").document(title);
        doc_ref.update("report", FieldValue.arrayUnion(report));
    }
}
