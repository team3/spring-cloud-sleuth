package org.springframework.cloud.sleuth.instrument.web.client.exceptionresolver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.SpanReporter;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.sleuth.util.ArrayListSpanAccumulator;
import org.springframework.context.annotation.Bean;

/**
 * Created by mganes004c on 5/4/17.
 */
@SpringBootApplication
public class Issue585Tests {
    public static void main(String[] args) {
        SpringApplication.run(Issue585Tests.class, args);
    }

    @Bean SpanReporter foo() {
        return new ArrayListSpanAccumulator();
    }

    @Bean Sampler sampler() {
        return new AlwaysSampler();
    }
}
