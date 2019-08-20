package com.blocksdecoded.dex.core.manager

import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.CoinType

object CoinManager {
    val coins: List<Coin> = listOf(
        Coin("Ethereum", "ETH", CoinType.Ethereum),
        Coin("Wrapped ETH", "WETH", CoinType.Erc20("0xc778417e063141139fce010982780140aa0cd5ab", 18)),
        Coin("0x", "ZRX", CoinType.Erc20("0xff67881f8d12f372d91baae9752eb3631ff0ed00", 18)),
        Coin("Dai", "DAI", CoinType.Erc20("0xd914796ec26edd3f9651393f9751e0f3c00dd027", 18)), // Its CHO
        Coin("ChainLink", "LINK", CoinType.Erc20("0x30845a385581ce1dc51d651ff74689d7f4415146", 18)), // Its TMKV2
        Coin("Tether USD", "USDT", CoinType.Erc20("0x6D00364318D008C3AEA08c097c25F5639AB5D2e6", 3)), // Its PPA
        Coin("Huobi", "HT", CoinType.Erc20("0x52E64BB7aEE0E5bdd3a1995E3b070e012277c0fd", 2)) // Its TMK
    )
    
    fun getCoin(code: String): Coin =
        coins.firstOrNull { it.code == code } ?: throw Exception("Coin $code not found")

    fun getErcCoinForAddress(address: String): Coin? = coins.firstOrNull {
        when(it.type) {
            is CoinType.Erc20 -> it.type.address.equals(address, true)
            else -> false
        }
    }

    //TODO: Refactor
    fun addressForSymbol(symbol: String): String = ((coins.firstOrNull {
        when(it.type) {
            is CoinType.Erc20 -> it.code.equals(symbol, true)
            else -> false
        }
    } ?: coins[1]).type as? CoinType.Erc20)?.address ?: ""

    val ercTokensNames : List<String> = coins
            .subList(1, coins.size)
            .map { it.code }

    val ercCoins: List<Coin> = coins
            .subList(1, coins.size)
}