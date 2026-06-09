package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.VeiculoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.SQLException;
import java.util.List;

// Responsável pelas regras de negócio relacionadas aos veículos.
// Faz a ponte entre os controllers e o VeiculoDAO.
public class VeiculoService {

    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Cadastra um novo veículo no sistema.
    // Todo veículo novo começa como disponível para locação.
    public void salvar(Veiculo veiculo) throws SQLException {
        veiculo.setDisponivel(true);
        veiculoDAO.salvar(veiculo);

        LogUtil.registrarAcao(
                "VEICULO_CADASTRADO",
                "PLACA=" + veiculo.getPlaca()
        );
    }

    // Retorna todos os veículos cadastrados.
    // Usado para preencher a tabela da tela de veículos.
    public List<Veiculo> listarTodos() throws SQLException {
        return veiculoDAO.listarTodos();
    }

    // Retorna apenas os veículos disponíveis.
    // Usado na tela de locação para mostrar somente veículos que podem ser alugados.
    public List<Veiculo> listarDisponiveis() throws SQLException {
        return veiculoDAO.listarDisponiveis();
    }

    // Conta os veículos disponíveis.
    // Usado nos cards de resumo da tela Home.
    public int contarDisponiveis() throws SQLException {
        return veiculoDAO.contarDisponiveis();
    }

    // Busca um veículo pela placa.
    // Usado nas telas de locação, devolução e gerenciamento de veículos.
    public Veiculo buscarPorPlaca(String placa) throws SQLException {
        return veiculoDAO.buscarPorPlaca(placa);
    }

    // Atualiza os dados cadastrais de um veículo.
    public void atualizar(Veiculo veiculo) throws SQLException {
        veiculoDAO.atualizar(veiculo);

        LogUtil.registrarAcao(
                "VEICULO_ATUALIZADO",
                "ID=" + veiculo.getId() + ", PLACA=" + veiculo.getPlaca()
        );
    }

    // Atualiza a disponibilidade do veículo.
    // É usado principalmente nos processos de locação e devolução.
    public void atualizarDisponivel(boolean disponivel, int id) throws SQLException {
        veiculoDAO.atualizarDisponivel(disponivel, id);

        LogUtil.registrarAcao(
                "VEICULO_DISPONIBILIDADE_ATUALIZADA",
                "ID=" + id + ", DISPONIVEL=" + disponivel
        );
    }

    // Remove fisicamente o veículo do banco.
    // No fluxo principal do sistema, é mais seguro controlar disponibilidade do que deletar veículos.
    public void deletar(int id) throws SQLException {
        veiculoDAO.deletar(id);

        LogUtil.registrarAcao(
                "VEICULO_DELETADO",
                "ID=" + id
        );
    }
}