package com.optiapk.optiski

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.optiapk.optiski.models.User

class ProfileActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var viewPager2: ViewPager2
    var viewPagerItemArrayList: ArrayList<ViewPagerItem>? = null
    private lateinit var niveauxExplicationsArray: Array<String>
    private lateinit var niveauxArray : Array<String>
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(SavedInstance: Bundle?) {
        super.onCreate(SavedInstance)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val exitButton = findViewById<ImageButton>(R.id.exitButton)
        val logoutButton = findViewById<ImageButton>(R.id.logoutButton)
        val saveButton = findViewById<Button>(R.id.saveProfileButton)
        val editName = findViewById<TextInputEditText>(R.id.text_name)



        viewPager2 = findViewById(R.id.profileViewPager)
        val images = intArrayOf(
            R.drawable.debutant,
            R.drawable.intermediaire,
            R.drawable.avance)

        niveauxExplicationsArray = resources.getStringArray(R.array.niveaux_explications)
        niveauxArray = resources.getStringArray(R.array.niveaux)

        viewPagerItemArrayList = ArrayList()

        for (i in images.indices) {
            val viewPagerItem = ViewPagerItem(images[i], niveauxArray.get(i), niveauxExplicationsArray.get(i))
            viewPagerItemArrayList!!.add(viewPagerItem)
        }
        val vpAdapter = VPAdapter(viewPagerItemArrayList!!)

        viewPager2.setAdapter(vpAdapter)

        viewPager2.setClipToPadding(false)

        viewPager2.setClipChildren(false)

        viewPager2.setOffscreenPageLimit(2)

        viewPager2.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER

        val tabLayout = findViewById<TabLayout>(R.id.tabLayoutProfile)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            //tab.text = niveauxArray[position].substringBefore(' ')
        }.attach()

        val user = auth.currentUser
        val userRef = firestore.collection("users")

        user?.let {
            userRef.document(user.uid).get()
                .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                    // Handle the successful retrieval of the document data
                    val name = documentSnapshot.getString("userName")
                    val level = documentSnapshot.getString("userLevel")
                    editName.setText(name)
                    if (level == "Débutant") {
                        viewPager2.currentItem = 0
                    } else if (level == "Intermédiaire") {
                        viewPager2.currentItem = 1
                    } else if (level == "Avancé") {
                        viewPager2.currentItem = 2

                    }
                }
                .addOnFailureListener { e: Exception ->
                    // Handle the failure to retrieve the document data
                }
        }


        exitButton.setOnClickListener {
            val intent = Intent(this, ChoicesActivity::class.java)
            startActivity(intent)
        }


        logoutButton.setOnClickListener{
            auth.signOut()
            googleSignInClient.signOut().addOnCompleteListener(this,
                OnCompleteListener<Void?> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)})
        }

        saveButton.setOnClickListener {
            val name = editName.text.toString()
            var level = niveauxArray[viewPager2.currentItem]

//                    updateUI(user)
            if (auth.currentUser?.displayName != editName.text.toString()) {

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(editName.text.toString())
                    .build()
                auth.currentUser?.updateProfile(profileUpdates)
            }

            val levelValue = niveauxArray[viewPager2.currentItem]
            user?.let {
                userRef.document(user.uid).update("userLevel", levelValue)
                userRef.document(user.uid).update("userName", editName.text.toString())
            }

            val intent = Intent(this, ChoicesActivity::class.java)
            startActivity(intent)
        }

    }
}