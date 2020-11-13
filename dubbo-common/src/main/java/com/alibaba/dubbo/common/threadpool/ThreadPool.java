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
package com.alibaba.dubbo.common.threadpool;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.Adaptive;
import com.alibaba.dubbo.common.extension.SPI;

import java.util.concurrent.Executor;

/**
 * ThreadPool
 *
 * 线程池接口
 */



/**
 *  todo:
 *       Dubbo 提供了三种线程池的实现：(ThreadPool)
 *      (1) fixed 固定大小线程池，启动时建立线程，不关闭，一直持有。(缺省)
 *      (2) cached 缓存线程池（自调整线程池），空闲一分钟自动删除，需要时重建。
 *      (3) limited 可伸缩线程池，但池中的线程数只会增长不会收缩。
 *          只增长不收缩的目的是为了避免收缩时突然来了大流量引起的性能问题。
 *      =========================== 后来有了第四种 ==========================
 *      （4）eager 优先创建Worker线程池。在任务数量大于corePoolSize但是小于maximumPoolSize时，
 *          优先创建Worker来处理任务。当任务数量大于maximumPoolSize时，将任务放入阻塞队列中。
 *          阻塞队列充满时抛出RejectedExecutionException。
 *          (相比于cached:cached在任务数量超过maximumPoolSize时直接抛出异常而不是将任务放入阻塞队列)
 *
 */



@SPI("fixed")                   // Dubbo SPI 拓展点
public interface ThreadPool {

    /**
     * Thread pool
     *
     * @param url URL contains thread parameter
     * @return thread pool
     */
    @Adaptive({Constants.THREADPOOL_KEY})   //  基于 Dubbo SPI Adaptive 机制，加载对应的线程池实现，
                                            // 使用 URL.threadpool 属性
    Executor getExecutor(URL url);  // 获得对应的线程池的执行器。

}