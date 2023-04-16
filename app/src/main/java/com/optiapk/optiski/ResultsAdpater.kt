package com.optiapk.optiski

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.optiapk.optiski.models.Piste
import com.optiapk.optiski.models.PisteFinal

class ResultsAdpater(var trackList: List<PisteFinal>) :
    RecyclerView.Adapter<ResultsAdpater.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultsAdpater.ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_track_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: ResultsAdpater.ViewHolder, position: Int) {
        val track = trackList[position]
        holder.textView.text = track.number

        val bitmap = Bitmap.createBitmap(100, 20, Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        when(track.difficulty) {
            1 -> canvas.drawColor(Color.GREEN)
            2-> canvas.drawColor(Color.BLUE)
            3-> canvas.drawColor(Color.BLACK)
        }
        holder.imageView.setImageBitmap(bitmap)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var textView: TextView

        init {
            imageView = itemView.findViewById(R.id.imagetrack)
            textView = itemView.findViewById(R.id.numbertrack)
        }
    }
}