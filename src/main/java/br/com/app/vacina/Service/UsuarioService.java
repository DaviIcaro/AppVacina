package br.com.app.vacina.Service;

import java.util.List;

import br.com.app.vacina.Entities.Bebe;
import br.com.app.vacina.Exceptions.UsuarioNaoEncontradoException;

public class UsuarioService {
    private List<Bebe> bebesCadastrados;
    
    

    public UsuarioService() {
    }

    public UsuarioService(List<Bebe> bebesCadastrados) {
        this.bebesCadastrados = bebesCadastrados;
    }
    
    public Bebe realizarLogin(String cpf) throws UsuarioNaoEncontradoException {
        for (Bebe bebe : bebesCadastrados) {
            if (bebe.getCpf().equals(cpf)) {
                return bebe;
            }
        }
        throw new UsuarioNaoEncontradoException("Não foi encontrado nenhum usuário com o CPF " + cpf);
    }
}









