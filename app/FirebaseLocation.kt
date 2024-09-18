package com.example.knuverse

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_myinformation.*
import kotlinx.android.synthetic.main.dialog_user.*

// 데이터를 받아오는 fragment이므로 xml 필요없음

val db = Firebase.firestore

Firestore.collection("locations").document("documentId").get()
.addOnSuccessListener { document ->
    if (document != null) {
        val name = document.getString("name")
        val geoPoint = document.getGeoPoint("geopoint")
        val latitude = geoPoint?.latitude
        val longitude = geoPoint?.longitude

        Log.d("FirestoreData", "Name: $name, Latitude: $latitude, Longitude: $longitude")
    }
}

//앞선 프래그먼트/액티비티의 이름에 따라 geopoint와 name이 변하도록 해야됨
val fragment = KakaoMapFragment()
val bundle = Bundle()
bundle.putString("name", locationName)
bundle.putDouble("latitude", geoPoint.latitude)
bundle.putDouble("longitude", geoPoint.longitude)
fragment.arguments = bundle

parentFragmentManager.beginTransaction()
.replace(R.id.fragment_container, fragment)
.commit()