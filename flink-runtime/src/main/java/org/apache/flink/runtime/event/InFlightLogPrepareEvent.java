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

import org.apache.flink.core.memory.DataInputView;
import org.apache.flink.core.memory.DataOutputView;
import org.apache.flink.runtime.event.TaskEvent;

import java.io.IOException;

/**
 * Event sent from downstream for preparing in-flight tuples for specific output channel, that is subpartition index, starting from the next appointed checkpoint.
 */
public class InFlightLogPrepareEvent extends InFlightLogEvent {

	/**
	 * Default constructor (should only be used for deserialization).
	 */
	public InFlightLogPrepareEvent() {
		// default constructor implementation.
		// should only be used for deserialization
	}

	public InFlightLogPrepareEvent(int subpartitionIndex, long checkpointId) {
		super(subpartitionIndex, checkpointId);
	}

	@Override
	public String toString() {
		return String.format("InFlightLogPrepareEvent ") + super.toString();
	}
}
