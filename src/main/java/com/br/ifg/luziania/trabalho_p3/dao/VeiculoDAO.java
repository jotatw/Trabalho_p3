package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO extends BaseDAO{
    // Salva um novo veículo no banco de dados.
    public void salvar(Veiculo veiculo) throws SQLException {
        String sql = """
            INSERT INTO veiculo (placa, modelo, marca, categoria, valor_diaria) VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getMarca());
            stmt.setString(4, veiculo.getCategoria());
            stmt.setDouble(5, veiculo.getValorLocacao());
            stmt.executeUpdate();

        } catch (SQLException e) {
            registrarErro("VeiculoDAO.salvar", e);
            throw  e;
        }
    }
    // Busca um veículo pela placa.
    public Veiculo buscarPorPlaca(String placa) throws SQLException {
        String sql = """
            SELECT id, placa, modelo, marca, categoria, valor_diaria, disponivel FROM veiculo WHERE placa = ?
            """;

        try ( Connection conn = getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, placa);
            try ( ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    return mapearVeiculo(rs);
                }
            }
        } catch (SQLException e) {
            registrarErro("VeiculoDAO.buscarPorPlaca", e);
            throw  e;
        }
        return null;
    }
    public List<Veiculo> listarTodos() throws SQLException {
        String sql = """
            SELECT id, placa, modelo, marca, categoria, valor_diaria, disponivel FROM veiculo ORDER BY modelo
            """;
        List<Veiculo> lista = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            try (ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    lista.add(mapearVeiculo(rs));
                }
            }
        }catch (SQLException e) {
            registrarErro("VeiculoDAO.listarTodos", e);
            throw  e;
        }
        return lista;
    }
    public List<Veiculo> listarDisponiveis() throws SQLException {
        String sql = """
                SELECT id, placa, modelo, marca, categoria, valor_diaria, disponivel FROM veiculo WHERE disponivel = true ORDER BY modelo
                """;
        List<Veiculo> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            try (ResultSet rs = stmt.executeQuery();) {
                while (rs.next()) {
                    lista.add(mapearVeiculo(rs));
                }
            }
        }catch (SQLException e) {
            registrarErro("VeiculoDAO.listarDisponiveis", e);
            throw e;
        }
        return lista;
    }
    public void atualizarDisponivel(boolean disponivel, int id) throws SQLException{
        String sql = """
                UPDATE veiculo SET disponivel = ? WHERE id = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setBoolean(1, disponivel);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("VeiculoDAO.atualizarDisponivel", e);
            throw e;
        }
    }
    public void atualizarDisponivel(Connection conn, boolean disponivel, int id) throws SQLException {
        String sql = """
        UPDATE veiculo
        SET disponivel = ?
        WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, disponivel);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("VeiculoDAO.atualizarDisponivelTransacao", e);
            throw e;
        }
    }
    public void atualizar(Veiculo veiculo) throws SQLException {
        String sql = """
                UPDATE veiculo SET placa = ?, modelo = ?, marca = ?, categoria = ?, valor_diaria = ? WHERE id = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getMarca());
            stmt.setString(4, veiculo.getCategoria());
            stmt.setDouble(5, veiculo.getValorLocacao());
            stmt.setInt(6, veiculo.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("VeiculoDAO.atualizar", e);
            throw e;
        }
    }
    public void deletar(int id) throws SQLException {
        String sql = """
                DELETE FROM veiculo WHERE id = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("VeiculoDAO.deletar", e);
            throw e;
        }
    }
    public int contarDisponiveis() throws SQLException {
        String sql = """
        SELECT COUNT(*) AS total
        FROM veiculo
        WHERE disponivel = true
        """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            registrarErro("VeiculoDAO.contarDisponiveis", e);
            throw e;
        }
        return 0;
    }
    private Veiculo mapearVeiculo(ResultSet rs) throws SQLException {
        Veiculo veiculo = new Veiculo();
        veiculo.setId(rs.getInt("id"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setModelo(rs.getString("modelo"));
        veiculo.setMarca(rs.getString("marca"));
        veiculo.setCategoria(rs.getString("categoria"));
        veiculo.setValorLocacao(rs.getDouble("valor_diaria"));
        veiculo.setDisponivel(rs.getBoolean("disponivel"));
        return veiculo;
    }
}
