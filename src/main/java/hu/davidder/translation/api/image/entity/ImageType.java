package hu.davidder.translation.api.image.entity;

public enum ImageType {

	PNG("PNG"), 
	JPG("JPG"),
	;

	private final String name;

	private ImageType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
