package ru.itwizardry.micro.common.testutils;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;


import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class FeignTestUtils {

    public static FeignException.NotFound createFeignNotFound(String message) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/dummy",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );

        return new FeignException.NotFound(
                message,
                request,
                null,
                Map.of()
        );
    }

    public static FeignException.NotFound createFeignNotFound(String message, String body) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/dummy",
                Collections.emptyMap(),
                null,
                new RequestTemplate()
        );

        return new FeignException.NotFound(
                message,
                request,
                body.getBytes(StandardCharsets.UTF_8),
                Map.of()
        );
    }
}