package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.ClienteDAO;
import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.SQLException;
import java.util.List;

public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public void salvar(Cliente cliente) throws SQLException {
        cliente.setAtivo(true);
        clienteDAO.salvar(cliente);

        LogUtil.registrarAcao(
                "CLIENTE_CADASTRADO",
                "CPF=" + cliente.getCpf()
        );
    }

    public List<Cliente> listarTodos() throws SQLException {
        return clienteDAO.listarTodos();
    }

    public int contarAtivos() throws SQLException {
        return clienteDAO.contarAtivos();
    }

    public Cliente buscarPorCpf(String cpf) throws SQLException {
        return clienteDAO.buscarPorCpf(cpf);
    }

    public void atualizar(Cliente cliente) throws SQLException {
        clienteDAO.atualizar(cliente);

        LogUtil.registrarAcao(
                "CLIENTE_ATUALIZADO",
                "ID=" + cliente.getId() + ", CPF=" + cliente.getCpf()
        );
    }
    public void inativar(Cliente cliente) throws SQLException {
        cliente.setAtivo(false);

        clienteDAO.atualizar(cliente);

        LogUtil.registrarAcao(
                "CLIENTE_INATIVADO",
                "ID=" + cliente.getId() + ", CPF=" + cliente.getCpf()
        );
    }

    public void deletar(Cliente cliente) throws SQLException {
        clienteDAO.deletar(cliente);

        LogUtil.registrarAcao(
                "CLIENTE_DELETADO",
                "ID=" + cliente.getId() + ", CPF=" + cliente.getCpf()
        );
    }
}
