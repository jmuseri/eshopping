package com.hackerrank.eshopping.product.dashboard.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.hackerrank.eshopping.product.dashboard.dto.UpdateProductDTO;
import com.hackerrank.eshopping.product.dashboard.exception.ProductBadRequestException;
import com.hackerrank.eshopping.product.dashboard.exception.ProductNotFoundException;
import com.hackerrank.eshopping.product.dashboard.model.Product;
import com.hackerrank.eshopping.product.dashboard.repository.ProductRepository;

/**
 * Java Solstice Challenge Product Service. Maybe is not necessary to add a
 * service class since we have only one Model Class and just a repository is
 * fine. But to keep the code cleaner I decided to add this layer keeping the
 * response data modeling entirely outside the controller.
 * 
 * @author jmuseri
 *
 */
@Service
public class ProductsService {

	@Autowired
	private ProductRepository productRepository;

	/**
	 * Add a Product.
	 * @param product
	 * @return
	 * @throws ProductBadRequestException
	 */
	public ResponseEntity<?> create(Product product) throws ProductBadRequestException {


		if (productRepository.existsById(product.getId())) {
			throw new ProductBadRequestException("Product already exist for this id : " + product.getId());
		}

		productRepository.save(product);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}


	/**
	 * Update an existing product.
	 * @param id
	 * @param dto
	 * @throws ProductBadRequestException
	 */
	public void update(@PathVariable("product_id") Long id, UpdateProductDTO dto) throws Exception {

		if (id==null) {
			throw new Exception("Product Id is required fields");
		}
		Product currentProduct = productRepository.findById(id)
				.orElseThrow(() -> new ProductBadRequestException("Product not found for this id : " + id));

		Double retailPrice = dto.getRetailPrice();
		Double discountedPrice = dto.getDiscountedPrice();
		Boolean availability = dto.getAvailability();

		if (retailPrice!=null)
			currentProduct.setRetailPrice(retailPrice);
		if (discountedPrice!=null)
			currentProduct.setDiscountedPrice(discountedPrice);
		if (availability!=null)
			currentProduct.setAvailability(availability);

		productRepository.save(currentProduct);
		
	}

	/**
	 * Find specific product for the given Id.
	 * @param id
	 * @return The given product.
	 * @throws ProductNotFoundException
	 */
	public Product findByid(@PathVariable("product_id") Long id) throws ProductNotFoundException {
		return productRepository.findById(id)
		          .orElseThrow(() -> new ProductNotFoundException("Product not found for this id :: " + id));

	}


	/**
	 * Retrieve All products.
	 * @param category, availability
	 * @return The list of products.
	 */
	public List<Product> list(String category,Boolean availability) {

		List<Product> products;
		if (category!=null && availability!=null) {
			
			products = productRepository.findByCategoryAndAvailability(category,availability);

			Collections.sort(products, new DiscountPercentageComparator().reversed()
					.thenComparing(Product::getDiscountedPrice)
                    .thenComparing(Product::getId));

		} else if (category !=null) {
			products = productRepository.findByCategory(category,
					Sort.by("availability").descending()
					.and(Sort.by("discountedPrice").ascending())
					.and(Sort.by("id").ascending()));
		}else {
			products = productRepository.findByOrderByIdAsc();
		}


		return products;
	}



	class DiscountPercentageComparator implements Comparator<Product> {

		@Override
		public int compare(Product o1, Product o2) {
			Double discPercentage1 =  ((o1.getRetailPrice() - o1.getDiscountedPrice())/o1.getRetailPrice() *100);
			Double discPercentage2 =  ((o2.getRetailPrice() - o2.getDiscountedPrice())/o2.getRetailPrice() *100);
			return discPercentage1.compareTo(discPercentage2);
		}
	}


}
