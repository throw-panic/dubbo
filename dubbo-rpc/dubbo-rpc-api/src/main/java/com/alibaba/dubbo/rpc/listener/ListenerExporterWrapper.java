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
package com.alibaba.dubbo.rpc.listener;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.ExporterListener;
import com.alibaba.dubbo.rpc.Invoker;

import java.util.List;

/**
 * ListenerExporter
 */
public class ListenerExporterWrapper<T> implements Exporter<T> {

    private static final Logger logger = LoggerFactory.getLogger(ListenerExporterWrapper.class);

    /**
     * 真实的 Exporter 对象
     */
    private final Exporter<T> exporter;

    /**
     * Exporter 监听器数组
     */
    private final List<ExporterListener> listeners;

    /**
     *   todo:
     *      ListenerExporterWrapper 对象 （创建带 ExporterListener 的 Exporter 对象）
     *
     * @param exporter
     * @param listeners
     */
    public ListenerExporterWrapper(Exporter<T> exporter, List<ExporterListener> listeners) {
        if (exporter == null) {
            throw new IllegalArgumentException("exporter == null");
        }
        this.exporter = exporter;
        this.listeners = listeners;
        // 执行监听器
        if (listeners != null && !listeners.isEmpty()) {
            RuntimeException exception = null;
            /**
             * 构造方法，循环 listeners ，执行 ExporterListener#exported(listener) 。
             * 若执行过程中发生异常 RuntimeException ，打印错误日志，继续执行，最终才抛出。
             */
            for (ExporterListener listener : listeners) {
                if (listener != null) {
                    try {
                        listener.exported(this);
                    } catch (RuntimeException t) {
                        logger.error(t.getMessage(), t);
                        exception = t;
                    }
                }
            }
            if (exception != null) {    // 最后抛出异常
                throw exception;
            }
        }
    }

    @Override
    public Invoker<T> getInvoker() {
        return exporter.getInvoker();
    }

    /**
     *  #unexport() 方法，循环 listeners ，执行 ExporterListener#unexported(listener) 。
     *  若执行过程中发生异常 RuntimeException ，打印错误日志，继续执行，最终才抛出。
     */
    @Override
    public void unexport() {
        try {
            exporter.unexport();
        } finally {
            // 执行监听器
            if (listeners != null && !listeners.isEmpty()) {
                RuntimeException exception = null;
                for (ExporterListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.unexported(this);
                        } catch (RuntimeException t) {
                            logger.error(t.getMessage(), t);
                            exception = t;
                        }
                    }
                }
                if (exception != null) {    // 最终抛出异常
                    throw exception;
                }
            }
        }
    }

}
