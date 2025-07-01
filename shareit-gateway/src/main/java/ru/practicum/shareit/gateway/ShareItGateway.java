package ru.practicum.shareit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
// не смог обнаружить, где зависимость от БД, без exclude приложение не стартует
public class ShareItGateway {
	public static void main(String[] args) {
		SpringApplication.run(ShareItGateway.class, args);
	}

}
