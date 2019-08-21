package com.blocksdecoded.dex.presentation.exchange.view.market

import com.blocksdecoded.dex.presentation.exchange.view.IExchangeViewState
import com.blocksdecoded.dex.presentation.exchange.view.ExchangePairItem
import java.math.BigDecimal

data class MarketOrderViewState(
	override var sendAmount: BigDecimal,
	var receiveAmount: BigDecimal,
	override var sendPair: ExchangePairItem?,
	override var receivePair: ExchangePairItem?
) : IExchangeViewState