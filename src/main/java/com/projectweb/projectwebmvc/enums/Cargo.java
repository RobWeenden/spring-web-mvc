package com.projectweb.projectwebmvc.enums;

public enum Cargo {
	JUNIOR("Júnior"), PLENO("Pleno"), SENIOR("Sênior"), ESTAGIO("Estágio");

	private String descricao;

	private Cargo(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
