package stanislav.kleinikov.exchangerate.domain;

import java.math.BigDecimal;

public class CurrencyRate {

    private String mCharCode;

    private BigDecimal firstRate;

    private BigDecimal secondRate;

    public CurrencyRate() {

    }

    public String getCharCode() {
        return mCharCode;
    }

    public void setCharCode(String charCode) {
        mCharCode = charCode;
    }

    public BigDecimal getFirstRate() {
        return firstRate;
    }

    public void setFirstRate(BigDecimal firstRate) {
        this.firstRate = firstRate;
    }

    public BigDecimal getSecondRate() {
        return secondRate;
    }

    public void setSecondRate(BigDecimal secondRate) {
        this.secondRate = secondRate;
    }
}
