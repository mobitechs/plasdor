package com.plasdor.app.view.activity

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.plasdor.app.R
import com.plasdor.app.utils.addFragmentWithData
import com.plasdor.app.utils.replaceFragment
import com.plasdor.app.utils.setStatusColor
import com.plasdor.app.view.fragment.admin.*
import com.plasdor.app.view.fragment.deliveryAgent.DeliveryAgentOrderListFragment

class DeliveryAgentHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_delivery_agent)

        setStatusColor(window, resources.getColor(R.color.colorPrimaryDark))

        actionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimaryDark)))
        displayView(1)
    }

    fun displayView(pos: Int) {
        when (pos) {
            1 -> {
                replaceFragment(
                    DeliveryAgentOrderListFragment(),
                    false,
                    R.id.nav_host_fragment,
                    "DeliveryAgentOrderListFragment"
                )
            }
        }
    }

    fun OpenOrderDetails(bundle: Bundle) {
        addFragmentWithData(
            AdminOrderDetailsFragment(),
            false,
            R.id.nav_host_fragment,
            "AdminOrderDetailsFragment",
            bundle
        )
    }

}