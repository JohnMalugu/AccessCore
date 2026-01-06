package com.jcmlabs.AccessCore.Configurations.Http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;


/**
 * <h2>Central HTTP Client Configuration</h2>
 *
 * <p>
 * This configuration class defines and manages all outbound HTTP clients used
 * by the application when communicating with external systems such as SMS,
 * Email gateways, or future third-party services.
 * </p>
 *
 * <p>
 * The goal of this class is to enforce <b>consistent, safe, and observable</b>
 * HTTP behavior across the entire application.
 * </p>
 *
 * <h3>Why this class exists</h3>
 * <ul>
 *   <li>
 *     <b>Centralization:</b> All HTTP client behavior (timeouts, headers, base
 *     settings) is defined in one place instead of being duplicated across services.
 *   </li>
 *   <li>
 *     <b>Reliability:</b> Explicit timeouts prevent thread exhaustion when an
 *     external system becomes slow or unresponsive.
 *   </li>
 *   <li>
 *     <b>Separation of Concerns:</b> Service classes focus only on business logic,
 *     not infrastructure or client creation.
 *   </li>
 *   <li>
 *     <b>Testability:</b> Clients defined as beans can be mocked or replaced
 *     during unit and integration testing.
 *   </li>
 * </ul>
 *
 * <h3>Rules of Thumb</h3>
 * <ul>
 *   <li>Never instantiate {@link RestClient} directly inside a service</li>
 *   <li>Define one {@link RestClient} per external system</li>
 *   <li>Timeouts are mandatory for all outbound HTTP calls</li>
 *   <li>Shared defaults belong here, not in business services</li>
 * </ul>
 */
@Configuration
public class HttpClientConfig {

    /**
     * <h3>SMS Provider HTTP Client</h3>
     *
     * <p>
     * This {@link RestClient} is dedicated to communication with the SMS provider.
     * It is intentionally isolated so that SMS-specific behavior (timeouts,
     * interceptors, retries, logging) can evolve independently from other clients.
     * </p>
     *
     * <p>
     * Timeouts are tuned for external SMS gateways, which are typically network-bound
     * and should fail fast if unavailable.
     * </p>
     */
    @Bean
    public RestClient smsRestClient(RestClient.Builder builder) {

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(5_000); // Max time to establish connection
        requestFactory.setReadTimeout(5_000);    // Max time waiting for response

        return builder
                .requestFactory(requestFactory)
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * <h3>Generic / Future HTTP Client</h3>
     *
     * <p>
     * This client serves as a template for future outbound integrations
     * (e.g., Email gateways, Notification services, Partner APIs).
     * </p>
     *
     * <p>
     * If a specific integration develops special requirements (custom headers,
     * longer timeouts, retries), it should receive its own dedicated bean.
     * </p>
     */
    @Bean
    public RestClient defaultRestClient(RestClient.Builder builder) {

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(3_000);
        requestFactory.setReadTimeout(3_000);

        return builder
                .requestFactory(requestFactory)
                .defaultHeader("Accept", "application/json")
                .build();
    }
}
