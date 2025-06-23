package view.dentistPanel;

import javax.swing.*;
import java.awt.*;

public class DentistExaminationPanel extends JPanel {

    private JLabel lblTitle, lblName, lblAge, lblSymptom, lblDiagnosis, lblTreatment;
    private JTextField tfName, tfAge, tfSymptom, tfDiagnosis, tfTreatment;
    private JRadioButton rbFromPrescription;
    private JButton btnCancel, btnAddService;
    private JPanel contentPanel;
    private int idPrescription;
    private String namePatient;
    private String sdtPatient;// dùng để tìm ra bệnh nhân đó
    private String symptom;
    private String diagnosis;
    private String treatment;
    public DentistExaminationPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        // Tiêu đề
        lblTitle = new JLabel("   Triệu chứng - chuẩn đoán");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(new Color(51, 51, 255));
        lblTitle.setPreferredSize(new Dimension(95, 30));
        lblTitle.setBackground(Color.WHITE);
        add(lblTitle, BorderLayout.PAGE_START);

        // Content Panel
        contentPanel = new JPanel();
        contentPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        GroupLayout layout = new GroupLayout(contentPanel);
        contentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        contentPanel.setBackground(Color.WHITE);
        layout.setAutoCreateContainerGaps(true);

        // Thành phần giao diện
        lblName = new JLabel("Họ tên:");
        tfName = new JTextField("Ngô Minh Khôi");
        tfName.setOpaque(true);
        tfName.setBackground(Color.WHITE);
        tfName.setEditable(false);

        lblAge = new JLabel("Tuổi:");
        tfAge = new JTextField("20");
        tfAge.setOpaque(true);
        tfAge.setBackground(Color.WHITE);
        tfAge.setEditable(false);

        rbFromPrescription = new JRadioButton("Điền từ đơn thuốc");
        rbFromPrescription.setBackground(Color.WHITE);
        rbFromPrescription.setOpaque(true);

        lblSymptom = new JLabel("Triệu chứng:");
        tfSymptom = new JTextField();
        tfSymptom.setPreferredSize(new Dimension(100, 30));
        tfSymptom.setOpaque(false);
        tfSymptom.setBackground(Color.WHITE);

        lblDiagnosis = new JLabel("Chuẩn đoán:");
        tfDiagnosis = new JTextField();
        tfDiagnosis.setPreferredSize(new Dimension(100, 30));
        tfDiagnosis.setOpaque(true);
        tfDiagnosis.setBackground(Color.WHITE);

        lblTreatment = new JLabel("Phương pháp điều trị:");
        tfTreatment = new JTextField();
        tfTreatment.setPreferredSize(new Dimension(100, 30));

