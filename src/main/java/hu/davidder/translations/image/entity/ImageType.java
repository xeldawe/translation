package hu.davidder.translations.image.entity;

/**
 * The ImageType enum represents different types of images.
 * It includes the type name and file extension for each image type.
 */
public enum ImageType {

    PNG("PNG", ".png"),
    JPG("JPG", ".jpg");

    /**
     * The name of the image type.
     */
    public final String typeName;

    /**
     * The file extension associated with the image type.
     */
    public final String value;

    /**
     * Constructor for the ImageType enum.
     * Initializes the enum with the specified type name and file extension.
     * 
     * @param typeName The name of the image type.
     * @param value The file extension of the image type.
     */
    private ImageType(String typeName, String value) {
        this.typeName = typeName;
        this.value = value;
    }
}
