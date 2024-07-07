package hu.davidder.translations.image.entity;


public enum ImageType {

	PNG("PNG",".png"), 
	JPG("JPG", ".jpg"),
	;

	public final String name;
	public final String value;

	private ImageType(String name, String value) {
		this.name = name;
		this.value = value;
	}

}
