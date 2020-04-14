package com.coffee.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class configcontroller {
	
	private static final Logger log = LoggerFactory.getLogger(configcontroller.class);

	 @GetMapping("/log")
	    public void log(){
	        log.trace("------------trace-----------");
	        log.debug("------------debug-----------");
	        log.info("------------info-----------");
	        log.warn("------------warn-----------");
	        log.error("------------error-----------");

	    }

}
