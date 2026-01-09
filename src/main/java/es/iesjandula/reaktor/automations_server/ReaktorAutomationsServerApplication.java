package es.iesjandula.reaktor.automations_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages =
{ "es.iesjandula" })
public class ReaktorAutomationsServerApplication 
{

	public static void main(String[] args) 
	{
		SpringApplication.run(ReaktorAutomationsServerApplication.class, args);
	}

}
