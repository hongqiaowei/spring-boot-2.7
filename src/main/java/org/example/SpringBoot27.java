/*
 *  Copyright (C) 2023 Hong Qiaowei <hongqiaowei@163.com>. All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.example;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jReactiveRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration;
import org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.neo4j.Neo4jAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration;
import org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.reactive.WebSocketReactiveAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Hong Qiaowei
 */
@SpringBootApplication(
        // 去掉：
        // 1 不需要的
        // 2 因为自定义，所以覆盖的
        // spring boot自动配置
        exclude = {
                EmbeddedLdapAutoConfiguration.class,
                LdapAutoConfiguration.class,
                LdapRepositoriesAutoConfiguration.class,
                JndiConnectionFactoryAutoConfiguration.class,
                JndiDataSourceAutoConfiguration.class,

                WebServicesAutoConfiguration.class,
                WebServiceTemplateAutoConfiguration.class,

                WebSocketMessagingAutoConfiguration.class,
                WebSocketReactiveAutoConfiguration.class,

                JerseyAutoConfiguration.class,
                RestTemplateAutoConfiguration.class,

                HypermediaAutoConfiguration.class,
                MustacheAutoConfiguration.class,
                ThymeleafAutoConfiguration.class,
                FreeMarkerAutoConfiguration.class,

                GsonAutoConfiguration.class,

                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,

                ErrorWebFluxAutoConfiguration.class,
                ReactiveSecurityAutoConfiguration.class,
                ReactiveUserDetailsServiceAutoConfiguration.class,

                RSocketMessagingAutoConfiguration.class,
                RSocketRequesterAutoConfiguration.class,
                RSocketSecurityAutoConfiguration.class,
                RSocketServerAutoConfiguration.class,
                RSocketStrategiesAutoConfiguration.class,

                RepositoryRestMvcAutoConfiguration.class,
                SpringDataWebAutoConfiguration.class,

                JndiDataSourceAutoConfiguration.class,
                XADataSourceAutoConfiguration.class,
                H2ConsoleAutoConfiguration.class,
                JdbcTemplateAutoConfiguration.class,
                JdbcRepositoriesAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                JtaAutoConfiguration.class,

                R2dbcAutoConfiguration.class,
                R2dbcDataAutoConfiguration.class,
                R2dbcRepositoriesAutoConfiguration.class,
                R2dbcTransactionManagerAutoConfiguration.class,

                FlywayAutoConfiguration.class,
                InfluxDbAutoConfiguration.class,
                LiquibaseAutoConfiguration.class,

                JooqAutoConfiguration.class,

                MongoAutoConfiguration.class,
                EmbeddedMongoAutoConfiguration.class,
                MongoReactiveAutoConfiguration.class,
                MongoDataAutoConfiguration.class,
                MongoRepositoriesAutoConfiguration.class,
                MongoReactiveDataAutoConfiguration.class,

                RedisRepositoriesAutoConfiguration.class,

                CouchbaseAutoConfiguration.class,
                CouchbaseReactiveDataAutoConfiguration.class,

                CassandraAutoConfiguration.class,
                CassandraReactiveDataAutoConfiguration.class,

                SolrAutoConfiguration.class,
                ElasticsearchDataAutoConfiguration.class,
                ElasticsearchRepositoriesAutoConfiguration.class,

                JmsAutoConfiguration.class,
                ActiveMQAutoConfiguration.class,
                KafkaAutoConfiguration.class,
                ArtemisAutoConfiguration.class,
                RabbitAutoConfiguration.class,

                MailSenderAutoConfiguration.class,
                MailSenderValidatorAutoConfiguration.class,

                Neo4jAutoConfiguration.class,
                Neo4jDataAutoConfiguration.class,
                Neo4jReactiveDataAutoConfiguration.class,
                Neo4jReactiveRepositoriesAutoConfiguration.class,

                HazelcastAutoConfiguration.class,
                HazelcastJpaDependencyAutoConfiguration.class,

                CacheAutoConfiguration.class,
                BatchAutoConfiguration.class,
                IntegrationAutoConfiguration.class,

                JmxAutoConfiguration.class,
                SpringApplicationAdminJmxAutoConfiguration.class,

                OAuth2ClientAutoConfiguration.class,
                ReactiveOAuth2ClientAutoConfiguration.class,
                ReactiveOAuth2ResourceServerAutoConfiguration.class,
                QuartzAutoConfiguration.class,

                TaskExecutionAutoConfiguration.class,
                TaskSchedulingAutoConfiguration.class
        },
    scanBasePackages = {SpringBoot27.ROOT_PKG}
)
public class SpringBoot27 {

    private static final   Logger  LOGGER           = LoggerFactory.getLogger(SpringBoot27.class);

//  private static final   String  configFiles      = "--spring.config.location=classpath:/application.yml --logging.config=classpath:/log4j2-spring.xml";

    public  static final   String  ROOT_PKG         = "org.example";

    public  static         ConfigurableApplicationContext APPLICATION_CONTEXT;

    public static void main(String[] args) {

        System.setProperty("log4j2.contextSelector",               "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("log4j2.formatMsgNoLookups",            "true");
        System.setProperty("log4j2.isThreadContextMapInheritable", "true");

        List<String> argList = Stream.of(args).collect(Collectors.toList());
        String[] runOpts = argList.toArray(new String[0]);

        SpringApplication sr = new SpringApplication(SpringBoot27.class);
        sr.setWebApplicationType(WebApplicationType.SERVLET);
        sr.setBannerMode(Banner.Mode.OFF);
        APPLICATION_CONTEXT = sr.run(runOpts);

        ConfigurableEnvironment env = APPLICATION_CONTEXT.getEnvironment();
        String applicationName  = env.getProperty("spring.application.name");
        String[] activeProfiles = env.getActiveProfiles();

        LOGGER.info(applicationName +  " main args: " + argList + ", active profiles: " + StringUtils.join(activeProfiles, ','));
    }

    /**
     * 在ApplicationContext初始化或刷新后，打印Environment
     * @param event
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        final MutablePropertySources mutablePropertySources = ((AbstractEnvironment) env).getPropertySources();

        StringBuilder b = new StringBuilder();
        b.append(System.lineSeparator()).append("\n====== env properties ======");

        StreamSupport.stream  (mutablePropertySources.spliterator(), false)
                     .filter  (ps -> ps instanceof EnumerablePropertySource)
                     .map     (ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                     .flatMap (Arrays::stream)
                     .distinct()
                     .filter  (prop -> !(prop.contains("credentials") || prop.contains("password")))
                     .forEach (prop -> { b.append(System.lineSeparator()).append(prop).append(": ").append(env.getProperty(prop)); });

        b.append(System.lineSeparator()).append("====== env properties ======\n");
        LOGGER.info(b.toString());
    }
}
