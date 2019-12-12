package com.hackerrank.eshopping.product.dashboard.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ProductBadRequestException extends Exception{

    private static final long serialVersionUID = 1L;

    public ProductBadRequestException(String message){
        super(message);
    }
}