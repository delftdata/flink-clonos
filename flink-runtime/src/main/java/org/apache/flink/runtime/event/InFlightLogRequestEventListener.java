/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.event;

//import org.apache.flink.runtime.event.TaskEvent;
import org.apache.flink.runtime.util.event.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A listener to in-flight log requests for replay.
 */
public class InFlightLogRequestEventListener extends InFlightLogEventListener {

	private static final Logger LOG = LoggerFactory.getLogger(InFlightLogRequestEventListener.class);

	public InFlightLogRequestEventListener(ClassLoader userCodeClassLoader) {
		super(userCodeClassLoader);
	}

	/** Barrier will turn the in-flight log request signal to true */
	@Override
	public void onEvent(TaskEvent event) {
		LOG.debug("{} received event {}.", this, event);
		if (event instanceof InFlightLogRequestEvent) {
			inFlightLogEvents.add((InFlightLogRequestEvent) event);
		}
		else {
			throw new IllegalArgumentException(String.format("Unknown event type: %s.", event));
		}
	}
}