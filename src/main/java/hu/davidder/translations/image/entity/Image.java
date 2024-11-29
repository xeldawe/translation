package hu.davidder.translations.image.entity;

import java.io.Serializable;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;

import hu.davidder.translations.core.base.EntityBase;
import hu.davidder.translations.translation.entity.Translation;
import jakarta.persistence.*;

/**
 * The Image class represents an image entity that includes details
 * such as its name, value, target size, and type. It is linked to a
 * Translation entity.
 */
@Entity
@Table(name = "image")
public class Image extends EntityBase implements Serializable {

    private static final long serialVersionUID = 684811543619976895L;

    /**
     * The Translation associated with this image.
     */
    @JsonBackReference
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH })
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "translation_id", nullable = true)
    private Translation translation;

    /**
     * The name of the image.
     */
    @Column(name = "NAME", nullable = false)
    private String name;

    /**
     * The value (binary data) of the image.
     */
    @Column(name = "VALUE", nullable = false)
    private byte[] value;

    /**
     * The target size of the image.
     */
    @Column(name = "TARGET_SIZE", nullable = false)
    private Integer targetSize;

    /**
     * The type of the image.
     */
//    @Enumerated(EnumType.STRING) //TODO
    @Column(name = "TYPE", nullable = false)
    private ImageType type;

    /**
     * Default constructor.
     * Creates a new instance of Image.
     */
    public Image() {}

    /**
     * Parameterized constructor.
     * Creates a new instance of Image with the specified attributes.
     * 
     * @param translation The Translation associated with this image.
     * @param name The name of the image.
     * @param value The binary data of the image.
     * @param targetSize The target size of the image.
     * @param type The type of the image.
     */
    public Image(Translation translation, String name, byte[] value, Integer targetSize, ImageType type) {
        this.translation = translation;
        this.name = name;
        this.value = value;
        this.targetSize = targetSize;
        this.type = type;
    }

    /**
     * Returns the type of the image.
     * 
     * @return The type of the image.
     */
    public ImageType getType() {
        return type;
    }

    /**
     * Sets the type of the image.
     * 
     * @param type The type of the image.
     */
    public void setType(ImageType type) {
        this.type = type;
    }

    /**
     * Returns the name of the image.
     * 
     * @return The name of the image.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the image.
     * 
     * @param name The name of the image.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the Translation associated with this image.
     * 
     * @return The Translation associated with this image.
     */
    public Translation getTranslation() {
        return translation;
    }

    /**
     * Sets the Translation associated with this image.
     * 
     * @param translation The Translation associated with this image.
     */
    public void setTranslation(Translation translation) {
        this.translation = translation;
    }

    /**
     * Returns the binary data of the image.
     * 
     * @return The binary data of the image.
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * Sets the binary data of the image.
     * 
     * @param value The binary data of the image.
     */
    public void setValue(byte[] value) {
        this.value = value;
    }

    /**
     * Returns the target size of the image.
     * 
     * @return The target size of the image.
     */
    public Integer getTargetSize() {
        return targetSize;
    }

    /**
     * Sets the target size of the image.
     * 
     * @param targetSize The target size of the image.
     */
    public void setTargetSize(Integer targetSize) {
        this.targetSize = targetSize;
    }
}
