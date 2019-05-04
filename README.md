import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Frame1 {

	private JFrame frame;
	private JLabel lblRead;
	private JLabel lblRandomRow;
	private JLabel lblCorrect;
	private JLabel lblWrong;
	private JLabel lblNWords;
	private JLabel lblHints;
	private JTextField tfAnswer;
	private JButton btnFileSelect;
	
	private String filePath = null;
	private String answer;
	private Set<Integer> usedValues = new HashSet<Integer>();
	private int randomRow = 0;
	private int correct = 0;
	private int wrong = 0;
	private int hints = 0;
	private int lastRow = 1; // TODO: needs work - "2" is just a random number to patch things up
	private JLabel lblAnswer;
	private String hint;

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

	public String readCell(int r, int c) {
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = sheet.getRow(r);
		lastRow = sheet.getLastRowNum();
		return row.getCell(c).getStringCellValue();
	}

	public void setRandomRow() { // sets the randomRow value to a random between the 2nd row and the last(included)
		Random random = new Random();
		int min = 1;
		int max = lastRow;

		int value = random.nextInt(max) + min;
		randomRow = value;
	}

	public void randomizeRows() {
		if (usedValues.size() == lastRow) { // checks if the last row is reached
			JOptionPane.showMessageDialog(null, "Congratulations! You have answered all the questions!");
			usedValues.removeAll(usedValues); // resets the set(usedValues) and the the program "starts" again
			correct = 0;
			wrong = 0;
			
			lblCorrect.setText("Correct: " + correct);
			lblWrong.setText("Wrong: " + wrong);
		}
		
		setRandomRow(); // gets a new random row number and checks if the Set(usedValues) already contains it
		while (true) { // if yes => repeat // if no adds the row number to the set.
			if (usedValues.contains(randomRow)) {
				setRandomRow();
			} else {
				usedValues.add(randomRow);
				break;
			}
		}
	}
	
	public void answeredOrReadClicked () { // Reads the next random row.
			randomizeRows();
			lblRandomRow.setText("Row: " + (randomRow + 1));
			lblRead.setText(readCell(randomRow, 1));
			lblNWords.setText("Words so far: " + usedValues.size());
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 722, 269);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		tfAnswer = new JTextField();
		tfAnswer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (filePath == null) {
					JOptionPane.showMessageDialog(null, "Please select a file first!");
				} else {
					answer = tfAnswer.getText();
					if (readCell(randomRow, 0).equals(answer)) {
						JOptionPane.showMessageDialog(null, "Correct!");
						correct++;
						lblCorrect.setText("Correct: " + correct);
					} else {
						JOptionPane.showMessageDialog(null, "Wrong!");
						usedValues.remove(randomRow);
						wrong++;
						lblWrong.setText("Wrong: " + wrong);
					}
					answeredOrReadClicked ();
					tfAnswer.setText(null);
					lblAnswer.setText(" ");
				}
			}
		});
		tfAnswer.setBounds(10, 45, 169, 20);
		frame.getContentPane().add(tfAnswer);
		tfAnswer.setColumns(10);
		
		////// Buttons ///////
		JButton btnRead = new JButton("Start");
		btnRead.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				btnRead.setText("Progress");
				if (filePath == null) {
					JOptionPane.showMessageDialog(null, "Please select a file first!");
				} else {
					if(usedValues.size()==0) {
						answeredOrReadClicked ();	
					} else {
						JOptionPane.showMessageDialog(null, "You have answered " + (usedValues.size()-1) + " out of " + lastRow + " questions. Only " + Math.abs(usedValues.size()-lastRow) + " left.");
					}
					
				}
			}
		});
		btnRead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnRead.setBounds(10, 11, 117, 23);
		frame.getContentPane().add(btnRead);

		btnFileSelect = new JButton("Select XLS File");
		btnFileSelect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fileChooser = new JFileChooser("D:\\From drive D\\Format\\Desktop\\AF\\Kurs 3\\The Books"); // new file chooser with the following default directory
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel 97-2003 .xls", "xls"); // Filters and shows only .xls extension files
				fileChooser.setFileFilter(filter); // implements filter
				java.io.File file = null;
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					file = fileChooser.getSelectedFile();
					filePath = file.getPath();
				}
			btnFileSelect.setVisible(false);
			/*btnRead.setBackground(Color.orange);*/
		
			}
		});
		btnFileSelect.setBounds(557, 196, 124, 23);
		frame.getContentPane().add(btnFileSelect);
		
		JButton btnHint = new JButton("Hint");
		btnHint.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (filePath == null) {
					JOptionPane.showMessageDialog(null, "Please select a file first!");
				} else {
					if(!lblAnswer.getText().equals(hint)) {
						hint = readCell(randomRow,0); 
						lblAnswer.setText(hint);
						hints++;
					}
					
					lblHints.setText("Hints: " + hints);
				}
			}
		});
		btnHint.setBounds(186, 44, 69, 23);
		frame.getContentPane().add(btnHint);

		////// Labels //////
		lblRead = new JLabel(" ");
		lblRead.setBounds(137, 15, 544, 14);
		frame.getContentPane().add(lblRead);
		
		lblRandomRow = new JLabel("Row: ");
		lblRandomRow.setBounds(10, 78, 89, 14);
		frame.getContentPane().add(lblRandomRow);

		lblCorrect = new JLabel("Correct: 0");
		lblCorrect.setBounds(10, 103, 89, 14);
		frame.getContentPane().add(lblCorrect);

		lblWrong = new JLabel("Wrong: 0");
		lblWrong.setBounds(10, 128, 89, 14);
		frame.getContentPane().add(lblWrong);

		lblNWords = new JLabel("Words so far: 0");
		lblNWords.setBounds(10, 153, 142, 14);
		frame.getContentPane().add(lblNWords);
		
		lblHints = new JLabel("Hints: 0");
		lblHints.setBounds(10, 178, 142, 14);
		frame.getContentPane().add(lblHints);
		
		lblAnswer = new JLabel("");
		lblAnswer.setBounds(265, 48, 194, 14);
		frame.getContentPane().add(lblAnswer);
		
	}
}
