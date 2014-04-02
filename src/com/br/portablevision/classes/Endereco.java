package com.br.portablevision.classes;

public class Endereco {
	private Integer id;
	private String endereco;
	private String nome;
	private String cep;
	
	@Override
	public String toString(){
		return nome;
	}
	
	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getId(){
		return id;
	}
	
	public void setEndereco(String endereco){
		this.endereco = endereco;
	}
	
	public String getEndereco(){
		return endereco;
	}
	
	public void setNome(String nome){
		this.nome = nome;
	}
	
	public String getNome(){
		return nome;
	}
	
	public void setCep(String cep){
		this.cep = cep;
	}
	
	public String getCep(){
		return cep;
	}
}
