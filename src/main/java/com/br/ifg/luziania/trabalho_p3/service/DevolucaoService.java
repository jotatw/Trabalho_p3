package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.LocacaoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DevolucaoService {

    private final LocacaoDAO locacaoDAO = new LocacaoDAO();
    private final VeiculoService veiculoService = new VeiculoService();

    public Locacao registrarDevolucao(String placa) throws SQLException {
        Locacao locacao = locacaoDAO.buscarLocacaoAtivaPorPlaca(placa);

        if (locacao == null) {
            LogUtil.registrarAcao(
                    "DEVOLUCAO_FALHOU",
                    "Nenhuma locação ativa encontrada. PLACA=" + placa
            );
            throw new IllegalArgumentException(
                    "Nenhuma locação ativa encontrada para essa placa informada."
            );
        }

        LocalDate hoje = LocalDate.now();
        locacao.setDataDevolucaoReal(hoje);

        long diasAtraso = ChronoUnit.DAYS.between(
                locacao.getDataDevolucaoPrevista(),
                hoje
        );

        double multa = 0.0;

        if (diasAtraso > 0) {
            long diasLocacao = ChronoUnit.DAYS.between(
                    locacao.getDataRetirada(),
                    locacao.getDataDevolucaoPrevista()
            );

            if (diasLocacao <= 0) {
                LogUtil.registrarAcao(
                        "DEVOLUCAO_FALHOU",
                        "Período de locação inválido. PLACA=" + placa
                );
                throw new IllegalArgumentException("Período de locação inválido.");
            }

            double valorDiaria = locacao.getValorTotal() / diasLocacao;
            multa = diasAtraso * valorDiaria * 0.20;
        }

        locacao.setMultas(multa);
        locacao.setStatus("ENCERRADA");

        locacaoDAO.atualizarDevolucao(locacao);
        veiculoService.atualizarDisponivel(true, locacao.getVeiculo().getId());

        LogUtil.registrarAcao(
                "DEVOLUCAO_REALIZADA",
                "PLACA=" + placa +
                        ", MULTA=" + multa +
                        ", DATA_DEVOLUCAO=" + hoje
        );

        return locacao;
    }
}
