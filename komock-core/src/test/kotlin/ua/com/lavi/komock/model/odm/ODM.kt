package ua.com.lavi.komock.model.odm

import java.math.BigDecimal
import java.util.Currency

/**
 * Created by Oleksandr Loushkin on 06.08.17.
 */
class OdmRequest(val uuid: String,
                 val merchantCode: String,
                 val amount: BigDecimal,
                 val currency: Currency)

class OdmRemoteApiResponse {
    var uuid: String? = null
    var status: RiskCheckStatus? = null
    val failedReason: String? = null
}

enum class RiskCheckStatus {
    APPROVED, REJECTED
}
