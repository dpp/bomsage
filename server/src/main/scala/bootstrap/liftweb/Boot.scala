package bootstrap.liftweb

import net.liftweb._
import common.Full
import http.Html5Properties
import util._
import Helpers._
import common._
import http._
import mapper._
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.{Level, LoggerContext}
import ch.qos.logback.classic.joran.JoranConfigurator
import java.io.{ByteArrayInputStream, InputStream}

/** A class that's instantiated early and run. It allows the application to
  * modify lift's environment
  */
class Boot {
  def boot: Unit = {

    val xml =
      """
        |<configuration>
        |  <appender name="STDOUT" class="am.telegr.lib.PerThreadLogger">
        |    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
        |      <pattern>%d{ISO8601,UTC} [%thread] %-5level %logger{36} %X{file_name} - %msg%n</pattern>
        |    </encoder>
        |  </appender>
        |
        |  <root level="info">
        |    <appender-ref ref="STDOUT" />
        |  </root>
        |</configuration>
      """.stripMargin

    Logger.setup = Full(() => {
      val lc = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
      val configurator = new JoranConfigurator()
      configurator.setContext(lc)
      // the context was probably already configured by default configuration rules
      lc.reset()
      val is: InputStream = new ByteArrayInputStream(xml.getBytes)
      configurator.doConfigure(is)
    })

    LiftRules.supplementalHeaders.default.set(() => Nil)
    // don't polute the logs
    LiftRules.contentSecurityPolicyViolationReport = { violation =>
      Empty
    }

    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor =
        new StandardDBVendor(
          Props.get("db.driver") openOr "org.h2.Driver",
          Props.get("db.url") openOr
            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
          Props.get("db.user"),
          Props.get("db.password")
        )

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(mapper.DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    // Schemifier.schemify(true, Schemifier.infoF _, User, Domain,
    //   Site, DropboxCredentials, GitHubCredentials, SiteHistory, ExtendedSession,
    //   SiteUpdateManager, GitHubDeployKey, AccessInfo, ProcessedMail,
    //   EmailBlacklist, MailSent, Notebook,
    //   WaitListInfo)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      Html5Properties(r.userAgent)
    )

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper())

    // FIXME LiftRules.statelessDispatch.append(Site)

  }
}
