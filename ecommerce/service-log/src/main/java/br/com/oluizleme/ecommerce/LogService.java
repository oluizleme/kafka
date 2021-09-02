package br.com.oluizleme.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class LogService {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        var logService = new LogService();
        try (var service = new KafkaService<>(LogService.class.getSimpleName(),
                Pattern.compile("ECOMMERCE.*"),
                logService::parse,
                Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("--------------------------------------------------------");
        System.out.println("LOG TOPIC " + record.topic());
        System.out.println("KEY " + record.key());
        System.out.println("VALUE " + record.value());
        System.out.println("PARTITION " + record.partition());
        System.out.println("OFFSET " + record.offset());
    }
}
