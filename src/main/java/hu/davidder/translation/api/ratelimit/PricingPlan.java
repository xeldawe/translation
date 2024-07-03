package hu.davidder.translation.api.ratelimit;

import java.time.Duration;

import io.github.bucket4j.Bandwidth;

public enum PricingPlan {

	TRIAL(1250), //1250/h

	TIER1(250000), //250k/h

	TIER2(1000000); //1m/h

	private int bucketCapacity;

	private PricingPlan(int bucketCapacity) {
		this.bucketCapacity = bucketCapacity;
	}

	Bandwidth getLimit() {
		return Bandwidth
				.builder()
				.capacity(bucketCapacity)
				.refillIntervally(bucketCapacity, Duration.ofHours(1))
				.build();
	}

	public int bucketCapacity() {
		return bucketCapacity;
	}
}