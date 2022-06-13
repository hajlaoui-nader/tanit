package tanit.infra.streams

import scala.concurrent.duration._

import tanit.infra.config.KafkaConfig

import cats.effect.kernel.Async
import cats.implicits._
import fs2.Stream
import fs2.kafka._
import org.typelevel.log4cats.Logger

class UserStream[F[_]: Async: Logger](kafkaConfig: KafkaConfig, topic: String, properties: Map[String, String]) {

  private val consumerSettings: ConsumerSettings[F, String, String] = ConsumerSettings[F, String, String]
    .withAutoOffsetReset(AutoOffsetReset.Earliest)
    .withBootstrapServers(kafkaConfig.bootstrapServers.mkString(","))
    .withProperties(properties)
    .withGroupId(kafkaConfig.groupId)

  def mkStream: Stream[F, Unit] = {
    KafkaConsumer
      .stream(consumerSettings)
      .subscribeTo(topic)
      .records
      .mapAsync(2) { committable =>
        processRecord(committable.record)
          .as(committable.offset)
      }
      .through(commitBatchWithin(kafkaConfig.commitBatchWithin, 5.seconds))
  }

  private def processRecord(record: ConsumerRecord[String, String]): F[Unit] =
    Logger[F].info(s"Received record: ${record.value}")
}
