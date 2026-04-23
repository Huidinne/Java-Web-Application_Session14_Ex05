package org.example.ex_05.dto;

public class CheckoutLineRequest {

	private final Long productId;
	private final int quantity;

	public CheckoutLineRequest(Long productId, int quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public Long getProductId() {
		return productId;
	}

	public int getQuantity() {
		return quantity;
	}
}

