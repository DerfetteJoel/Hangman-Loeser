package de.dfj.hangman;

import java.awt.EventQueue;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JList;

public class FrmHangman extends JFrame {
	
	private static final long serialVersionUID = 5649298949067450186L;
	
	private JPanel contentPane;
	private ButtonGroup bg = new ButtonGroup();
	private JTextField textField;
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private JTextField[] textFields;
	private ArrayList<String> sprache = new ArrayList<String>(); //Speichert alle Wörter einer Sprache
	private JLabel lbStatus;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrmHangman frame = new FrmHangman();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FrmHangman() {
		setTitle("Hangman L\u00F6ser");
		try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
		} 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(450, 350);
		setLocationRelativeTo(null);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		
		//Lade Deutsch...
		sprache = ladeSprache(new File("lang"+File.separator+"Deutsch.txt"));
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 444, 21);
		contentPane.add(menuBar);
		
		JMenu mnSprache = new JMenu("Sprache");
		menuBar.add(mnSprache);
		
		JRadioButtonMenuItem rdbtnmntmDeutsch = new JRadioButtonMenuItem("Deutsch");
		rdbtnmntmDeutsch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sprache = ladeSprache(new File("lang"+File.separator+"Deutsch.txt"));
				lbStatus.setText(sprache.size()+" Wörter geladen!");
			}
		});
		mnSprache.add(rdbtnmntmDeutsch);
		rdbtnmntmDeutsch.setSelected(true);
		bg.add(rdbtnmntmDeutsch);
		
		JRadioButtonMenuItem rdbtnmntmEnglisch = new JRadioButtonMenuItem("Englisch");
		rdbtnmntmEnglisch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sprache = ladeSprache(new File("lang"+File.separator+"Englisch.txt"));
				lbStatus.setText(sprache.size()+" Wörter geladen!");
			}
		});
		mnSprache.add(rdbtnmntmEnglisch);
		bg.add(rdbtnmntmEnglisch);
		
		JLabel lblAnzahlDerBuchstaben = new JLabel("Anzahl der Buchstaben:");
		lblAnzahlDerBuchstaben.setBounds(10, 32, 114, 14);
		contentPane.add(lblAnzahlDerBuchstaben);
		
		textField = new JTextField();
		textField.setBounds(134, 29, 50, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnBesttigen = new JButton("Best\u00E4tigen");
		btnBesttigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//Erst alle TextFields löschen
				if(textFields!=null) {
					try {
						for (int i = 0; i < textFields.length; i++) {
							contentPane.remove(textFields[i]);
							contentPane.validate();
							contentPane.repaint();
						}
					} catch(NullPointerException e) {
						
					}
				}
				
				textFields = new JTextField[Integer.parseInt(textField.getText())];
				
				//Alle TextFields zeichnen
				for (int i = 0; i < Integer.parseInt(textField.getText()); i++) {
					textFields[i] = new JTextField();
					if(textFields.length<=20) {
						//Erste Reihe
						textFields[i].setBounds((i+1)*20, 70, 20, 20);
					} else {
						//Abbrechen
						JOptionPane.showMessageDialog(null, "So große Wörter werden nicht unterstützt!");
						contentPane.add(textFields[i]);
						contentPane.validate();
						contentPane.repaint();
						break;
					}
					contentPane.add(textFields[i]);
					contentPane.validate();
					contentPane.repaint();
				}
			}
		});
		btnBesttigen.setBounds(194, 28, 89, 23);
		contentPane.add(btnBesttigen);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 141, 444, 180);
		contentPane.add(scrollPane);
		
		JList<String> list = new JList<String>();
		list.setModel(listModel);
		scrollPane.setViewportView(list);
		
		JButton btnWrterFinden = new JButton("W\u00F6rter finden!");
		btnWrterFinden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Zunächst muss die Liste komplett geleert werden
				listModel.clear();
				
				//Alle Wörter finden, auf die die Begrenzungen zutreffen. Zuerst werden alle Wörter
				//herausgefiltert, die die gleiche Anzahl an Buchstaben hat.
				ArrayList<String> filterList = new ArrayList<String>();
				for (int i=0; i<sprache.size(); i++) {
					try {
						if(sprache.get(i).length() == Integer.parseInt(textField.getText())) {
							filterList.add(sprache.get(i));
						}
					} catch(NumberFormatException err) {
						JOptionPane.showMessageDialog(null, "Du hast keine gültige Buchstabenzahl angegeben!");
					} catch(Exception err) {
						System.out.println(err);
					}
				}
				//Als nächstes sehen wir uns die Buchstaben an, die angegeben werden.
				//Da das gesuchte Wort nur in der wesentlich kleineren filterList vorkommen kann,
				//dauert dieser Schritt hoffentlich nicht sehr lange.
				
				filterList = filtereBuchstaben(filterList);
				for(int i=0; i<filterList.size(); i++) {
					listModel.addElement(filterList.get(i));
				}
				lbStatus.setText(filterList.size()+" Wörter gefunden!");
				
			}
		});
		btnWrterFinden.setBounds(10, 107, 114, 23);
		contentPane.add(btnWrterFinden);
		
		
		lbStatus = new JLabel(sprache.size()+" Wörter geladen!");
		lbStatus.setBounds(134, 111, 300, 14);
		contentPane.add(lbStatus);
		
	}
	
	private ArrayList<String> ladeSprache(File file) {
		ArrayList<String> returnList = new ArrayList<String>();
		BufferedReader in;
		String deutschString;
		try {
			in = new BufferedReader(new FileReader(file));
			while((deutschString = in.readLine()) != null) {
				returnList.add(deutschString);
			}
			in.close();
			return returnList;
		} catch(IOException e) {
			System.out.println(e);
			returnList.add("");
			return returnList;
		}
	}
	
	/**
	 * Erwartet eine ArrayList als Eingabeargument und liefert eine ArrayList, bei der die in den textFields[]
	 * angegebenen Buchstaben mit denen aus der Liste übereinstimmt
	 * @param list
	 * @return
	 */
	private ArrayList<String> filtereBuchstaben(ArrayList<String> list) {
		ArrayList<String> retList = new ArrayList<String>();
		//Zuerst müssen die Eingaben aus den textFields ausgewertet werden.
		String textFieldString = "";
		//CharContainInfo speichert, an welchen Stellen des textFieldStrings sich Buchstaben befinden.
		ArrayList<Integer> CharContainInfo = new ArrayList<Integer>();
		
		//Die Bedeutung von Checkval wird weiter unten erklärt.
		int checkval = 0;
		
		for(int i=0;i<textFields.length;i++) {
			if(textFields[i].getText().equals("")) {
				textFieldString += " ";
			} else {
				textFieldString += textFields[i].getText().toUpperCase();
				CharContainInfo.add(textFieldString.indexOf(textFields[i].getText(), i));
			}
		}
		int length = textFieldString.replaceAll(" ", "").length();
		//Jetzt müssen alle Wörter aus der mitgelieferten Liste mit den eingegeben Werten überprüft werden.
		for (int i=0; i<list.size();i++) {
			checkval = 0;
			for (int j=0;j<length; j++) {
				//System.out.println("textFieldString.charAt(CharContainInfo.get("+j+")): "+textFieldString.charAt(CharContainInfo.get(j)));
				//System.out.println("list.get(i).charAt(CharContainInfo.get("+j+")): "+list.get(i).charAt(CharContainInfo.get(j)));
				if ( list.get(i).toUpperCase().charAt(CharContainInfo.get(j)) == textFieldString.charAt(CharContainInfo.get(j)) ) {
					//Zähler um eins hoch. Wenn der Zähler bis zum Ende des Wortes einen bestimmten Score erreicht hat
					//(den von length), stimmt das Wort überein. Dieser Wert muss zum Start jedes Wertes zurückgesetzt werden.
					checkval++;
				}
			}
			if(checkval == length) {
				retList.add(list.get(i));
			}
		}
		
		return retList;
	}
}
