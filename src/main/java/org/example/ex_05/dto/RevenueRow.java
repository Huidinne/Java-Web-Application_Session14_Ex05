package org.example.ex_05.dto;

import java.math.BigDecimal;

public class RevenueRow {

	private final Long vendorId;
	private final String vendorName;
	private final BigDecimal revenue;

	public RevenueRow(Long vendorId, String vendorName, BigDecimal revenue) {
		this.vendorId = vendorId;
		this.vendorName = vendorName;
		this.revenue = revenue;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public BigDecimal getRevenue() {
		return revenue;
	}
}

