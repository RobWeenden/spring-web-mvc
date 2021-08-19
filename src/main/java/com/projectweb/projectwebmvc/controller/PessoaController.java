package com.projectweb.projectwebmvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.projectweb.projectwebmvc.model.Pessoa;
import com.projectweb.projectwebmvc.model.Telefone;
import com.projectweb.projectwebmvc.repository.PessoaRepository;
import com.projectweb.projectwebmvc.repository.ProfissaoRepository;
import com.projectweb.projectwebmvc.repository.TelefoneRepository;
import com.projectweb.projectwebmvc.service.ReportUtil;


@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private TelefoneRepository telefoneRepository;

	@Autowired
	private ProfissaoRepository profissaoRepository;

	@Autowired
	private ReportUtil reportUtil;

	@RequestMapping(method = RequestMethod.GET, value = "**/cadastro")
	public ModelAndView telaInicial() {

		ModelAndView andView = new ModelAndView("cadastro/cadastro-pessoa");
		andView.addObject("objPessoa", new Pessoa());
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("profissoes", profissaoRepository.findAll());
		return andView;
	}

	@GetMapping("/paginas-cadastros")
	public ModelAndView paginacaoCadastro(@PageableDefault(size = 5) Pageable pageable, ModelAndView model,
			@RequestParam("nomePesquisa") String nomePesquisa) {

		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomePesquisa, pageable);
		model.addObject("pessoas", pagePessoa);
		model.addObject("objPessoa", new Pessoa());
		model.addObject("nomePesquisa", nomePesquisa);
		model.setViewName("cadastro/cadastro-pessoa");
		model.addObject("profissoes", profissaoRepository.findAll());
		return model;

	}

	@RequestMapping(method = RequestMethod.POST, value = "**/salvar-cadastro", consumes = { "multipart/form-data" })
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file)
			throws IOException {
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		if (bindingResult.hasErrors()) {
			ModelAndView andView = new ModelAndView("cadastro/cadastro-pessoa");
			andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			andView.addObject("objPessoa", pessoa);

			List<String> msg = new ArrayList<>();

			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage());
			}
			andView.addObject("msg", msg);
			andView.addObject("profissoes", profissaoRepository.findAll());
			return andView;
		}
		if (file.getSize() > 0) {
			pessoa.setArquivo(file.getBytes());
			pessoa.setTipoArquivo(file.getContentType());
			pessoa.setNameArquivo(file.getOriginalFilename());

		} else if (pessoa.getId() != null && pessoa.getId() > 0) {
			Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
			pessoa.setArquivo(pessoaTemp.getArquivo());
			pessoa.setTipoArquivo(pessoaTemp.getTipoArquivo());
			pessoa.setNameArquivo(pessoaTemp.getNameArquivo());
		}

		pessoaRepository.save(pessoa);

		ModelAndView andView = new ModelAndView("cadastro/cadastro-pessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("objPessoa", new Pessoa());

		return andView;
	}

	@RequestMapping(method = RequestMethod.GET, value = "**/listar-cadastro")
	public ModelAndView listar() {
		ModelAndView andView = new ModelAndView("cadastro/cadastro-pessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("objPessoa", new Pessoa());

		return andView;
	}

	@GetMapping("**/editar-cadastro/{id}")
	public ModelAndView editar(@PathVariable("id") Long id) {

		Optional<Pessoa> pessoa = pessoaRepository.findById(id);
		ModelAndView andView = new ModelAndView("cadastro/cadastro-pessoa");
		andView.addObject("objPessoa", pessoa.get());
		andView.addObject("profissoes", profissaoRepository.findAll());

		return andView;
	}

	@GetMapping("**/remover-cadastro/{id}")
	public ModelAndView remover(@PathVariable("id") Long id) {
		pessoaRepository.deleteById(id);
		ModelAndView andView = new ModelAndView("cadastro/cadastro-pessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("objPessoa", new Pessoa());

		return andView;
	}

	@PostMapping("**/pesquisa-cadastro")
	public ModelAndView pesquisar(@RequestParam("nomePesquisa") String nomePesquisa,
			@RequestParam("generoPesquisa") String generoPesquisa,
			@PageableDefault(size = 5, sort = { "nome" }) Pageable pageable) {

		Page<Pessoa> pessoas = null;

		if (generoPesquisa != null && !generoPesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNomeByGeneroPage(nomePesquisa, generoPesquisa, pageable);
		} else if (nomePesquisa != null && nomePesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByGeneroPage(generoPesquisa, pageable);
		} else {
			pessoas = pessoaRepository.findPessoaByNamePage(nomePesquisa, pageable);
		}

		ModelAndView andView = new ModelAndView("cadastro/cadastro-pessoa");
		andView.addObject("pessoas", pessoas);
		andView.addObject("objPessoa", new Pessoa());
		andView.addObject("nomePesquisa", nomePesquisa);
		andView.addObject("profissoes", profissaoRepository.findAll());

		return andView;
	}

	@GetMapping("**/telefones-cadastro/{id}")
	public ModelAndView telefonesCadastro(@PathVariable("id") Long id) {

		Optional<Pessoa> pessoa = pessoaRepository.findById(id);
		ModelAndView andView = new ModelAndView("cadastro/cadastro-telefones");
		andView.addObject("objPessoa", pessoa.get());
		andView.addObject("telefones", telefoneRepository.getTelefones(id));
		return andView;
	}

	@PostMapping("**/addfonePessoa/{id}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("id") Long id) {

		Pessoa pessoa = pessoaRepository.findById(id).get();
		if (telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {

			ModelAndView andView = new ModelAndView("cadastro/cadastro-telefones");
			andView.addObject("objPessoa", pessoa);
			andView.addObject("telefones", telefoneRepository.getTelefones(id));

			List<String> msg = new ArrayList<>();
			if (telefone.getNumero().isEmpty()) {
				msg.add("Numero de Telefone deve ser informado");

			}
			if (telefone.getTipo().isEmpty()) {
				msg.add("Tipo de Telefone deve ser informado");
			}
			andView.addObject("msg", msg);
			return andView;
		}

		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		ModelAndView andView = new ModelAndView("cadastro/cadastro-telefones");
		andView.addObject("objPessoa", pessoa);
		andView.addObject("telefones", telefoneRepository.getTelefones(id));
		return andView;
	}

	@GetMapping("**/remover-telefone/{id}")
	public ModelAndView removerTelefone(@PathVariable("id") Long id) {
		Pessoa pessoa = telefoneRepository.findById(id).get().getPessoa();
		telefoneRepository.deleteById(id);
		ModelAndView andView = new ModelAndView("cadastro/cadastro-telefones");
		andView.addObject("objPessoa", pessoa);
		andView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));

		return andView;
	}

	@GetMapping("**/pesquisa-cadastro")
	public void imprimiRelatorio(@RequestParam("nomePesquisa") String nomePesquisa,
			@RequestParam("generoPesquisa") String generoPesquisa, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		if (generoPesquisa != null && !generoPesquisa.isEmpty() && nomePesquisa != null && !nomePesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameGenero(nomePesquisa, generoPesquisa);

		} else if (nomePesquisa != null && !nomePesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByName(generoPesquisa);

		} else if (generoPesquisa != null && !generoPesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByGenero(generoPesquisa);

		} else {
			for (Pessoa pessoa : pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome")))) {
				pessoas.add(pessoa);
			}
		}
		byte[] relatorio = reportUtil.gerarRelatorio(pessoas, "relatorios", request.getServletContext());
		response.setContentLength(relatorio.length);
		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attchment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		response.getOutputStream().write(relatorio);

	}

	@GetMapping("**/download-arquivo/{id}")
	public void downloadArquivo(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
		Pessoa pessoa = pessoaRepository.findById(id).get();
		if (pessoa.getArquivo() != null) {
			response.setContentLength(pessoa.getArquivo().length);
			response.setContentType(pessoa.getTipoArquivo());// GENERIC : application/octet-stream
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNameArquivo());
			response.setHeader(headerKey, headerValue);
			response.getOutputStream().write(pessoa.getArquivo());

		}
	}

}
