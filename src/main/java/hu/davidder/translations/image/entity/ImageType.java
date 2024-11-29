package hu.davidder.translations.image.entity;


public enum ImageType {

	PNG("PNG",".png"), 
	JPG("JPG", ".jpg"),
	;

	public final String typeName;
	public final String value;

	private ImageType(String typeName, String value) {
		this.typeName = typeName;
		this.value = value;
	}

}
