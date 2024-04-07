import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ncorti.kotlin.template.app.ImageItem
import com.ncorti.kotlin.template.app.R

class ImagesAdapter(private var imageList: MutableList<ImageItem>, private val context: Context, val listener: OnItemClickListener?) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    // ViewHolder class for holding the view references
    interface OnItemClickListener {
        fun onItemClick(imageItem: ImageItem)
    }

//    var listener: OnItemClickListener = object : OnItemClickListener {
//        override fun onItemClick(imageItem: ImageItem) {
//            // No operation or default implementation
//        }
//    }

    class ImageViewHolder(itemView: View, val listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(itemView.tag as ImageItem)
                }
            }
        }

        fun bind(imageItem: ImageItem, context: Context) {
            itemView.tag = imageItem
            val imageView: ImageView = itemView.findViewById(R.id.imageViewCard)
            Glide.with(context).load(imageItem.url).into(imageView)

            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(imageItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_card, parent, false)
        return ImageViewHolder(view, listener) // Ensure the listener is passed here
    }
    // Binds data to the view
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position], context) // Remove the listener from here
    }


    // Returns the size of the data list
    override fun getItemCount() = imageList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newImageList: List<ImageItem>) {
        imageList.clear()
        imageList.addAll(newImageList)
        notifyDataSetChanged()
    }
}
