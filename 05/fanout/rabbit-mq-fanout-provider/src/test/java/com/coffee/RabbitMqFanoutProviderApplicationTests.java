package com.coffee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitMqFanoutProviderApplicationTests {
	@Autowired
	Sender sender;

	@Test
	void Sender() throws InterruptedException {
		this.sender.send();
	}

}
