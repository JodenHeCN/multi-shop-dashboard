// src/main/java/com/multishop/controller/HomeController.java
package com.multishop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.multishop.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HomeController {

    private final ProductService productService;
    
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("GET / - render index");
        // service now returns DTOs; Thymeleaf template uses same property names so it works with ProductDto
        model.addAttribute("products", productService.getAllProducts());
        return "index";
    }
}
