package hu.davidder.translations.image.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * The ImageInsertBody class contains an image URL and its target sizes.
 * This class is used for uploading and processing images.
 */
public class ImageInsertBody {

    // The URL of the image
    private String url;

    // The list of target sizes for the image
    private List<Integer> targetSizes = new ArrayList<>();

    /**
     * Default constructor.
     * Creates a default ImageInsertBody object.
     */
    public ImageInsertBody() {}

    /**
     * Constructor with URL parameter.
     * 
     * @param url The URL of the image.
     */
    public ImageInsertBody(String url) {
        this.url = url;
    }

    /**
     * Constructor with URL and target sizes parameters.
     * 
     * @param url The URL of the image.
     * @param targetSizes The list of target sizes.
     */
    public ImageInsertBody(String url, List<Integer> targetSizes) {
        this.url = url;
        this.targetSizes = targetSizes;
    }

    /**
     * Returns the URL of the image.
     * 
     * @return The URL of the image.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the image.
     * 
     * @param url The URL of the image.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Returns the list of target sizes.
     * 
     * @return The list of target sizes.
     */
    public List<Integer> getTargetSizes() {
        return targetSizes;
    }

    /**
     * Sets the list of target sizes.
     * 
     * @param targetSizes The list of target sizes.
     */
    public void setTargetSizes(List<Integer> targetSizes) {
        this.targetSizes = targetSizes;
    }
}
