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

package org.gradle.messaging.dispatch;

import org.gradle.util.UncheckedException;
import org.slf4j.Logger;

public class ExceptionTrackingDispatch<T> implements StoppableDispatch<T> {
    private final Dispatch<T> dispatch;
    private final Logger logger;
    private Throwable failure;

    public ExceptionTrackingDispatch(Dispatch<T> dispatch, Logger logger) {
        this.dispatch = dispatch;
        this.logger = logger;
    }

    public void dispatch(T message) {
        try {
            dispatch.dispatch(message);
        } catch (Throwable t) {
            if (failure != null) {
                logger.error(String.format("Failed to dispatch message %s.", message), t);
            } else {
                failure = t;
            }
        }
    }

    public void stop() {
        if (failure != null) {
            try {
                throw UncheckedException.asRuntimeException(failure);
            } finally {
                failure = null;
            }
        }
    }
}
