package hu.davidder.translation.api.image.entity;

import java.io.Serializable;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import hu.davidder.translation.api.translation.entity.Translation;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "image")
public class Image implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 684811543619976895L;
	@Id
	@GeneratedValue
	private Long id;
	@JsonBackReference
	@ManyToOne
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "translation_id", nullable = false)
	private Translation translation;
	private String name;
	private byte[] value;
	private Integer targetSize;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
