/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.documentation.spring.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

public class ObjectMapperConfigurerIntegrationTest {
  @Before
  public void setup() {
    TestObjectMapperListener.firedCount = 0;
  }

  @Test
  public void event_is_fired_when_default_rmh_is_loaded() {

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestDefaultConfig.class);

    context.getBean("defaultRmh", RequestMappingHandlerAdapter.class);

    assertEquals(TestObjectMapperListener.firedCount, 1L);
  }

  @Test
  public void event_is_fired_when_rmh_with_multiple_message_converters_is_loaded() {

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestMultipleConfig.class);

    context.getBean("multipleMCRmh", RequestMappingHandlerAdapter.class);

    assertEquals(TestObjectMapperListener.firedCount, 2L);
  }

  static class TestObjectMapperListener implements ApplicationListener<ObjectMapperConfigured> {
    static long firedCount = 0;

    @Override
    public void onApplicationEvent(ObjectMapperConfigured event) {
      firedCount++;
    }
  }


  @Configuration
  static class TestDefaultConfig {


    @Bean
    public RequestMappingHandlerAdapter defaultRmh() {
      RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
      return adapter;
    }

    @Bean
    public static ObjectMapperConfigurer objectMapperConfigurer() {
      return new ObjectMapperConfigurer();
    }

    @Bean
    public static TestObjectMapperListener listener() {
      return new TestObjectMapperListener();
    }
  }

  @Configuration
  static class TestMultipleConfig {

    @Bean
    public RequestMappingHandlerAdapter multipleMCRmh() {
      RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
      List<HttpMessageConverter<?>> messageConverters = newArrayList();
      messageConverters.add(new MappingJackson2HttpMessageConverter());
      messageConverters.add(new MappingJackson2HttpMessageConverter());
      adapter.setMessageConverters(messageConverters);
      return adapter;
    }

    @Bean
    public static ObjectMapperConfigurer objectMapperConfigurer() {
      return new ObjectMapperConfigurer();
    }

    @Bean
    public static TestObjectMapperListener listener() {
      return new TestObjectMapperListener();
    }
  }
}