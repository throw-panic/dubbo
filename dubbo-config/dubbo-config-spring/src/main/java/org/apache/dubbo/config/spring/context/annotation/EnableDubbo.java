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
package org.apache.dubbo.config.spring.context.annotation;

import org.apache.dubbo.config.AbstractConfig;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * todo:
 *   (1) 通过 @EnableDubbo 可以在指定的包名下（通过 scanBasePackages 属性），
 *       或者指定的类中（通过scanBasePackageClasses 属性）扫描Dubbo 的服务提供者（以 @Service 注解）
 *       以及 Dubbo 的服务消费者（以 @Reference 注解）。
 *   (2) 扫描到 Dubbo 的服务提供方和消费者之后，对其做相应的组装并初始化，
 *       并最终完成服务暴露或者引用的工作。
 *
 */


/**
 * Enables Dubbo components as Spring Beans, equals
 * {@link DubboComponentScan} and {@link EnableDubboConfig} combination.
 * <p>
 * Note : {@link EnableDubbo} must base on Spring Framework 4.2 and above
 *
 * @see DubboComponentScan
 * @see EnableDubboConfig
 * @since 2.5.8
 */

/**
 * @Target 说明了Annotation 所修饰的对象范围：Annotation可被用于 packages、types
 * （类、接口、枚举、Annotation类型）、类型成员（方法、构造方法、成员变量、枚举值）、方法参数
 * 和本地变量（如循环变量、catch参数）。在Annotation类型的声明中使用了target可更加明晰其修饰的目标。
 *
 * 作用：用于描述注解的使用范围（即：被描述的注解可以用在什么地方）
 * 取值(ElementType)有：
 *
 * 1.CONSTRUCTOR:用于描述构造器
 * 2.FIELD:用于描述域
 * 3.LOCAL_VARIABLE:用于描述局部变量
 * 4.METHOD:用于描述方法
 * 5.PACKAGE:用于描述包
 * 6.PARAMETER:用于描述参数
 * 7.TYPE:用于描述类、接口(包括注解类型) 或enum声明
 *
 */
@Target({ElementType.TYPE})

/**
 * @Retention 注解说明：表示这种类型的注解会被保留到具体的哪一个阶段（三个阶段）
 *  （1）RetentionPolicy.SOURCE —— 这种类型的Annotations只在源代码级别保留,编译时就会被忽略;
 *  （2）RetentionPolicy.CLASS —— 这种类型的Annotations编译时被保留,在class文件中存在,但JVM将会忽略;
 *  （3）RetentionPolicy.RUNTIME —— 这种类型的Annotations将被JVM保留,
 *      所以他们能在运行时被JVM或其他使用反射机制的代码所读取和使用.
 */
@Retention(RetentionPolicy.RUNTIME)

/**
 * 这是一个稍微复杂的注解类型. 它指明被注解的类会自动继承.
 * 更具体地说,如果定义注解时使用了 @Inherited 标记,然后用定义的注解来标注另一个父类,
 * 父类又有一个子类(subclass),则父类的所有属性将被继承到它的子类中.
 */
@Inherited
// 表明这个注解应该被 javadoc工具记录
@Documented

// todo:******************** 上面是 spring 的注解；下面是 dubbo 的注解 *******************//
/**
 *  @EnableDubbo 注解，是 @EnableDubboConfig 和 @DubboComponentScan
 *  的组合注解，使用时更加便利。
 */
@EnableDubboConfig      // 开启 Dubbo Config
@DubboComponentScan     // 扫描 Dubbo @Service 和 @Reference Bean

public @interface EnableDubbo {

    /**
     * TODO: (1) 配置 @DubboComponentScan 注解，扫描的包
     *
     * Base packages to scan for annotated @Service classes.
     * <p>
     * Use {@link #scanBasePackageClasses()} for a type-safe alternative to String-based
     * package names.
     *
     * @return the base packages to scan
     * @see DubboComponentScan#basePackages()
     */
    @AliasFor(annotation = DubboComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    /**
     * TODO: (2) 配置 @DubboComponentScan 注解，扫描的类
     *
     * Type-safe alternative to {@link #scanBasePackages()} for specifying the packages to
     * scan for annotated @Service classes. The package of each class specified will be
     * scanned.
     *
     * @return classes from the base packages to scan
     * @see DubboComponentScan#basePackageClasses
     */
    @AliasFor(annotation = DubboComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};


    /**
     *
     * TODO: (3) 配置 @EnableDubboConfig 注解，配置是否绑定到多个 Spring Bean 上
     *
     * It indicates whether {@link AbstractConfig} binding to multiple Spring Beans.
     *
     * @return the default value is <code>false</code>
     * @see EnableDubboConfig#multiple()
     */
    @AliasFor(annotation = EnableDubboConfig.class, attribute = "multiple")
    boolean multipleConfig() default true;

}
