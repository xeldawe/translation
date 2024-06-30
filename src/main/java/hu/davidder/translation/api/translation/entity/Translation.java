package hu.davidder.translation.api.translation.entity;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import hu.davidder.translation.api.image.entity.Image;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "translation")
public class Translation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4288082517634337250L;

	public Translation(String key, String value, Type type) {
		super();
		this.key = key;
		this.value = value;
		this.type = type;
	}

	public Translation() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue
	private Long id;
	@Column(length = 2000)
	private String key;
	@Column(length = 20000)
	private String value;
	@Enumerated(EnumType.STRING)
	private Type type;
	@JsonIgnore
	@JsonManagedReference
	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "translation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Image> images;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}
	
	
}
