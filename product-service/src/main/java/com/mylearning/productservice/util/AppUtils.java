package com.mylearning.productservice.util;

import com.mylearning.productservice.dto.ProductRequest;
import com.mylearning.productservice.dto.ProductResponse;
import com.mylearning.productservice.model.Product;
import org.springframework.beans.BeanUtils;


//  A utility class is a class that only has static members, hence it should not be instantiated.
public class AppUtils {

    private AppUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Product dto2modelpro(ProductRequest prodrequest){
        Product product = new Product();
        BeanUtils.copyProperties(prodrequest,product);
        return product;
    }

    public static ProductResponse model2dto(Product prod){
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(prod,response);
        return response;
    }
}
