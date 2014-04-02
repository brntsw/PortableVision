package com.br.portablevision.persistente;

import java.util.List;

import com.br.portablevision.classes.Endereco;

public interface IEnderecoDAO {
	public void insere(Endereco endereco);
	public int alterar(String nomeLocal, String endereco, String cep, int id);
	public int alterarById(Endereco endereco, int id);
	public boolean excluir(int id);
	public Endereco getEnderecoById(Integer id);
	public Endereco getEndereco(String strEndereco);
	public List<Endereco> getEnderecos();
	public Endereco getNomeLocal(String local);
	public Endereco getNomeEndereco(String strLocal, String strEndereco);
}
