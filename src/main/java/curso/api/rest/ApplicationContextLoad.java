package curso.api.rest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Esta classe é capaz de recuperar qualquer controller, service ou model que esta carregado na memória.
 * 
 * Classe utilizada para poder fazer a injeção de dependência da classe UsuarioRepository,
 * visto que a classe JWTTokenAutenticacaoService não permite fazer a injeção lá dentro da classe. 
 */
@Component
public class ApplicationContextLoad implements ApplicationContextAware{

	@Autowired
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}