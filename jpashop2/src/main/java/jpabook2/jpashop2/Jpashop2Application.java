package jpabook2.jpashop2;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Jpashop2Application {

	public static void main(String[] args) {

//		Hello hello = new Hello();
//		hello.setData("test");
//		String data = hello.getData();
//		System.out.println(data);

		SpringApplication.run(Jpashop2Application.class, args);
	}

	@Bean
	Hibernate5Module hibernate5Module(){
		// 기본적으로 지연로딩이면 무시하는데, 다음과 같이 옵션을 주면 json생성시점에 로딩을 한다.
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING,true);
		return hibernate5Module;
	}


}
