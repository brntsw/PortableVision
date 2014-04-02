package com.br.portablevision.classes;

public class Validacao {
	public boolean validaCEP(String cep){
		boolean valido = true;
		String caracteresInvalidos = "abcdefghijklmnopqrstuvxwyzçáéíóúü!@#$%¨&*()/_+=§{[}]:;<,>.";
		
		for(int i = 0; i < cep.length(); i++){
			for(int j = 0; j < caracteresInvalidos.length(); j++){
				if(cep.charAt(i) == caracteresInvalidos.charAt(j)){
					valido = false;
				}
			}
		}
		
		return valido;
	}
}
