package br.com.oluizleme.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {

        try(var orderDispatcher = new KafkaDispatcher<Order>()) {
            try(var emailDispatcher = new KafkaDispatcher<String>()) {

                var email = String.valueOf(Math.random() + "@email.com");

                for (var i = 0; i < 10; i ++) {
                    var orderId = UUID.randomUUID().toString();
                    var amount = new BigDecimal(Math.random() * 5000 + 1);
                    var order = new Order(orderId, amount,email);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, new CorrelationId(NewOrderMain.class.getName()), order);

                    var emailCode = "Thank you for your order";
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, new CorrelationId(NewOrderMain.class.getName()), emailCode);
                }
            }
        }
    }
}
