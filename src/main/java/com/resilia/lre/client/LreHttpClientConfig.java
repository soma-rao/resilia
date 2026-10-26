package com.resilia.lre.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
public class LreHttpClientConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public HttpClient lrePooledHttpClient() {
        try {
            // Trust manager that accepts all certs for enterprise self-signed LRE endpoints
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .connectTimeout(Duration.ofSeconds(10))
                    .executor(Executors.newFixedThreadPool(20))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize LRE HttpClient", e);
        }
    }

    @Bean
    public RestClient.Builder lreRestClientBuilder(HttpClient lrePooledHttpClient) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(lrePooledHttpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(60));

        return RestClient.builder()
                .requestFactory(requestFactory);
    }
}