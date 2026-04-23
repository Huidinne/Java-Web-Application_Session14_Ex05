package org.example.ex_05.dto;

import java.math.BigDecimal;

public class RevenueSummary {

	private final BigDecimal totalRevenue;
	private final long successfulOrders;

	public RevenueSummary(BigDecimal totalRevenue, long successfulOrders) {
		this.totalRevenue = totalRevenue;
		this.successfulOrders = successfulOrders;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	public long getSuccessfulOrders() {
		return successfulOrders;
	}
}

