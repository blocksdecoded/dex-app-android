package com.blocksdecoded.dex.presentation.transactions.recycler

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.presentation.transactions.TransactionViewItem
import com.blocksdecoded.dex.presentation.widgets.CoinIconView
import com.blocksdecoded.dex.utils.setTextColorRes
import com.blocksdecoded.dex.utils.TimeUtils
import com.blocksdecoded.dex.utils.ui.toDisplayFormat
import com.blocksdecoded.dex.utils.ui.toFiatDisplayFormat
import java.math.BigDecimal

class TransactionViewHolder(
    view: View,
    private val listener: OnClickListener
) : RecyclerView.ViewHolder(view) {

    private val iconImage: CoinIconView = itemView.findViewById(R.id.transaction_coin_icon)
    private val dateTxt: TextView = itemView.findViewById(R.id.transaction_date)
    private val amountTxt: TextView = itemView.findViewById(R.id.transaction_amount)
    private val actionTxt: TextView = itemView.findViewById(R.id.transaction_action)
    private val fiatAmountTxt: TextView = itemView.findViewById(R.id.transaction_fiat_amount)

    init {
        itemView.setOnClickListener { listener.onClick(adapterPosition) }
    }

    fun onBind(transaction: TransactionViewItem) {
        val isPositive = transaction.coinValue > BigDecimal.ZERO

        iconImage.bind(transaction.coin.code)

        actionTxt.setText(if (isPositive) R.string.transaction_receive else R.string.transaction_sent)

        amountTxt.text = "${if (isPositive) "+" else "-"}${transaction.coinValue.abs().toDisplayFormat()} ${transaction.coin.code}"
        amountTxt.setTextColorRes(if (isPositive) R.color.green else R.color.red)

        fiatAmountTxt.text = "$${transaction.fiatValue?.abs()?.toFiatDisplayFormat()}"

        transaction.date?.let {
            dateTxt.text = TimeUtils.dateToShort(it)
        }
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }
}