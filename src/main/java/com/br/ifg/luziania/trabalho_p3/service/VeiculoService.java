package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.VeiculoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.SQLException;
import java.util.List;

public class VeiculoService {
    private VeiculoDAO veiculoDAO = new VeiculoDAO();

    public void salvar(Veiculo veiculo) throws SQLException {
        veiculo.setDisponivel(true);
        veiculoDAO.salvar(veiculo);
        LogUtil.registrarAcao("VEICULO_CADASTRADO", ", PLACA=" + veiculo.getPlaca());
    }
    public List<Veiculo> listarTodos() throws SQLException {
        return veiculoDAO.listarTodos();
    }
    public List<Veiculo> listarDisponiveis() throws SQLException {
        return veiculoDAO.listarDisponiveis();
    }
    public Veiculo buscarPorPlaca(String placa) throws SQLException {
        return veiculoDAO.buscarPorPlaca(placa);
    }
    public void atualizar(Veiculo veiculo) throws SQLException {
        veiculoDAO.atualizar(veiculo);
        LogUtil.registrarAcao("VEICULO_ATUALIZADO", "ID=" + veiculo.getId() + ", PLACA=" + veiculo.getPlaca());
    }
    public void atualizarDisponivel(boolean disponivel, int id) throws SQLException {
        veiculoDAO.atualizarDisponivel(disponivel, id);

        LogUtil.registrarAcao("VEICULO_DISPONIBILIDADE_ATUALIZADA", "ID=" + id + ", DISPONIVEL=" + disponivel);
    }
    public void deletar(int id) throws SQLException {
        veiculoDAO.deletar(id);
        LogUtil.registrarAcao("VEICULO_DELETADO", "ID=" + id);
    }
}
