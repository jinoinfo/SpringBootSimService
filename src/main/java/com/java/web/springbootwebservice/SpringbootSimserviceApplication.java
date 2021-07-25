package com.java.web.springbootwebservice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;




@CrossOrigin 
@ServletComponentScan
@SpringBootApplication
@RestController
public class SpringbootSimserviceApplication extends SpringBootServletInitializer{

	@Autowired
	ResourceLoader resourceLoader;
	
	private static Class<SpringbootSimserviceApplication> applicationClass = SpringbootSimserviceApplication.class;

	Map<String, Object> deviceCache =null;
	public static void main(String[] args) {
		SpringApplication.run(SpringbootSimserviceApplication.class, args);
	}
	
	
	 @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(applicationClass);
	    }
	 

	
	@RequestMapping(path = "/iccid", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getDevice(@RequestBody Map<String, String> simMap) {
		
		String iccid= simMap.get("iccid");
		System.out.println ("ICCID is  "+iccid);
		
		
		
		deviceCache = new ConcurrentHashMap<String, Object>();
		
		//loading the device json file 
		loadDeviceJson();
		
		JSONObject iccidCacheObj = (JSONObject)deviceCache.get("sim");
		
		//System.out.println ("deviceObj is  "+deviceObj);
		if (iccidCacheObj != null ) {
			JSONObject iccidObj = (JSONObject) iccidCacheObj.get(iccid);
			System.out.println("Iccid Object is "+ iccidCacheObj.get(iccid));
			
			if (iccidObj != null ) {
		 		
				return iccidCacheObj.get(iccid).toString();
			}	else {
				
				return iccidCacheObj.get("errors").toString();
			}
			
		}else {
			
			JsonObject errorObj = new JsonObject();

			errorObj.addProperty("error", "-1");
			errorObj.addProperty("statusMessage", "Failed to Read the device configuration");
			
			return errorObj.toString();
		}
		
		
	
		
		
		
	}

	private void loadDeviceJson() {
		

		JSONParser jsonParser = new JSONParser();
		
		try {
			
			Gson gson = new GsonBuilder()
			        .setLenient()
			        .create();
			
			
		//	gson.
			Resource resource=resourceLoader.getResource("classpath:./device.json");
			
			System.out.println("resource .."+resource);
			
					 
			
			
			
		      //System.out.println("JsonObject .."+obj);
		      InputStream stream= resource.getInputStream();
		      String jsonData ="";
		      BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(stream));

			    
		      Object obj=  jsonParser.parse(bufferedReader);
		      
		      System.out.println("obj.."+obj);
		      
		    
		      JSONObject jsonObj = (org.json.simple.JSONObject)obj;
		      
		  
		      JSONObject deviceObj =  (JSONObject) jsonObj.get("device");
		  				
			  System.out.println("deviceObj is .."+deviceObj);
				
			  deviceCache.put("device", deviceObj);
				
							
			  JSONObject simObj = (JSONObject) jsonObj.get("iccid");
				
			  System.out.println("Sim Obj is .."+simObj);
			  if (null !=simObj) {
						deviceCache.put("sim", simObj);
				}
				
				
				
		}catch(Exception ex) {
			
			ex.printStackTrace();
		}
		
	    
		
	}


	}

