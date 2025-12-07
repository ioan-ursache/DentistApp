package UI;

import Config.DatabaseInitializer;
import Domain.Appointment;
import Domain.Patient;
import Repository.*;
import Repository.AppointmentRepositories.*;
import Repository.PatientRepositories.*;
import Service.AppointmentService;
import Service.PatientService;
import Service.ReportService;
import Config.Settings;
import Validation.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AppointmentGUI extends Application {
    private PatientService patientService;
    private AppointmentService appointmentService;
    private ReportService reportService;
    private UndoRedoManager undoRedoManager;
    
    // UI Components
    private TableView<Patient> patientTable;
    private TableView<Appointment> appointmentTable;
    private ListView<String> undoRedoListView;
    
    // Patient form fields
    private TextField patientIDField;
    private TextField patientNameField;
    private TextField patientEmailField;
    private TextField patientPhoneField;
    
    // Appointment form fields
    private TextField appointmentIDField;
    private ComboBox<String> patientComboBox;
    private DatePicker appointmentDatePicker;
    private Spinner<Integer> hourSpinner;
    private Spinner<Integer> minuteSpinner;
    
    // Filter fields
    private TextField patientNameFilterField;
    private TextField patientEmailFilterField;
    private TextField appointmentDateFilterField;
    private TextField appointmentPatientFilterField;
    
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public void start(Stage primaryStage) {
        // Initialize services with proper dependency injection
        initializeServices();
        undoRedoManager = new UndoRedoManager();
        
        // Set up primary stage
        primaryStage.setTitle("Appointment Management System");
        primaryStage.setWidth(1400);
        primaryStage.setHeight(900);
        
        // Create main layout
        BorderPane root = new BorderPane();
        root.setTop(createMenuBar());
        root.setCenter(createMainContent());
        root.setBottom(createStatusBar());
        
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Load initial data
        refreshPatientData();
        refreshAppointmentData();
    }
    
    /**
     * Initialize services with proper dependency injection following the existing pattern
     */
    private void initializeServices() {
        try {
            // 1. Get Settings
            String repoType = Settings.getRepositoryType();
            String pFileName = Settings.getPatients();
            String aFileName = Settings.getAppointments();
            String dbURL = Settings.getDatabaseURL();

            // 2. Initialize Repositories
            IRepository<String, Patient> pRepo;
            IRepository<String, Appointment> aRepo;

            switch (repoType.toLowerCase()) {
                case "text" -> {
                    pRepo = new PatientTextFileRepository(pFileName);
                    aRepo = new AppointmentTextFileRepository(aFileName);
                }
                case "binary" -> {
                    pRepo = new PatientBinaryFileRepository(pFileName);
                    aRepo = new AppointmentBinaryFileRepository(aFileName);
                }
                case "database" -> {
                    PatientDBRepository pdbRepo = new PatientDBRepository();
                    pdbRepo.setJDBC_URL(dbURL);
                    pRepo = pdbRepo;

                    AppointmentDBRepository adbRepo = new AppointmentDBRepository();
                    adbRepo.setJDBC_URL(dbURL);
                    aRepo = adbRepo;

                    DatabaseInitializer.initializeDatabase(dbURL);
                }
                case "xml" -> {
                    pRepo = new PatientXMLRepository(pFileName);
                    aRepo = new AppointmentXMLRepository(aFileName);
                }
                case "json" -> {
                    pRepo = new PatientJSONRepository(pFileName);
                    aRepo = new AppointmentJSONRepository(aFileName);
                }
                default -> {
                    pRepo = new PatientRepository();
                    aRepo = new AppointmentRepository();
                }
            }

            // 3. Initialize services with validators
            IValidator<Patient> patientValidator = new PatientValidator();
            IValidator<Appointment> appointmentValidator = new AppointmentValidator();

            this.patientService = new PatientService(pRepo, patientValidator);
            this.appointmentService = new AppointmentService(aRepo, appointmentValidator);
            this.reportService = new ReportService(patientService, appointmentService);
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(exitItem);
        
        // Edit menu with Undo/Redo
        Menu editMenu = new Menu("Edit");
        MenuItem undoItem = new MenuItem("Undo (Ctrl+Z)");
        undoItem.setOnAction(e -> undo());
        MenuItem redoItem = new MenuItem("Redo (Ctrl+Y)");
        redoItem.setOnAction(e -> redo());
        editMenu.getItems().addAll(undoItem, new SeparatorMenuItem(), redoItem);
        
        // Reports menu
        Menu reportsMenu = new Menu("Reports");
        MenuItem reportsItem = new MenuItem("Generate Reports");
        reportsItem.setOnAction(e -> showReportsWindow());
        reportsMenu.getItems().add(reportsItem);
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, editMenu, reportsMenu, helpMenu);
        return menuBar;
    }
    
    private VBox createMainContent() {
        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(10));
        
        // Create tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Patient management tab
        Tab patientTab = new Tab("Patients", createPatientManagementPanel());
        
        // Appointment management tab
        Tab appointmentTab = new Tab("Appointments", createAppointmentManagementPanel());
        
        // Undo/Redo history tab
        Tab historyTab = new Tab("History", createHistoryPanel());
        
        tabPane.getTabs().addAll(patientTab, appointmentTab, historyTab);
        
        mainVBox.getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        return mainVBox;
    }
    
    private VBox createPatientManagementPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        // Patient form section
        vbox.getChildren().add(new Label("Add/Edit Patient:"));
        GridPane formGrid = createPatientFormGrid();
        vbox.getChildren().add(formGrid);
        
        // Buttons section
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button addButton = new Button("Add Patient");
        addButton.setStyle("-fx-font-size: 12;");
        addButton.setOnAction(e -> addPatient());
        
        Button updateButton = new Button("Update Patient");
        updateButton.setStyle("-fx-font-size: 12;");
        updateButton.setOnAction(e -> updatePatient());
        
        Button deleteButton = new Button("Delete Patient");
        deleteButton.setStyle("-fx-font-size: 12;");
        deleteButton.setOnAction(e -> deletePatient());
        
        Button clearButton = new Button("Clear Form");
        clearButton.setStyle("-fx-font-size: 12;");
        clearButton.setOnAction(e -> clearPatientForm());
        
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
        vbox.getChildren().add(buttonBox);
        
        // Filter section
        vbox.getChildren().add(new Separator());
        vbox.getChildren().add(new Label("Filter Patients:"));
        HBox filterBox = createPatientFilterBox();
        vbox.getChildren().add(filterBox);
        
        // Patient table
        vbox.getChildren().add(new Label("Patient List:"));
        patientTable = createPatientTable();
        patientTable.setOnMouseClicked(e -> loadPatientToForm());
        vbox.getChildren().add(patientTable);
        VBox.setVgrow(patientTable, Priority.ALWAYS);
        
        return vbox;
    }
    
    private HBox createPatientFilterBox() {
        HBox filterBox = new HBox(10);
        filterBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 10;");
        
        filterBox.getChildren().add(new Label("Name:"));
        patientNameFilterField = new TextField();
        patientNameFilterField.setPromptText("Filter by name...");
        patientNameFilterField.setPrefWidth(150);
        filterBox.getChildren().add(patientNameFilterField);
        
        filterBox.getChildren().add(new Label("Email:"));
        patientEmailFilterField = new TextField();
        patientEmailFilterField.setPromptText("Filter by email domain...");
        patientEmailFilterField.setPrefWidth(150);
        filterBox.getChildren().add(patientEmailFilterField);
        
        Button filterByNameButton = new Button("Filter by Name");
        filterByNameButton.setOnAction(e -> filterPatientsByName());
        filterBox.getChildren().add(filterByNameButton);
        
        Button filterByEmailButton = new Button("Filter by Email");
        filterByEmailButton.setOnAction(e -> filterPatientsByEmail());
        filterBox.getChildren().add(filterByEmailButton);
        
        Button clearFilterButton = new Button("Show All");
        clearFilterButton.setOnAction(e -> refreshPatientData());
        filterBox.getChildren().add(clearFilterButton);
        
        return filterBox;
    }
    
    private GridPane createPatientFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        grid.add(new Label("Patient ID:"), 0, 0);
        patientIDField = new TextField();
        patientIDField.setPromptText("Enter unique ID");
        grid.add(patientIDField, 1, 0);
        
        grid.add(new Label("Name:"), 0, 1);
        patientNameField = new TextField();
        patientNameField.setPromptText("Enter patient name");
        grid.add(patientNameField, 1, 1);
        
        grid.add(new Label("Email:"), 0, 2);
        patientEmailField = new TextField();
        patientEmailField.setPromptText("Enter email address");
        grid.add(patientEmailField, 1, 2);
        
        grid.add(new Label("Phone:"), 0, 3);
        patientPhoneField = new TextField();
        patientPhoneField.setPromptText("Enter phone number");
        grid.add(patientPhoneField, 1, 3);
        
        return grid;
    }
    
    private TableView<Patient> createPatientTable() {
        TableView<Patient> table = new TableView<>();
        
        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getID()));
        idCol.setPrefWidth(80);
        
        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameCol.setPrefWidth(150);
        
        TableColumn<Patient, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        emailCol.setPrefWidth(150);
        
        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNumber()));
        phoneCol.setPrefWidth(120);
        
        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        return table;
    }
    
    private VBox createAppointmentManagementPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        // Appointment form section
        vbox.getChildren().add(new Label("Add/Edit Appointment:"));
        GridPane formGrid = createAppointmentFormGrid();
        vbox.getChildren().add(formGrid);
        
        // Buttons section
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button addButton = new Button("Add Appointment");
        addButton.setStyle("-fx-font-size: 12;");
        addButton.setOnAction(e -> addAppointment());
        
        Button updateButton = new Button("Update Appointment");
        updateButton.setStyle("-fx-font-size: 12;");
        updateButton.setOnAction(e -> updateAppointment());
        
        Button deleteButton = new Button("Delete Appointment");
        deleteButton.setStyle("-fx-font-size: 12;");
        deleteButton.setOnAction(e -> deleteAppointment());
        
        Button clearButton = new Button("Clear Form");
        clearButton.setStyle("-fx-font-size: 12;");
        clearButton.setOnAction(e -> clearAppointmentForm());
        
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton, clearButton);
        vbox.getChildren().add(buttonBox);
        
        // Filter section
        vbox.getChildren().add(new Separator());
        vbox.getChildren().add(new Label("Filter Appointments:"));
        HBox filterBox = createAppointmentFilterBox();
        vbox.getChildren().add(filterBox);
        
        // Appointment table
        vbox.getChildren().add(new Label("Appointment List:"));
        appointmentTable = createAppointmentTable();
        appointmentTable.setOnMouseClicked(e -> loadAppointmentToForm());
        vbox.getChildren().add(appointmentTable);
        VBox.setVgrow(appointmentTable, Priority.ALWAYS);
        
        return vbox;
    }
    
    private HBox createAppointmentFilterBox() {
        HBox filterBox = new HBox(10);
        filterBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-padding: 10;");
        
        filterBox.getChildren().add(new Label("Date (DD-MM-YYYY):"));
        appointmentDateFilterField = new TextField();
        appointmentDateFilterField.setPromptText("Filter by date...");
        appointmentDateFilterField.setPrefWidth(150);
        filterBox.getChildren().add(appointmentDateFilterField);
        
        filterBox.getChildren().add(new Label("Patient ID:"));
        appointmentPatientFilterField = new TextField();
        appointmentPatientFilterField.setPromptText("Filter by patient ID...");
        appointmentPatientFilterField.setPrefWidth(150);
        filterBox.getChildren().add(appointmentPatientFilterField);
        
        Button filterByDateButton = new Button("Filter by Date");
        filterByDateButton.setOnAction(e -> filterAppointmentsByDate());
        filterBox.getChildren().add(filterByDateButton);
        
        Button filterByPatientButton = new Button("Filter by Patient");
        filterByPatientButton.setOnAction(e -> filterAppointmentsByPatient());
        filterBox.getChildren().add(filterByPatientButton);
        
        Button clearFilterButton = new Button("Show All");
        clearFilterButton.setOnAction(e -> refreshAppointmentData());
        filterBox.getChildren().add(clearFilterButton);
        
        return filterBox;
    }
    
    private GridPane createAppointmentFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        
        grid.add(new Label("Appointment ID:"), 0, 0);
        appointmentIDField = new TextField();
        appointmentIDField.setPromptText("Enter unique ID");
        grid.add(appointmentIDField, 1, 0);
        
        grid.add(new Label("Patient:"), 0, 1);
        patientComboBox = new ComboBox<>();
        patientComboBox.setPromptText("Select patient");
        grid.add(patientComboBox, 1, 1);
        
        grid.add(new Label("Date:"), 0, 2);
        appointmentDatePicker = new DatePicker();
        grid.add(appointmentDatePicker, 1, 2);
        
        grid.add(new Label("Time:"), 0, 3);
        HBox timeBox = new HBox(5);
        hourSpinner = new Spinner<>(0, 23, 12);
        hourSpinner.setPrefWidth(60);
        minuteSpinner = new Spinner<>(0, 59, 0);
        minuteSpinner.setPrefWidth(60);
        timeBox.getChildren().addAll(
            new Label("Hours:"), hourSpinner,
            new Label("Minutes:"), minuteSpinner
        );
        grid.add(timeBox, 1, 3);
        
        return grid;
    }
    
    private TableView<Appointment> createAppointmentTable() {
        TableView<Appointment> table = new TableView<>();
        
        TableColumn<Appointment, String> idCol = new TableColumn<>("Appointment ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getID()));
        idCol.setPrefWidth(100);
        
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient ID");
        patientCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPatientID()));
        patientCol.setPrefWidth(100);
        
        TableColumn<Appointment, String> dateTimeCol = new TableColumn<>("Date & Time");
        dateTimeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDatetime()));
        dateTimeCol.setPrefWidth(150);
        
        table.getColumns().addAll(idCol, patientCol, dateTimeCol);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        return table;
    }
    
    private VBox createHistoryPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        vbox.getChildren().add(new Label("Operation History (Undo/Redo):"));
        
        undoRedoListView = new ListView<>();
        undoRedoListView.setPrefHeight(300);
        vbox.getChildren().add(undoRedoListView);
        VBox.setVgrow(undoRedoListView, Priority.ALWAYS);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button undoButton = new Button("Undo");
        undoButton.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        undoButton.setOnAction(e -> undo());
        
        Button redoButton = new Button("Redo");
        redoButton.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        redoButton.setOnAction(e -> redo());
        
        Button clearHistoryButton = new Button("Clear History");
        clearHistoryButton.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        clearHistoryButton.setOnAction(e -> clearHistory());
        
        buttonBox.getChildren().addAll(undoButton, redoButton, clearHistoryButton);
        vbox.getChildren().add(buttonBox);
        
        return vbox;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");
        
        Label statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);
        
        return statusBar;
    }
    
    // ===== PATIENT OPERATIONS =====
    private void addPatient() {
        String id = patientIDField.getText().trim();
        String name = patientNameField.getText().trim();
        String email = patientEmailField.getText().trim();
        String phone = patientPhoneField.getText().trim();
        
        if (id.isEmpty() || name.isEmpty()) {
            showError("Please fill in ID and Name fields");
            return;
        }
        
        try {
            // Create the new patient
            Patient newPatient = new Patient(id, name, email, phone);
            
            // Track the operation with state
            undoRedoManager.addAction("ADD", "PATIENT", 
                "Added patient: " + id, 
                null,                    // previousState: null for add
                newPatient);              // currentState: the new patient
            
            // Actually add to service
            patientService.addPatient(id, name, email, phone);
            
            refreshHistory();
            refreshPatientData();
            clearPatientForm();
            showInfo("Patient added successfully");
        } catch (Exception e) {
            showError("Error adding patient: " + e.getMessage());
        }
    }
    
    private void updatePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a patient to update");
            return;
        }
        
        String id = patientIDField.getText().trim();
        String name = patientNameField.getText().trim();
        String email = patientEmailField.getText().trim();
        String phone = patientPhoneField.getText().trim();
        
        try {
            // Save previous state before modification
            Patient previousState = new Patient(selected.getID(), selected.getName(), 
                                               selected.getEmail(), selected.getNumber());
            
            // Create new state
            Patient newState = new Patient(id, name, email, phone);
            
            // Track the operation
            undoRedoManager.addAction("UPDATE", "PATIENT",
                "Updated patient: " + id,
                previousState,            // previousState: old patient
                newState);                // currentState: updated patient
            
            // Actually update
            patientService.modifyPatient(id, name, email, phone);
            
            refreshHistory();
            refreshPatientData();
            clearPatientForm();
            showInfo("Patient updated successfully");
        } catch (Exception e) {
            showError("Error updating patient: " + e.getMessage());
        }
    }
    
    private void deletePatient() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a patient to delete");
            return;
        }
        
        if (confirmAction("Are you sure you want to delete this patient?")) {
            try {
                // Save the patient being deleted (for undo)
                Patient deletedPatient = new Patient(selected.getID(), selected.getName(),
                                                    selected.getEmail(), selected.getNumber());
                
                String patientId = selected.getID();
                
                // Track the operation
                undoRedoManager.addAction("DELETE", "PATIENT",
                    "Deleted patient: " + patientId,
                    deletedPatient,       // previousState: the deleted patient
                    null);                // currentState: null for delete
                
                // Actually delete
                patientService.deletePatient(patientId);
                
                refreshHistory();
                refreshPatientData();
                refreshAppointmentData();
                clearPatientForm();
                showInfo("Patient deleted successfully");
            } catch (Exception e) {
                showError("Error deleting patient: " + e.getMessage());
            }
        }
    }
    
    private void clearPatientForm() {
        patientIDField.clear();
        patientNameField.clear();
        patientEmailField.clear();
        patientPhoneField.clear();
    }
    
    private void loadPatientToForm() {
        Patient selected = patientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            patientIDField.setText(selected.getID());
            patientNameField.setText(selected.getName());
            patientEmailField.setText(selected.getEmail());
            patientPhoneField.setText(selected.getNumber());
        }
    }
    
    private void filterPatientsByName() {
        String keyword = patientNameFilterField.getText().trim();
        if (keyword.isEmpty()) {
            showError("Please enter a name to filter");
            return;
        }
        
        try {
            ObservableList<Patient> filtered = FXCollections.observableArrayList();
            patientService.filterByName(keyword).forEach(filtered::add);
            patientTable.setItems(filtered);
        } catch (Exception e) {
            showError("Error filtering patients: " + e.getMessage());
        }
    }
    
    private void filterPatientsByEmail() {
        String keyword = patientEmailFilterField.getText().trim();
        if (keyword.isEmpty()) {
            showError("Please enter an email domain to filter");
            return;
        }
        
        try {
            ObservableList<Patient> filtered = FXCollections.observableArrayList();
            patientService.filterByEmail(keyword).forEach(filtered::add);
            patientTable.setItems(filtered);
        } catch (Exception e) {
            showError("Error filtering patients: " + e.getMessage());
        }
    }
    
    // ===== APPOINTMENT OPERATIONS =====
    private void addAppointment() {
        String id = appointmentIDField.getText().trim();
        String patientID = patientComboBox.getValue();
        
        if (id.isEmpty() || patientID == null) {
            showError("Please fill in Appointment ID and select a Patient");
            return;
        }
        
        if (appointmentDatePicker.getValue() == null) {
            showError("Please select a date");
            return;
        }
        
        try {
            String[] dateParts = appointmentDatePicker.getValue().toString().split("-");
            String datetime = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0] + " "
                + String.format("%02d:%02d", hourSpinner.getValue(), minuteSpinner.getValue());
            
            // Create new appointment
            Appointment newAppointment = new Appointment(id, patientID, datetime);
            
            // Track the operation
            undoRedoManager.addAction("ADD", "APPOINTMENT",
                "Added appointment: " + id,
                null,                     // previousState: null for add
                newAppointment);           // currentState: the new appointment
            
            // Actually add
            appointmentService.addAppointment(id, patientID, datetime);
            
            refreshHistory();
            refreshAppointmentData();
            clearAppointmentForm();
            showInfo("Appointment added successfully");
        } catch (Exception e) {
            showError("Error adding appointment: " + e.getMessage());
        }
    }
    
    private void updateAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an appointment to update");
            return;
        }
        
        String id = appointmentIDField.getText().trim();
        String patientID = patientComboBox.getValue();
        if (patientID == null) {
            showError("Please select a patient");
            return;
        }
        
        try {
            String[] dateParts = appointmentDatePicker.getValue().toString().split("-");
            String datetime = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0] + " "
                + String.format("%02d:%02d", hourSpinner.getValue(), minuteSpinner.getValue());
            
            // Save previous state
            Appointment previousState = new Appointment(selected.getID(), selected.getPatientID(), selected.getDatetime());
            
            // Create new state
            Appointment newState = new Appointment(id, patientID, datetime);
            
            // Track the operation
            undoRedoManager.addAction("UPDATE", "APPOINTMENT",
                "Updated appointment: " + id,
                previousState,            // previousState: old appointment
                newState);                // currentState: updated appointment
            
            // Actually update
            appointmentService.modifyAppointment(id, patientID, datetime);
            
            refreshHistory();
            refreshAppointmentData();
            clearAppointmentForm();
            showInfo("Appointment updated successfully");
        } catch (Exception e) {
            showError("Error updating appointment: " + e.getMessage());
        }
    }
    
    private void deleteAppointment() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an appointment to delete");
            return;
        }
        
        if (confirmAction("Are you sure you want to delete this appointment?")) {
            try {
                // Save the appointment being deleted
                Appointment deletedAppointment = new Appointment(selected.getID(), 
                    selected.getPatientID(), selected.getDatetime());
                
                String appointmentId = selected.getID();
                
                // Track the operation
                undoRedoManager.addAction("DELETE", "APPOINTMENT",
                    "Deleted appointment: " + appointmentId,
                    deletedAppointment,   // previousState: the deleted appointment
                    null);                // currentState: null for delete
                
                // Actually delete
                appointmentService.deleteAppointment(appointmentId);
                
                refreshHistory();
                refreshAppointmentData();
                clearAppointmentForm();
                showInfo("Appointment deleted successfully");
            } catch (Exception e) {
                showError("Error deleting appointment: " + e.getMessage());
            }
        }
    }
    
    private void clearAppointmentForm() {
        appointmentIDField.clear();
        patientComboBox.setValue(null);
        appointmentDatePicker.setValue(null);
        hourSpinner.getValueFactory().setValue(12);
        minuteSpinner.getValueFactory().setValue(0);
    }
    
    private void loadAppointmentToForm() {
        Appointment selected = appointmentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            appointmentIDField.setText(selected.getID());
            patientComboBox.setValue(selected.getPatientID());
            
            // Parse datetime string
            String datetime = selected.getDatetime();
            String[] parts = datetime.split(" ");
            String[] dateParts = parts[0].split("-");
            String[] timeParts = parts[1].split(":");
            
            try {
                appointmentDatePicker.setValue(
                    LocalDate.of(
                        Integer.parseInt(dateParts[2]),
                        Integer.parseInt(dateParts[1]),
                        Integer.parseInt(dateParts[0])
                    )
                );
                hourSpinner.getValueFactory().setValue(Integer.parseInt(timeParts[0]));
                minuteSpinner.getValueFactory().setValue(Integer.parseInt(timeParts[1]));
            } catch (Exception e) {
                showError("Error parsing appointment date/time");
            }
        }
    }
    
    private void filterAppointmentsByDate() {
        String dateStr = appointmentDateFilterField.getText().trim();
        if (dateStr.isEmpty()) {
            showError("Please enter a date to filter");
            return;
        }
        
        try {
            ObservableList<Appointment> filtered = FXCollections.observableArrayList();
            appointmentService.filterByDate(dateStr).forEach(filtered::add);
            appointmentTable.setItems(filtered);
        } catch (Exception e) {
            showError("Error filtering appointments: " + e.getMessage());
        }
    }
    
    private void filterAppointmentsByPatient() {
        String patientId = appointmentPatientFilterField.getText().trim();
        if (patientId.isEmpty()) {
            showError("Please enter a patient ID to filter");
            return;
        }
        
        try {
            ObservableList<Appointment> filtered = FXCollections.observableArrayList();
            appointmentService.filterByPatient(patientId).forEach(filtered::add);
            appointmentTable.setItems(filtered);
        } catch (Exception e) {
            showError("Error filtering appointments: " + e.getMessage());
        }
    }
    
    // ===== DATA REFRESH =====
    private void refreshPatientData() {
        try {
            ObservableList<Patient> patients = FXCollections.observableArrayList();
            patientService.getAllPatients().forEach(patients::add);
            patientTable.setItems(patients);
            
            // Update patient combo box
            ObservableList<String> patientIDs = FXCollections.observableArrayList();
            for (Patient p : patients) {
                patientIDs.add(p.getID());
            }
            patientComboBox.setItems(patientIDs);
            
            // Clear filters
            patientNameFilterField.clear();
            patientEmailFilterField.clear();
        } catch (Exception e) {
            showError("Error refreshing patient data: " + e.getMessage());
        }
    }
    
    private void refreshAppointmentData() {
        try {
            ObservableList<Appointment> appointments = FXCollections.observableArrayList();
            appointmentService.getAllAppointments().forEach(appointments::add);
            appointmentTable.setItems(appointments);
            
            // Clear filters
            appointmentDateFilterField.clear();
            appointmentPatientFilterField.clear();
        } catch (Exception e) {
            showError("Error refreshing appointment data: " + e.getMessage());
        }
    }
    
    // ===== UNDO/REDO OPERATIONS =====
    private void undo() {
        UndoRedoManager.UndoRedoAction action = undoRedoManager.undo();
        if (action == null) {
            showInfo("Nothing to undo");
            return;
        }
        
        try {
            // Perform the undo action based on the operation type
            if ("ADD".equals(action.getActionType())) {
                // For ADD: delete the added item
                if ("PATIENT".equals(action.getEntityType())) {
                    Patient p = (Patient) action.getCurrentState();
                    patientService.deletePatient(p.getID());
                } else if ("APPOINTMENT".equals(action.getEntityType())) {
                    Appointment a = (Appointment) action.getCurrentState();
                    appointmentService.deleteAppointment(a.getID());
                }
            } else if ("DELETE".equals(action.getActionType())) {
                // For DELETE: re-add the deleted item
                if ("PATIENT".equals(action.getEntityType())) {
                    Patient p = (Patient) action.getPreviousState();
                    patientService.addPatient(p.getID(), p.getName(), p.getEmail(), p.getNumber());
                } else if ("APPOINTMENT".equals(action.getEntityType())) {
                    Appointment a = (Appointment) action.getPreviousState();
                    appointmentService.addAppointment(a.getID(), a.getPatientID(), a.getDatetime());
                }
            } else if ("UPDATE".equals(action.getActionType())) {
                // For UPDATE: restore to previous state
                if ("PATIENT".equals(action.getEntityType())) {
                    Patient p = (Patient) action.getPreviousState();
                    patientService.modifyPatient(p.getID(), p.getName(), p.getEmail(), p.getNumber());
                } else if ("APPOINTMENT".equals(action.getEntityType())) {
                    Appointment a = (Appointment) action.getPreviousState();
                    appointmentService.modifyAppointment(a.getID(), a.getPatientID(), a.getDatetime());
                }
            }
            
            // Refresh UI after undo
            refreshHistory();
            refreshPatientData();
            refreshAppointmentData();
            clearPatientForm();
            clearAppointmentForm();
            showInfo("Operation undone successfully");
        } catch (Exception e) {
            showError("Error undoing operation: " + e.getMessage());
            // Restore the action to undo stack if it failed
            undoRedoManager.redo();
            refreshHistory();
        }
    }
    
    private void redo() {
        UndoRedoManager.UndoRedoAction action = undoRedoManager.redo();
        if (action == null) {
            showInfo("Nothing to redo");
            return;
        }
        
        try {
            // Perform the redo action based on the operation type
            if ("ADD".equals(action.getActionType())) {
                // For ADD: re-add the item
                if ("PATIENT".equals(action.getEntityType())) {
                    Patient p = (Patient) action.getCurrentState();
                    patientService.addPatient(p.getID(), p.getName(), p.getEmail(), p.getNumber());
                } else if ("APPOINTMENT".equals(action.getEntityType())) {
                    Appointment a = (Appointment) action.getCurrentState();
                    appointmentService.addAppointment(a.getID(), a.getPatientID(), a.getDatetime());
                }
            } else if ("DELETE".equals(action.getActionType())) {
                // For DELETE: delete the item again
                if ("PATIENT".equals(action.getEntityType())) {
                    Patient p = (Patient) action.getPreviousState();
                    patientService.deletePatient(p.getID());
                } else if ("APPOINTMENT".equals(action.getEntityType())) {
                    Appointment a = (Appointment) action.getPreviousState();
                    appointmentService.deleteAppointment(a.getID());
                }
            } else if ("UPDATE".equals(action.getActionType())) {
                // For UPDATE: apply the new state
                if ("PATIENT".equals(action.getEntityType())) {
                    Patient p = (Patient) action.getCurrentState();
                    patientService.modifyPatient(p.getID(), p.getName(), p.getEmail(), p.getNumber());
                } else if ("APPOINTMENT".equals(action.getEntityType())) {
                    Appointment a = (Appointment) action.getCurrentState();
                    appointmentService.modifyAppointment(a.getID(), a.getPatientID(), a.getDatetime());
                }
            }
            
            // Refresh UI after redo
            refreshHistory();
            refreshPatientData();
            refreshAppointmentData();
            clearPatientForm();
            clearAppointmentForm();
            showInfo("Operation redone successfully");
        } catch (Exception e) {
            showError("Error redoing operation: " + e.getMessage());
            // Restore the action to redo stack if it failed
            undoRedoManager.undo();
            refreshHistory();
        }
    }
    
    private void clearHistory() {
        undoRedoManager.clear();
        refreshHistory();
        showInfo("History cleared");
    }
    
    private void refreshHistory() {
        ObservableList<String> history = FXCollections.observableArrayList(
            undoRedoManager.getHistory()
        );
        undoRedoListView.setItems(history);
    }
    
    // ===== REPORTS =====
    private void showReportsWindow() {
        Stage reportsStage = new Stage();
        reportsStage.setTitle("Reports");
        reportsStage.setWidth(800);
        reportsStage.setHeight(600);
        
        VBox mainVBox = new VBox(10);
        mainVBox.setPadding(new Insets(10));
        
        // Report selector
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<String> reportCombo = new ComboBox<>();
        reportCombo.setPromptText("Select a report...");
        
        // Get all available reports
        for (String reportName : reportService.getReports().keySet()) {
            reportCombo.getItems().add(reportName);
        }
        
        selectorBox.getChildren().add(new Label("Report:"));
        selectorBox.getChildren().add(reportCombo);
        
        // Input field for report parameters
        TextField inputField = new TextField();
        inputField.setPromptText("Enter parameter (if needed)...");
        selectorBox.getChildren().add(inputField);
        
        Button runButton = new Button("Run Report");
        selectorBox.getChildren().add(runButton);
        
        mainVBox.getChildren().add(selectorBox);
        
        // Results display
        TextArea resultsArea = new TextArea();
        resultsArea.setWrapText(true);
        resultsArea.setEditable(false);
        mainVBox.getChildren().add(resultsArea);
        VBox.setVgrow(resultsArea, Priority.ALWAYS);
        
        // Run button action
        runButton.setOnAction(e -> {
            String selectedReport = reportCombo.getValue();
            if (selectedReport == null) {
                resultsArea.setText("Please select a report.");
                return;
            }
            
            String input = inputField.getText();
            String result = reportService.runReport(selectedReport, input);
            resultsArea.setText(result);
        });
        
        // Export button
        Button exportButton = new Button("Export Report");
        exportButton.setOnAction(e -> {
            String content = resultsArea.getText();
            if (content.isEmpty()) {
                showError("No report to export. Please run a report first.");
                return;
            }
            showInfo("Report content can be copied from the text area above.");
        });
        
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.getChildren().add(exportButton);
        mainVBox.getChildren().add(bottomBox);
        
        Scene scene = new Scene(mainVBox);
        reportsStage.setScene(scene);
        reportsStage.show();
    }
    
    // ===== DIALOG UTILITIES =====
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private boolean confirmAction(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Appointment Management System");
        alert.setContentText("Version 1.0\nAssignment 5: JavaFX GUI with Undo/Redo\n\n" +
            "A comprehensive patient and appointment management system with:\n" +
            "- Full CRUD operations\n" +
            "- Filtering capabilities\n" +
            "- Report generation\n" +
            "- Undo/Redo functionality with state restoration\n" +
            "- Multiple storage backends (Database, XML, JSON, Text, Binary)");
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
