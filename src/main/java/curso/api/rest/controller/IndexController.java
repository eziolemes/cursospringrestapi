package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

@RestController /* Aqui define arquitetura REST */
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /* Injeção de dependência */
	private UsuarioRepository usuarioRepository;
	
	/*Serviço RESTful*/
	@GetMapping(value = "v1/{id}", produces = "application/json")
	public ResponseEntity<Usuario> initV1(@PathVariable (value = "id") Long id) {
		
		/*o retorno seria um relatório*/
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		System.out.println("Executando Versão 1");
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "v2/{id}", produces = "application/json")
	public ResponseEntity<Usuario> initV2(@PathVariable (value = "id") Long id) {
		
		/*o retorno seria um relatório*/
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		System.out.println("Executando Versão 2");
		
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/*Vamos supor que o carregamento de usuario seja um processo lento
	 * e queremos controlar ele com cahce para agilizar o processo para simular cache*/
	@GetMapping(value = "/", produces = "application/json")
	@Cacheable("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarios () throws InterruptedException {
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		Thread.sleep(6000);
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		
		usuario.setSenha(senhaCriptografada);
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemporario = usuarioRepository.findUserByLogin(usuario.getLogin());
		
		if(!userTemporario.getSenha().equals( usuario.getSenha() )) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable (value = "id") Long id) {
		
		usuarioRepository.deleteById(id);
		
		return "ok";
	}
	
}