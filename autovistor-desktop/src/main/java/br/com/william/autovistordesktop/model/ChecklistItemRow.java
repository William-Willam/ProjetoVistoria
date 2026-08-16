package br.com.william.autovistordesktop.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChecklistItemRow {

    private final StringProperty nomeItem = new SimpleStringProperty();
    private final StringProperty situacao = new SimpleStringProperty("OK");
    private final StringProperty observacao = new SimpleStringProperty("");
    private final boolean editavel; // false = item da lista fixa (nome travado); true = item livre

    public ChecklistItemRow(String nomeItem, boolean editavel) {
        this.nomeItem.set(nomeItem);
        this.editavel = editavel;
    }

    public StringProperty nomeItemProperty() {
        return nomeItem;
    }

    public String getNomeItem() {
        return nomeItem.get();
    }

    public void setNomeItem(String valor) {
        nomeItem.set(valor);
    }

    public StringProperty situacaoProperty() {
        return situacao;
    }

    public String getSituacao() {
        return situacao.get();
    }

    public void setSituacao(String valor) {
        situacao.set(valor);
    }

    public StringProperty observacaoProperty() {
        return observacao;
    }

    public String getObservacao() {
        return observacao.get();
    }

    public void setObservacao(String valor) {
        observacao.set(valor);
    }

    public boolean isEditavel() {
        return editavel;
    }

    public ItemVistoriaRequest toRequest() {
        String obs = observacao.get();
        return new ItemVistoriaRequest(nomeItem.get(), situacao.get(), (obs == null || obs.isBlank()) ? null : obs);
    }
}