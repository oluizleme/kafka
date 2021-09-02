package br.com.oluizleme.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailService {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        var emailService = new EmailService();
        try (var service = new KafkaService<String>(EmailService.class.getSimpleName(),
                "ECOMMERCE_SEND_EMAIL",
                emailService::parse,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("--------------------------------------------------------");
        System.out.println("Send email");
        System.out.println("KEY " + record.key());
        System.out.println("VALUE " + record.value());
        System.out.println("PARTITION " + record.partition());
        System.out.println("OFFSET " + record.offset());
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
