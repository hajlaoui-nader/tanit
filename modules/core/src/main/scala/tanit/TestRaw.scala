package tanit

import org.slf4j.LoggerFactory

object TestRaw extends App {
  def logger = LoggerFactory.getLogger("LoggerRaw")

  logger.error("hello world")
}
