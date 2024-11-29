package hu.davidder.translations.translation.entity;

/**
 * The TranslationTextInsertBody class represents a data transfer object that includes
 * a translation key and its corresponding value.
 */
public class TranslationTextInsertBody {

    private String key;
    private String value;

    /**
     * Default constructor.
     * Creates a new instance of TranslationTextInsertBody.
     */
    public TranslationTextInsertBody() {}

    /**
     * Parameterized constructor.
     * Creates a new instance of TranslationTextInsertBody with the specified attributes.
     * 
     * @param key The key of the translation.
     * @param value The value of the translation.
     */
    public TranslationTextInsertBody(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key of the translation.
     * 
     * @return The key of the translation.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of the translation.
     * 
     * @param key The key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the value of the translation.
     * 
     * @return The value of the translation.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the translation.
     * 
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TranslationTextInsertBody [key=" + key + ", value=" + value + "]";
    }
}
