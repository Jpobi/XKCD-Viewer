package com.jpobi.xkcd_viewer

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import db.ComicDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var name: TextView
    private lateinit var idNum : TextView
    private lateinit var release : TextView
    private lateinit var description : TextView
    private lateinit var image : ImageView
    private lateinit var heartView : ImageView
    private lateinit var database: ComicDataBase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database=db.getDataBase(this)

        name=findViewById(R.id.detail_title)
        idNum=findViewById(R.id.detail_id)
        release=findViewById(R.id.detail_release)
        description=findViewById(R.id.detail_description)
        image=findViewById(R.id.detailImg)
        heartView=findViewById(R.id.heart)

        var comic: Comic? = getIntent().getParcelableExtra("comic") as? Comic
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (comic != null) {
                    saveToDB(comic = comic)
                }
            }
        }

        name.text=comic?.num.toString()+": "+comic?.title
        Picasso.get().load(comic?.img).into(image)

        /*idNum.text="Num: "+comic?.num.toString()*/
        idNum.text=""
        release.text=comic?.day+"/"+comic?.month+"/"+comic?.year
        description.text=comic?.alt
        if(comic?.isFav == true){
            heartView.setImageResource(R.drawable.full_heart)
        }else{
            heartView.setImageResource(R.drawable.empty_heart)
        }

        heartView.setOnClickListener{
            comic!!.isFav=!comic.isFav
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    database.comicDao.updateOrInsert(comic)
                    if(comic?.isFav == true){
                        heartView.setImageResource(R.drawable.full_heart)
                    }else{
                        heartView.setImageResource(R.drawable.empty_heart)
                    }
                }
                val sharedPreferences= applicationContext.getSharedPreferences(
                    "preferences", Context.MODE_PRIVATE
                )
                val editor= sharedPreferences.edit()
                val mySet = sharedPreferences.getStringSet("toBeUpdated", emptySet())?.toMutableSet() ?: mutableSetOf()
                mySet.add(comic?.num.toString())
                editor.putStringSet("toBeUpdated", mySet)
                editor.apply()
            }

        }

    }

    fun saveToDB(comic: Comic){
        var currentids=database.comicDao.getIds()
        if(!currentids.contains(comic.num)){
            database.comicDao.insertOrIgnore(comic)
        }
    }
}