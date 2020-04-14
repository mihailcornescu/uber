package com.uber.api.core.product;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductService {

    Product createProduct(@RequestBody Product body);

    /**
     * Sample usage: curl $HOST:$PORT/product/1
     *
     * @param productId
     * @return the product, if found, else null
     */
    @GetMapping(
        value    = "/product/{paramId}",
        produces = "application/json")
     Mono<Product> getProduct(@PathVariable String paramId);

    void deleteProduct(@PathVariable int productId);
}