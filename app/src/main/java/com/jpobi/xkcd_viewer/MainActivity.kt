package com.jpobi.xkcd_viewer

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Adapter
    private var comicsList = mutableListOf<Comic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView=findViewById(R.id.recView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Adapter()
        comicsList.addAll(
            listOf(
                Comic(
                    month = "7",
                    num = 614,
                    link = "",
                    year = "2009",
                    news = "",
                    safe_title = "Woodpecker",
                    transcript = "[[A man with a beret and a woman are standing on a boardwalk, leaning on a handrail.]]\nMan: A woodpecker!\n\u003C\u003CPop pop pop\u003E\u003E\nWoman: Yup.\n\n[[The woodpecker is banging its head against a tree.]]\nWoman: He hatched about this time last year.\n\u003C\u003CPop pop pop pop\u003E\u003E\n\n[[The woman walks away.  The man is still standing at the handrail.]]\n\nMan: ... woodpecker?\nMan: It's your birthday!\n\nMan: Did you know?\n\nMan: Did... did nobody tell you?\n\n[[The man stands, looking.]]\n\n[[The man walks away.]]\n\n[[There is a tree.]]\n\n[[The man approaches the tree with a present in a box, tied up with ribbon.]]\n\n[[The man sets the present down at the base of the tree and looks up.]]\n\n[[The man walks away.]]\n\n[[The present is sitting at the bottom of the tree.]]\n\n[[The woodpecker looks down at the present.]]\n\n[[The woodpecker sits on the present.]]\n\n[[The woodpecker pulls on the ribbon tying the present closed.]]\n\n((full width panel))\n[[The woodpecker is flying, with an electric drill dangling from its feet, held by the cord.]]\n\n{{Title text: If you don't have an extension cord I can get that too.  Because we're friends!  Right?}}",
                    alt = "If you don't have an extension cord I can get that too.  Because we're friends!  Right?",
                    img = "https://imgs.xkcd.com/comics/woodpecker.png",
                    title = "Woodpecker",
                    day = "24"
                ),
                Comic(
                    month = "7",
                    num = 615,
                    link = "",
                    year = "2009",
                    news = "",
                    safe_title = "Woodpecker",
                    transcript = "[[A man with a beret and a woman are standing on a boardwalk, leaning on a handrail.]]\nMan: A woodpecker!\n\u003C\u003CPop pop pop\u003E\u003E\nWoman: Yup.\n\n[[The woodpecker is banging its head against a tree.]]\nWoman: He hatched about this time last year.\n\u003C\u003CPop pop pop pop\u003E\u003E\n\n[[The woman walks away.  The man is still standing at the handrail.]]\n\nMan: ... woodpecker?\nMan: It's your birthday!\n\nMan: Did you know?\n\nMan: Did... did nobody tell you?\n\n[[The man stands, looking.]]\n\n[[The man walks away.]]\n\n[[There is a tree.]]\n\n[[The man approaches the tree with a present in a box, tied up with ribbon.]]\n\n[[The man sets the present down at the base of the tree and looks up.]]\n\n[[The man walks away.]]\n\n[[The present is sitting at the bottom of the tree.]]\n\n[[The woodpecker looks down at the present.]]\n\n[[The woodpecker sits on the present.]]\n\n[[The woodpecker pulls on the ribbon tying the present closed.]]\n\n((full width panel))\n[[The woodpecker is flying, with an electric drill dangling from its feet, held by the cord.]]\n\n{{Title text: If you don't have an extension cord I can get that too.  Because we're friends!  Right?}}",
                    alt = "If you don't have an extension cord I can get that too.  Because we're friends!  Right?",
                    img = "https://imgs.xkcd.com/comics/woodpecker.png",
                    title = "Woodpecker",
                    day = "24"
                ),
                Comic(
                    month = "7",
                    num = 616,
                    link = "",
                    year = "2009",
                    news = "",
                    safe_title = "Woodpecker",
                    transcript = "[[A man with a beret and a woman are standing on a boardwalk, leaning on a handrail.]]\nMan: A woodpecker!\n\u003C\u003CPop pop pop\u003E\u003E\nWoman: Yup.\n\n[[The woodpecker is banging its head against a tree.]]\nWoman: He hatched about this time last year.\n\u003C\u003CPop pop pop pop\u003E\u003E\n\n[[The woman walks away.  The man is still standing at the handrail.]]\n\nMan: ... woodpecker?\nMan: It's your birthday!\n\nMan: Did you know?\n\nMan: Did... did nobody tell you?\n\n[[The man stands, looking.]]\n\n[[The man walks away.]]\n\n[[There is a tree.]]\n\n[[The man approaches the tree with a present in a box, tied up with ribbon.]]\n\n[[The man sets the present down at the base of the tree and looks up.]]\n\n[[The man walks away.]]\n\n[[The present is sitting at the bottom of the tree.]]\n\n[[The woodpecker looks down at the present.]]\n\n[[The woodpecker sits on the present.]]\n\n[[The woodpecker pulls on the ribbon tying the present closed.]]\n\n((full width panel))\n[[The woodpecker is flying, with an electric drill dangling from its feet, held by the cord.]]\n\n{{Title text: If you don't have an extension cord I can get that too.  Because we're friends!  Right?}}",
                    alt = "If you don't have an extension cord I can get that too.  Because we're friends!  Right?",
                    img = "https://imgs.xkcd.com/comics/woodpecker.png",
                    title = "Woodpecker",
                    day = "24"
                )
            )
        )
        adapter.submitList(comicsList)
        recyclerView.adapter = adapter


        adapter.onItemClickListener={
            val intent= Intent(this,DetailActivity::class.java)
            intent.putExtra("comic",it)
            startActivity(intent)
        }
        getComics()
    }


    private fun getComics() {
        CoroutineScope(Dispatchers.IO).launch {
            comicsList.clear()
            var index=0
            while (index<=50){
                val call = getRetrofit().create(ApiService::class.java).getComic(index.toString())
                val response = call.body()
                if (response != null) {
                    comicsList.add(response)
                    withContext(Dispatchers.Main) {
                        adapter.notifyItemInserted(comicsList.size - 1)
                    }
                }
                index+=1
            }

            /*runOnUiThread {
                if (comicsList.size>0) {
                    adapter.notifyDataSetChanged()
                }
            }*/
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MAIN_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        const val MAIN_URL = "https://xkcd.com/"
    }
}
