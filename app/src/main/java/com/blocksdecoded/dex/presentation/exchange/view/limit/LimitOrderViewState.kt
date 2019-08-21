package com.blocksdecoded.dex.presentation.exchange.view.limit

import com.blocksdecoded.dex.presentation.exchange.view.IExchangeViewState
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import java.math.BigDecimal

data class LimitOrderViewState(
    override var sendAmount: BigDecimal,
    override var sendPair: ExchangePairItem?,
    override var receivePair: ExchangePairItem?
) : IExchangeViewState

data class ExchangePriceInfo(
    var sendPrice: BigDecimal
)