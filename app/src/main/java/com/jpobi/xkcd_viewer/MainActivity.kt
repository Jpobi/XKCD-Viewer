package com.jpobi.xkcd_viewer

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
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
    private lateinit var searchButton: ImageView
    private lateinit var adapter: Adapter
    private var comicsList = mutableListOf<Comic>()
    private var isFinishedLoading=true
    var currentMaxId=50
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchButton=findViewById(R.id.searchIcon)
        searchButton.setOnClickListener {
            val dialog=SearchDialog(
                OnSubmitClickListener ={comicId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val call = getRetrofit().create(ApiService::class.java).getComic(comicId)
                        val response = call.body()
                        if (call.isSuccessful){
                            val intent= Intent(getApplicationContext() ,DetailActivity::class.java)
                            intent.putExtra("comic",response)
                            startActivity(intent)
                        } else{
                            Toast.makeText(getApplicationContext(), "No se ha encontrado un comic con ese Id", Toast.LENGTH_SHORT).show()
                        }

                    }

                }
            ).show(supportFragmentManager,"Buscar comic")
        }

        recyclerView=findViewById(R.id.recView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Adapter()

        adapter.submitList(comicsList)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    onRecyclerViewEndReached()
                }
            }
        })

        adapter.onItemClickListener={
            val intent= Intent(this,DetailActivity::class.java)
            intent.putExtra("comic",it)
            startActivity(intent)
        }
        getComics(currentMaxId)
        currentMaxId+=51
    }


    private fun getComics(maxId: Int) {
        isFinishedLoading=false
        CoroutineScope(Dispatchers.IO).launch {
            var index=maxId-50
            while (index<=maxId){
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
            isFinishedLoading=true

            /*runOnUiThread {
                if (comicsList.size>0) {
                    adapter.notifyDataSetChanged()
                }
            }*/
        }
    }

    private fun onRecyclerViewEndReached() {
        if(isFinishedLoading){
            Toast.makeText(this, "Cargando +50 comics...", Toast.LENGTH_SHORT).show()
            getComics(currentMaxId)
            currentMaxId+=51
        }else{
            if(currentMaxId>103) {Toast.makeText(this, "Espere la carga e intente denuevo", Toast.LENGTH_SHORT).show()}
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
