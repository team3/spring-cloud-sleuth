/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.cloud.sleuth.stream;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.cloud.sleuth.Span;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerPropertiesHostLocatorTests {

	public static final byte[] ADR1234 = { 1, 2, 3, 4 };

	Span span = new Span(1, 3, "http:name", 1L, Collections.<Long>emptyList(), 2L, true, true,
			"process");

	MockEnvironment mockEnvironment = new MockEnvironment();

	@Before
	public void setup() {
		this.mockEnvironment.setProperty("spring.application.name", "foo");
	}

	@Test
	public void portDefaultsTo8080() throws UnknownHostException {
		Span span = Span.builder().begin(1).end(3).name("http:name").traceId(1L).spanId(2L).remote(true).exportable(true).build();

		ServerPropertiesHostLocator locator = new ServerPropertiesHostLocator(
				new ServerProperties(), new ZipkinProperties(),
				localAddress(ADR1234));
		locator.setEnvironment(this.mockEnvironment);

		assertThat(locator.locate(span).getPort()).isEqualTo((short) 8080);
		assertThat(locator.locate(span).getServiceName()).isEqualTo("foo");
	}

	@Test
	public void shouldSetUnknownServiceWhenNoEnvironmentIsSet() throws UnknownHostException {
		Span span = Span.builder().begin(1).end(3).name("http:name").traceId(1L).spanId(2L).remote(true).exportable(true).build();

		ServerPropertiesHostLocator locator = new ServerPropertiesHostLocator(
				new ServerProperties(), new ZipkinProperties(),
				localAddress(ADR1234));

		assertThat(locator.locate(span).getServiceName()).isEqualTo("unknown");
	}

	@Test
	public void portFromServerProperties() throws UnknownHostException {
		ServerProperties properties = new ServerProperties();
		properties.setPort(1234);

		ServerPropertiesHostLocator locator = new ServerPropertiesHostLocator(properties,
				new ZipkinProperties(),localAddress(ADR1234));
		locator.setEnvironment(this.mockEnvironment);

		assertThat(locator.locate(this.span).getPort()).isEqualTo((short) 1234);
	}

	@Test
	public void portDefaultsToLocalhost() throws UnknownHostException {
		ServerPropertiesHostLocator locator = new ServerPropertiesHostLocator(
				new ServerProperties(), new ZipkinProperties(),
						localAddress(ADR1234));

		assertThat(locator.locate(this.span).getAddress()).isEqualTo("1.2.3.4");
	}

	@Test
	public void hostFromServerPropertiesIp() throws UnknownHostException {
		ServerProperties properties = new ServerProperties();
		properties.setAddress(InetAddress.getByAddress(ADR1234));

		ServerPropertiesHostLocator locator = new ServerPropertiesHostLocator(properties,
				new ZipkinProperties(),localAddress(new byte[] { 1, 1, 1, 1 }));
		locator.setEnvironment(this.mockEnvironment);

		assertThat(locator.locate(this.span).getAddress()).isEqualTo("1.2.3.4");
	}

	@Test
	public void nameTakenFromProperties() throws UnknownHostException {
		ServerProperties properties = new ServerProperties();
		properties.setAddress(InetAddress.getByAddress(new byte[] { 1, 2, 3, 4 }));
		ZipkinProperties zipkinProperties = new ZipkinProperties();
		zipkinProperties.getService().setName("bar");

		ServerPropertiesHostLocator locator = new ServerPropertiesHostLocator(properties,
				zipkinProperties,localAddress(ADR1234));
		locator.setEnvironment(this.mockEnvironment);

		assertThat(locator.locate(this.span).getServiceName()).isEqualTo("bar");
	}

	@Test
	public void negativePortFromServerProperties() throws UnknownHostException {
		ServerProperties properties = new ServerProperties();
		properties.setPort(-1);

		ServerPropertiesHostLocator locator = new ServerPropertiesHostLocator(properties,
				new ZipkinProperties(),localAddress(ADR1234));
		locator.setEnvironment(this.mockEnvironment);

		assertThat(locator.locate(this.span).getPort()).isEqualTo((short) 8080);
	}

	private InetUtils localAddress(byte[] address) throws UnknownHostException {
		InetUtils mocked = Mockito.spy(new InetUtils(new InetUtilsProperties()));
		Mockito.when(mocked.findFirstNonLoopbackAddress())
				.thenReturn(InetAddress.getByAddress(address));
		return mocked;
	}
}
