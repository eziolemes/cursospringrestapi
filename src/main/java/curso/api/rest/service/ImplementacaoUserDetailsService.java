package curso.api.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

@Service
public class ImplementacaoUserDetailsService implements UserDetailsService{

	@Autowired
	private UsuarioRepository usuarioRepository;

//	@Autowired
//	private JdbcTemplate jdbcTemplate;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		/*Consultar o usuario no banco*/
		Usuario usuario = usuarioRepository.findUserByLogin(username);

		if(usuario == null) {
			throw new UsernameNotFoundException("Usuário não foi encontrado");
		}

		return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
	}

//	public void insereAcessoPadrao(Long id) {
//		/*1 - descobrir o nome da constraint para remover*/
//		String constraint = usuarioRepository.consultaConstraintRole();
//
//		if(constraint != null) {
//			/*2 - delete na constraint*/
//			jdbcTemplate.execute("alter table usuarios_role drop constraint " + constraint);
//		}
//
//		/*3 - inserir os acessos padrão para o usuario novo cadastrado*/
//		usuarioRepository.insereAcessoRolePadrao(id);
//	}

}