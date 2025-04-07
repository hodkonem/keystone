package ru.itwizardry.micro.product.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/main")
public class ProductController {

    @GetMapping("/product")
    public String testPort() {
               return "product";
    }
}