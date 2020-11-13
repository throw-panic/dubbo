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
package com.alibaba.dubbo.common.threadpool.support.cached;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.threadlocal.NamedInternalThreadFactory;
import com.alibaba.dubbo.common.threadpool.ThreadPool;
import com.alibaba.dubbo.common.threadpool.support.AbortPolicyWithReport;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This thread pool is self-tuned. Thread will be recycled after idle for one minute, and new thread will be created for
 * the upcoming request.
 *
 * 此线程池是自调整的。线程在空闲一分钟后将被回收，并且将为即将到来的请求创建新的线程。
 *
 * @see java.util.concurrent.Executors#newCachedThreadPool()
 */


/**
 * todo:
 *      实现 ThreadPool 接口，缓存线程池（自调整线程池），空闲一定时长，自动删除，需要时重建。
 */

public class CachedThreadPool implements ThreadPool {

    @Override
    public Executor getExecutor(URL url) {
        // 线程池名
        String name = url.getParameter(Constants.THREAD_NAME_KEY, Constants.DEFAULT_THREAD_NAME);
        // 核心线程数
        int cores = url.getParameter(Constants.CORE_THREADS_KEY, Constants.DEFAULT_CORE_THREADS);
        // 最大线程数
        int threads = url.getParameter(Constants.THREADS_KEY, Integer.MAX_VALUE);
        // 队列数
        int queues = url.getParameter(Constants.QUEUES_KEY, Constants.DEFAULT_QUEUES);
        // 线程存活时长
        int alive = url.getParameter(Constants.ALIVE_KEY, Constants.DEFAULT_ALIVE);
        // 创建执行器
        return new ThreadPoolExecutor(cores, threads, alive, TimeUnit.MILLISECONDS,

                /**
                 * todo: 创建执行器：
                 *      根据不同队列数量，使用不同的队列实现；
                 *      （1）queue == 0 ：SynchronousQueue 对象；             ---> 同步队列
                 *      （2）queue < 0 ：LinkedBlockingQueue 对象；           ---> 阻塞队列
                 *      （3）queue > 0 : 带队列数的 LinkedBlockingQueue 对象。  ---> 阻塞队列
                 */
                queues == 0 ? new SynchronousQueue<Runnable>() :
                        (queues < 0 ? new LinkedBlockingQueue<Runnable>()
                                : new LinkedBlockingQueue<Runnable>(queues)),

                // 创建 NamedInternalThreadFactory 对象，用于生成线程名。
                new NamedInternalThreadFactory(name, true),
                // 创建 AbortPolicyWithReport 对象，用于当任务添加到线程池中被拒绝时。
                new AbortPolicyWithReport(name, url));
    }
}