        btnAddService = new JButton("Thêm dịch vụ");
        btnAddService.setBackground(new Color(51, 255, 51));
        btnAddService.setForeground(Color.WHITE);
        btnAddService.setFont(new Font("Arial", Font.BOLD, 12));

        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(153, 153, 153));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 12));

        // Tạo nhóm ngang chính
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        GroupLayout.ParallelGroup hParallel = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        // Thông tin bệnh nhân
        GroupLayout.SequentialGroup rowInfoGroup = layout.createSequentialGroup()
                .addComponent(lblName)
                .addGap(20)
                .addComponent(tfName, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                .addGap(30)
                .addComponent(lblAge)
                .addComponent(tfAge, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(rbFromPrescription);
        // Các trường nhập liệu
        GroupLayout.ParallelGroup inputGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(lblSymptom)
                .addComponent(tfSymptom)
                .addComponent(lblDiagnosis)
                .addComponent(tfDiagnosis)
                .addComponent(lblTreatment)
                .addComponent(tfTreatment);

        // Nút hành động
        GroupLayout.SequentialGroup actionGroup = layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnAddService)
                .addGap(20)
                .addComponent(btnCancel);

        // Gom lại toàn bộ vào hParallel
        hParallel.addGroup(rowInfoGroup);
        hParallel.addGroup(inputGroup);
        hParallel.addGroup(actionGroup);

        // Gắn vào layout
        hGroup.addGroup(hParallel);
        layout.setHorizontalGroup(hGroup);
        // Tạo nhóm dọc chính
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

        //Thông tin bệnh nhân
        GroupLayout.ParallelGroup infoRow = layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblName)
                .addComponent(tfName)
                .addComponent(lblAge)
                .addComponent(tfAge)
                .addComponent(rbFromPrescription);

        vGroup.addGap(10);
        vGroup.addGroup(infoRow);
        vGroup.addGap(15);

        // Nhập liệu
        vGroup.addComponent(lblSymptom);
        vGroup.addComponent(tfSymptom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        vGroup.addGap(15);
        vGroup.addComponent(lblDiagnosis);
        vGroup.addComponent(tfDiagnosis, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        vGroup.addGap(15);
        vGroup.addComponent(lblTreatment);
        vGroup.addComponent(tfTreatment, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE);
        vGroup.addGap(15);

        // Các nút
        GroupLayout.ParallelGroup btnRow = layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnAddService)
                .addComponent(btnCancel);
        vGroup.addGroup(btnRow);

        // Gắn vào layout
        layout.setVerticalGroup(vGroup);
        layout.setVerticalGroup(vGroup);
        add(contentPanel, BorderLayout.CENTER);
    }

    public JLabel getLblTitle() {
        return lblTitle;
    }

    public void setLblTitle(JLabel lblTitle) {
        this.lblTitle = lblTitle;
    }

    public JLabel getLblName() {
        return lblName;
    }

    public void setLblName(JLabel lblName) {
        this.lblName = lblName;
    }

    public JLabel getLblAge() {
        return lblAge;
    }

    public void setLblAge(JLabel lblAge) {
        this.lblAge = lblAge;
    }

    public JLabel getLblSymptom() {
        return lblSymptom;
    }

    public void setLblSymptom(JLabel lblSymptom) {
        this.lblSymptom = lblSymptom;
    }

    public JLabel getLblDiagnosis() {
        return lblDiagnosis;
    }

    public void setLblDiagnosis(JLabel lblDiagnosis) {
        this.lblDiagnosis = lblDiagnosis;
    }

    public JLabel getLblTreatment() {
        return lblTreatment;
    }

    public void setLblTreatment(JLabel lblTreatment) {
        this.lblTreatment = lblTreatment;
    }

    public JTextField getTfName() {
        return tfName;
    }

    public void setTfName(JTextField tfName) {
        this.tfName = tfName;
    }

    public JTextField getTfAge() {
        return tfAge;
    }

    public void setTfAge(JTextField tfAge) {
        this.tfAge = tfAge;
    }

    public JTextField getTfSymptom() {
        return tfSymptom;
    }

    public void setTfSymptom(JTextField tfSymptom) {
        this.tfSymptom = tfSymptom;
    }

    public JTextField getTfDiagnosis() {
        return tfDiagnosis;
    }

    public void setTfDiagnosis(JTextField tfDiagnosis) {
        this.tfDiagnosis = tfDiagnosis;
    }

    public JTextField getTfTreatment() {
        return tfTreatment;
    }

    public void setTfTreatment(JTextField tfTreatment) {
        this.tfTreatment = tfTreatment;
    }

    public JRadioButton getRbFromPrescription() {
        return rbFromPrescription;
    }

    public void setRbFromPrescription(JRadioButton rbFromPrescription) {
        this.rbFromPrescription = rbFromPrescription;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public void setBtnCancel(JButton btnCancel) {
        this.btnCancel = btnCancel;
    }

    public JButton getBtnAddService() {
        return btnAddService;
    }

    public void setBtnAddService(JButton btnAddService) {
        this.btnAddService = btnAddService;
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }
    public void setNamePatient(String name){
        tfName.setText(name);
    }
    public void setAgePatient(String age){
        tfAge.setText(age);
    }

    public int getIdPrescription() {
        return idPrescription;
    }

    public void setIdPrescription(int idPrescription) {
        this.idPrescription = idPrescription;
    }

    public String getNamePatient() {
        return namePatient;
    }

    public String getSdtPatient() {
        return sdtPatient;
    }

    public void setSdtPatient(String sdtPatient) {
        this.sdtPatient = sdtPatient;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    public void setInfo(){
        namePatient=tfName.getText();
        symptom=tfSymptom.getText();
        diagnosis=tfDiagnosis.getText();
        treatment=tfTreatment.getText();
    }
    public void resetInfo(){
        namePatient=tfName.getText();
        symptom=tfSymptom.getText();
        diagnosis=tfDiagnosis.getText();
        treatment=tfTreatment.getText();

        tfSymptom.setText("");
        tfDiagnosis.setText("");
        tfTreatment.setText("");
    }
}