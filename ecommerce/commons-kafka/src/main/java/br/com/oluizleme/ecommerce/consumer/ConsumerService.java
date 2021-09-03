package br.com.oluizleme.ecommerce.consumer;

import br.com.oluizleme.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface ConsumerService<T> {

    void parse(ConsumerRecord<String, Message<T>> record) throws IOException, ExecutionException, InterruptedException;
    String getTopic();
    String getConsumerGroup();
}
