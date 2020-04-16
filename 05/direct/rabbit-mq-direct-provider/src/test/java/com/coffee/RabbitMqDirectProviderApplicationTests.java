package com.coffee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitMqDirectProviderApplicationTests {

	@Autowired
	private Sender sender;
	
	@Test
	public void send() throws InterruptedException {
			this.sender.send();
			
	}

}
