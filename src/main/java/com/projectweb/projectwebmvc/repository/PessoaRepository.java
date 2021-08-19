package com.projectweb.projectwebmvc.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projectweb.projectwebmvc.model.Pessoa;


@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

	@Query("SELECT obj FROM Pessoa obj WHERE  obj.nome LIKE %?1%")
	List<Pessoa> findPessoaByName(String nome);

	@Query("SELECT obj FROM Pessoa obj WHERE  obj.nome LIKE %?1% and obj.genero = ?2")
	List<Pessoa> findPessoaByNameGenero(String nome, String genero);

	@Query("SELECT obj FROM Pessoa obj WHERE  obj.genero =?1")
	List<Pessoa> findPessoaByGenero(String genero);

	default Page<Pessoa> findPessoaByNamePage(String nome, Pageable pageable) {
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);

		ExampleMatcher exm = ExampleMatcher.matchingAny().withMatcher("nome",
				ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		Example<Pessoa> expes = Example.of(pessoa, exm);

		Page<Pessoa> pessoas = findAll(expes, pageable);

		return pessoas;
	}

	default Page<Pessoa> findPessoaByNomeByGeneroPage(String nome, String genero, Pageable pageable) {
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		pessoa.setGenero(genero);

		ExampleMatcher exm = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("generoPesquisa", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		Example<Pessoa> expes = Example.of(pessoa, exm);

		Page<Pessoa> pessoas = findAll(expes, pageable);

		return pessoas;
	}

	default Page<Pessoa> findPessoaByGeneroPage(String genero, Pageable pageable) {
		Pessoa pessoa = new Pessoa();
		pessoa.setGenero(genero);

		ExampleMatcher exm = ExampleMatcher.matchingAny().withMatcher("generoPesquisa",
				ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		Example<Pessoa> expes = Example.of(pessoa, exm);

		Page<Pessoa> pessoas = findAll(expes, pageable);

		return pessoas;
	}


}
