package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.LocacaoDAO;
import com.br.ifg.luziania.trabalho_p3.dao.VeiculoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DevolucaoService {
    private LocacaoDAO locacaoDAO =  new LocacaoDAO();
    private VeiculoDAO veiculoDAO = new VeiculoDAO();

    public Locacao registrarDevolucao(String placa) throws SQLException{
        //1. busca a locação ativa
        Locacao locacao = locacaoDAO.buscarLocacaoAtivaPorPlaca(placa);
        if(locacao == null){
            LogUtil.registrarAcao("DEVOLUCAO_FALHOU", "Nenhuma locação encontrada. PLACA:" + placa);
            throw new IllegalArgumentException("Nenhuma locação ativa encontrada para essa placa informada.");
        }
        //2. defini a data de devolução real como hoje
        LocalDate hoje = LocalDate.now();
        locacao.setDataDevolucaoReal(hoje);
        //3. calcula dias de atraso
        long diasAtraso = ChronoUnit.DAYS.between(locacao.getDataDevolucaoPrevista(), hoje);
        //4. calcula multas se houver atraso
        double multa = 0.0;
        if(diasAtraso > 0){
            //multa = dias de atraso x valor diaria x 20%
            double valorDiaria = locacao.getValorTotal() / ChronoUnit.DAYS.between(locacao.getDataRetirada(), locacao.getDataDevolucaoPrevista());
            multa = diasAtraso * valorDiaria * 0.20;
        }
        //5. atualiza locaçaõ
        locacao.setMultas(multa);
        locacao.setStatus("ENCERRADA");
        locacaoDAO.atualizarDevolucao(locacao);
        veiculoDAO.atualizarDisponivel(true, locacao.getVeiculo().getId());
        LogUtil.registrarAcao("DEVOLUCAO_REALIZADA", "PLACA:" + placa + " MULTA: " + multa + "DATA_DEVOLUCAO" + hoje);
        return locacao;
    }
}
