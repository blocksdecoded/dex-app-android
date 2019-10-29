package com.blocksdecoded.dex.presentation.send.model

import com.blocksdecoded.dex.data.adapter.FeeRatePriority
import java.math.BigDecimal

class SendUserInput {
    var amount: BigDecimal = BigDecimal.ZERO
    var address: String? = null
    var feePriority: FeeRatePriority = FeeRatePriority.MEDIUM
}
