package br.com.oluizleme.ecommerce.consumer;

import br.com.oluizleme.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction <T> {

    void consume(ConsumerRecord<String, Message<T>> record) throws Exception;
}
