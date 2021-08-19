package com.projectweb.projectwebmvc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projectweb.projectwebmvc.model.Profissao;



@Repository
@Transactional
public interface ProfissaoRepository extends CrudRepository<Profissao, Long>{
	

}
