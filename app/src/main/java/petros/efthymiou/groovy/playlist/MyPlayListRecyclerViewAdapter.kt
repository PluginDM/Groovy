package petros.efthymiou.groovy.playlist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import petros.efthymiou.groovy.placeholder.PlaceholderContent.PlaceholderItem
import petros.efthymiou.groovy.databinding.PlaylistItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyPlayListRecyclerViewAdapter(
    private val values: Result<List<PlayList>>
) : RecyclerView.Adapter<MyPlayListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            PlaylistItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


                                      //WE CAN DO THIS BECAUSE WE KNOW THE TYPE!!!!
            val list: List<PlayList> = values as List<PlayList>


            val item = list[position]
            holder.playlistName.text = item.name
            holder.playListCategory.text = item.category
            holder.playListImage.setImageResource(item.image)


    }

    override fun getItemCount(): Int = (values as List<PlayList>).size

    inner class ViewHolder(binding: PlaylistItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        var playlistName: TextView = binding.playlistName
        val playListCategory: TextView = binding.playlistCategory
        val playListImage: ImageView = binding.playlistImage


    }

}