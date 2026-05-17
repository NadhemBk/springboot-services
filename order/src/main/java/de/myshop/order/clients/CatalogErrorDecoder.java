package de.myshop.order.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import de.myshop.order.services.exceptions.CatalogUnavailableException;
import de.myshop.order.services.exceptions.ProductValidationException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CatalogErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 422 -> {
                try {
                    Map<String, Object> errorBody = objectMapper.readValue(
                            response.body().asInputStream(),
                            new TypeReference<>() {
                            }
                    );
                    List<String> missingIds = objectMapper.convertValue(
                            errorBody.get("missingIds"),
                            new TypeReference<>() {
                            }
                    );
                    yield new ProductValidationException("Invalid product IDs", missingIds == null ? List.of() : missingIds);
                } catch (IOException e) {
                    yield new ProductValidationException("Invalid product IDs");
                }
            }
            case 500, 502, 503, 504 -> new CatalogUnavailableException("Catalog service is currently unavailable");
            default -> new Exception("Generic error calling catalog service");
        };
    }
}

