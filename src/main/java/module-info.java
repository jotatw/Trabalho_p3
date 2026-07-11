/**
 * Declaração do módulo do Sistema de Locadora de Veículos.
 *
 * Define as dependências externas necessárias para compilar
 * e executar a aplicação, além de abrir os pacotes internos
 * para os frameworks que precisam de acesso via reflexão.
 */
module com.br.ifg.luziania.trabalho_p3 {

    // ── Dependências JavaFX ───────────────────────────────────
    // javafx.controls: TableView, Button, TextField, Label etc.
    // javafx.fxml: carregamento dos arquivos FXML via FXMLLoader.
    requires javafx.controls;
    requires javafx.fxml;

    // ── Banco de dados ────────────────────────────────────────
    // java.sql: Connection, PreparedStatement, ResultSet etc.
    // org.postgresql.jdbc: driver JDBC do PostgreSQL.
    requires java.sql;
    requires org.postgresql.jdbc;

    // ── Bibliotecas externas ──────────────────────────────────
    // org.controlsfx.controls: notificações visuais de sucesso (Notifications).
    // jbcrypt: hash e verificação de senhas com BCrypt.
    requires org.controlsfx.controls;
    requires jbcrypt;

    // ── Abertura de pacotes para reflexão ─────────────────────
    // O JavaFX usa reflexão para instanciar controllers e injetar
    // campos @FXML, por isso os pacotes precisam estar abertos.
    opens com.br.ifg.luziania.trabalho_p3 to javafx.fxml, javafx.graphics;
    opens com.br.ifg.luziania.trabalho_p3.controller to javafx.fxml;

    // O pacote model precisa ser aberto para javafx.base
    // porque o PropertyValueFactory usa reflexão para acessar
    // os getters das classes de modelo nas TableViews.
    opens com.br.ifg.luziania.trabalho_p3.model to javafx.base, javafx.fxml;

    // ── Exportação ────────────────────────────────────────────
    // Exporta o pacote principal para que o Launcher funcione
    // como ponto de entrada da aplicação.
    exports com.br.ifg.luziania.trabalho_p3;
}
