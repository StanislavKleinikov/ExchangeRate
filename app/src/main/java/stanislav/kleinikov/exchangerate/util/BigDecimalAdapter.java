package stanislav.kleinikov.exchangerate.util;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class BigDecimalAdapter {
    @FromJson
    public BigDecimal fromJson(String string) {
        return new BigDecimal(string);
    }

    @ToJson
    public String toJson(BigDecimal value) {
        return value.toString();
    }
}
