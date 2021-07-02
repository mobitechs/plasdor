package com.plasdor.app.view.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plasdor.app.R
import com.plasdor.app.adapter.CartListAdapter
import com.plasdor.app.callbacks.AddOrRemoveListener
import com.plasdor.app.callbacks.AlertDialogBtnClickedCallBack
import com.plasdor.app.callbacks.CartListUpdated
import com.plasdor.app.model.ProductListItems
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.utils.openActivity
import com.plasdor.app.utils.showAlertDialog
import com.plasdor.app.view.activity.AuthActivity
import com.plasdor.app.view.activity.UserHomeActivity


class CartFragment : Fragment(), AddOrRemoveListener, CartListUpdated,
    AlertDialogBtnClickedCallBack {
    lateinit var rootView: View
    lateinit var listAdapter: CartListAdapter
    var cartListItems = ArrayList<ProductListItems>()
    var cartListItems2 = ArrayList<ProductListItems>()
    lateinit var mLayoutManager: LinearLayoutManager

    lateinit var txtTotalItem: AppCompatTextView
    lateinit var txtTotalPrice: AppCompatTextView
    lateinit var txtDiscount: AppCompatTextView
    lateinit var txtDiscountedPrice: AppCompatTextView
    lateinit var txtDeliveryCharges: AppCompatTextView
    lateinit var txtFinalTotal: AppCompatTextView
    lateinit var txtShopMore: AppCompatTextView
    lateinit var txtYouSave: AppCompatTextView
    lateinit var txtTotalAmount: AppCompatButton
    lateinit var btnCheckout: AppCompatButton
    lateinit var layoutPriceDetails: LinearLayout
    lateinit var layoutBrnsCheckout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_cart, container, false)
        intView()
        return rootView
    }

    private fun intView() {

        txtTotalAmount = rootView.findViewById(R.id.txtTotalAmount)!!
        btnCheckout = rootView.findViewById(R.id.btnCheckout)!!
        txtTotalItem = rootView.findViewById(R.id.txtTotalItem)!!
        txtTotalPrice = rootView.findViewById(R.id.txtTotalPrice)!!
        txtDiscount = rootView.findViewById(R.id.txtDiscount)!!
        txtDiscountedPrice = rootView.findViewById(R.id.txtDiscountedPrice)!!
        txtDeliveryCharges = rootView.findViewById(R.id.txtDeliveryCharges)!!
        txtFinalTotal = rootView.findViewById(R.id.txtFinalTotal)!!
        txtShopMore = rootView.findViewById(R.id.txtShopMore)!!
        txtYouSave = rootView.findViewById(R.id.txtYouSave)!!
        layoutPriceDetails = rootView.findViewById(R.id.layoutPriceDetails)!!
        layoutBrnsCheckout = rootView.findViewById(R.id.layoutBrnsCheckout)!!


        btnCheckout.setOnClickListener {

            checkLogin()
        }
        setupRecyclerView()

    }

    fun checkLogin() {
        if (SharePreferenceManager.getInstance(requireContext())
                .getValueBoolean(Constants.ISLOGIN)
        ) {
            (activity as UserHomeActivity)!!.OpenAddressList()
        } else {

            requireContext().showAlertDialog(
                "Confirmation",
                "You have not logged in yet, Do you want to login?",
                "Yes",
                "NO",
                this
            )
//            val flatDialog = FlatDialog(requireActivity())
//            flatDialog.setTitle("Confirmation")
//                .setSubtitle("You have not logged in yet, Do you want to login?")
//                .setFirstButtonText("YES")
//                .setSecondButtonText("CANCEL")
//                .withFirstButtonListner {
//                    requireContext().openActivity(AuthActivity::class.java)
//                }
//                .withSecondButtonListner {
//                    flatDialog.dismiss()
//                }
//                .show()


        }

    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = rootView.findViewById(R.id.recyclerView)!!

        cartListItems = SharePreferenceManager.getInstance(requireContext()).getCartListItems(
            Constants.CartList
        ) as ArrayList<ProductListItems>
        if (SharePreferenceManager.getInstance(requireContext())
                .getCartListItems(Constants.CartList2) == null
        ) {
            cartListItems2.addAll(cartListItems)
        } else {
            cartListItems2 = SharePreferenceManager.getInstance(requireContext()).getCartListItems(
                Constants.CartList2
            ) as ArrayList<ProductListItems>
        }

        mLayoutManager = LinearLayoutManager(requireActivity())
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        listAdapter = CartListAdapter(requireActivity(), this, this)
        recyclerView.adapter = listAdapter
        listAdapter.updateListItems(cartListItems2)

    }

    override fun addToCart(item: ProductListItems, position: Int) {

    }

    override fun removeFromCart(item: ProductListItems, position: Int) {
//        if (cartListItems2.contains(item)) {
//            var pos = cartListItems.indexOf(item.productName)
//
//            cartListItems.removeAt(position)
////            cartListItems2.remove(item)
//
//            cartListItems2!!.removeAt(position)
//            listAdapter.notifyItemRemoved(position)
//            listAdapter.notifyItemRangeChanged(position, cartListItems2!!.size)
//            listAdapter.notifyDataSetChanged()
//        }
        (activity as UserHomeActivity)!!.updateCartCount(cartListItems2.size)
        SharePreferenceManager.getInstance(requireContext()).saveCartListItems(
            Constants.CartList,
            cartListItems
        )
        SharePreferenceManager.getInstance(requireContext()).saveCartListItems(
            Constants.CartList2,
            cartListItems2
        )

        cartListUpdated(cartListItems2)
        if (cartListItems.size == 0) {
            layoutPriceDetails.visibility = View.GONE
            layoutBrnsCheckout.visibility = View.GONE
        }
    }

    override fun editProduct(item: ProductListItems, position: Int) {

    }

    override fun deleteProduct(item: ProductListItems, position: Int) {

    }

    override fun selectProduct(item: ProductListItems, position: Int) {

    }

    override fun cartListUpdated(cListItems: ArrayList<ProductListItems>) {
        //calculate the amount
        cartListItems2 = cListItems
        SharePreferenceManager.getInstance(requireContext()).saveCartListItems(
            Constants.CartList2,
            cartListItems2
        )
        var totalAmount = 0
        var totalDiscountedAmount = 0
        var totalDiscount = 0
        var deliveryCharges = 0
        var totalPayable = 0

        var pName: String
        var pAmount: String
        var msg = ","
        var no = 0
        var pQty: Int
        var total = 0
        var fTotal = 0

        for (i in 0..cartListItems2.size - 1) {
            totalDiscountedAmount =
                totalDiscountedAmount + cartListItems2[i]._qty * cartListItems2[i].priceToSell.toInt()
            totalAmount = totalAmount + cartListItems2[i]._qty * cartListItems2[i].price.toInt()

            no++;
            pName = cartListItems2[i].productName
            pQty = cartListItems2[i]._qty
            var qtyWisePrice = cartListItems2[i]._qtyWisePrice
            pAmount = cartListItems2[i].priceToSell;
            total = pQty * pAmount.toInt()
            fTotal = fTotal + total;


            var mydata = ""
            mydata = "   Qty: $pQty *  $pAmount"

            msg =
                msg + " " + no.toString() + ". " + pName + ", " + mydata + " = " + total.toString() + " Rs. ,";
        }

//        var finalMsg = msg + ",Amount = " + fTotal.toString()+" Rs.";
        var finalMsg = msg

        totalDiscount = totalAmount - totalDiscountedAmount

        if (totalDiscountedAmount >= Constants.deliveryChargesBelow) {
            deliveryCharges = 0
            txtShopMore.visibility = View.GONE
        } else {
            var remainingAmount = Constants.deliveryChargesBelow - totalDiscountedAmount
            txtShopMore.visibility = View.VISIBLE
            txtShopMore.text =
                "* Shop more of Rs." + remainingAmount.toString() + " to get free delivery."
            deliveryCharges = Constants.deliveryCharges
        }

//        finalMsg = finalMsg + ",Delivery Charges = " + deliveryCharges+" Rs.";

        totalPayable = totalDiscountedAmount + deliveryCharges

        txtTotalItem.text = "Price (" + cartListItems2.size.toString() + "Item)"
        txtTotalPrice.text = totalAmount.toString() + " Rs."
        txtDiscount.text = "-" + totalDiscount.toString() + " Rs."
        txtDiscountedPrice.text = totalDiscountedAmount.toString() + " Rs."
        txtDeliveryCharges.text = "+" + deliveryCharges.toString() + " Rs."
        txtFinalTotal.text = totalPayable.toString() + " Rs."
        txtTotalAmount.text = "Total Amount Rs." + totalPayable.toString()
        txtYouSave.text = "You will save Rs." + totalDiscount.toString() + " on this order"

//        finalMsg = finalMsg + ",Total Amount = " + totalPayable.toString()+" Rs.";
        SharePreferenceManager.getInstance(requireContext()).save(Constants.finalOrderMsg, finalMsg)
        SharePreferenceManager.getInstance(requireContext())
            .save(Constants.totalDiscountedAmount, totalDiscountedAmount.toString())
        SharePreferenceManager.getInstance(requireContext())
            .save(Constants.delCharges, deliveryCharges.toString())
        SharePreferenceManager.getInstance(requireContext())
            .save(Constants.totalAmount, totalPayable.toString())
    }


    override fun positiveBtnClicked() {
        requireContext().openActivity(AuthActivity::class.java)
    }

    override fun negativeBtnClicked() {

    }

}