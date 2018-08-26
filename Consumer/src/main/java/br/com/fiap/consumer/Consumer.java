package br.com.fiap.consumer;

import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class Consumer {
	
	private static int ONE_MINUTE = 60 * 1000;

	public static void main(String[] args) throws Exception {

		final MessageProcessor messageProcessor = new Consumer().new MessageProcessor();
		CamelContext context = new DefaultCamelContext();
		RouteBuilder routeBuilder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("jms:queue:fiap_queue").process(messageProcessor);
			}
		};
		
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(new ActiveMQConnectionFactory("tcp://127.0.0.1:61616")));
		context.addRoutes(routeBuilder);
		context.start();
		Thread.sleep(ONE_MINUTE);
		context.stop();
		messageProcessor.seeConsumedMessages();
	}

	private class MessageProcessor implements Processor {

		private List<String> messages = new ArrayList<String>();

		public void process(Exchange exchange) throws Exception {
			String message = exchange.getIn().getBody().toString();
			System.out.println("Consuming message: " + message);
			messages.add(message);
		}

		public void seeConsumedMessages() {
			System.out.println("Consumed messages: ");
			messages.forEach(System.out::println);
		}
	}

}
