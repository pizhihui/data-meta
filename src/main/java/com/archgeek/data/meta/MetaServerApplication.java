package com.archgeek.data.meta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


@SpringBootApplication
@EnableDiscoveryClient
public class MetaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetaServerApplication.class, args);
	}

	@RestController
	class EchoController {
		@RequestMapping(value = "/api/rest_j/v1/serviceecho/{string}", method = RequestMethod.GET)
		public String echo(@PathVariable String string) throws IOException, InterruptedException {


			HttpClient client = HttpClient.newBuilder()
					.version(HttpClient.Version.HTTP_1_1)	//可以手动指定客户端的版本，如果不指定，那么默认是Http2
					.followRedirects(HttpClient.Redirect.NORMAL)	//设置重定向策略
					.connectTimeout(Duration.ofSeconds(20))	//连接超时时间
//				.proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))	//代理地址设置
//				.authenticator(Authenticator.getDefault())
					//.executor(Executors.newFixedThreadPoolExecutor(8))  //可手动配置线程池
					.build();

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://www.baidu.com/"))    //设置url地址
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());  	//同步发送
			System.out.println(response.statusCode()); 	//打印响应状态码
			System.out.println(response.body());

			return "Hello Nacos Discovery " + string;
		}
	}
}
