package com.optiapk.optiski


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class VPAdapter(var viewPagerItemArrayList: ArrayList<ViewPagerItem>) :
    RecyclerView.Adapter<VPAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_inscription, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewPagerItem = viewPagerItemArrayList[position]
        holder.imageView.setImageResource(viewPagerItem.imageNiveau)
        holder.niveau.text = viewPagerItem.niveau
        holder.niveauExp.text = viewPagerItem.niveauDescription
    }

    override fun getItemCount(): Int {
        return viewPagerItemArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var niveau: TextView
        var niveauExp: TextView

        init {
            imageView = itemView.findViewById<ImageView>(R.id.imageNiveau)
            niveau = itemView.findViewById<TextView>(R.id.niveau)
            niveauExp = itemView.findViewById<TextView>(R.id.niveauxExplication)
        }
    }
}