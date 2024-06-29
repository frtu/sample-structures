package com.github.frtu.sample.kafka.source.async

import com.github.frtu.logs.core.RpcLogger
import com.github.frtu.logs.core.RpcLogger.entry
import com.github.frtu.logs.core.StructuredLogger.message
import com.github.frtu.sample.kafka.persistence.basic.IEmailRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Service


@Service
class KafkaEmailProcessor(
    private val repository: IEmailRepository,
) {
    @KafkaListener(
        topics = ["\${application.channel.email-source.topic}"],
        groupId = "consumer-group-email-2",
        concurrency = "2",
        properties = [
            "max.poll.records=\${application.channel.email-source.max-poll.records}",
            "max.poll.interval.ms=\${application.channel.email-source.max-poll.interval-ms}",
        ]
    )
    @RetryableTopic(
        kafkaTemplate = "kafkaTemplate",
        attempts = "3",
        backoff = Backoff(
            delay = 2000,
            maxDelay = 5000,
            multiplier = 2.0,
        ),
        timeout = "\${application.channel.email-source.retry.timeout}",
        autoCreateTopics = "true",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
        autoStartDltHandler = "false",
        include = [RuntimeException::class],
    ) // https://docs.spring.io/spring-kafka/reference/retrytopic/retry-config.html
    fun listen(
        record: ConsumerRecord<String, ByteArray>,
    ) {
        rpcLogger.info(
            message("Event received"),
            entry("topic", record.topic()),
            entry("key", record.key()),
            entry("partition", record.partition()),
            entry("offset", record.offset()),
            entry("timestamp", record.timestamp())
        )
        throw RuntimeException("test")
    }

    @DltHandler
    fun processMessage(
        record: ConsumerRecord<String, ByteArray>,
    ) {
        rpcLogger.info(
            message("DLT Event received"),
            entry("topic", record.topic()),
            entry("key", record.key()),
            entry("partition", record.partition()),
            entry("offset", record.offset()),
            entry("timestamp", record.timestamp())
        )
    }

    internal val logger = LoggerFactory.getLogger(this::class.java)
    internal val rpcLogger = RpcLogger.create(logger)
}
