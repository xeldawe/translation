package hu.davidder.translations.translation.entity;

import hu.davidder.translations.image.entity.ImageInsertBody;

public class TranslationImageInsertBody {
	
	public TranslationImageInsertBody(String key, ImageInsertBody value) {
		super();
		this.key = key;
		this.value = value;
	}
	private String key;
	private ImageInsertBody value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public ImageInsertBody getValue() {
		return value;
	}
	public void setValue(ImageInsertBody value) {
		this.value = value;
	}
	
	
}
