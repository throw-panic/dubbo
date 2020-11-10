/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.dubbo.rpc;

/**
 * Exporter. (API/SPI, Prototype, ThreadSafe)
 *
 * @see com.alibaba.dubbo.rpc.Protocol#export(Invoker)
 * @see com.alibaba.dubbo.rpc.ExporterListener
 * @see com.alibaba.dubbo.rpc.protocol.AbstractExporter
 */


/**
 * todo:Dubbo 处理服务暴露的关键就在 Invoker 转换到 Exporter 的过程。
 *      下面我们以 Dubbo 和 RMI 这两种典型协议的实现来进行说明：
 *
 * （1）Dubbo 的实现
 * Dubbo 协议的 Invoker 转为 Exporter 发生在 DubboProtocol 类的 export 方法，
 * 它主要是打开 socket 侦听服务，并接收客户端发来的各种请求，通讯细节由 Dubbo 自己实现。
 *
 * （2）RMI 的实现
 * RMI 协议的 Invoker 转为 Exporter 发生在 RmiProtocol 类的 export 方法，
 * 它通过 Spring 或 Dubbo 或 JDK 来实现 RMI 服务，通讯细节这一块由 JDK 底层来实现，这就省了不少工作量。
 */



public interface Exporter<T> {

    /**
     * TODO： #getInvoker() 方法，获得对应的 Invoker 。
     * get invoker.
     *
     * @return invoker
     */
    Invoker<T> getInvoker();

    /**
     * TODO: #unexport() 方法，取消暴露。
     *       Exporter 相比 Invoker 接口，多了这个方法。
     *       通过实现该方法，使相同的 Invoker 在不同的Protocol 实现的 取消暴露逻辑。
     * unexport.
     * <p>
     * <code>
     * getInvoker().destroy();
     * </code>
     */
    void unexport();

}