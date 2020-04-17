package com.coffee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitMqTopicProviderApplicationTests {


	@Autowired
	private UserSender userSender;
	
	@Autowired
	private ProductSender productSender;
	
	@Autowired
	private OrderSender orderSender;
	@Test
	public void send() throws InterruptedException {
			this.userSender.send();
			this.productSender.send();
			this.orderSender.send();
			
	}
}
