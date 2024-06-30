package hu.davidder.translation.api.ratelimit;

import java.io.Serializable;

public class ApiKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8666081824450498131L;
	private String key;
	private String ipAddress;
	private PricingPlan plan;
	
	public ApiKey() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ApiKey(String key, PricingPlan plan, String ipAddress) {
		super();
		this.key = key;
		this.plan = plan;
		this.ipAddress = ipAddress;
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
		return this.key+"-"+this.ipAddress;
	}
	
	
}
