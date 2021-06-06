package stanislav.kleinikov.exchangerate.util;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("unused")
public class DateAdapter {

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

    @FromJson
    public synchronized Date fromJson(String string) throws ParseException {
        return dateFormatter.parse(string);
    }

    @ToJson
    public synchronized String toJson(Date date) {
        return dateFormatter.format(date);
    }
}
