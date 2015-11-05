package com.goovis.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.goovis.imagecache.ImageLoader;
import com.goovis.pojoclasses.Product;
import com.goovis.windows.R;

public class CustomAdapter extends BaseAdapter {
	private Context context;
	private List<Product> productList;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	private ImageView productImageView;
	private TextView productNameView;
	private TextView productPriceView;
	private TextView productBrandView;

	public CustomAdapter(Context context, List<Product> productList) {
		this.context = context;
		this.productList = productList;
		imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return productList.size();
	}

	@Override
	public Object getItem(int position) {
		return productList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// return productList.get(position).getId();
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.custom_row, null);

		Product product = productList.get(position);

		productImageView = (ImageView) convertView
				.findViewById(R.id.product_image);
		// productImageView.setImageBitmap(product.getProductImage());
		imageLoader
				.DisplayImage(product.getProductImageUrl(), productImageView);

		productNameView = (TextView) convertView
				.findViewById(R.id.product_name);
		productNameView.setText(product.getProductName());

		productPriceView = (TextView) convertView
				.findViewById(R.id.product_price);
		productPriceView.setText(String.valueOf(product.getProductPrice())
				+ " £");

		productBrandView = (TextView) convertView
				.findViewById(R.id.product_brand);
		productBrandView.setText(product.getProductBrand());

		return convertView;
	}
}
