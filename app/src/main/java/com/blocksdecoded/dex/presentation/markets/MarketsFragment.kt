package com.blocksdecoded.dex.presentation.markets

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.ui.CoreFragment
import com.blocksdecoded.dex.presentation.markets.recycler.MarketViewHolder
import com.blocksdecoded.dex.presentation.markets.recycler.MarketsAdapter
import kotlinx.android.synthetic.main.fragment_markets.*

class MarketsFragment : CoreFragment(R.layout.fragment_markets), MarketViewHolder.Listener {

    private lateinit var adapter: MarketsAdapter
    private lateinit var viewModel: MarketsViewModel
    
    //region Lifecycle
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MarketsAdapter(this)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MarketsViewModel::class.java)
        
        viewModel.markets.observe(this, Observer { adapter.setMarkets(it) })
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        markets_recycler?.layoutManager = LinearLayoutManager(context)
        markets_recycler?.adapter = adapter
    }
    
    //endregion
    
    //region ViewHolder
    
    override fun onClick(position: Int) {
    
    }
    
    //endregion
    
    companion object {
        fun newInstance() = MarketsFragment()
    }

}
