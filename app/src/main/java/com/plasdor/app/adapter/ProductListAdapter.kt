package com.plasdor.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.callbacks.AddOrRemoveListener
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.ThreeTwoImageView
import com.plasdor.app.utils.setImage


class ProductListAdapter(
    activityContext: Context,
    private val addOrRemoveListener: AddOrRemoveListener,
    private var cartListItems: ArrayList<ProductListItems>,
    private var allProduct: ArrayList<ProductListItems>,
    val userType: String

) :
    RecyclerView.Adapter<ProductListAdapter.MyViewHolder>() {

    private var listItems = ArrayList<ProductListItems>()
    private var actualListItems = ArrayList<ProductListItems>()
    var check = false
    var context: Context = activityContext

    lateinit var adapter: ArrayAdapter<String>

    lateinit var holderItem: MyViewHolder

    fun updateListItems(items: ArrayList<ProductListItems>) {
        if (SharePreferenceManager.getInstance(context)
                .getCartListItems(Constants.CartList) != null
        ) {
            cartListItems = SharePreferenceManager.getInstance(context)
                .getCartListItems(Constants.CartList) as ArrayList<ProductListItems>
        }
        listItems.clear()
        listItems.addAll(items)
        actualListItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_list_items_product, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var item: ProductListItems = listItems.get(position)

        holder.txtProductName.text = item.productName
//        holder.lblType.text = "Type: " + item.type

        holder.lblDiscountedPrice.text = "Offer Rs." + item.priceToSellDaily
        holder.lblPrice.text = "Rs." + item.priceToShowDaily
        holder.lblPrice.setPaintFlags(holder.lblPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)


        val imagepath = item.img
        if (imagepath == null || imagepath == " ") {
            holder.productImage!!.background =
                context.resources.getDrawable(R.drawable.img_not_available)
            holder.imageLoader!!.visibility = View.GONE
        } else {
            holder.productImage!!.setImage(imagepath)
            holder.imageLoader!!.visibility = View.GONE
        }

        if (cartListItems.contains(item)) {
            holder.btnWishList.setImageResource(R.drawable.ic_baseline_remove_circle_24)
        } else {
            holder.btnWishList.setImageResource(R.drawable.ic_outline_add_circle_24)
        }

        holder.btnWishList.setOnClickListener {

            if (SharePreferenceManager.getInstance(context)
                    .getCartListItems(Constants.CartList) == null
            ) {
                holder.btnWishList.setImageResource(R.drawable.ic_baseline_remove_circle_24)
                addOrRemoveListener.addToCart(item, position)
            } else {
                cartListItems = SharePreferenceManager.getInstance(context).getCartListItems(
                    Constants.CartList
                ) as ArrayList<ProductListItems>
                if (cartListItems.contains(item)) {
                    holder.btnWishList.setImageResource(R.drawable.ic_outline_add_circle_24)
                    addOrRemoveListener.removeFromCart(item, position)
                } else {
                    holder.btnWishList.setImageResource(R.drawable.ic_baseline_remove_circle_24)
                    addOrRemoveListener.addToCart(item, position)
                }
            }
        }


        if (userType.equals(Constants.ADMIN)) {
            holder.adminBtnLayout.visibility = View.VISIBLE
            holder.btnWishList.visibility = View.GONE

        } else {
            holder.btnWishList.visibility = View.VISIBLE
            holder.adminBtnLayout.visibility = View.GONE
        }

        holder.btnWishList.visibility=View.GONE


        holder.btnEdit.setOnClickListener {
            addOrRemoveListener.editProduct(item, position)
        }
        holder.btnDelete.setOnClickListener {
            addOrRemoveListener.deleteProduct(item, position)
        }

        holder.itemView.setOnClickListener {
            addOrRemoveListener.selectProduct(item, position)
//            context.openActivity(ProductDetailsFragment::class.java)
//            {
//                putParcelable("detail", item)
//            }
        }

    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageLoader: ProgressBar? = view.findViewById(R.id.progressBar)
        var productImage: ThreeTwoImageView? = view.findViewById(R.id.productImage)
        var txtProductName: TextView = view.findViewById(R.id.lblProductName)
        var lblType: TextView = view.findViewById(R.id.lblType)
        var lblPrice: TextView = view.findViewById(R.id.lblPrice)
        var lblDiscountedPrice: TextView = view.findViewById(R.id.lblDiscountedPrice)
        var btnWishList: AppCompatImageView = view.findViewById(R.id.btnWishList)
        var btnDelete: AppCompatImageView = view.findViewById(R.id.btnDelete)
        var btnEdit: AppCompatImageView = view.findViewById(R.id.btnEdit)
        var adminBtnLayout: LinearLayout = view.findViewById(R.id.adminBtnLayout)
        val cardView: View = itemView

    }

    fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    listItems = actualListItems
                } else {
                    val filteredList: MutableList<ProductListItems> = ArrayList()
                    for (row in allProduct) {
                        if (row.productName.toLowerCase()
//                                .contains(charString.toLowerCase()) || row.type.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }
                    listItems = filteredList as ArrayList<ProductListItems>
                }
                val filterResults = FilterResults()
                filterResults.values = listItems
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                listItems = filterResults.values as ArrayList<ProductListItems>
                notifyDataSetChanged()
            }
        }
    }
}