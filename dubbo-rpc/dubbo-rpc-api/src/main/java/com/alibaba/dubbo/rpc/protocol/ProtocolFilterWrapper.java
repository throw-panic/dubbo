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
package com.alibaba.dubbo.rpc.protocol;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;

import java.util.List;

/**
 * ListenerProtocol
 */
public class ProtocolFilterWrapper implements Protocol {

    private final Protocol protocol;

    public ProtocolFilterWrapper(Protocol protocol) {
        if (protocol == null) {
            throw new IllegalArgumentException("protocol == null");
        }
        this.protocol = protocol;
    }

    /**
     * todo:
     *      #buildInvokerChain(invoker, key, group) 方法，
     *      创建带 Filter 链的 Invoker 对象。
     *
     * @param invoker   Invoker 对象
     * @param key       key 获取 URL 参数名（该参数用于获得 ServiceConfig或 ReferenceConfig配置的自定义过滤器。）
     * @param group     group 分组 （在暴露服务时，group = provider; 在引用服务时，group = consumer ）
     * @param <T>       泛型
     * @return          Invoker 对象
     */
    private static <T> Invoker<T> buildInvokerChain(final Invoker<T> invoker, String key, String group) {
        Invoker<T> last = invoker;
        // 获得过滤器数组
        List<Filter> filters = ExtensionLoader.getExtensionLoader(Filter.class).getActivateExtension(invoker.getUrl(), key, group);
        // 倒序循环 Filter ，创建带 Filter 链的 Invoker 对象
        if (!filters.isEmpty()) {
            for (int i = filters.size() - 1; i >= 0; i--) {
                final Filter filter = filters.get(i);
                final Invoker<T> next = last;
                last = new Invoker<T>() {

                    @Override
                    public Class<T> getInterface() {
                        return invoker.getInterface();
                    }

                    @Override
                    public URL getUrl() {
                        return invoker.getUrl();
                    }

                    @Override
                    public boolean isAvailable() {
                        return invoker.isAvailable();
                    }

                    @Override
                    public Result invoke(Invocation invocation) throws RpcException {
                        return filter.invoke(next, invocation);
                    }

                    @Override
                    public void destroy() {
                        invoker.destroy();
                    }

                    @Override
                    public String toString() {
                        return invoker.toString();
                    }
                };
            }
        }
        return last;
    }

    @Override
    public int getDefaultPort() {
        return protocol.getDefaultPort();
    }

    @Override
    /**
     * 服务暴露
     */
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        // 注册中心
        // 当 invoker.url.protocl = registry ，跳过；
        // 本地暴露服务不会符合这个判断。在远程暴露服务会符合暴露该判断，所以，当为本地服务导出时，跳过判断。
        if (Constants.REGISTRY_PROTOCOL.equals(invoker.getUrl().getProtocol())) {
            // 导出服务
            // 建立带有 Filter 过滤链的 Invoker ，再暴露服务。
            return protocol.export(invoker);
        }
        /*
         * todo:
         *      调用 #buildInvokerChain(invoker, key, group) 方法，
         *      创建带有 Filter 过滤链的 Invoker 对象。
         *      再暴露服务。
         */
        return protocol.export(buildInvokerChain(invoker, Constants.SERVICE_FILTER_KEY, Constants.PROVIDER));
    }

    @Override
    /**
     * 服务引用
     */
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        // registry 本地引用还是远程引用
        // 注册中心
        if (Constants.REGISTRY_PROTOCOL.equals(url.getProtocol())) {    // 远程
            return protocol.refer(type, url);
        }
        // 引用服务，返回 Invoker 对象
        // 构建调用链：给该 Invoker 对象，包装成带有 Filter 过滤链的 Invoker 对象
        return buildInvokerChain(protocol.refer(type, url), Constants.REFERENCE_FILTER_KEY, Constants.CONSUMER);
    }

    @Override
    public void destroy() {
        protocol.destroy();
    }

}
