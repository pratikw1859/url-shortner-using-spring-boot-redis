package com.app.rest.controller;

import java.nio.charset.StandardCharsets;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.hash.Hashing;

@RestController
public class UrlShortnerController {

	private RedisTemplate<String,String> redisTemplate;
	
	public UrlShortnerController(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@PostMapping
	public String createShortUrl(@RequestBody String url){
		//Validate The URL using UrlValidator present in Common-validator
		UrlValidator validator = new UrlValidator(new String[] {"http","https"});
		if(validator.isValid(url)) {
			/*
			 * If Url is Valid Then Do Hahing using Murmur_3 provided by Google Gauva
			 */
			String id = Hashing.murmur3_128().hashString(url, StandardCharsets.UTF_16).toString();
			/*
			 * Store Id in Redis as a Key and Url as Value
			 */
			redisTemplate.opsForValue().set(id, url);
			return id;
		}
		throw new RuntimeException("Invalid Url...");
	}
	
	@GetMapping("/{shortUrl}")
	public String getOriginalUrl(@PathVariable("shortUrl") String shortUrl){
		String originalUrl = redisTemplate.opsForValue().get(shortUrl);
		return originalUrl;
	}
}
