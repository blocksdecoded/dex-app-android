package com.blocksdecoded.dex.presentation.convert

import androidx.lifecycle.MutableLiveData
import com.blocksdecoded.dex.App
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.core.adapter.FeeRatePriority
import com.blocksdecoded.dex.core.adapter.IAdapter
import com.blocksdecoded.dex.core.adapter.SendStateError
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.ui.CoreViewModel
import com.blocksdecoded.dex.core.ui.SingleLiveEvent
import com.blocksdecoded.dex.presentation.convert.model.ConvertConfig
import com.blocksdecoded.dex.presentation.convert.model.ConvertConfig.ConvertType.*
import com.blocksdecoded.dex.presentation.convert.model.ConvertInfo
import com.blocksdecoded.dex.presentation.convert.model.ConvertState
import com.blocksdecoded.dex.presentation.model.FeeInfo
import com.blocksdecoded.dex.presentation.widgets.balance.TotalBalanceInfo
import com.blocksdecoded.dex.utils.Logger
import com.blocksdecoded.dex.utils.uiSubscribe
import java.math.BigDecimal
import java.net.SocketTimeoutException
import kotlin.math.absoluteValue

class ConvertViewModel : CoreViewModel() {

    private lateinit var config: ConvertConfig
    private val coinManager = App.coinManager
    private val wethWrapper = App.zrxKitManager.zrxKit().getWethWrapperInstance()
    private val ratesConverter = App.ratesConverter
    private var adapter: IAdapter? = null
    
    private lateinit var fromCoin: Coin
    private lateinit var toCoin: Coin
    
	private var sendAmount = BigDecimal.ZERO
	
    var decimalSize: Int = 18
	
    val convertState = MutableLiveData<ConvertState>()
    val convertAmount = MutableLiveData<BigDecimal>()
    val receiveAmount = MutableLiveData<BigDecimal>()
    val convertEnabled = MutableLiveData<Boolean>()
    val info = MutableLiveData<ConvertInfo>()
    val feeInfo = MutableLiveData<FeeInfo>()

    val dismissDialog = SingleLiveEvent<Unit>()
    val transactionSentEvent = SingleLiveEvent<String>()
    val processingEvent = SingleLiveEvent<Unit>()
    val dismissProcessingEvent = SingleLiveEvent<Unit>()
    
    fun init(config: ConvertConfig) {
        this.config = config
    
        adapter = App.adapterManager.adapters
            .firstOrNull { it.coin.code == config.coinCode }
    
        if (adapter == null) {
            errorEvent.postValue(R.string.error_invalid_coin)
            dismissDialog.call()
            return
        }
        
        fromCoin = coinManager.getCoin(config.coinCode)
        toCoin = coinManager.getCoin(
            if (config.type == WRAP)
                "WETH"
            else
                "ETH"
        )
        
        val balanceInfo = TotalBalanceInfo(
            adapter!!.coin,
            adapter!!.balance,
            ratesConverter.getCoinsPrice(adapter!!.coin.code, adapter!!.balance)
        )
        
        convertState.value = ConvertState(
            fromCoin,
            toCoin,
            balanceInfo,
            config.type
        )

        onAmountChanged(BigDecimal.ZERO, true)
        transactionSentEvent.reset()
        dismissDialog.reset()
        decimalSize = adapter?.decimal ?: 18

        val transactionPrice = when(config.type) {
            WRAP -> wethWrapper.depositEstimatedPrice
            UNWRAP -> wethWrapper.withdrawEstimatedPrice
        }

        val transactionPriceFiat = ratesConverter.getCoinsPrice(adapter!!.coin.code, transactionPrice)

        feeInfo.value = FeeInfo(
            adapter!!.coin,
            transactionPrice,
            transactionPriceFiat,
            0
        )
    }

    private fun refreshInfo(sendAmount: BigDecimal) {
        adapter?.let { adapter ->
            val info = ConvertInfo(
                ratesConverter.getCoinsPrice(adapter.coin.code, sendAmount),
                0
            )

            adapter.validate(sendAmount, null, FeeRatePriority.MEDIUM)
                .forEach {
                    when(it) {
                        is SendStateError.InsufficientAmount -> {
                            info.error = R.string.error_invalid_amount
                        }
                        is SendStateError.InsufficientFeeBalance -> {
                            info.error = R.string.error_insufficient_fee_balance
                        }
                    }
                }

            this.info.value = info
        }
    }

    fun onMaxClicked() {
        val availableBalance = adapter?.availableBalance(null, FeeRatePriority.HIGHEST) ?: BigDecimal.ZERO
        
        onAmountChanged(availableBalance, true)
    }
	
	fun onConvertClick() {
        val availableBalance = adapter?.availableBalance(null, FeeRatePriority.HIGHEST) ?: BigDecimal.ZERO
        
        if (sendAmount <= availableBalance) {
            processingEvent.call()
            val sendRaw = sendAmount.movePointRight(18).stripTrailingZeros().toBigInteger()
            onAmountChanged(BigDecimal.ZERO, true)
            
            when(config.type) {
                WRAP -> wethWrapper.deposit(sendRaw)
                UNWRAP -> wethWrapper.withdraw(sendRaw)
            }.uiSubscribe(disposables, {
                dismissProcessingEvent.call()
                transactionSentEvent.postValue(it.transactionHash)
                dismissDialog.call()
            }, {
                Logger.e(it)

                errorEvent.postValue(
                    when {
                        it is SocketTimeoutException -> R.string.error_timeout_error
                        config.type == UNWRAP -> R.string.error_unwrap_failed
                        else -> R.string.error_wrap_failed
                    }
                )

                dismissProcessingEvent.call()
            })
        } else {
            errorEvent.postValue(R.string.error_invalid_amount)
        }
	}
    
    fun onAmountChanged(amount: BigDecimal?, updateLiveData: Boolean = false) {
        if (sendAmount != amount) {
            sendAmount = amount ?: BigDecimal.ZERO

            amount?.let { refreshInfo(sendAmount) }

            val noErrors = (info.value?.error?.absoluteValue ?: 0) == 0
            val nonZeroAmount = sendAmount > BigDecimal.ZERO

            convertEnabled.value = noErrors && nonZeroAmount

            if (updateLiveData) {
                this.convertAmount.value = sendAmount
            }
        }
    }
}