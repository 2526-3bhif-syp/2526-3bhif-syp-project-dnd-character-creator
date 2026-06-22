package com.dnd.creator.presenter;

import com.dnd.creator.data.DbManager;
import com.dnd.creator.view.RulesView;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class RulesPresenter {
    private final RulesView view;
    private final DbManager dbManager;

    public RulesPresenter(RulesView view) {
        this.view = view;
        this.dbManager = new DbManager();
        this.dbManager.connect();
    }
}
