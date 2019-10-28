package com.blocksdecoded.dex.core.manager.zrx

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.manager.zrx.model.NormalizedOrderData
import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.dex.presentation.orders.model.EOrderSide
import com.blocksdecoded.dex.utils.normalizedDiv
import com.blocksdecoded.zrxkit.model.EAssetProxyId
import com.blocksdecoded.zrxkit.model.IOrder
import com.blocksdecoded.zrxkit.model.SignedOrder
import com.blocksdecoded.zrxkit.relayer.model.OrderRecord
import java.math.BigDecimal

object OrdersUtil {
    val coinManager = App.coinManager

    private fun getErcCoin(coinCode: String): CoinType.Erc20 =
        coinManager.getCoin(coinCode).type as CoinType.Erc20

    fun normalizeOrderData(
        order: IOrder,
        side: EOrderSide
    ): NormalizedOrderData {
        val makerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(order.makerAssetData))!!
        val takerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(order.takerAssetData))!!

        val makerAmount = order.makerAssetAmount.toBigDecimal()
            .movePointLeft((makerCoin.type as CoinType.Erc20).decimal)
            .stripTrailingZeros()

        val takerAmount = order.takerAssetAmount.toBigDecimal()
            .movePointLeft((takerCoin.type as CoinType.Erc20).decimal)
            .stripTrailingZeros()

        val price = takerAmount.normalizedDiv(makerAmount)

        return NormalizedOrderData(makerCoin, takerCoin, makerAmount, takerAmount, price)
    }

    fun normalizeOrderDataPrice(record: OrderRecord, isSellPrice: Boolean = true): NormalizedOrderData {
        val makerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(record.order.makerAssetData))!!
        val takerCoin = coinManager.getErcCoinForAddress(EAssetProxyId.ERC20.decode(record.order.takerAssetData))!!

        val makerAmount = record.order.makerAssetAmount.toBigDecimal()
            .movePointLeft((makerCoin.type as CoinType.Erc20).decimal)
            .stripTrailingZeros()

        val takerAmount = record.order.takerAssetAmount.toBigDecimal()
            .movePointLeft((takerCoin.type as CoinType.Erc20).decimal)
            .stripTrailingZeros()

        val price = if (isSellPrice) {
            makerAmount.normalizedDiv(takerAmount)
        } else {
            takerAmount.normalizedDiv(makerAmount)
        }

        return NormalizedOrderData(makerCoin, takerCoin, makerAmount, takerAmount, price)
    }

    fun calculateBasePrice(orders: List<SignedOrder>, coinPair: Pair<String, String>, side: EOrderSide): BigDecimal =
        calculateOrderPrice(coinPair, orders.first(), side)

    fun calculateOrderPrice(coinPair: Pair<String, String>, order: IOrder, side: EOrderSide): BigDecimal {
        val baseCoin = getErcCoin(coinPair.first)
        val quoteCoin = getErcCoin(coinPair.second)

        val makerAmount = order.makerAssetAmount.toBigDecimal()
            .movePointLeft(if (side == EOrderSide.BUY) quoteCoin.decimal else baseCoin.decimal)
            .stripTrailingZeros()

        val takerAmount = order.takerAssetAmount.toBigDecimal()
            .movePointLeft(if (side == EOrderSide.BUY) baseCoin.decimal else quoteCoin.decimal)
            .stripTrailingZeros()

        return makerAmount.normalizedDiv(takerAmount)
    }
}