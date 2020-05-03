package curso.api.rest.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import curso.api.rest.repository.TelefoneRepository;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDetailsService;

@RestController /* Aqui define arquitetura REST */
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /* Injeção de dependência */
	private UsuarioRepository usuarioRepository;
	
	@Autowired /* Injeção de dependência */
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
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
	@GetMapping(value = "v1/", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuariosCache () throws InterruptedException {
		
		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
		Thread.sleep(6000);
		
		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
	}
	
	@GetMapping(value = "/", produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuarios () {
//		public ResponseEntity<List<Usuario>> usuarios () {
		
		//criar paginação para performance, do registro 0 listando 5
		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		
		Page<Usuario> list = usuarioRepository.findAll(page);
		
		//listagem normal com todos os registros
//		List<Usuario> list = (List<Usuario>) usuarioRepository.findAll();
		
//		return new ResponseEntity<List<Usuario>>(list, HttpStatus.OK);
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuarioPagina (@PathVariable("pagina") int pagina) {
		
		//criar paginação para performance, do registro 0 listando 5
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		
		Page<Usuario> list = usuarioRepository.findAll(page);
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuarioPorNome (@PathVariable(value="nome") String nome) {
		
		PageRequest page = null;
		Page<Usuario> list = null;
		
		if(nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			page = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(page);
		} else {
			page = PageRequest.of(0, 5, Sort.by("nome"));
			list = (Page<Usuario>) usuarioRepository.findUserByNamePageContainingIgnoreCase(nome, page);
		}
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	public ResponseEntity<Page<Usuario>> usuarioPorNomePage (@PathVariable(value="nome") String nome, @PathVariable("page") int page) {
		
		PageRequest pageRequest = null;
		Page<Usuario> list = null;
		
		if(nome == null || (nome != null && nome.trim().isEmpty()) || nome.equalsIgnoreCase("undefined")) {
			pageRequest = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageRequest);
		} else {
			pageRequest = PageRequest.of(0, 5, Sort.by("nome"));
			list = (Page<Usuario>) usuarioRepository.findUserByNamePageContainingIgnoreCase(nome, pageRequest);
		}
		
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}
	
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario usuario) { //@Valid é para o BeanValidator do CPF
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		usuario.setRole(2L); //Nível de acesso de código 2.
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		
		usuario.setSenha(senhaCriptografada);
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
//		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());
		
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@Valid @RequestBody Usuario usuario) { //@Valid é para o BeanValidator do CPF
		
		for(int pos = 0; pos < usuario.getTelefones().size(); pos ++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemporario = usuarioRepository.findById(usuario.getId()).get();
		
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
	
	@DeleteMapping(value="/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		
		telefoneRepository.deleteById(id);
		
		return "ok";
	}
	
}