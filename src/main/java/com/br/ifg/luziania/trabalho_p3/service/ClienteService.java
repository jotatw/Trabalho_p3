package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.ClienteDAO;
import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;

import java.sql.SQLException;
import java.util.List;

// Responsável pelas regras de negócio relacionadas aos clientes.
// Faz a ponte entre os controllers e o ClienteDAO.
public class ClienteService {

    private final ClienteDAO clienteDAO = new ClienteDAO();

    // Cadastra um novo cliente no sistema.
    // Todo cliente novo começa como ativo.
    public void salvar(Cliente cliente) throws SQLException {
        cliente.setAtivo(true);
        clienteDAO.salvar(cliente);

        LogUtil.registrarAcao(
                "CLIENTE_CADASTRADO",
                "CPF=" + cliente.getCpf()
        );
    }

    // Retorna todos os clientes cadastrados.
    // Usado para preencher a tabela da tela de clientes.
    public List<Cliente> listarTodos() throws SQLException {
        return clienteDAO.listarTodos();
    }

    // Conta apenas os clientes ativos.
    // Usado nos cards de resumo da tela Home.
    public int contarAtivos() throws SQLException {
        return clienteDAO.contarAtivos();
    }

    // Busca um cliente pelo CPF.
    // Usado principalmente no processo de locação.
    public Cliente buscarPorCpf(String cpf) throws SQLException {
        return clienteDAO.buscarPorCpf(cpf);
    }

    // Atualiza os dados de um cliente existente.
    public void atualizar(Cliente cliente) throws SQLException {
        clienteDAO.atualizar(cliente);

        LogUtil.registrarAcao(
                "CLIENTE_ATUALIZADO",
                "ID=" + cliente.getId() + ", CPF=" + cliente.getCpf()
        );
    }

    // Inativa um cliente sem o remover do banco.
    // Isso preserva o histórico e evita problemas com locações antigas.
    public void inativar(Cliente cliente) throws SQLException {
        cliente.setAtivo(false);
        clienteDAO.atualizar(cliente);

        LogUtil.registrarAcao(
                "CLIENTE_INATIVADO",
                "ID=" + cliente.getId() + ", CPF=" + cliente.getCpf()
        );
    }

    // Remove fisicamente o cliente do banco.
    // No fluxo principal do sistema, a inativação é preferível ao delete.
    public void deletar(Cliente cliente) throws SQLException {
        clienteDAO.deletar(cliente);

        LogUtil.registrarAcao(
                "CLIENTE_DELETADO",
                "ID=" + cliente.getId() + ", CPF=" + cliente.getCpf()
        );
    }
}