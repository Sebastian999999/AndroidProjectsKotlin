package com.example.rcvprac1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class MainActivity : AppCompatActivity() {
    private lateinit var rcv : RecyclerView
    private lateinit var newsAdapter:NewsAdapter
    private var news = ArrayList<News>()
    private var newNews = ArrayList<News>()
    private var images = mutableListOf<Int>(
        R.drawable.a,
        R.drawable.b,
        R.drawable.c,
        R.drawable.d,
        R.drawable.e,
        R.drawable.f,
        R.drawable.g,
        R.drawable.h,
        R.drawable.i,
        R.drawable.j,
    )
    private var headings = mutableListOf<String>(
        "Biden aims to expand vaccines for adults and children",
        "Just got my first shot, helping the world to be a safer place",
        "Local trains to be suspended in Bengal from tomorrow in view of covid-19",
        "MHA asks states,UTs to ensure there are no fires in hospitals",
        "Australian citizen sues PM Morrison over flight ban from india",
        "Former India hockey coach Kaushik hospitalised after testing positive for COVID",
        "Delhi records 20,960 fresh covid-19 infections, positivity rate at 26.37%",
        "Barcelona church offers open-air space for Ramadan",
        "Trillions of cicadas set to emerge in the US, here's why",
        "Homemaker, economist: Candidates from all walks of life in Bengal assembly"
    )

    private var newsText = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        rcv = findViewById(R.id.rcv1)
        rcv.layoutManager= LinearLayoutManager(this)
        newsText.add(getString(R.string.news_a))
        newsText.add(getString(R.string.news_b))
        newsText.add(getString(R.string.news_c))
        newsText.add(getString(R.string.news_d))
        newsText.add(getString(R.string.news_e))
        newsText.add(getString(R.string.news_f))
        newsText.add(getString(R.string.news_g))
        newsText.add(getString(R.string.news_h))
        newsText.add(getString(R.string.news_i))
        newsText.add(getString(R.string.news_j))

        updateAdapter()
    }
    private fun updateAdapter(){
        for (i in images.indices){
            news.add(News(images[i],headings[i]))
        }
        newsAdapter = NewsAdapter(news)
        rcv.adapter = newsAdapter
        newsAdapter.setOnItemClickListener(object: NewsAdapter.OnItemClickListener{
            override fun onItemClick(position: Int){
                Toast.makeText(this@MainActivity,"Item $position clicked",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity,NewsActivity::class.java)
                intent.putExtra("Heading",headings[position])
                intent.putExtra("Image",images[position])
                intent.putExtra("Body",newsText[position])
                startActivity(intent)
            }
        })
        newNews = news
        var itemTouchHelper = object : SwipeGesture() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.LEFT){
                    newsAdapter.archiveItem(viewHolder.adapterPosition)
                }else{
                    newsAdapter.deleteItem(viewHolder.adapterPosition)
                    news = newsAdapter.getAllNews()
                }
            }

            override fun onMove(recyclerView:RecyclerView , viewHolder:RecyclerView.ViewHolder,target:RecyclerView.ViewHolder): Boolean{
                var frompos = viewHolder.adapterPosition
                var topos = target.adapterPosition
                Collections.swap(news,frompos,topos)
                newsAdapter.notifyItemMoved(frompos,topos)
                return true
            }
        }

       ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rcv)
    }

    override fun onCreateOptionsMenu(optionsMenu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, optionsMenu)
        var item = optionsMenu!!.findItem(R.id.svrecyclerview)
        var search = item.actionView as SearchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = ArrayList<News>()

                if (!newText.isNullOrEmpty()) {
                    for (item in news) {
                        if (item.newsText.lowercase().contains(newText.lowercase())) {
                            filteredList.add(item)
                        }
                    }
                } else {
                    filteredList.addAll(news)
                }

                newsAdapter = NewsAdapter(filteredList)
                rcv.adapter = newsAdapter
                newsAdapter.notifyDataSetChanged()

                return false
            }


        })

        return super.onCreateOptionsMenu(optionsMenu)
    }
}