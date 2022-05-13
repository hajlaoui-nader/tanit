package tanit.infra.config

final case class KafkaConfig(bootstrapServers: List[String], groupId: String)

final case class AppConfig(kafka: KafkaConfig)
