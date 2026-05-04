package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {
    //salva um novo veiculo
    public void salvar(Veiculo veiculo) throws SQLException {
        String sql = "INSERT INTO veiculo (placa, modelo, marca, categoria, valor_diaria) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getMarca());
            stmt.setString(4, veiculo.getCategoria());
            stmt.setDouble(5, veiculo.getValorLocacao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            LogUtil.registrarErro("VeiculoDAO.salvar", Sessao.getUsuarioLogado(), e);
            throw  e;
        }
    }
    //busca um veiculo a partir da placa
    public Veiculo buscarPorPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM veiculo WHERE placa = ?";

        try {
            Connection conn = DBConnection.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Veiculo v = new Veiculo();
                v.setId(rs.getInt("id"));
                v.setPlaca(rs.getString("placa"));
                v.setModelo(rs.getString("modelo"));
                v.setMarca(rs.getString("marca"));
                v.setCategoria(rs.getString("categoria"));
                v.setValorLocacao(rs.getDouble("valor_diaria"));
                v.setDisponivel(rs.getBoolean("disponivel"));
                return v;
            }
        } catch (SQLException e) {
            LogUtil.registrarErro("VeiculoDAO.buscarPorPlaca", Sessao.getUsuarioLogado(), e);
            throw  e;
        }
        return null;
    }
    public List<Veiculo> listarTodos() throws SQLException {
        String sql = "SELECT * FROM veiculo ORDER BY modelo";
        List<Veiculo> lista = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Veiculo v = new Veiculo();
                v.setId(rs.getInt("id"));
                v.setPlaca(rs.getString("placa"));
                v.setModelo(rs.getString("modelo"));
                v.setMarca(rs.getString("marca"));
                v.setCategoria(rs.getString("categoria"));
                v.setValorLocacao(rs.getDouble("valor_diaria"));
                v.setDisponivel(rs.getBoolean("disponivel"));
                lista.add(v);
            }
        } catch (SQLException e) {
            LogUtil.registrarErro("VeiculoDAO.listarTodos", Sessao.getUsuarioLogado(), e);
            throw  e;
        }
        return lista;
    }
}
