package hu.davidder.translations.image.entity;

import java.util.ArrayList;
import java.util.List;

public class ImageInsertBody {

	private String url;
	private List<Integer> targetSizes = new ArrayList<>();

	public ImageInsertBody(String url) {
		super();
		this.url = url;
	}

	public ImageInsertBody(String url, List<Integer> targetSizes) {
		super();
		this.url = url;
		this.targetSizes = targetSizes;
	}

	public ImageInsertBody() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Integer> getTargetSizes() {
		return targetSizes;
	}

	public void setTargetSizes(List<Integer> targetSizes) {
		this.targetSizes = targetSizes;
	}

}
