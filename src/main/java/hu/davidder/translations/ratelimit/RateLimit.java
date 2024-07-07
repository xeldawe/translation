package hu.davidder.translations.ratelimit;


import java.io.Serializable;

import io.github.bucket4j.Bucket;

public class RateLimit implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8666081824450498131L;
	private String key;
	private String ipAddress;
	private PricingPlan plan;
	private Bucket bucket;
	private RateLimitType type;
	
	public RateLimit() {
		super();
		// TODO Auto-generated constructor stub
	}
	public RateLimit(String key, PricingPlan plan, String ipAddress, RateLimitType type) {
		super();
		this.key = key;
		this.plan = plan;
		this.ipAddress = ipAddress;
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public PricingPlan getPlan() {
		return plan;
	}
	public void setPlan(PricingPlan plan) {
		this.plan = plan;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public String getCacheKey() {
		return this.key+"-"+this.ipAddress+"-"+this.type;
	}
	public Bucket getBucket() {
		return bucket;
	}
	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}
	public RateLimitType getRateLimitType() {
		return type;
	}
	public void setRateLimitType(RateLimitType type) {
		this.type = type;
	}
	
	
	
}
