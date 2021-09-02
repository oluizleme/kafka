package br.com.oluizleme.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudDetectorService {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        var fraudDetectorService = new FraudDetectorService();
        try(var service = new KafkaService(FraudDetectorService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                fraudDetectorService::parse,
                Map.of())){
            service.run();
        }
    }

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>();

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("--------------------------------------------------------");
        System.out.println("Processing new order, checking for fraud.");
        System.out.println("Key " + record.key());
        System.out.println("Value " + record.value());
        System.out.println("Partition " + record.partition());
        System.out.println("Offset " + record.offset());

        var message = record.value();

        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var order = message.getPayload();

        if(isFraud(order)) {
            //pretending thar the fraud happens when the amount is > = 4500
            System.out.println("Order is a fraud!!! " + order.toString());
            orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getEmail(),
                    message.getId().continueWith(FraudDetectorService.class.getSimpleName()),
                    order);
        } else {
            System.out.println("Order approved: " + order.toString());
            orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getEmail(),
                    new CorrelationId(FraudDetectorService.class.getSimpleName()),order);
        }
        System.out.println("Order processed");
    }

    private boolean isFraud(Order order) {
        return order.getAmount().compareTo(new BigDecimal(4500)) >= 0 ;
    }
}
