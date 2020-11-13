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

package com.alibaba.dubbo.common.threadpool.support.eager;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.threadlocal.NamedInternalThreadFactory;
import com.alibaba.dubbo.common.threadpool.ThreadPool;
import com.alibaba.dubbo.common.threadpool.support.AbortPolicyWithReport;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * EagerThreadPool
 * When the core threads are all in busy,
 * create new thread instead of putting task into blocking queue.
 *
 *  当核心线程都很忙时，创建新线程，而不是将任务放入阻塞队列。
 *
 */

/**
 * todo:
 *      eager 优先创建Worker线程池。在任务数量大于corePoolSize 但是小于 maximumPoolSize 时，
 *      优先创建Worker 来处理任务。当任务数量大于maximumPoolSize 时，将任务放入阻塞队列中。
 *      阻塞队列充满时抛出RejectedExecutionException。
 *      (相比于cached:cached在任务数量超过maximumPoolSize 时直接抛出异常而不是将任务放入阻塞队列)
 *
 *      corePoolSize：    核心线程池数量
 *      maximumPoolSize： 最大线程池数量
 */
public class EagerThreadPool implements ThreadPool {

    @Override
    public Executor getExecutor(URL url) {
        // 线程池名称
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        // 核心线程数
        int cores = url.getParameter(Constants.CORE_THREADS_KEY, Constants.DEFAULT_CORE_THREADS);
        // 最大线程数
        int threads = url.getParameter(Constants.THREADS_KEY, Integer.MAX_VALUE);
        // 队列数
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
        // 线程存活时间长度
        int alive = url.getParameter(Constants.ALIVE_KEY, Constants.DEFAULT_ALIVE);

        // init queue and executor
        // 初始化任务队列
        TaskQueue<Runnable> taskQueue = new TaskQueue<Runnable>(queues <= 0 ? 1 : queues);

        // 创建线程执行器
        EagerThreadPoolExecutor executor = new EagerThreadPoolExecutor(cores,
                threads,
                alive,
                TimeUnit.MILLISECONDS,
                taskQueue,  // 任务队列 ---> 是在不行（ > maximumPoolSize 再扔到阻塞队列）
                // 创建 NamedInternalThreadFactory 对象，用于生成线程名。
                new NamedInternalThreadFactory(name, true),
                // 创建 AbortPolicyWithReport 对象，用于当任务添加到线程池中被拒绝时。
                new AbortPolicyWithReport(name, url));
        taskQueue.setExecutor(executor);
        return executor;
    }
}
