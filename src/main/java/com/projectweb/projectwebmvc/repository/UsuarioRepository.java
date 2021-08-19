package com.projectweb.projectwebmvc.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.projectweb.projectwebmvc.model.Usuario;



@Repository
@Transactional
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{
	
	@Query("SELECT obj FROM Usuario obj WHERE obj.login = ?1")
	Usuario findUserByLogin(String login);
}
