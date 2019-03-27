package stanislav.kleinikov.exchangerate.domain;

import android.support.annotation.NonNull;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.math.BigDecimal;
import java.util.Objects;

@Root
public class Currency {
    @Attribute(name = "Id")
    private int mId;
    @Element(name = "NumCode")
    private String mNumCode;
    @Element(name = "CharCode")
    private String mCharCode;
    @Element(name = "Scale")
    private int mScale;
    @Element(name = "Name")
    private String mName;
    @Element(name = "Rate")
    private BigDecimal mRate;

    public Currency() {

    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getNumCode() {
        return mNumCode;
    }

    public void setNumCode(String numCode) {
        mNumCode = numCode;
    }

    public String getCharCode() {
        return mCharCode;
    }

    public void setCharCode(String charCode) {
        mCharCode = charCode;
    }

    public int getScale() {
        return mScale;
    }

    public void setScale(int scale) {
        mScale = scale;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public BigDecimal getRate() {
        return mRate;
    }

    public void setRate(BigDecimal rate) {
        mRate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return mId == currency.mId &&
                mScale == currency.mScale &&
                Objects.equals(mNumCode, currency.mNumCode) &&
                Objects.equals(mCharCode, currency.mCharCode) &&
                Objects.equals(mName, currency.mName) &&
                Objects.equals(mRate, currency.mRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mNumCode, mCharCode, mScale, mName, mRate);
    }

    @NonNull
    @Override
    public String toString() {
        return "Currency{" +
                "mId=" + mId +
                ", mNumCode='" + mNumCode + '\'' +
                ", mCharCode='" + mCharCode + '\'' +
                ", mScale=" + mScale +
                ", mName='" + mName + '\'' +
                ", mRate=" + mRate +
                '}';
    }
}
