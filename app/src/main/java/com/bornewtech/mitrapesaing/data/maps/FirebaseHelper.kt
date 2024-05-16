package com.bornewtech.mitrapesaing.data.maps
import com.google.firebase.database.*

class FirebaseHelper {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getData(callback: (DataSnapshot) -> Unit) {
        databaseReference.child("data").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Kirim hasil data ke callback
                callback(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tangani kesalahan
            }
        })
    }

}
