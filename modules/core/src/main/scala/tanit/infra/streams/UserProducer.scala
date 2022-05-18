package tanit.infra.streams

import java.util.UUID

import tanit.infra.config.KafkaConfig

import cats.effect.kernel.Async
import fs2.kafka._

class UserProducer[F[_]: Async](kafkaConfig: KafkaConfig, topic: String) {

  private val producerSettings =
    ProducerSettings[F, String, String]
      .withBootstrapServers(kafkaConfig.bootstrapServers.mkString(","))

  def produce =
    KafkaProducer
      .stream(producerSettings)
      .evalMap { producer =>
        val key    = UUID.randomUUID().toString
        val value  = UUID.randomUUID().toString
        val record = ProducerRecord(topic, key, value)
        producer.produce(ProducerRecords.one(record))
      }

}
