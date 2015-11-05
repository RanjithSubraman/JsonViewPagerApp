package com.goovis.pojoclasses;

import java.io.Serializable;

import android.graphics.Bitmap;

@SuppressWarnings("serial")
public class Product implements Serializable{
	private Bitmap productImage;
	private String productName;
	private double productPrice;
	private String productBrand;
	private String productImageUrl;

	public Product(Bitmap productImage, String productName,
			double productPrice, String productBrand) {
		this.productImage = productImage;
		this.productName = productName;
		this.productPrice = productPrice;
		this.productBrand = productBrand;
	}

	public Product(String productImageUrl, String productName,
			double productPrice, String productBrand) {
		this.productImageUrl = productImageUrl;
		this.productName = productName;
		this.productPrice = productPrice;
		this.productBrand = productBrand;
	}

	public Bitmap getProductImage() {
		return productImage;
	}

	public void setProductImage(Bitmap productImage) {
		this.productImage = productImage;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public String getProductImageUrl() {
		return productImageUrl;
	}

	public void setProductImageUrl(String productImageUrl) {
		this.productImageUrl = productImageUrl;
	}

}
