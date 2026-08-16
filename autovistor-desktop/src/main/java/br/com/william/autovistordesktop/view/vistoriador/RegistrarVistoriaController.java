package br.com.william.autovistordesktop.view.vistoriador;

import br.com.william.autovistordesktop.client.ApiClient;
import br.com.william.autovistordesktop.client.ApiException;
import br.com.william.autovistordesktop.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RegistrarVistoriaController {

    @FXML private Label labelTitulo;
    @FXML private ComboBox<String> comboResultado;
    @FXML private TextArea campoObservacoes;
    @FXML private TableView<ChecklistItemRow> tabelaChecklist;
    @FXML private TableColumn<ChecklistItemRow, String> colunaNomeItem;
    @FXML private TableColumn<ChecklistItemRow, String> colunaSituacao;
    @FXML private TableColumn<ChecklistItemRow, String> colunaObservacao;
    @FXML private Button botaoAdicionarItem;
    @FXML private Button botaoRemoverItem;
    @FXML private Button botaoAdicionarFoto;
    @FXML private Label labelFotosSelecionadas;
    @FXML private ComboBox<String> comboFormaPagamento;
    @FXML private TextField campoValor;
    @FXML private Button botaoCancelar;
    @FXML private Button botaoSalvar;

    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<ChecklistItemRow> itensChecklist = FXCollections.observableArrayList();
    private final List<File> fotosSelecionadas = new ArrayList<>();
    private AgendamentoResponse agendamento;
    private boolean salvouComSucesso = false;

    private static final List<String> ITENS_FIXOS = List.of(
            "Pneus", "Freios", "Motor", "Lataria", "Vidros", "Luzes", "Suspensão", "Painel/Elétrica"
    );

    public void inicializar(AgendamentoResponse agendamento) {
        this.agendamento = agendamento;
        labelTitulo.setText("Registrar Vistoria — Agendamento #" + agendamento.getId());
    }

    @FXML
    private void initialize() {
        comboResultado.setItems(FXCollections.observableArrayList("APROVADO", "REPROVADO", "APROVADO_COM_RESSALVAS"));
        comboResultado.setValue("APROVADO");

        comboFormaPagamento.setItems(FXCollections.observableArrayList("PIX", "DEBITO", "CREDITO", "BOLETO", "DINHEIRO"));
        comboFormaPagamento.setValue("PIX");

        configurarTabelaChecklist();
        ITENS_FIXOS.forEach(nome -> itensChecklist.add(new ChecklistItemRow(nome, false)));
        tabelaChecklist.setItems(itensChecklist);

        tabelaChecklist.getSelectionModel().selectedItemProperty().addListener(
                (obs, antigo, novo) -> botaoRemoverItem.setDisable(novo == null || !novo.isEditavel()));

        botaoAdicionarItem.setOnAction(e -> itensChecklist.add(new ChecklistItemRow("Novo item", true)));
        botaoRemoverItem.setOnAction(e -> removerItemSelecionado());
        botaoAdicionarFoto.setOnAction(e -> selecionarFotos());
        botaoCancelar.setOnAction(e -> fechar());
        botaoSalvar.setOnAction(e -> salvar());
    }

    private void configurarTabelaChecklist() {
        tabelaChecklist.setEditable(true);

        colunaNomeItem.setCellValueFactory(c -> c.getValue().nomeItemProperty());
        colunaNomeItem.setCellFactory(col -> new TextFieldTableCell<>(new javafx.util.converter.DefaultStringConverter()) {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                ChecklistItemRow row = getTableRow() != null ? (ChecklistItemRow) getTableRow().getItem() : null;
                setEditable(row != null && row.isEditavel());
            }
        });
        colunaNomeItem.setOnEditCommit(e -> e.getRowValue().setNomeItem(e.getNewValue()));

        colunaSituacao.setCellValueFactory(c -> c.getValue().situacaoProperty());
        colunaSituacao.setCellFactory(ChoiceBoxTableCell.forTableColumn("OK", "AVARIA"));
        colunaSituacao.setOnEditCommit(e -> e.getRowValue().setSituacao(e.getNewValue()));

        colunaObservacao.setCellValueFactory(c -> c.getValue().observacaoProperty());
        colunaObservacao.setCellFactory(TextFieldTableCell.forTableColumn());
        colunaObservacao.setOnEditCommit(e -> e.getRowValue().setObservacao(e.getNewValue()));
    }

    private void removerItemSelecionado() {
        ChecklistItemRow selecionado = tabelaChecklist.getSelectionModel().getSelectedItem();
        if (selecionado == null) return;

        if (!selecionado.isEditavel()) {
            new Alert(Alert.AlertType.WARNING, "Itens do checklist padrão não podem ser removidos.").showAndWait();
            return;
        }

        itensChecklist.remove(selecionado);
    }

    private void selecionarFotos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar fotos da vistoria");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens", "*.jpg", "*.jpeg", "*.png"));

        List<File> selecionados = fileChooser.showOpenMultipleDialog(botaoAdicionarFoto.getScene().getWindow());
        if (selecionados != null) {
            fotosSelecionadas.addAll(selecionados);
            labelFotosSelecionadas.setText(fotosSelecionadas.size() + " foto(s) selecionada(s)");
        }
    }

    private void salvar() {
        String observacoes = campoObservacoes.getText();
        if (observacoes == null || observacoes.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Observações gerais são obrigatórias.").showAndWait();
            return;
        }

        BigDecimal valor;
        try {
            valor = new BigDecimal(campoValor.getText().replace(",", "."));
        } catch (Exception ex) {
            new Alert(Alert.AlertType.WARNING, "Informe um valor de pagamento válido.").showAndWait();
            return;
        }

        botaoSalvar.setDisable(true);

        try {
            List<ItemVistoriaRequest> itensRequest = itensChecklist.stream()
                    .map(ChecklistItemRow::toRequest)
                    .toList();

            VistoriaCadastroRequest requestVistoria = new VistoriaCadastroRequest(
                    comboResultado.getValue(), observacoes, itensRequest);

            VistoriaResponse vistoria = apiClient.post(
                    "/agendamentos/" + agendamento.getId() + "/vistoria",
                    requestVistoria, VistoriaResponse.class, true);

            for (File foto : fotosSelecionadas) {
                apiClient.postMultipart(
                        "/agendamentos/" + agendamento.getId() + "/vistoria/" + vistoria.getId() + "/fotos",
                        foto, null);
            }

            PagamentoCadastroRequest requestPagamento = new PagamentoCadastroRequest(
                    comboFormaPagamento.getValue(), valor);
            apiClient.post("/vistorias/" + vistoria.getId() + "/pagamento",
                    requestPagamento, PagamentoResponse.class, true);

            apiClient.post("/vistorias/" + vistoria.getId() + "/laudo", null, Void.class, true);

            salvouComSucesso = true;
            new Alert(Alert.AlertType.INFORMATION, "Vistoria, pagamento e laudo registrados com sucesso!").showAndWait();
            fechar();

        } catch (ApiException ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        } finally {
            botaoSalvar.setDisable(false);
        }
    }

    private void fechar() {
        ((Stage) botaoCancelar.getScene().getWindow()).close();
    }

    public boolean isSalvouComSucesso() {
        return salvouComSucesso;
    }
}