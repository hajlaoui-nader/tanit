package tanit.infra.streams

import java.util.UUID

import tanit.infra.config.KafkaConfig

import cats.effect.kernel.Async
import cats.implicits._
import fs2.kafka._
import org.typelevel.log4cats.Logger

class UserProducer[F[_]: Async: Logger](kafkaConfig: KafkaConfig, topic: String) {

  private val producerSettings =
    ProducerSettings[F, String, String]
      .withBootstrapServers(kafkaConfig.bootstrapServers.mkString(","))

    
    private val stream = KafkaProducer
      .stream(producerSettings)

  def produce =
    stream
      .evalMap { producer =>
        val key    = UUID.randomUUID().toString
        val value  = UUID.randomUUID().toString
        val record = ProducerRecord(topic, key, value)
        producer.produce(ProducerRecords.one(record)) <* Logger[F].info(s"produced record with key $key")
      }
      .compile
      .drain

}
