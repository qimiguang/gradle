/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.messaging.dispatch

import spock.lang.Specification
import static org.gradle.util.Matchers.*

class ProxyDispatchAdapterTest extends Specification {
    private final Dispatch<MethodInvocation> dispatch = Mock()
    private final ProxyDispatchAdapter<ProxyTest> adapter = new ProxyDispatchAdapter<ProxyTest>(ProxyTest.class, dispatch)

    def proxyForwardsToDispatch() {
        when:
        adapter.getSource().doStuff('param')

        then:
        1 * dispatch.dispatch(new MethodInvocation(ProxyTest.class.getMethod('doStuff', String.class), ['param'] as Object[]))
    }
    
    def proxyIsEqualWhenItHasTheSameTypeAndDispatch() {
        def other = new ProxyDispatchAdapter<ProxyTest>(ProxyTest.class, dispatch)
        def differentType = new ProxyDispatchAdapter<Runnable>(Runnable.class, dispatch)
        def differentDispatch = new ProxyDispatchAdapter<ProxyTest>(ProxyTest.class, Mock(Dispatch.class))

        expect:
        ProxyTest proxy = adapter.getSource()
        strictlyEquals(proxy, other.getSource())
        proxy != differentType.getSource()
        proxy != differentDispatch.getSource()
    }
}

interface ProxyTest {
    void doStuff(String param)
}