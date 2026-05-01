package com.microfinanceBank.springcloudconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Spring Cloud Config 서버 애플리케이션.
 * Git 저장소(git-localconfig-repo)에서 모든 마이크로서비스의 설정 파일을 중앙 관리한다.
 */
@SpringBootApplication
@EnableConfigServer
public class SpringCloudConfigServerApplication {

	/** Spring Cloud Config 서버 애플리케이션을 시작한다. */
	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	}

}
