package com.blocksdecoded.dex.presentation.orders

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.orders.model.UiOrder
import com.blocksdecoded.dex.presentation.orders.recycler.OrderViewHolder
import com.blocksdecoded.dex.presentation.orders.recycler.OrdersAdapter
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment: CoreFragment(R.layout.fragment_orders), OrderViewHolder.Listener {

    private lateinit var adapter: OrdersAdapter
    private lateinit var viewModel: OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = OrdersAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orders_recycler?.layoutManager = LinearLayoutManager(context)
        orders_recycler?.adapter = adapter

        adapter.setOrders(listOf(UiOrder(1.toBigDecimal())))
    }

    override fun onClick(position: Int) {

    }
}