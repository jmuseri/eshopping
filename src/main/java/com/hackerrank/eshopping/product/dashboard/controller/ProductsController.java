package com.hackerrank.eshopping.product.dashboard.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hackerrank.eshopping.product.dashboard.dto.UpdateProductDTO;
import com.hackerrank.eshopping.product.dashboard.exception.ProductBadRequestException;
import com.hackerrank.eshopping.product.dashboard.exception.ProductNotFoundException;
import com.hackerrank.eshopping.product.dashboard.model.Product;
import com.hackerrank.eshopping.product.dashboard.repository.ProductRepository;

@RestController
@RequestMapping(value = "/products")
public class ProductsController {

	@Autowired
	private ProductRepository productRepository;
	
	/**
	 * Add a Product.
	 * @param product
	 * @return 
	 * @throws ProductBadRequestException 
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces="application/json; charset=UTF-8")
	public ResponseEntity<?> saveProduct(@RequestBody @Valid Product product) throws ProductBadRequestException {


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
	@RequestMapping(value = "/{product_id}", method = RequestMethod.PUT, produces="application/json; charset=UTF-8")
	public ResponseEntity<?> updateProduct(@PathVariable("product_id") Long id, 
    		@RequestBody @Valid UpdateProductDTO dto) throws Exception {
		
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
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * Find specific product for the given Id.
	 * @param id
	 * @return The given product.
	 * @throws ProductNotFoundException
	 */
	@RequestMapping(value = "/{product_id}", method = RequestMethod.GET)
	public ResponseEntity<Product> getProductByid(@PathVariable("product_id") Long id) throws ProductNotFoundException {
		Product product = productRepository.findById(id)
		          .orElseThrow(() -> new ProductNotFoundException("Product not found for this id :: " + id));


		
		return new ResponseEntity<>(product,HttpStatus.OK);
		
		
	}
	
	
	/**
	 * Retrieve All products.
	 * @param category, availability
	 * @return The list of products.
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<List<Product>> getProducts(
    		@RequestParam(value = "category", required = false) String category,
    		@RequestParam(value = "availability", required = false) Boolean availability) {
		
		List<Product> products;
		if (category!=null && availability!=null) {
			products = productRepository.findByCategoryAndAvailability(category.replaceAll("%20", " "),availability);
			
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
		 

		return new ResponseEntity<>(products,HttpStatus.OK);
	}
	
	
	
	class DiscountPercentageComparator implements Comparator<Product> {
	    
		@Override
		public int compare(Product o1, Product o2) {
			Double discPercentage1 =  ((o1.getRetailPrice() - o1.getDiscountedPrice())/o1.getRetailPrice() *100);
			Double discPercentage2 =  ((o2.getRetailPrice() - o2.getDiscountedPrice())/o2.getRetailPrice() *100);
			return discPercentage1.compareTo(discPercentage2);
		}
	}	
	
	
	//Con mas tiempo quiza podria haber mejorado algunas cosas mas.
	
}
