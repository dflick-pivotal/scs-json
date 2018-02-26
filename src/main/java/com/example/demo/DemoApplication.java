package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.spring.cloud.service.config.PlainTextConfigClient;

@SpringBootApplication
@EnableOAuth2Client
@RestController
public class DemoApplication {

	@Autowired
	private Environment environment;
	
	@Autowired private ApplicationContext applicationContext;
	
	@Value("http://localhost:8888/demo/default/master/application.json")
	Resource resource;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@RequestMapping
	String request() {
		
		InputStream is = null;
		StringBuffer buffer = new StringBuffer();
		String line;

		if (Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> (env.equalsIgnoreCase("cloud")))) {
			try {
				is = applicationContext.getBean(PlainTextConfigClient.class).getConfigFile("cloud", "master", "config/application.json").getInputStream();
			} catch (BeansException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				is = resource.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buffer.toString();
	}
}
