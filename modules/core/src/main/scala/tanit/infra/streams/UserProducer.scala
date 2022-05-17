package tanit.infra.streams

import cats.effect.kernel.Async
import tanit.infra.config.KafkaConfig
import fs2.kafka.ProducerSettings
import fs2.kafka.KafkaProducer
import java.util.UUID
import fs2.kafka.ProducerRecord
import fs2.kafka.ProducerRecords

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
        println(s"going to produce $record")
        producer.produce(ProducerRecords.one(record))
      }

}
