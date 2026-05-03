package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.ClienteDAO;
import com.br.ifg.luziania.trabalho_p3.dao.LocacaoDAO;
import com.br.ifg.luziania.trabalho_p3.dao.VeiculoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LocacaoService {
    private ClienteDAO clienteDAO = new ClienteDAO();
    private VeiculoDAO veiculoDAO = new VeiculoDAO();
    private LocacaoDAO locacaoDAO = new LocacaoDAO();

    public Locacao realizarLocacao(String cpf, String placa, LocalDate dataRetirada, LocalDate dataDevolucao, Usuario usuarioLogado) throws SQLException{
        //1. buscar o cliente
        Cliente cliente = clienteDAO.buscarPorCpf(cpf);
        if(cliente == null){
            throw new IllegalArgumentException("Cliente não encontrado para o CPF informado.");
        }
        //2. busca o veiculo
        Veiculo veiculo = veiculoDAO.buscarPorPlaca(placa);
        if(veiculo == null){
            throw new IllegalArgumentException("Veiculo não encontrado para o placa informada.");
        }
        //3. verificar a disponibilidade
        if(!veiculo.isDisponivel()){
            throw new IllegalArgumentException("Veiculo não esta disponivel para a locação");
        }
        //4. calcular o valor total
        long dias = ChronoUnit.DAYS.between(dataRetirada, dataDevolucao);
        if(dias <= 0){
            throw new IllegalArgumentException("Data de devolução deve ser posterior a data de retirada.");
        }
        double valorTotal = dias*veiculo.getValorLocacao();
        //5. cria e salva a locação
        Locacao locacao = new Locacao(cliente, veiculo, usuarioLogado, dataRetirada, dataDevolucao, valorTotal);
        locacaoDAO.salvar(locacao);
        LogUtil.registrar("LOCACAO_REALIZADA", usuarioLogado);
        return locacao;
    }

}
