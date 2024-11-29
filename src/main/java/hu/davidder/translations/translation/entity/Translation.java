package hu.davidder.translations.translation.entity;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import hu.davidder.translations.core.base.EntityBase;
import hu.davidder.translations.image.entity.Image;
import jakarta.persistence.*;

/**
 * The Translation class represents a translation entity that includes details
 * such as key, value, type, associated images, and possible forwarding to another translation.
 */
@Entity
@Table(name = "translation", uniqueConstraints = {@UniqueConstraint(columnNames = {"key"})})
public class Translation extends EntityBase implements Serializable {

    private static final long serialVersionUID = -4288082517634337250L;

    /**
     * The key of the translation.
     */
    @Column(name = "KEY", length = 2000, nullable = false)
    private String key;

    /**
     * The value of the translation.
     */
    @Column(name = "VALUE", length = 20000, nullable = false)
    private String value;

    /**
     * The type of the translation.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private Type type;

    /**
     * The images associated with the translation.
     */
    @JsonIgnore
    @JsonManagedReference
    @Fetch(FetchMode.JOIN)
    @OneToMany(mappedBy = "translation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Image> images;

    /**
     * The forwarded translation, if any.
     */
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "forwarded", referencedColumnName = "id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Translation forwarded;

    /**
     * Default constructor.
     * Creates a new instance of Translation.
     */
    public Translation() {}

    /**
     * Parameterized constructor.
     * Creates a new instance of Translation with the specified attributes.
     * 
     * @param key The key of the translation.
     * @param value The value of the translation.
     * @param type The type of the translation.
     */
    public Translation(String key, String value, Type type) {
        this.key = key;
        this.value = value;
        this.type = type;
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
     * If the translation is forwarded, returns the value of the forwarded translation.
     * 
     * @return The value of the translation.
     */
    public String getValue() {
        return forwarded == null ? this.value : forwarded.value;
    }

    /**
     * Sets the value of the translation.
     * 
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the type of the translation.
     * If the translation is forwarded, returns the type of the forwarded translation.
     * 
     * @return The type of the translation.
     */
    public Type getType() {
        return forwarded == null ? type : forwarded.type;
    }

    /**
     * Sets the type of the translation.
     * 
     * @param type The type to set.
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the list of images associated with the translation.
     * If the translation is forwarded, returns the images of the forwarded translation.
     * 
     * @return The list of images.
     */
    public List<Image> getImages() {
        return forwarded == null ? images : forwarded.images;
    }

    /**
     * Sets the list of images associated with the translation.
     * 
     * @param images The images to set.
     */
    public void setImages(List<Image> images) {
        this.images = images;
    }

    /**
     * Returns the forwarded translation.
     * 
     * @return The forwarded translation.
     */
    public Translation getForwarded() {
        return forwarded;
    }

    /**
     * Sets the forwarded translation.
     * 
     * @param forwarded The forwarded translation to set.
     */
    public void setForwarded(Translation forwarded) {
        this.forwarded = forwarded;
    }

    @Override
    public String toString() {
        return "Translation [id=" + super.id + ", key=" + key + ", value=" + value + ", type=" + type + ", images=" + images + "]";
    }
}
