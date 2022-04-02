package cn.promptness.rpt.desktop.controller;

import cn.promptness.rpt.base.config.Config;
import cn.promptness.rpt.base.config.RemoteConfig;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainController {

    @FXML
    public TableView<RemoteConfig> tableView;

    public static TableView<RemoteConfig> back;

    private static final List<RemoteConfig> CONFIG = new CopyOnWriteArrayList<>();

    public static void addConfig(RemoteConfig remoteConfig) {
        CONFIG.add(remoteConfig);
        back.getItems().clear();
        back.setItems(FXCollections.observableArrayList(CONFIG));
        Config.getClientConfig().setConfig(CONFIG);
    }

    public void initialize() {
        back = tableView;
        TableColumn<RemoteConfig, String> proxyType = new TableColumn<>("传输类型");
        proxyType.setMinWidth(100);
        proxyType.setCellValueFactory(new PropertyValueFactory<>("proxyType"));

        TableColumn<RemoteConfig, String> localIp = new TableColumn<>("本地地址");
        localIp.setMinWidth(100);
        localIp.setCellValueFactory(new PropertyValueFactory<>("localIp"));

        TableColumn<RemoteConfig, Integer> localPort = new TableColumn<>("本地端口");
        localPort.setMinWidth(100);
        localPort.setCellValueFactory(new PropertyValueFactory<>("localPort"));

        TableColumn<RemoteConfig, String> domain = new TableColumn<>("暴露域名");
        domain.setMinWidth(150);
        domain.setCellValueFactory(new PropertyValueFactory<>("domain"));

        TableColumn<RemoteConfig, Integer> remotePort = new TableColumn<>("暴露端口");
        remotePort.setMinWidth(100);
        remotePort.setCellValueFactory(new PropertyValueFactory<>("remotePort"));

        TableColumn<RemoteConfig, String> description = new TableColumn<>("备注");
        description.setMinWidth(180);
        description.setCellValueFactory(new PropertyValueFactory<>("description"));

        tableView.getColumns().addAll(proxyType, localIp, localPort, domain, remotePort, description);

        tableView.setItems(FXCollections.observableArrayList(CONFIG));

        tableView.setRowFactory(param -> {
            TableRow<RemoteConfig> remoteConfigTableRow = new TableRow<>();
            remoteConfigTableRow.setOnMouseClicked(event -> {
                MouseButton button = event.getButton();
                //左键双击操作
                if (button == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    update(remoteConfigTableRow);
                }
                // 右键点击
                if (button == MouseButton.SECONDARY) {
                    remove(remoteConfigTableRow);
                }
            });
            return remoteConfigTableRow;
        });
    }

    private void update(TableRow<RemoteConfig> remoteConfigTableRow) {
        RemoteConfig remoteConfig = remoteConfigTableRow.getItem();
        if (remoteConfig == null) {
            return;
        }
        RemoteConfig result = ConfigController.buildDialog("修改", "修改映射配置", remoteConfig);
        if (result == null) {
            return;
        }
        tableView.getItems().clear();
        tableView.setItems(FXCollections.observableArrayList(CONFIG));
        Config.getClientConfig().setConfig(CONFIG);
    }

    private void remove(TableRow<RemoteConfig> remoteConfigTableRow) {
        RemoteConfig remoteConfig = remoteConfigTableRow.getItem();
        if (remoteConfig == null) {
            return;
        }
        RemoteConfig result = ConfigController.buildDialog("删除", "删除映射配置", remoteConfig);
        if (result == null) {
            return;
        }
        if (CONFIG.remove(remoteConfigTableRow.getItem())) {
            tableView.getItems().clear();
            tableView.setItems(FXCollections.observableArrayList(CONFIG));
            Config.getClientConfig().setConfig(CONFIG);
        }
    }

}
