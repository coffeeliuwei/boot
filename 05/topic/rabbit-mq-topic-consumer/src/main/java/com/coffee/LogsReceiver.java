package com.coffee;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(bindings=@QueueBinding(
		value= @Queue(value="${rabbit.logs}",autoDelete="true"),
		exchange=@Exchange(value="${rabbit.exchange}",type=ExchangeTypes.TOPIC),
		key="*.log.*"
		)
		)
public class LogsReceiver {
	
	@RabbitHandler
	public void process(String msg){
		System.out.println("all所有日志:"+msg);
	}
}
