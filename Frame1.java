import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Frame1 {

	private static final String FILE_CHOOSER_DIRECTORY = "C:\\Users\\ClumsyBarber\\Desktop\\Test";

	private JFrame frame;
	private java.io.File file;

	private int index;
	private int mistakes = 0;
	private List<Integer> rows;
	private HashMap<Integer, String> words; // <row, meaning>
	private HashMap<String, String> meanings; // <meaning, word>

	private JLabel lblOpenedFile;
	private JLabel lblWorkingOn;
	private JLabel lblHint;
	private JLabel lblWord;
	private JLabel lblTotalWords;
	private JLabel lblWordsLeft;
	private JLabel lblMistakes;

	private JButton btnOpenFile;
	private JButton btnConfirm;
	private JButton btnHint;

	private JTextField tfAnswerBox;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame1 window = new Frame1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Frame1() {
		initialize();
	}

	private boolean fileIsNull() {
		if (file == null) {
			JOptionPane.showMessageDialog(null, "Please select a file first.");
			return true;
		}

		return false;
	}

	private void hintRequested() {
		lblHint.setVisible(true);
	}

	private void checkAnswer() {
		if (fileIsNull())
			return;
		String answer = tfAnswerBox.getText().toLowerCase();

		String meaning = words.get(index);
		String word = meanings.get(meaning);

		if (answer == null || answer.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please enter a word.");
			return;
		} else {
			if (meanings.containsValue(answer)) {
				meanings.remove(meaning);
				words.remove(index);
				rows.remove(0);
				JOptionPane.showMessageDialog(null, "Correct!");
				lblWordsLeft.setText("Words Left: " + String.valueOf(rows.size()));
				if (rows.size() == 0) {
					JOptionPane.showMessageDialog(null, "Kudos! You have answered correctly to all the words!!!");
					file = null;
					return;
				}
			} else {
				JOptionPane.showMessageDialog(null, "Wrong! The answer is: " + word);
				lblMistakes.setText("Mistakes: " + ++mistakes);

			}
		}
		getRandomWord();
	}

	private void getRandomWord() {
		lblHint.setVisible(false);
		tfAnswerBox.setText(null);
		Collections.shuffle(rows);
		index = rows.get(0);
		String meaning = words.get(index);
		String word = meanings.get(meaning);
		lblHint.setText("Word: " + word + " | Meaning: " + meaning);
		lblWord.setText(meaning);
	}

	private void populateCollection() {
		rows = new ArrayList<>();
		words = new HashMap<>();
		meanings = new HashMap<>();

		try {
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
			XSSFSheet sheet = wb.getSheetAt(0);
			int lastRow = sheet.getLastRowNum();

			for (int i = 1; i <= lastRow; i++) {
				rows.add(i);
				XSSFRow row = sheet.getRow(i);
				String key = row.getCell(1).getStringCellValue().toLowerCase(); // gets first column -> word
				String value = row.getCell(0).getStringCellValue().toLowerCase(); // gets second column -> meaning

				words.put(i, key);
				meanings.put(key, value);
			}
			setNewFileLabels();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void setNewFileLabels() {
		lblTotalWords.setText("Total Words: " + String.valueOf(rows.size()));
		lblWordsLeft.setText("Words Left: " + String.valueOf(rows.size()));
		lblMistakes.setText("Mistakes: " + mistakes);
	}

	private void clickOpenFileBtn() {
		int choice = 0;

		if (file != null) {
			String[] options = { "Yes", "No" };
			String workingOn = "You are currently working on: " + file.getName() + ". " + System.lineSeparator()
					+ "Would you like to select a new file?";

			choice = JOptionPane.showOptionDialog(null, workingOn, "Open a new file", JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		}
		if (choice == 0) { // if 'Yes' is clicked on the option box
			JFileChooser fileChooser = new JFileChooser(FILE_CHOOSER_DIRECTORY); // choose a file from default path
			FileNameExtensionFilter xlsxFilter = new FileNameExtensionFilter("Microsoft Excel Worksheet .xlsx", "xlsx"); // filter
																															// to
																															// .xlsx
																															// only

			fileChooser.setFileFilter(xlsxFilter);
			java.io.File oldFile = file;
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();
			}
			if (oldFile != file) { // If 'Cancel' is clicked on the fileChooser form will do nothing
				String fileName = file.getName();

				lblOpenedFile.setText(fileName);
				JOptionPane.showMessageDialog(null, "You have opened: " + fileName);

				mistakes = 0;
				populateCollection();
				getRandomWord();
			}

		}

	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		btnOpenFile = new JButton("Open .xlsx");
		btnOpenFile.setBounds(10, 11, 94, 23);
		btnOpenFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				clickOpenFileBtn();
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnOpenFile);

		lblWorkingOn = new JLabel("Working on:");
		lblWorkingOn.setBounds(114, 15, 78, 14);
		frame.getContentPane().add(lblWorkingOn);

		lblOpenedFile = new JLabel("No file has been opened.");
		lblOpenedFile.setBounds(185, 15, 227, 14);
		frame.getContentPane().add(lblOpenedFile);

		lblHint = new JLabel("Select a file.");
		lblHint.setVisible(false);
		lblHint.setBounds(10, 236, 402, 14);
		frame.getContentPane().add(lblHint);

		tfAnswerBox = new JTextField();
		tfAnswerBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				checkAnswer();
			}
		});
		tfAnswerBox.setBounds(10, 65, 209, 20);
		frame.getContentPane().add(tfAnswerBox);
		tfAnswerBox.setColumns(10);

		btnConfirm = new JButton("Confirm");
		btnConfirm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkAnswer();
			}

		});
		btnConfirm.setBounds(10, 89, 89, 23);
		frame.getContentPane().add(btnConfirm);

		lblWord = new JLabel("");
		lblWord.setBounds(10, 45, 373, 14);
		frame.getContentPane().add(lblWord);

		lblTotalWords = new JLabel("Total Words: 0");
		lblTotalWords.setBounds(20, 123, 123, 14);
		frame.getContentPane().add(lblTotalWords);

		lblWordsLeft = new JLabel("Words Left: 0");
		lblWordsLeft.setBounds(20, 139, 123, 14);
		frame.getContentPane().add(lblWordsLeft);

		lblMistakes = new JLabel("Mistakes: 0");
		lblMistakes.setBounds(20, 155, 123, 14);
		frame.getContentPane().add(lblMistakes);

		btnHint = new JButton("Need a Hint?");
		btnHint.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				hintRequested();
			}
		});
		btnHint.setBounds(301, 215, 123, 23);
		frame.getContentPane().add(btnHint);
	}
}
