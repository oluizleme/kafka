package br.com.oluizleme.ecommerce;

import br.com.oluizleme.ecommerce.consumer.ConsumerService;
import br.com.oluizleme.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class EmailService implements ConsumerService<String> {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        new ServiceRunner(EmailService::new).start(5);
    }

    public String getConsumerGroup() {
        return EmailService.class.getSimpleName();
    }

    public String getTopic() {
        return "ECOMMERCE_SEND_EMAIL";
    }

    public void parse(ConsumerRecord<String, Message<String>> record) {
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
