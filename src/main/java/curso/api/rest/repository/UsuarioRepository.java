package curso.api.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import curso.api.rest.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String login);
	
	/*Padrão do Spring Data para trazer os dados ignorando o case sentivive
	 * para melhorar o uso do campo de pesquisa*/
	//@Query("select u from Usuario u where u.nome like %?1%")
	List<Usuario> findUserByNomeContainingIgnoreCase(String nome);
}