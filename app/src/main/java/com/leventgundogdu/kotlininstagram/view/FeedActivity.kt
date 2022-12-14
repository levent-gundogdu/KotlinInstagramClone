package com.leventgundogdu.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.leventgundogdu.kotlininstagram.R
import com.leventgundogdu.kotlininstagram.adapter.FeedRecyclerAdapter
import com.leventgundogdu.kotlininstagram.databinding.ActivityFeedBinding
import com.leventgundogdu.kotlininstagram.model.Post

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var feedAdapter: FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = Firebase.firestore

        postArrayList = ArrayList<Post>()

        getDate()

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter


    }

    private fun getDate() {

        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error -> //orderBy ile tarihe gore siralama yaptik.
            //whereEqualTo("userEmail", "james@gmail.com") ile filtreleme yapilabilir.

            if (error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()

            } else {

                if (value != null) {
                    if (!value.isEmpty) { //degerler bos degilse.

                        val documents = value.documents

                        postArrayList.clear() //Postlari koymadan ArrayList'i temizlemek

                        for (document in documents) { //ArrayList icine verileri koymak.
                            //casting
                            val comment = document.get("comment") as String
                            val useremail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            //println(comment)

                            val post = Post(useremail, comment, downloadUrl)
                            postArrayList.add(post)

                        }

                        feedAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_post) { //Gonderi yapmak
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
            //Finish() yok cunku kullanici gonderi yapmaktan vazgecebilir.
        } else if (item.itemId == R.id.signout) {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() //Kullanici cikis yaptiginda oraya geri donemez.
        }

        return super.onOptionsItemSelected(item)
    }

}