package hu.davidder.translations.translation.entity;

import hu.davidder.translations.image.entity.ImageInsertBody;

/**
 * The TranslationImageInsertBody class represents a data transfer object that includes
 * a translation key and associated image insertion details.
 */
public class TranslationImageInsertBody {

    private String key;
    private ImageInsertBody value;

    /**
     * Default constructor.
     * Creates a new instance of TranslationImageInsertBody.
     */
    public TranslationImageInsertBody() {}

    /**
     * Parameterized constructor.
     * Creates a new instance of TranslationImageInsertBody with the specified attributes.
     * 
     * @param key The key of the translation.
     * @param value The image insertion details.
     */
    public TranslationImageInsertBody(String key, ImageInsertBody value) {
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
     * Returns the image insertion details.
     * 
     * @return The image insertion details.
     */
    public ImageInsertBody getValue() {
        return value;
    }

    /**
     * Sets the image insertion details.
     * 
     * @param value The image insertion details to set.
     */
    public void setValue(ImageInsertBody value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TranslationImageInsertBody [key=" + key + ", value=" + value + "]";
    }
}
