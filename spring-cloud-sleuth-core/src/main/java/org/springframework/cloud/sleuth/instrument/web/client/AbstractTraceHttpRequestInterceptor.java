/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.sleuth.instrument.web.client;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanInjector;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.web.HttpTraceKeysInjector;
import org.springframework.cloud.sleuth.util.SpanNameUtil;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Abstraction over classes that interact with Http requests. Allows you
 * to enrich the request headers with trace related information.
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
abstract class AbstractTraceHttpRequestInterceptor {

	protected static final Log log = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	protected final Tracer tracer;
	protected final HttpSpanInjector spanInjector;
	protected final HttpTraceKeysInjector keysInjector;

	protected AbstractTraceHttpRequestInterceptor(Tracer tracer,
			HttpSpanInjector spanInjector, HttpTraceKeysInjector keysInjector) {
		this.tracer = tracer;
		this.spanInjector = spanInjector;
		this.keysInjector = keysInjector;
	}

	/**
	 * Enriches the request with proper headers and publishes
	 * the client sent event
	 */
	protected void publishStartEvent(HttpRequest request) {
		URI uri = request.getURI();
		String spanName = getName(uri);
		Span newSpan = this.tracer.createSpan(spanName);
		this.spanInjector.inject(newSpan, new HttpRequestTextMap(request));
		addRequestTags(request);
		newSpan.logEvent(Span.CLIENT_SEND);
		if (log.isDebugEnabled()) {
			log.debug("Starting new client span [" + newSpan + "]");
		}
	}

	/**
	 * Tracks the http response using proper tags
	 */
	protected void publishFinishEvent(ClientHttpResponse response) {
		addResponseTags(response);

		if (log.isDebugEnabled()) {
			log.debug("Span [" + this.tracer.getCurrentSpan() + "] finished");
		}
	}

	private String getName(URI uri) {
		// The returned name should comply with RFC 882 - Section 3.1.2.
		// i.e Header values must composed of printable ASCII values.
		return SpanNameUtil.shorten(uriScheme(uri) + ":" + uri.getRawPath());
	}

	private String uriScheme(URI uri) {
		return uri.getScheme() == null ? "http" : uri.getScheme();
	}

	/**
	 * Adds HTTP request tags to the client side span
	 */
	protected void addRequestTags(HttpRequest request) {
		this.keysInjector.addRequestTags(request.getURI().toString(),
				request.getURI().getHost(),
				request.getURI().getPath(),
				request.getMethod().name(),
				request.getHeaders());
	}

	/**
	 * Adds HTTP response tags to the client side span
	 */
	protected void addResponseTags(ClientHttpResponse response) {
		try {
			this.keysInjector.addResponseTags(this.tracer.getCurrentSpan(), response.getStatusCode());
		} catch (IOException e) {
			log.error(e);
		}
	}

	/**
	 * Close the current span and log the client received event
	 */
	public void finish() {
		if (!isTracing()) {
			return;
		}
		currentSpan().logEvent(Span.CLIENT_RECV);
		this.tracer.close(this.currentSpan());
	}

	protected Span currentSpan() {
		return this.tracer.getCurrentSpan();
	}

	protected boolean isTracing() {
		return this.tracer.isTracing();
	}

}
