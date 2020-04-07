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
package org.apache.flink.runtime.causal;

import org.apache.flink.runtime.causal.determinant.Determinant;
import org.apache.flink.runtime.causal.determinant.DeterminantEncodingStrategy;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * Manages a causal recovery process.
 * Processes the {@link DeterminantResponseEvent} to stitch together the complete determinant history.
 * When the determinant history is obtained, it unblocks the standby execution, who will start consuming replay records using a  ForceFeederStreamInputProcessor
 * It can deserialize determinants one by one, maintaining low resource usage.
 */
public class RecoveryManager {

	private final DeterminantEncodingStrategy determinantEncodingStrategy;

	private int numDeterminantResponsesReceived;

	private int numDownstreamChannels;

	private CompletableFuture<Void> outputChannelConnectionsFuture;

	private byte[] mostCompleteDeterminantLog;


	private boolean recovering;

	private ByteBuffer mostCompleteDeterminantLogBuffer;

	private Determinant next;

	public RecoveryManager(int numDownstreamChannels, DeterminantEncodingStrategy determinantEncodingStrategy) {
		this.numDownstreamChannels = numDownstreamChannels;
		this.determinantEncodingStrategy = determinantEncodingStrategy;
		reset();
	}

	private void reset(){
		this.recovering = false;
		mostCompleteDeterminantLog = new byte[0];
		mostCompleteDeterminantLogBuffer = ByteBuffer.wrap(mostCompleteDeterminantLog);
		this.numDeterminantResponsesReceived = 0;

	}

	public boolean isReadyToStartRecovery() {
		return numDeterminantResponsesReceived == numDownstreamChannels;
	}

	public boolean isRecovering() {
		return recovering;
	}

	public void setOutputChannelConnectionsFuture(CompletableFuture<Void> outputChannelConnectionsFuture) {
		this.outputChannelConnectionsFuture = outputChannelConnectionsFuture;
	}

	public void processDeterminantResponseEvent(DeterminantResponseEvent determinantResponseEvent) {
		byte[] receivedDeterminants = determinantResponseEvent.getVertexCausalLogDelta().logDelta;
		if (mostCompleteDeterminantLog.length < receivedDeterminants.length) {
			mostCompleteDeterminantLog = receivedDeterminants;
		}

		numDeterminantResponsesReceived++;

		if (isReadyToStartRecovery()) {
			outputChannelConnectionsFuture.complete(null); //unblock future
			mostCompleteDeterminantLogBuffer = ByteBuffer.wrap(mostCompleteDeterminantLog);
			recovering = true;
			next = determinantEncodingStrategy.decodeNext(mostCompleteDeterminantLogBuffer);
		}
	}

	public Determinant popNext(){
		Determinant toReturn = next;
		next = determinantEncodingStrategy.decodeNext(mostCompleteDeterminantLogBuffer);
		if(next == null)
			reset();

		return toReturn;
	}

	public Determinant peekNext(){
		return next;
	}

	public boolean hasMoreDeterminants(){
		return next != null;
	}

}
