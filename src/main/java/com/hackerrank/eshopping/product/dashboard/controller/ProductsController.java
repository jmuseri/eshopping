package com.hackerrank.eshopping.product.dashboard.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.hackerrank.eshopping.product.dashboard.service.ProductsService;
/**
 * Java Solstice Challenge Controller. 
 * @author jmuseri
 *
 */
@RestController
@RequestMapping(value = "/products")
public class ProductsController {

	@Autowired
	private ProductsService productsService;

	/**
	 * Add a Product.
	 * @param product
	 * @return
	 * @throws ProductBadRequestException
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces="application/json; charset=UTF-8")
	public ResponseEntity<?> saveProduct(@RequestBody @Valid Product product) throws ProductBadRequestException {

		productsService.create(product);
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

		productsService.update(id, dto);
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

		Product product = productsService.findByid(id);
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


		List<Product> products = productsService.list(category, availability);

		return new ResponseEntity<>(products,HttpStatus.OK);
	}


}
