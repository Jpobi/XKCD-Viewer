package com.jpobi.xkcd_viewer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class Adapter() : ListAdapter<Comic, Adapter.ViewHolder>(DiffCallBack) {

    lateinit var onItemClickListener: (Comic) -> Unit

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val nombreElem: TextView = view.findViewById(R.id.name)
        val imageElem: ImageView = view.findViewById(R.id.listLogo)
        val releaseElem: TextView = view.findViewById(R.id.year)
        val idNumElem: TextView = view.findViewById(R.id.idNum)
        val heartElem: ImageView = view.findViewById(R.id.itemHeart)


        fun bind (comic: Comic) {
            nombreElem.text=comic.title
            Picasso.get().load(comic.img).into(imageElem)
            releaseElem.text=comic.day+"/"+comic.month+"/"+comic.year
            idNumElem.text=comic.num.toString()
            heartElem.visibility= if (comic.isFav) View.VISIBLE else View.INVISIBLE
            view.setOnClickListener{
                onItemClickListener(comic)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        val quake = getItem(position)
        holder.bind(quake)
    }
    fun findPositionById(id: Int): Int {
        return currentList.indexOfFirst { it.num == id }
    }

    fun updatePosition(position: Int, newComic: Comic) {
        if (position != -1 && position < currentList.size) {
            val updatedList = currentList.toMutableList()
            updatedList[position] = newComic
            submitList(updatedList)
            notifyItemChanged(position)
        }
    }

    fun updateComic(newComic: Comic){
        var pos=findPositionById(newComic.num)
        updatePosition(pos,newComic)
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Comic>() {
        override fun areItemsTheSame(oldItem: Comic, newItem: Comic): Boolean {
            return  oldItem.num == newItem.num
        }

        override fun areContentsTheSame(oldItem: Comic, newItem: Comic): Boolean {
            return oldItem == newItem
        }
    }
}