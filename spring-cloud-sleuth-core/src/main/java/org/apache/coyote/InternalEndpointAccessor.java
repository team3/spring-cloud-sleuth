package org.apache.coyote;

/**
 * Internal class to access the protected scope endpoint.
 * Please, do not use this class
 *
 * @author Marcin Grzejszczak
 * @since 1.3.0
 */
final public class InternalEndpointAccessor {
	public void createExecutor(AbstractProtocol protocol) {
		protocol.getEndpoint().createExecutor();
	}
}
