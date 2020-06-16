/*
 *
 *
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 *
 *
 */
package org.apache.flink.runtime.causal.log.job;

import org.apache.flink.runtime.causal.VertexID;
import org.apache.flink.runtime.causal.determinant.*;
import org.apache.flink.runtime.causal.log.vertex.VertexCausalLogDelta;
import org.apache.flink.runtime.io.network.partition.consumer.InputChannelID;
import org.apache.flink.runtime.jobgraph.IntermediateResultPartitionID;
import org.apache.flink.runtime.state.CheckpointListener;

import java.util.List;

/**
 * A CausalLog contains the determinant logs of all upstream operators and itself.
 */
public interface IJobCausalLog extends CheckpointListener {

	void setCheckpointLock(Object lock);

	void registerDownstreamConsumer(InputChannelID inputChannelID, IntermediateResultPartitionID intermediateResultPartitionID, int consumedSubpartition);

	void unregisterDownstreamConsumer(InputChannelID toCancel);

	/*
	Encodes and appends to this tasks log
	 */
	void appendDeterminant(Determinant determinant, long checkpointID);

	void appendSubpartitionDeterminants(Determinant determinant, long checkpointID, IntermediateResultPartitionID intermediateResultPartitionID, int subpartitionIndex);

	void processUpstreamVertexCausalLogDelta(VertexCausalLogDelta d, long checkpointID);

	VertexCausalLogDelta getDeterminantsOfVertex(VertexID vertexId);

	List<VertexCausalLogDelta> getNextDeterminantsForDownstream(InputChannelID inputChannelID, long checkpointID);

	DeterminantEncodingStrategy getDeterminantEncodingStrategy();

	VertexID getVertexId();


}