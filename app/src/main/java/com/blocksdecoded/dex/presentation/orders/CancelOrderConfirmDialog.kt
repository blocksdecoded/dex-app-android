package com.blocksdecoded.dex.presentation.orders

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.dialogs.BaseDialog
import java.math.BigDecimal
import kotlinx.android.synthetic.main.dialog_cancel_confirm.*

class CancelOrderConfirmDialog : BaseDialog(R.layout.dialog_cancel_confirm) {

    lateinit var cancelOrderInfo: CancelOrderInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancel_confirm_btn?.setOnClickListener {
            cancelOrderInfo.onConfirm()
            dismiss()
        }

        cancel_fee?.setCoin(cancelOrderInfo.feeCoinCode ?: "", cancelOrderInfo.estimatedFee, isExactAmount = false)
        cancel_duration?.setMillis(cancelOrderInfo.processingDuration)
    }

    companion object {
        fun show(fragmentManager: FragmentManager, cancelInfo: CancelOrderInfo) {
            val dialog = CancelOrderConfirmDialog()

            dialog.cancelOrderInfo = cancelInfo

            dialog.show(fragmentManager, "cancel_order_confirm")
        }
    }
}

data class CancelOrderInfo(
    val estimatedFee: BigDecimal,
    val feeCoinCode: String?,
    val processingDuration: Long,
    val onConfirm: () -> Unit
)
