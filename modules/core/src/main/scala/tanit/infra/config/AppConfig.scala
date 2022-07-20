package tanit.infra.config

final case class KafkaConfig(bootstrapServers: List[String], groupId: String, commitBatchWithin: Int)

final case class OpensearchConfig(host: String, port: Int)

final case class AppConfig(kafka: KafkaConfig, opensearch: OpensearchConfig)
