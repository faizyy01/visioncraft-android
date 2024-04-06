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

class ImagesAdapter(private val imageList: List<ImageItem>, private val context: Context) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    // ViewHolder class for holding the view references
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(imageItem: ImageItem, context: Context) {
            val imageView: ImageView = itemView.findViewById(R.id.imageViewCard)
            val textView: TextView = itemView.findViewById(R.id.textViewDescription)

            // Using Glide to load the image from URL
            Glide.with(context)
                .load(imageItem.url)
                .into(imageView)

            textView.text = imageItem.prompt
        }
    }

    // Inflates the item layout and creates a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_card, parent, false)
        return ImageViewHolder(view)
    }

    // Binds data to the view
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position], context)
    }

    // Returns the size of the data list
    override fun getItemCount() = imageList.size
}
