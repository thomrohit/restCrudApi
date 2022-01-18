package com.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.rest.dto.RestApi;
import com.rest.service.RestService;

@RestController
public class RestApiController {


	@Autowired
	Gson gson;

	@Autowired
	RestService restService;

	private static Logger logger = LoggerFactory.getLogger(RestApiController.class);
	
	@GetMapping("/heathCheck")
	public ResponseEntity healthCheck() {
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/")
	public ResponseEntity getApi() {
		logger.info("executing Get Method without params");  
		String json = gson.toJson(restService.readFile2List());
		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	@GetMapping("/{obj}")
	public ResponseEntity getKeyValue(@PathVariable String obj) {
		logger.info("executing Get Method with params");  
		List<RestApi> lstObj = restService.readFile2List();
		boolean found = false;
		for(RestApi restApi :lstObj) {
			if(restApi.getKey().equals(obj)) {
				found = true;
				return new ResponseEntity<>(restApi.getValue(), HttpStatus.OK);
			}
		}
		if(!found)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		return null;
	}

	@PostMapping(path = "/{key}/{value}")
	public ResponseEntity pushKeyValue(@PathVariable String key,@PathVariable String value) {
		logger.info("executing Post Method");  
		restService.writeFile(key,value);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping(value = "/{obj}")
	public ResponseEntity deletePost(@PathVariable String obj) {
		logger.info("executing Delete Method");  
		restService.deleteContent(obj);
		return new ResponseEntity<>(obj, HttpStatus.NO_CONTENT);
	}

}
