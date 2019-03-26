package stanislav.kleinikov.exchangerate.domain;

public class Rate {
    private String mName;
    private String mNumber;

    public Rate() {

    }

    public Rate(String name, String number) {
        mName = name;
        mNumber = number;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }
}
