package de.myshop.order.clients;

import de.myshop.order.dtos.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "catalog-service", url = "${CATALOG_SERVICE_URL:http://localhost:8081/api/v1/products}")
public interface CatalogClient {

    @PostMapping("/search")
    List<ProductResponse> verifyProducts(@RequestBody List<String> ids);
}
