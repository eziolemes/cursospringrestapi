package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	/*Tempo de validade do Token 2 dias*/
	private static final long EXPIRATION_TIME = 172800000;

	/*Uma senha única para compor a autenticação e ajudar na segurança, 
	 * pode ser uma senha de alto nível de segurança, com caracteres especiais*/
	private static final String SECRET = "SenhaExtremamenteSecreta";

	/*Prefixo padrão de Token*/
	private static final String TOKEN_PREFIX = "Bearer";

	private static final String HEADER_STRING = "Authorization";

	/*Gerando token de autenticação e adicionando ao cabeçalho e resposta Http*/
	public void addAutenthication(HttpServletResponse response, String username) throws IOException {

		/*Montagem do Token*/
		String JWT = Jwts.builder() /*Chama o gerador de Token*/
				.setSubject(username) /*Adiciona o usuario*/
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) /*Tempo de expiração*/
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Compactação e algoritmo de geração de senha*/

		/*Junta o token com o prefixo*/
		String token = TOKEN_PREFIX + " " + JWT;

		/*Adiciona no cabeçalho Http*/
		response.addHeader(HEADER_STRING, token);

		/*Escreve token como resposta no corpo Http*/
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");

	}

	/*Retorna o usuário validado com token ou caso não seja válido, retorna null*/
	public Authentication getAuthentication(HttpServletRequest request) {

		/*Pega o token enviado no cabeçalho http*/
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {

			/*Faz a validação do token do usuário na requisição*/
			String user = Jwts.parser().setSigningKey(SECRET) /*Bearer 8we7r98q7r9q7r97qer9q7er9q7e*/
					.parseClaimsJws(token.replace(TOKEN_PREFIX, "")) /* 8we7r98q7r9q7r97qer9q7er9q7e*/
					.getBody().getSubject(); /*Nome do usuário*/

			if(user != null) {
				/*                recuperar qualquer controller, service ou model que esta carregado na memória.*/
				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).findUserByLogin(user);

				if(usuario != null) {

					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(),
							usuario.getSenha(),
							usuario.getAuthorities());

				} 

			} 

		}

		return null; /*Não autorizado*/

	}

}