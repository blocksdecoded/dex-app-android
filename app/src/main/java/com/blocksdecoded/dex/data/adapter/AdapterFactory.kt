package com.blocksdecoded.dex.data.adapter

import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.core.model.AuthData
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType
import com.blocksdecoded.dex.data.manager.IEthereumKitManager
import com.blocksdecoded.dex.data.manager.fee.IFeeRateProvider
import java.math.BigDecimal

class AdapterFactory(
    private val ethereumKitManager: IEthereumKitManager,
    private val feeRateProvider: IFeeRateProvider
) {

    fun adapterForCoin(coin: Coin, authData: AuthData): IAdapter = when (coin.type) {
        is CoinType.Ethereum -> {
            EthereumAdapter(coin, ethereumKitManager.ethereumKit(authData), feeRateProvider)
        }
        is CoinType.Erc20 -> {
            Erc20Adapter(coin, App.instance, ethereumKitManager.ethereumKit(authData), feeRateProvider, coin.type.decimal, BigDecimal(0.0), coin.type.address)
        }
    }

    fun unlinkAdapter(adapter: IAdapter) {
        when (adapter) {
            is EthereumBaseAdapter -> {
                ethereumKitManager.unlink()
            }
        }
    }
}
