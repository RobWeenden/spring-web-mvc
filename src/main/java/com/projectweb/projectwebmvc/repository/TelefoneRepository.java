package com.projectweb.projectwebmvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projectweb.projectwebmvc.model.Telefone;



@Repository
@Transactional
public interface TelefoneRepository  extends CrudRepository<Telefone, Long>{
	
	@Query("SELECT tl FROM Telefone tl WHERE  tl.pessoa.id = ?1")
	public List<Telefone> getTelefones(Long idPessoa);
}
