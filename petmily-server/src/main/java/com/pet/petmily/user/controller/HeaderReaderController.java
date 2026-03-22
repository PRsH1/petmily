package com.pet.petmily.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HeaderReaderController
{
    @GetMapping
    public ResponseEntity<Object> authHeaderChecker(HttpServletRequest request) {
        String authorizationHeaderValue = request.getHeader("Authorization");
        Map<String, String> response = new HashMap<>(){{
            put("Authorization", authorizationHeaderValue);
        }};
        return ResponseEntity.ok(response);
    }
}