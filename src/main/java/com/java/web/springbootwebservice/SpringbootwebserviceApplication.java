package com.java.web.springbootwebservice;

import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@CrossOrigin 
@ServletComponentScan
@SpringBootApplication
@RestController
public class SpringbootwebserviceApplication extends SpringBootServletInitializer{

	private static Class<SpringbootwebserviceApplication> applicationClass = SpringbootwebserviceApplication.class;

	Map<String, Object> deviceCache =null;
	public static void main(String[] args) {
		SpringApplication.run(SpringbootwebserviceApplication.class, args);
	}
	
	
	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(applicationClass);
	    }
	 

	@RequestMapping (value ="/device")
	@ResponseBody

	public String getDevice(@RequestParam(name = "esn") String esn) {
		
		System.out.println ("ESN is  "+esn);
		
		deviceCache = new ConcurrentHashMap<String, Object>();
		
		//loading the device json file 
		loadDeviceJson();
		
		JsonObject deviceObj = (JsonObject)deviceCache.get("device");
		
		//System.out.println ("deviceObj is  "+deviceObj);
		if (deviceObj != null ) {
			JsonObject esnObj = (JsonObject) deviceObj.get(esn);
			System.out.println("ESN Object is "+ deviceObj.get(esn));
			
			if (esnObj != null ) {
		 		
				return deviceObj.get(esn).toString();
			}	else {
				
				return deviceObj.get("error").toString();
			}
			
		}else {
			
			JsonObject errorObj = new JsonObject();

			errorObj.addProperty("responseCode", "0");
			errorObj.addProperty("statusMessage", "Failed to Read the device configuration");
			
			return errorObj.toString();
		}
		
		
	
		
		
		
	}

	private void loadDeviceJson() {
		

		JsonParser jsonParser = new JsonParser();
		
		try {
			
			//	JsonObject jsonObj= (JsonObject) jsonParser.parse(new FileReader("\\WEB-INF\\classes\\device.json"));
			File resource = new ClassPathResource("device.json").getFile();
			
			JsonObject jsonObj= (JsonObject) jsonParser.parse(new FileReader(resource));
			
				
				System.out.println("Json Object .."+jsonObj);
				JsonObject deviceObj = jsonObj.getAsJsonObject("device");
				
		//		System.out.println("deviceObj is .."+deviceObj);
				
				deviceCache.put("device", deviceObj);
				
							
				JsonObject simObj = jsonObj.getAsJsonObject("iccid");
				
		//		System.out.println("Sim Obj is .."+simObj);
				
				deviceCache.put("sim", simObj);
				
				
				
		}catch(Exception ex) {
			
			ex.printStackTrace();
		}
		
	    
		
	}


	}

