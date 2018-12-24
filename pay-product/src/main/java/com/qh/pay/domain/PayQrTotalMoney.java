package com.qh.pay.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class PayQrTotalMoney implements Serializable {
    private String merchNo;
    private String outChannel;
    private String accountNo;
    private BigDecimal totalMoney;

    public String getMerchNo() {
        return merchNo;
    }

    public void setMerchNo(String merchNo) {
        this.merchNo = merchNo;
    }

    public String getOutChannel() {
        return outChannel;
    }

    public void setOutChannel(String outChannel) {
        this.outChannel = outChannel;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }
}
