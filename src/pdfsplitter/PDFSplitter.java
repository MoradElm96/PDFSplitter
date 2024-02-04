
package pdfsplitter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

/**
 *
 * @author Morad
 */

public class PDFSplitter extends JFrame {

    // Constantes para los nombres de los meses, años y el número máximo de páginas a dividir por vez.
    private static final String[] MONTHS = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private static final String[] YEARS = {"2023", "2024", "2025", "2026"};
    private static final int MAX_PAGES_TO_SPLIT = 2;

    // Campos de entrada y salida de texto, combobox y barra de progreso.
    private JTextField inputFileField;
    private JTextField outputDirField;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JProgressBar progressBar;
    
// Constructor que inicializa la interfaz gráfica y muestra la ventana principal.
     
    public PDFSplitter() {
        setTitle("PDF Splitter");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridLayout(6, 2));

        addLabelAndTextField("Seleccionar PDF de entrada:");
        addBrowseButton("Examinar", this::chooseInputFile);

        addLabelAndTextField("Carpeta de salida:");
        addBrowseButton("Examinar", this::chooseOutputDirectory);

        addLabel("Seleccionar Mes:");
        addMonthComboBox();

        addLabel("Seleccionar Año:");
        addYearComboBox();

        progressBar = new JProgressBar(0, 100);
        add(new JLabel("Progreso:"));
        add(progressBar);

        JButton splitButton = new JButton("Dividir PDF");
        splitButton.addActionListener(this::splitPDF);
        add(splitButton);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addLabelAndTextField(String labelText) {
        add(new JLabel(labelText));
        JTextField textField = new JTextField();
        add(textField);

        if (labelText.contains("entrada")) {
            inputFileField = textField;
        } else if (labelText.contains("salida")) {
            outputDirField = textField;
        }
    }

    private void addBrowseButton(String buttonText, ActionListener actionListener) {
        JButton browseButton = new JButton(buttonText);
        browseButton.addActionListener(actionListener);
        add(browseButton);
    }

    private void addLabel(String labelText) {
        add(new JLabel(labelText));
    }

    private void addMonthComboBox() {
        monthComboBox = new JComboBox<>(MONTHS);
        add(monthComboBox);
    }

    private void addYearComboBox() {
        yearComboBox = new JComboBox<>(YEARS);
        add(yearComboBox);
    }

    private void chooseInputFile(ActionEvent event) {
        chooseFile("Archivos PDF", "pdf", inputFileField);
    }

    private void chooseOutputDirectory(ActionEvent event) {
        chooseDirectory(outputDirField);
    }

    private void splitPDF(ActionEvent event) {
        String inputFile = inputFileField.getText();
        String outputDir = outputDirField.getText();
        String selectedMonth = (String) monthComboBox.getSelectedItem();
        String selectedYear = (String) yearComboBox.getSelectedItem();

        try {
            File file = new File(inputFile);
            PDDocument document = Loader.loadPDF(file);

            int totalPages = document.getNumberOfPages();

            String outputFileNameMohamed = outputDir + "/Nomina_" + selectedMonth + selectedYear + ".pdf";
            String outputFileNameCarlosJose = outputDir + "/Nomina_" + selectedMonth + selectedYear + ".pdf";

            progressBar.setMaximum(totalPages);

            for (int pageNum = 0; pageNum < Math.min(totalPages, MAX_PAGES_TO_SPLIT); pageNum++) {
                PDDocument singlePageDocument = createSinglePageDocument(document, pageNum);
                String outputFileName = determineOutputFileName(pageNum, outputFileNameMohamed, outputFileNameCarlosJose);
                saveAndClose(singlePageDocument, outputFileName);
                progressBar.setValue(pageNum + 1);
            }

            document.close();

            JOptionPane.showMessageDialog(this, "Proceso completado. PDFs generados en la carpeta: " + outputDir);
        } catch (IOException e) {
            handleIOException(e);
        }
    }

    private PDDocument createSinglePageDocument(PDDocument originalDocument, int pageNum) throws IOException {
        PDDocument singlePageDocument = new PDDocument();
        PDPage importedPage = (PDPage) singlePageDocument.importPage(originalDocument.getPage(pageNum));
        singlePageDocument.addPage(importedPage);
        return singlePageDocument;
    }

    private String determineOutputFileName(int pageNum, String outputFileNameMohamed, String outputFileNameCarlosJose) {
        return (pageNum == 0) ? outputFileNameMohamed : outputFileNameCarlosJose;
    }

    private void saveAndClose(PDDocument document, String outputFileName) throws IOException {
        document.save(new File(outputFileName));
        document.close();
    }

    private void handleIOException(IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al dividir el PDF. Verifica la entrada y salida.");
    }

   // Métodos para seleccionar archivos y directorios
    private void chooseFile(String description, String extension, JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(description, extension));
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            textField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void chooseDirectory(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            textField.setText(selectedDir.getAbsolutePath());
        }
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PDFSplitter::new);
    }
}
