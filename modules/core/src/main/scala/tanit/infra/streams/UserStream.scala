package tanit.infra.streams

import tanit.infra.config.KafkaConfig

import cats.effect.IO
import fs2.Stream
import fs2.kafka._

class UserStream(kafkaConfig: KafkaConfig, topic: String) {

  private val consumerSettings: ConsumerSettings[IO, String, String] = ConsumerSettings[IO, String, String]
    .withAutoOffsetReset(AutoOffsetReset.Earliest)
    .withBootstrapServers(kafkaConfig.bootstrapServers.mkString(","))
    // .withProperties(properties)
    .withGroupId(kafkaConfig.groupId)

  def mkStream: Stream[IO, Unit] =
    KafkaConsumer
      .stream(consumerSettings)
      .subscribeTo(topic)
      .records
      .mapAsync(1) { committable =>
        processRecord(committable.record)
      }

  private def processRecord(record: ConsumerRecord[String, String]): IO[Unit] =
    IO(println(s"Received record: ${record.value}"))
}
