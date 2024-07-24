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
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "translation", uniqueConstraints={@UniqueConstraint(columnNames={"key"})})
public class Translation extends EntityBase implements Serializable{
	
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
	}
	
	@Column(name="KEY",length = 2000, nullable = false)
	private String key;
	@Column(name="VALUE",length = 20000, nullable = false)
	private String value;
	@Enumerated(EnumType.STRING)
	@Column(name="TYPE", nullable = false)
	private Type type;
	@JsonIgnore
	@JsonManagedReference
	@Fetch(FetchMode.JOIN)
	@OneToMany(mappedBy = "translation", fetch = FetchType.LAZY, cascade = {
			CascadeType.ALL
	})
	private List<Image> images;
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "forwarded", referencedColumnName = "id", nullable = true)
	@OnDelete(action = OnDeleteAction.SET_NULL)
	private Translation forwarded = null;

	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return forwarded==null?this.value:forwarded.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Type getType() {
		return forwarded==null?type:forwarded.type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<Image> getImages() {
		return forwarded==null?images:forwarded.images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	@Deprecated
	@Override
	public String toString() {
		return "Translation [id=" + super.id + ", key=" + key + ", value=" + value + ", type=" + type + ", images=" + images
				+ "]";
	}

	public Translation getForwarded() {
		return forwarded;
	}

	public void setForwarded(Translation forwarded) {
		this.forwarded = forwarded;
	}
	
	
}
