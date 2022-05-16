package tanit.infra.streams

import tanit.infra.config.KafkaConfig

import fs2.Stream
import fs2.kafka._
import cats.effect.kernel.Async

class UserStream[F[_]: Async](kafkaConfig: KafkaConfig, topic: String) {

  private val consumerSettings: ConsumerSettings[F, String, String] = ConsumerSettings[F, String, String]
    .withAutoOffsetReset(AutoOffsetReset.Earliest)
    .withBootstrapServers(kafkaConfig.bootstrapServers.mkString(","))
    // .withProperties(properties)
    .withGroupId(kafkaConfig.groupId)

  def mkStream: Stream[F, Unit] =
    KafkaConsumer
      .stream(consumerSettings)
      .subscribeTo(topic)
      .records
      .mapAsync(2) { committable =>
        processRecord(committable.record)
      }

  private def processRecord(record: ConsumerRecord[String, String]): F[Unit] =
    Async[F].delay(println(s"Received record: ${record.value}"))
}
