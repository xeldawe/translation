package hu.davidder.translations.core.enums;



import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Headers {

	CONTENT_TYPE("Content-Type"), 
	X_RATE_LIMIT_GLOBAL_RETRY_AFTER_SECONDS("X-Rate-Limit-Global-Retry-After-Seconds"), 
	X_RATE_GLOBAL_LIMIT_REMAINING("X-Rate-Global-Limit-Remaining"), 
	X_RATE_LIMIT_RETRY_AFTER_SECONDS("X-Rate-Limit-Retry-After-Seconds"),  
	X_RATE_LIMIT_REMAINING("X-Rate-Limit-Remaining"),  
	X_API_KEY("X-Api-Key"), 
	X_TIER("X-Tier"),
	;

	private final String name;
	
	private Headers(String name) {
		this.name=name;
	}

	public String getName() {
		return name;
	}
	
	public static List<String> asList() {
		return Stream.of(Headers.values())
                .map(Enum::name)
                .collect(Collectors.toList());
	}

}
