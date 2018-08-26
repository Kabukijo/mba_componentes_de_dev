package br.com.fiap.producer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class Producer {
	
	private static int ONE_MINUTE = 60 * 1000;

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		RouteBuilder routeBuilder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("file:/C:/inputFolder").split().tokenize("\n").to("jms:queue:fiap_queue");
			}
		};

		createMessages();
		context.addComponent("jms", JmsComponent.jmsComponent(new ActiveMQConnectionFactory("tcp://127.0.0.1:61616")));
		context.addRoutes(routeBuilder);
		context.start();
		Thread.sleep(ONE_MINUTE);
		context.stop();
	}

	private static void createMessages() throws IOException {

		FileWriter fileWriter = new FileWriter(new File("C:/inputFolder/messages.txt"));
		StringBuilder messages = new StringBuilder();

		messages.append("message_1" + "\n");
		messages.append("message_2" + "\n");
		messages.append("message_3" + "\n");
		fileWriter.write(messages.toString());
		fileWriter.close();

		System.out.println("Produced messages: \n" + messages.toString());
	}
}
