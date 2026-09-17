package com.example.demospringpedidos.config;

import com.example.demospringpedidos.entities.Order;
import com.example.demospringpedidos.entities.User;
import com.example.demospringpedidos.entities.enums.OrderStatus;
import com.example.demospringpedidos.repositories.OrderRepository;
import com.example.demospringpedidos.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.Arrays;

@Configuration
@Profile("test") // o nome "test" foi o utilizado na config spring.profiles.active do application.properties
public class TestConfig implements CommandLineRunner {
    // Essa classe precisa implementar a interface CommandLineRunner para poder ser executada assim que a aplicação subir
    // Tudo o que estiver dentro do método run() será executado assim que a aplicação for iniciada
    // Neste caso, estaremos populando o banco de dados de teste com objetos instanciados dentro do run()

    @Autowired // O próprio Spring faz a injeção de dependência automaticamente (ou seja, não precisamos declarar um construtor dentro de
    // TestConfig que recebe um UserRepository como argumento e seta no atributo userRepository)
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public void run(String... args) throws Exception {
        // Está sendo colocado null no id dos objetos abaixo, porque o próprio banco de dados irá gerá-los
        User u1 = new User(null, "Maria Brown", "maria@gmail.com", "988888888", "123456");
        User u2 = new User(null, "Alex Green", "alex@gmail.com", "977777777", "123456");

        Order o1 = new Order(null, Instant.parse("2019-06-20T19:53:07Z"), OrderStatus.PAID, u1);
        Order o2 = new Order(null, Instant.parse("2019-07-21T03:42:10Z"), OrderStatus.WAITING_PAYMENT,u2);
        Order o3 = new Order(null, Instant.parse("2019-07-22T15:21:22Z"), OrderStatus.WAITING_PAYMENT, u1);

        userRepository.saveAll(Arrays.asList(u1, u2));
        orderRepository.saveAll(Arrays.asList(o1, o2, o3));
    }
}
