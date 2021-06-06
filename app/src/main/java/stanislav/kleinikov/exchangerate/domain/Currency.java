package stanislav.kleinikov.exchangerate.domain;

import androidx.annotation.NonNull;

import com.squareup.moshi.Json;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("unused")
public class Currency {
    @Json(name = "Cur_ID")
    private int mId;
    @Json(name = "Date")
    private Date mDate;
    @Json(name = "Cur_Abbreviation")
    private String mCharCode;
    @Json(name = "Cur_Scale")
    private int mScale;
    @Json(name = "Cur_Name")
    private String mName;
    @Json(name = "Cur_OfficialRate")
    private BigDecimal mRate;

    public Currency() {
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
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
                Objects.equals(mDate, currency.mDate) &&
                Objects.equals(mCharCode, currency.mCharCode) &&
                Objects.equals(mName, currency.mName) &&
                Objects.equals(mRate, currency.mRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mDate, mCharCode, mScale, mName, mRate);
    }

    @NonNull
    @Override
    public String toString() {
        return "Currency{" +
                "mId=" + mId +
                ", mNumCode='" + mDate + '\'' +
                ", mCharCode='" + mCharCode + '\'' +
                ", mScale=" + mScale +
                ", mName='" + mName + '\'' +
                ", mRate=" + mRate +
                '}';
    }
}
