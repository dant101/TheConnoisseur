package Resources;

/**
 * Created by Alexandre on 22/05/2015.
 */
public enum LANGUAGE_ID {
    FRENCH(0),
    ITALIAN(1),
    ENGLISH(2),
    SPANISH(3),
    ARABIC(4),
    RUSSIAN(5),
    KOREAN(6),
    CHINESE(7),
    GERMAN(8);

    private int numVal;

    LANGUAGE_ID(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
