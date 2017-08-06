package ua.com.lavi.komock.model.odm;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by Oleksandr Loushkin on 06.08.17.
 */
public class OdmRequest {
    private String uuid;
    private String merchantCode;
    private BigDecimal amount;
    private Currency currency;

    public OdmRequest(String uuid, String merchantCode, BigDecimal amount, Currency currency) {
        this.uuid = uuid;
        this.merchantCode = merchantCode;
        this.amount = amount;
        this.currency = currency;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}