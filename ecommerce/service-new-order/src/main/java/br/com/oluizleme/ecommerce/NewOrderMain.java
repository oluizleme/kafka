package br.com.oluizleme.ecommerce;

import br.com.oluizleme.ecommerce.dispatcher.KafkaDispatcher;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

        try (var orderDispatcher = new KafkaDispatcher<Order>()) {
            var email = String.valueOf(Math.random() + "@email.com");

            for (var i = 0; i < 10; i++) {
                var orderId = UUID.randomUUID().toString();
                var amount = new BigDecimal(Math.random() * 5000 + 1);
                var order = new Order(orderId, amount, email);

                var id = new CorrelationId(NewOrderMain.class.getName());

                orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, id, order);

            }
        }
    }
}
