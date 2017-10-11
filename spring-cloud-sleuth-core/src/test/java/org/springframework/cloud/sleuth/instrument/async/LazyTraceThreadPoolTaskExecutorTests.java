package org.springframework.cloud.sleuth.instrument.async;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Marcin Grzejszczak
 */
@RunWith(MockitoJUnitRunner.class)
public class LazyTraceThreadPoolTaskExecutorTests {

	@Mock Tracer tracer;
	@Mock BeanFactory beanFactory;
	ThreadPoolTaskExecutor spy;
	LazyTraceThreadPoolTaskExecutor lazyExecutor;

	@Before
	public void setup() {
		BDDMockito.given(this.beanFactory.getBean(Tracer.class)).willReturn(this.tracer);
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor() {
			@Override protected String getDefaultThreadNamePrefix() {
				return "foo";
			}
		};
		executor.initialize();
		this.spy = BDDMockito.spy(executor);
		this.lazyExecutor = new LazyTraceThreadPoolTaskExecutor(this.beanFactory, this.spy);
	}

	@After
	public void after() {
		this.spy.shutdown();
	}

	@Test public void should_call_on_delegate_method_execute() throws Exception {
		lazyExecutor.execute(() -> {});

		BDDMockito.then(this.spy).should().execute(BDDMockito.any(Runnable.class));
	}

	@Test public void should_call_on_delegate_method_execute1() throws Exception {
		lazyExecutor.execute(() -> {}, 10L);

		BDDMockito.then(this.spy).should().execute(BDDMockito.any(Runnable.class), BDDMockito.eq(10L));
	}

	@Test public void should_call_on_delegate_method_submit() throws Exception {
	}

	@Test public void should_call_on_delegate_method_submit1() throws Exception {
	}

	@Test public void should_call_on_delegate_method_submitListenable() throws Exception {
	}

	@Test public void should_call_on_delegate_method_submitListenable1() throws Exception {
	}

	@Test public void should_call_on_delegate_method_prefersShortLivedTasks() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setThreadFactory() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setThreadNamePrefix() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setRejectedExecutionHandler() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setWaitForTasksToCompleteOnShutdown() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setAwaitTerminationSeconds() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setBeanName() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getThreadPoolExecutor() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getPoolSize() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getActiveCount() throws Exception {
	}

	@Test public void should_call_on_delegate_method_destroy() throws Exception {
	}

	@Test public void should_call_on_delegate_method_afterPropertiesSet() throws Exception {
	}

	@Test public void should_call_on_delegate_method_initialize() throws Exception {
	}

	@Test public void should_call_on_delegate_method_shutdown() throws Exception {
	}

	@Test public void should_call_on_delegate_method_newThread() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getThreadNamePrefix() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setThreadPriority() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getThreadPriority() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setDaemon() throws Exception {
	}

	@Test public void should_call_on_delegate_method_isDaemon() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setThreadGroupName() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setThreadGroup() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getThreadGroup() throws Exception {
	}

	@Test public void should_call_on_delegate_method_createThread() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setCorePoolSize() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getCorePoolSize() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setMaxPoolSize() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getMaxPoolSize() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setKeepAliveSeconds() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getKeepAliveSeconds() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setQueueCapacity() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setAllowCoreThreadTimeOut() throws Exception {
	}

	@Test public void should_call_on_delegate_method_setTaskDecorator() throws Exception {
	}

	@Test public void should_call_on_delegate_method_initializeExecutor() throws Exception {
	}

	@Test public void should_call_on_delegate_method_createQueue() throws Exception {
	}

	@Test public void should_call_on_delegate_method_nextThreadName() throws Exception {
	}

	@Test public void should_call_on_delegate_method_getDefaultThreadNamePrefix() throws Exception {
	}

}