package com.rest.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rest.dto.RestApi;

@Service
public class RestService {

	@Value("${file.path}")
	private String path;

	@Autowired
	Gson gson;

	private static Logger logger = LoggerFactory.getLogger(RestService.class);
	
	public void createFileifNotexists() {
	    try {
	        File file = new File(path);
	        if (!file.exists()) {
	            file.createNewFile();
	            FileOutputStream writer = new FileOutputStream(path);
	            writer.write(("[]").getBytes());
	            writer.close();
	        } 
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public List<RestApi> readFile2List() {
		createFileifNotexists();
		logger.info("Inside ReadFile2List");
		List<RestApi> objList = new ArrayList<>();
		try (FileReader reader = new FileReader(path)) {
			ObjectMapper mapper = new ObjectMapper();
			objList = Arrays.asList(mapper.readValue(Paths.get(path).toFile(), RestApi[].class));
		} catch (FileNotFoundException e) {
			logger.error("File NotFound" +e);
		} catch (IOException e) {
			logger.error("IO Exception" +e);
		}
		return objList;
	}

	public void writeFile(String key, String value) {
		logger.info("Inside Write file");
		List<RestApi> objList = readFile2List();
		ArrayList<RestApi> tmpList = new ArrayList<RestApi>(objList);
		RestApi restApi = new RestApi (key,value);
		if (tmpList.isEmpty()) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
				writer.write(gson.toJson(Arrays.asList(restApi)));
				writer.close();
			} catch (IOException e) {
				logger.error("IO Exception" +e);
			}
		} else {
			tmpList.add(restApi);
			String newData = gson.toJson(tmpList);
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
				writer.write(newData);
				writer.close();
			} catch (IOException e) {
				logger.error("IO Exception" +e);
			}
		}

	}

	public void deleteContent(String obj) {
		logger.info("Inside deleteContent");
		List<RestApi> objList = readFile2List();
		ArrayList<RestApi> tmpList = new ArrayList<RestApi>(objList);
		int i=0;
		for(RestApi restApi :tmpList) {
			if(restApi.getKey().equals(obj)) {
				break;
			}
			i++;
		}
		tmpList.remove(i);
		String newData = gson.toJson(tmpList);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
			writer.write(newData);
			writer.close();
		} catch (IOException e) {
			logger.error("IO Exception" +e);
		}
	}

}
