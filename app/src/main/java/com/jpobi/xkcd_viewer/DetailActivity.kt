package com.jpobi.xkcd_viewer

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {
    private lateinit var name: TextView
    private lateinit var idNum : TextView
    private lateinit var release : TextView
    private lateinit var description : TextView
    private lateinit var image : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        name=findViewById(R.id.detail_title)
        idNum=findViewById(R.id.detail_id)
        release=findViewById(R.id.detail_release)
        description=findViewById(R.id.detail_description)
        image=findViewById(R.id.detailImg)

        val comic: Comic? = getIntent().getParcelableExtra("comic") as? Comic

        name.text=comic?.num.toString()+": "+comic?.title
        Picasso.get().load(comic?.img).into(image)
        /*idNum.text="Num: "+comic?.num.toString()*/
        idNum.text=""
        release.text=comic?.day+"/"+comic?.month+"/"+comic?.year
        description.text=comic?.alt
    }
}