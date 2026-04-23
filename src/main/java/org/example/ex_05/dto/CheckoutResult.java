package org.example.ex_05.dto;

import java.math.BigDecimal;

public class CheckoutResult {

	private final Long orderId;
	private final BigDecimal totalAmount;
	private final String message;

	public CheckoutResult(Long orderId, BigDecimal totalAmount, String message) {
		this.orderId = orderId;
		this.totalAmount = totalAmount;
		this.message = message;
	}

	public Long getOrderId() {
		return orderId;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public String getMessage() {
		return message;
	}
}

