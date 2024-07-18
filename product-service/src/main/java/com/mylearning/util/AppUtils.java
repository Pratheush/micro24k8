package com.mylearning.util;

import com.mylearning.dto.ProductRequest;
import com.mylearning.dto.ProductResponse;
import com.mylearning.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;


//  A utility class is a class that only has static members, hence it should not be instantiated.
@Slf4j
public class AppUtils {


    private AppUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Product dto2modelpro(ProductRequest productrequest){
        log.info("AppUtils :: ProductRequest {}", productrequest);
        Product product = new Product();
        BeanUtils.copyProperties(productrequest,product);
        log.info("AppUtils :: Product {}",product);
        return product;
    }

    public static ProductResponse model2dto(Product prod){
        //ProductResponse response = ProductResponse.builder().build();
        //BeanUtils.copyProperties(prod,response);
        return new ProductResponse(prod.getId(), prod.getName(), prod.getDescription(), prod.getPrice());
    }
}
