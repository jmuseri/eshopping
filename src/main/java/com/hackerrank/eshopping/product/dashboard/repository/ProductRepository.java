package com.hackerrank.eshopping.product.dashboard.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.hackerrank.eshopping.product.dashboard.model.Product;
/**
 * Product JPA Repository.
 * @author jmuseri
 *
 */
public interface ProductRepository extends CrudRepository<Product, Long> {


	Product findById(long id);
	
	List<Product>findByOrderByIdAsc();
	
	List<Product>findByCategory(
			@Param("category")String category, Sort sort);
	
	List<Product> findByCategoryAndAvailability(
			@Param("category")String category, 
			@Param("availability")Boolean availability);
	
	
}
