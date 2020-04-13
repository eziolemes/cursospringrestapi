package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;

/*Mapear url, enderecos, autoriza ou bloqueia acessos a url*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	/*Configura as solicitações de acesso por Http*/
	@Override
		protected void configure(HttpSecurity http) throws Exception {
			
			/*Ativando a proteção contra usuario que não estão validados por token*/
			http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			
			/*Ativando a permissão para acesso a página inicial do sistema sem token EX: sistema.com.br/index*/
			.disable().authorizeRequests().antMatchers("/").permitAll()
			.antMatchers("index").permitAll()
			
			/*URL de logout - Redireciona apos o user deslogar do sistema*/
			.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
			
			/*Mapeia URL de Logout e invalida o usuário*/
			.logoutRequestMatcher( new AntPathRequestMatcher("/logout"))
			
			/*Filtra as requisições de login para autenticação*/
			.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), 
					UsernamePasswordAuthenticationFilter.class)
			
			/*Filtra demais requisições para verificar a presença do TOKEN JWT no HEADER HTTP*/
			.addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
		}
	
	@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
				
			/*Service que irá consultar o usuário no banco de dados*/
			auth.userDetailsService(implementacaoUserDetailsService)
			
			/*Padrão de codificação de senha*/
			.passwordEncoder(new BCryptPasswordEncoder());
		}
}