package hu.davidder.translations.image.entity;

import java.io.Serializable;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;

import hu.davidder.translations.core.base.EntityBase;
import hu.davidder.translations.translation.entity.Translation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "image")
public class Image extends EntityBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 684811543619976895L;

	@JsonBackReference
	@ManyToOne(cascade = { CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH})
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "translation_id", nullable = true)
	private Translation translation;
	@Column(name="NAME", nullable = false)
	private String name;
	@Column(name="VALUE", nullable = false)
	private byte[] value;
	@Column(name="TARGET_SIZE", nullable = false)
	private Integer targetSize;
	@Column(name="TYPE", nullable = false)
	private ImageType type;

	public Image() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Image(Translation translation, String name, byte[] value, Integer targetSize, ImageType type) {
		super();
		this.translation = translation;
		this.name = name;
		this.value = value;
		this.targetSize = targetSize;
		this.type = type;
	}

	public ImageType getType() {
		return type;
	}

	public void setType(ImageType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Translation getTranslation() {
		return translation;
	}

	public void setTranslation(Translation translation) {
		this.translation = translation;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public Integer getTargetSize() {
		return targetSize;
	}

	public void setTargetSize(Integer targetSize) {
		this.targetSize = targetSize;
	}

}
