package com.projectweb.projectwebmvc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementsUserDetailService serviceUser;
	
		@Override//Configura Solicitações HTTP
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf()
			.disable() //Desativação padrão de config em memoria
			.authorizeRequests() //Permiti restringir acessos
			.antMatchers(HttpMethod.GET, "/").permitAll()
			.antMatchers(HttpMethod.GET, "/cadastro").hasAnyRole("ADMIN", "GERENTE")//qualquer usuario podera acessar a pagina inicial
			.anyRequest().authenticated()
			.and().formLogin().permitAll()//permite qualquer usuario.
			.loginPage("/login")
			.defaultSuccessUrl("/cadastro")
			.failureUrl("/login?error=true")
			.and()
			.logout().logoutSuccessUrl("/login")//Mapeia URL de Sair e invalida usuario autenticado
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
		}
		@Override	//Cria autenticacao do usuario com DB ou em Memoria
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			
			auth.userDetailsService(serviceUser).passwordEncoder(new BCryptPasswordEncoder());

		}
		@Override //Ignora URLs Especificas
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/materialize/**");
		}
		
		
}
