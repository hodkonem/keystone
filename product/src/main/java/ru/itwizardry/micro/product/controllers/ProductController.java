package ru.itwizardry.micro.product.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ProductController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping("/product")
    public String testPort() {
        return "product";
    }
}