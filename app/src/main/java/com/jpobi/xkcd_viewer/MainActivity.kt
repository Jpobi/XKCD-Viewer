package com.jpobi.xkcd_viewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.ComicDataBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchButton: ImageView
    private lateinit var mainButton: ImageView
    private lateinit var favsButton: ImageView
    private lateinit var adapter: Adapter
    private lateinit var database: ComicDataBase
    private var comicsList = mutableListOf<Comic>()
    private var isFinishedLoading=true
    private var isFavsView=false
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
        database=db.getDataBase(this)
        searchButton=findViewById(R.id.searchIcon)
        mainButton=findViewById(R.id.mainLogo)
        favsButton=findViewById(R.id.favButton)
        searchButton.setOnClickListener {
            val dialog=SearchDialog(
                OnSubmitClickListener ={comicId ->
                    CoroutineScope(Dispatchers.IO).launch {
                        var currentIds=database.comicDao.getIds()
                        if(!currentIds.contains(comicId.toInt())){
                            val call = getRetrofit().create(ApiService::class.java).getComic(comicId)
                            val response = call.body()
                            if (call.isSuccessful){
                                val intent= Intent(getApplicationContext() ,DetailActivity::class.java)
                                intent.putExtra("comic",response)
                                startActivity(intent)
                            } else{
                                Toast.makeText(getApplicationContext(), "No se ha encontrado un comic con ese Id", Toast.LENGTH_SHORT).show()
                            }
                        } else{
                            var comic=database.comicDao.getComicById(comicId)
                            val intent= Intent(getApplicationContext() ,DetailActivity::class.java)
                            intent.putExtra("comic",comic)
                            startActivity(intent)
                        }
                    }

                }
            ).show(supportFragmentManager,"Buscar comic")
        }

        mainButton.setOnClickListener {
            isFavsView=false
            setFavsColor()
            this.recreate();
        }

        setFavsColor()

        favsButton.setOnClickListener {
            isFavsView=true
            setFavsColor()
            CoroutineScope(Dispatchers.IO).launch {
                val list =database.comicDao.getFavComics()
                runOnUiThread{
                    comicsList.clear()
                    comicsList.addAll(list)
                    adapter.notifyDataSetChanged()
                }
            }
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
        comicsList.clear()
        getComics(currentMaxId)
        currentMaxId+=51
    }


    private fun getComics(maxId: Int) {
        isFinishedLoading=false
        //TODO: Optimizable para q los traiga directo en bulk de la base de datos en vez de uno por uno y solo busque en el api los q faltan
        CoroutineScope(Dispatchers.IO).launch {
            var currentIds=database.comicDao.getIds()
            var index=maxId-50
            while (index<=maxId){
                //si no esta en la base de datos
                if(!currentIds.contains(index)){
                    val call = getRetrofit().create(ApiService::class.java).getComic(index.toString())
                    val response = call.body()
                    if (call.isSuccessful && response != null) {
                        comicsList.add(response)
                        //cargarlo
                        database.comicDao.insertOrIgnore(response)
                        withContext(Dispatchers.Main) {
                            adapter.notifyItemInserted(comicsList.size - 1)
                            adapter.notifyDataSetChanged()
                        }
                    } else{
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                getApplicationContext(),
                                "Ha habido un error. Asegúrese de tener conexion a Internet!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else{
                    //si esta buscarlo ahí
                    var comic=database.comicDao.getComicById(index.toString())
                    comicsList.add(comic)
                    withContext(Dispatchers.Main) {
                        adapter.notifyItemInserted(comicsList.size - 1)
                        adapter.notifyDataSetChanged()
                    }
                }
                index+=1
            }
            isFinishedLoading=true
        }
    }

    private fun onRecyclerViewEndReached() {
        if(isFavsView){
            currentMaxId=50
            return
        }
        if(isFinishedLoading){
            Toast.makeText(this, "Cargando +50 comics...", Toast.LENGTH_SHORT).show()
            getComics(currentMaxId)
            currentMaxId+=51
        }else{
            if(currentMaxId>103) {Toast.makeText(this, "Espere la carga e intente denuevo", Toast.LENGTH_SHORT).show()}
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = applicationContext.getSharedPreferences(
            "preferences", Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()

        val mySet = sharedPreferences.getStringSet("toBeUpdated", emptySet())?.toMutableSet() ?: mutableSetOf()
        val idsToUpdate = mySet.toSet()

        CoroutineScope(Dispatchers.IO).launch {
            idsToUpdate.forEach { id ->
                val newComic = database.comicDao.getComicById(id)
                if (newComic != null) {
                    withContext(Dispatchers.Main) {
                        adapter.updateComic(newComic)
                    }
                }
                mySet.remove(id)
            }

            withContext(Dispatchers.Main) {
                editor.putStringSet("toBeUpdated", mySet)
                editor.apply()
            }
        }
    }

    private fun setFavsColor(){
        if(isFavsView){
            favsButton.setImageResource(R.drawable.full_heart)
        } else{
            favsButton.setImageResource(R.drawable.empty_heart)
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
