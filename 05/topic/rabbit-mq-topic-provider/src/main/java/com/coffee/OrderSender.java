package com.coffee;

import java.util.Date;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderSender {
	
	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@Value("${rabbit.exchange}")
	private String exchange;
	
	public void send() throws InterruptedException{
		this.rabbitTemplate.convertAndSend(this.exchange,"push.log.debug", "push.log.debug.......");
		this.rabbitTemplate.convertAndSend(this.exchange,"push.log.info", "push.log.info.......");
		this.rabbitTemplate.convertAndSend(this.exchange,"push.log.warn", "push.log.warn.......");
		this.rabbitTemplate.convertAndSend(this.exchange,"push.log.error", "push.log.error.......");
	}
}
