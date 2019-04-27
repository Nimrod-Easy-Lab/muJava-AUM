/*
 * Crosslexic, a tool for solving crossword puzzles.
 * Copyright (c) 2010-2011, Arthur Gouros
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of Arthur Gouros nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

/**
 **************************************************************************
 * Crosslexic - a tool for solving crossword puzzles.                     *
 *              Original Author: Arthur Gouros.                           *
 *                                                                        *
 *              Please consult the documentation for further information. *
 **************************************************************************
 **/

package openjava.test;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

//public class Crosslexic extends JApplet implements ActionListener, KeyListener   {
public class Crosslexic implements ActionListener, KeyListener   {
	/**
	 * Find English words that match partially
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String version = "@VERSION@";
	public static final Integer MAXSIZE = 20;
    public static JFrame clFrame;
    public static JPanel clPanel, letterPanel, wlengthPanel, buttonPanel;
    protected static JMenuBar menuBar;
    protected static JMenu menu;
    protected static JMenuItem helpMenuItem, aboutMenuItem, licMenuItem;
    private static boolean menu_activated = false;
    private static JComboBox wordlengthComboBox;
    private static int wordlength;
    private static JFormattedTextField[] letterText;
    private static int key_code;
    protected static JLabel wlengthLabel, matchesLabel;
    private static DefaultListModel listModel;
    protected static JList dictlist;
    protected static JScrollPane listScrollPane;
    protected static Dimension listDimension;
    //private static ListCellRenderer defaultCellRenderer;
    private static myListCellRenderer iconListCellRenderer;
        
    private static JButton searchButton;

	private static JButton clearButton;
    private static JProgressBar progressBar;
    private static Thread searchThread = null;

	protected static Thread repaintlistThread = null;
    protected static URL dictionaryResource, imgMatch, imgHelp, imgAlert, imgAbout, imgGuru;
    protected static final String imgMatchPath = "lib/resource/match.png";
    protected static final String imgHelpPath = "lib/resource/help.png";
    protected static final String imgAlertPath = "lib/resource/alert.png";
    protected static final String imgAboutPath = "lib/resource/about.png";
    protected static final String imgGuruPath = "lib/resource/arthurguru.png";
    protected static ImageIcon iconMatch, iconHelp, iconAlert, iconAbout, iconGuru;
       

    public Crosslexic () {
        //Create and set up the window.
        JFrame.setDefaultLookAndFeelDecorated(true);
        clFrame = new JFrame("Solve crossword puzzles.");
        clFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the panel and widgets.
        addWidgets();
        clFrame.setJMenuBar(menuBar);

        //Add the panel to the window.
        clFrame.getContentPane().add(clPanel, BorderLayout.CENTER);

        //Display the window.
        clFrame.pack();
        clFrame.setLocationRelativeTo(null);
        clFrame.setVisible(true);
    }


    public String getAppletInfo() {
        return "Crosslexic " + version + " by Arthur Gouros.";
    }
    
    
    protected static void addtolist(ImageIcon icon, String listItem) {
    	/* Painting a JList with GridBagLayout is buggy when called
    	 * repeatedly. This function uses a thread to reduce the number
    	 * of Jlist repaint calls and is suitable for random bulk 
    	 * additions to a JList object.
    	 */
    	
    	// Split multi-line strings into single lines.
    /*	for (String listDisplay : listItem.split("\n"))
    		listModel.addElement(createLabel(icon, listDisplay));*/
    	    	
    	// Implemented into a control thread due to JList painting problems
    	//if (indexvisibleThread == null) {
    	if (repaintlistThread == null) {
    		repaintlistThread = new Thread(new repaintlist());
    		repaintlistThread.start();
    	}
       	
       	return;
    }
    

    private static class repaintlist implements Runnable {
    	public void run() {
        	try {
        		Thread.sleep(200);
        		try {getListModel().wait(200); }
                catch(Exception e) { /* noop */ }
                
        		int listsize = getListModel().getSize() > 0 ? getListModel().getSize() - 1 : 0;
        		dictlist.setSelectedIndex(listsize);
        		dictlist.ensureIndexIsVisible(listsize);
        	} catch (Exception e) {
    			getListModel().addElement(createLabel(iconAlert, "Oh bugger! Jlist scroll bug!"));
    			getListModel().addElement(createLabel(null, "> You may have to click Clear and try again."));
    			setSearchThread(null);    // Terminate search thread (if not already gone)
    			app_setEnabled(true);   // Reset GUI
    		}
        	dictlist.repaint();
        	
           	repaintlistThread = null;
           	
           	return;
    	}
    }
    

    protected static void addtolist_now(ImageIcon icon, String listItem) {
    	/* This function best handles low input to the JList object and
    	 * maintains highlight on the last item entered. If adding
    	 * bulk items randomly then please use the addtolist()
    	 * function because calling this function repetitively causes
    	 * the JList object with GradBagLoayout to randomly barf.
    	 */
    	
    	// Split multi-line strings into single lines.
    /*	for (String listDisplay : listItem.split("\n"))
    		listModel.addElement(createLabel(icon, listDisplay));*/
    	
    	try {
    		try {listModel.wait(200); }
            catch(Exception e) { /* noop */ }
            
    		int listsize = listModel.getSize() > 0 ? listModel.getSize() - 1 : 0;
    		dictlist.setSelectedIndex(listsize);
    		dictlist.ensureIndexIsVisible(listsize);
    	} catch (Exception e) {
			listModel.addElement(createLabel(iconAlert, "Oh bugger! Jlist scroll bug!"));
			listModel.addElement(createLabel(null, "> You may have to click Clear and try again."));
			searchThread = null;    // Terminate search thread (if not already gone)
			app_setEnabled(true);   // Reset GUI
		}
    	dictlist.repaint();
            	        	
       	return;
    }
    

    private void addWidgets(){
    	//showStatus("Crosslexic: Loading...");
    	
        //Create the widgets.
        clPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
                
        //Add menu to Japplet frame
        //imgGuru = this.getClass().getResource("/" + imgGuruPath);
        URI uri = null;
        try {
			uri = new URI("file:///" + System.getProperty("user.dir") + "lib/resource/rthurguru.png");
			imgGuru = uri.toURL();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       	iconGuru = new ImageIcon(imgGuru);
       //	imgAlert = this.getClass().getResource("/" + imgAlertPath);
       	try {
			uri = new URI("file:///" + System.getProperty("user.dir") +  "/lib/resource/alert.png");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	try {
			imgAlert = uri.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	//imgAlert = this.getClass().getResource("F:/eclipseModelingSpace/CrossLexic/lib/resource/alert.png");
       	iconAlert = new ImageIcon(imgAlert);
       	
       // imgHelp = this.getClass().getResource("F://eclipseModelingSpace//CrossLexic//lib//resource//rthurguru.png");
    	try {
			uri = new URI("file:///" + System.getProperty("user.dir") + "/lib/resource/help.png");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	try {
       		imgHelp = uri.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       	iconHelp = new ImageIcon(imgHelp);
        menuBar = new JMenuBar();
        menuBar.add(Box.createHorizontalGlue());
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);
        helpMenuItem = new JMenuItem("Help", iconHelp);
        helpMenuItem.setMnemonic(KeyEvent.VK_H);
        helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
        helpMenuItem.getAccessibleContext().setAccessibleDescription("Help");
        helpMenuItem.addActionListener(this);
        menu.add(helpMenuItem);
        menu.addSeparator();
       // imgAbout = this.getClass().getResource("/" + imgAboutPath);

        	try {
 			uri = new URI("file:///" + System.getProperty("user.dir") + "/lib/resource/about.png");
 		} catch (URISyntaxException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
        	try {
        		imgAbout = uri.toURL();
 		} catch (MalformedURLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
       	iconAbout = new ImageIcon(imgAbout);
        aboutMenuItem = new JMenuItem("About", iconAbout);
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
        aboutMenuItem.getAccessibleContext().setAccessibleDescription("About"); 
        aboutMenuItem.addActionListener(this);
        menu.add(aboutMenuItem);
        menu.addSeparator();
        licMenuItem = new JMenuItem("License", iconAlert);
        licMenuItem.setMnemonic(KeyEvent.VK_L);
        licMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.ALT_MASK));
        licMenuItem.getAccessibleContext().setAccessibleDescription("License"); 
        licMenuItem.addActionListener(this);
        menu.add(licMenuItem);
        
       	
        // Label 1
       	c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets(2,2,10,2);
        c.fill = GridBagConstraints.BOTH;
        JLabel titleLabel = new JLabel("Crosslexic " + version + " by Arthur Gouros", JLabel.CENTER);
        clPanel.add(titleLabel, c);

        // Label 2     
        wlengthPanel = new JPanel(new GridLayout(1, 0));
        JLabel wlengthLabel = new JLabel("Word Length: ");
        wlengthPanel.add(wlengthLabel);
        
        // Drop down list
        setWordlength(MAXSIZE);
        setWordlengthComboBox(new JComboBox());
        for (int i = 1 ; i <= MAXSIZE ; i++)
        	getWordlengthComboBox().addItem(new Integer(i).toString());
        wlengthPanel.add(getWordlengthComboBox());
        getWordlengthComboBox().setPrototypeDisplayValue(MAXSIZE);
        getWordlengthComboBox().setSelectedItem(MAXSIZE.toString());
        getWordlengthComboBox().addActionListener(this);
        getWordlengthComboBox().setToolTipText("Click this to select your word length.");
        
        // Filler
        wlengthPanel.add(Box.createHorizontalGlue());
        wlengthPanel.add(Box.createHorizontalGlue());
        
        // Panel wlengthPanel
        c.insets = new Insets(2,2,2,2);
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        clPanel.add(wlengthPanel, c);
        
        // Text input
        letterPanel = new JPanel(new GridLayout(1, MAXSIZE, 2, 0));
        letterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        setLetterText(new JFormattedTextField[MAXSIZE]);
        /*
        for (int i = 0 ; i < MAXSIZE ; i ++) {
        	getLetterText()[i] = new JFormattedTextField(createFormatter("L"));
        	getLetterText()[i].setColumns(1);
        	getLetterText()[i].setHorizontalAlignment(JTextField.CENTER);
        	getLetterText()[i].addKeyListener(this);
            letterPanel.add(getLetterText()[i]);
        }
        */
        // Panel letterPanel
        c.gridy = 2;
        clPanel.add(letterPanel, c);
        
        // Label 3
        matchesLabel = new JLabel("Matches", SwingConstants.LEFT);
        c.gridy = 3;
        clPanel.add(matchesLabel, c);
        
        // List
        listModel = new DefaultListModel();
        listModel.ensureCapacity(200);
        dictlist = new JList(listModel);
        //defaultCellRenderer = dictlist.getCellRenderer();
        dictlist.setFixedCellWidth(100);
        dictlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dictlist.setSelectedIndex(0);
        dictlist.setLayoutOrientation(JList.VERTICAL);
        dictlist.setVisibleRowCount(10);
                       
        iconListCellRenderer = new myListCellRenderer();
		dictlist.setCellRenderer(iconListCellRenderer);
		iconListCellRenderer.setFont(dictlist.getFont());
				
		listScrollPane = new JScrollPane(dictlist);
		c.gridheight = 10;
       	c.gridy = 4;
        clPanel.add(listScrollPane, c);
        		
		// Buttons
		buttonPanel = new JPanel(new GridLayout(1, 0));
		setClearButton(new JButton("Clear"));
        buttonPanel.add(getClearButton());
        getClearButton().addActionListener(this);
        getClearButton().setToolTipText("Click this button to clear the word.");
       
        buttonPanel.add(Box.createHorizontalGlue());
        //imgMatch = this.getClass().getResource("/" + imgMatchPath);
        
    	try {
 			uri = new URI("file:///" + System.getProperty("user.dir") + "/lib/resource/match.png");
 		} catch (URISyntaxException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
        	try {
        		imgMatch = uri.toURL();
 		} catch (MalformedURLException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
        
       	iconMatch = new ImageIcon(imgMatch);
        setSearchButton(new JButton("Search", iconMatch));
        buttonPanel.add(getSearchButton()); 
        getSearchButton().addActionListener(this);
        getSearchButton().setToolTipText("Click this button to search for matching words.");

        // Panel buttonPanel
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,20,5,20));
        c.gridheight = 1;
       	c.gridy = 14;
        clPanel.add(buttonPanel, c);
        
        // Progress Bar
        progressBar = new JProgressBar(0,100);
        c.gridy = 15;
        clPanel.add(progressBar, c);
        progressBar.setValue(0);
		progressBar.setStringPainted(true);
        
        		        
		/* Final GUI Initialisations */
		matchesLabel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        // Finish setting up the content pane and its border.
		clPanel.setBorder(BorderFactory.createCompoundBorder(
                                 BorderFactory.createLineBorder(Color.black),
                                 BorderFactory.createEmptyBorder(5,5,5,5)));
        
		// Define the main panel of the Applet
        //setContentPane(clPanel);
        
        // Scroll bug initialisation.
        // Get dimension of scroll area to overcome scroll bug with GridBagLayout
        addtolist_now(null, "Line 1");
        addtolist_now(null, "Line 2");
        addtolist_now(null, "Line 3");
        addtolist_now(null, "Line 4");
        addtolist_now(null, "Line 5");
        addtolist_now(null, "Line 4");
        addtolist_now(null, "Line 7");
        addtolist_now(null, "Line 8");
        addtolist_now(null, "Line 9");
        addtolist_now(null, "Line 10");
        listDimension = dictlist.getSize();
        listScrollPane.setSize(listDimension);
        dictlist.setMinimumSize(listDimension);
        //listScrollPane.setPreferredSize(listDimension);
        listScrollPane.setMinimumSize(listDimension);
        listScrollPane.setMaximumSize(listDimension);
        listModel.clear();
                 
        addtolist_now(null, "Enter letters and click Search.");
                
        /* Other Initialisations */
        try {
			//uri = new URI("file:///F:/eclipseModelingSpace/CrossLexic/lib/resource/dictionarywords");
        	uri = new URI("file://" + System.getProperty("user.dir") + "/lib/resource/dictionarywords");
			dictionaryResource = uri.toURL();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       // dictionaryResource = this.getClass().getResource("/" + Dictionary.filename);
               
        return;
    }
    
    
    public void destroy() {
        //Set up the UI.
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    clFrame.getContentPane().removeAll();
                }
            });
        } catch (Exception e) { 
        		// Failure needs no words on impending destruction. 
      	}
    }
    
    public void dispose(){
    	clFrame.dispose();
    }


	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		// Button Search or Press Enter
	    if (source == getSearchButton()) {
	    	if (getSearchButton().getText() == "Search") {
	    		if (menu_activated) {
	    			listModel.clear();
	    			menu_activated = false;
	    		}
	    		searchThread = new Thread(new Search()) ;
	    		searchThread.start();
	    		getSearchButton().grabFocus();
	    	}
	    	else {
	    		if (searchThread.isAlive())
	    			searchThread = null;
	    		// Wait for thread to end
	    		try {searchThread.join(); Thread.sleep(100);}
	    		catch (Exception InterruptException) {}
	    		addtolist(iconAlert, "Search aborted at " + progressBar.getValue() + "%.");
	    	}
	        return;
	    }
	    
	    
	    // Button Clear all
	    if (source == getClearButton()) {
	    	//System.out.println("CLEAR START");
	    	/*
	    	for (int i = 0 ; i < MAXSIZE ; i++) {
	    		//System.out.println("CLEAR START");
	    		//System.out.println(i + getLetterText()[i].getText());
	    	    getLetterText()[i].setText("");
	    		getLetterText()[i].setValue(null);
	    		//System.out.println(i + getLetterText()[i].getText());
	    		//System.out.println("CLEAR END");
	    	}*/
	    	listModel.clear();
	    	progressBar.setValue(0);
	    	addtolist_now(null, "Enter letters and click Search.");
	    	return;
	    }
	    
	    // Combo Box wordlength
	    if (source == getWordlengthComboBox()) {
	    	if (menu_activated) {
    			listModel.clear();
    			menu_activated = false;
    		}
	    	setWordlength(Integer.parseInt(getWordlengthComboBox().getSelectedItem().toString()));
	    /*	for (int i = 0 ; i < MAXSIZE ; i++) {
	    		if (i < getWordlength()) {
	    			getLetterText()[i].setVisible(true);
	    			getLetterText()[i].setEnabled(true);
	    		}
	    		else {
	    			getLetterText()[i].setText("");
	    			getLetterText()[i].setValue(null);
	    			getLetterText()[i].setEnabled(false);
	    			getLetterText()[i].setVisible(false);
	    		}
	    	}*/
	    	addtolist_now(null, "Word length set to " + getWordlength() + " characters.");
	      	return;
	    }
	  
	    // MenuItem Help
	    if (source == helpMenuItem) {
	    	app_help();
	    	menu_activated = true;
	    	return;
	    }
	    
	    // MenuItem About
	    if (source == aboutMenuItem) {
	    	app_about();
	    	menu_activated = true;
	    	return;
	    }

	    // MenuItem License
	    if (source == licMenuItem) {
	    	app_license();
	    	menu_activated = true;
	    	return;
	    }
	}
	

	/* Key Listener method group */
	public void keyTyped(KeyEvent e) {
		Object source = e.getSource();
		Character key_typed;
			
		/*for (int i = 0 ; i < MAXSIZE ; i++) {
			if (source == getLetterText()[i]) {
				key_typed = e.getKeyChar();
				if (Character.isLetter(key_typed)) {
					getLetterText()[i].setText("");
					getLetterText()[i].setValue(key_typed.toString());
				}
				else if (Character.isSpaceChar(key_typed) ||
						 key_code == KeyEvent.VK_BACK_SPACE ||
					     key_code == KeyEvent.VK_DELETE) {
					getLetterText()[i].setText("");
					getLetterText()[i].setValue(null);
				}
				//addtolist_now(null, "KEY TYPED: " + e.getKeyChar() + " " + key_code);
			}
		} */
	}
	
	/**
	 * Gets the number of filled letters.
	 * @return
	 */
	public int getLettersLength(){
		int number  = 0;/*
		for(int i = 0; i < wordlength; i ++){
			//System.out.println(i + getLetterText()[i].getText());
			if(!getLetterText()[i].getText().trim().equals(""))
				number++;
		}*/
		return number;
	}
	public void keyPressed(KeyEvent e) {
		key_code = e.getKeyCode();
    	return;
	}
	public void keyReleased(KeyEvent e) {
		key_code = e.getKeyCode();
    	return;
    }
    

    protected MaskFormatter createFormatter(String s) {
	    MaskFormatter formatter = null;
	    try {
	        formatter = new MaskFormatter(s);
	    } catch (java.text.ParseException exc) {
	    	addtolist_now(iconAlert, "Java formatter for character input is bad.");
	    }
	    return formatter;
	}
	

	protected static void app_setEnabled(boolean enabler) {
		menu.setEnabled(enabler);
		getWordlengthComboBox().setEnabled(enabler);
	/*	for (int i = 0 ; i < MAXSIZE ; i++)
    	    getLetterText()[i].setEditable(enabler);;
		for (int i = 0 ; i < getWordlength(); i++) 
			getLetterText()[i].setEnabled(enabler);*/
		getClearButton().setEnabled(enabler);
		if (enabler)
			getSearchButton().setText("Search");
		else
			getSearchButton().setText("Stop");
		
		return;
	}
	

	protected static void app_help() {
		listModel.clear();
		progressBar.setValue(0);
				
		addtolist_now(iconHelp, "Crosslexic " + version + " Help.");
		addtolist_now(null, " ");
		addtolist_now(null, "Crosslexic scans through a dictionary looking for"); 
		addtolist_now(null, "words that match the entered characters. This is"); 
		addtolist_now(null, "ideal for solving crosswords puzzles. It's also"); 
		addtolist_now(null, "called cheating!"); 
		addtolist_now(null, " ");
		addtolist_now(null, "You could use a Thesaurus or even the Unix grep"); 
		addtolist_now(null, "command, however a rich personal vocabulary will");	
		addtolist_now(null, "always serve you better in life.");	
		addtolist_now(null, " ");	
		addtolist_now(null, "Usage:");	
		addtolist_now(null, "Select the length of the word you want to solve.");	
		addtolist_now(null, "Type in the known characters in the text boxes and");
		addtolist_now(null, "click on the 'Search' button. Results will be placed"); 
		addtolist_now(null, "in the 'Matches' field. Only alphabetic characters"); 
		addtolist_now(null, "are allowed.");
		addtolist_now(null, " ");
		addtolist_now(null, "You can clear individual characters by pressing the");
		addtolist_now(null, "BACKSPACE, DELETE or SPACEBAR keys. You can");
		addtolist_now(null, "overwrite individual characters by placing the cursor");
		addtolist_now(null, "in the text box and pressing any alphabetic key.");
		addtolist_now(null, " ");
		addtolist_now(null, "You can revise your characters and search again or"); 
		addtolist_now(null, "you can click the 'Clear' button to start afresh.");
		addtolist_now(null, " ");
		addtolist_now(null, "Progress of the search through the dictionary");
		addtolist_now(null, "is displayed below and if the search is taking too");
		addtolist_now(null, "long you can click the 'Stop' button.");
		addtolist_now(null, " ");
		addtolist_now(null, "Refer to the license for terms and conditions.");
		addtolist_now(null, " ");
		addtolist_now(iconGuru, "Enjoy, Arthur Gouros, 2010.");
		dictlist.setSelectedIndex(0);
        dictlist.ensureIndexIsVisible(0);
 
        return;
	}
	

	protected static void app_about() {
		listModel.clear();
		progressBar.setValue(0);
				
		addtolist_now(iconAbout, "Crosslexic " + version + " by Arthur Gouros.");
		addtolist_now(null, "This Java program evolved from the Phonetics");
		addtolist_now(null, "code base which also does dictionary searches"); 
		addtolist_now(null, "for matching words in a similar way. It's only"); 
		addtolist_now(null, "purpose is to find that elusive word."); 
		addtolist_now(null, " ");
		addtolist_now(null, "Yes, Arthur Gouros likes doing crosswords. ");
		addtolist_now(null, " ");
		addtolist_now(null, "Refer to the license for terms and conditions.");
		addtolist_now(null, " ");
		addtolist_now(iconGuru, "Enjoy, Arthur Gouros.");
		dictlist.setSelectedIndex(0);
        dictlist.ensureIndexIsVisible(0);
        
        return;
	}


   	protected static void app_license() {
		listModel.clear();
		progressBar.setValue(0);
        
        JOptionPane.showMessageDialog(clFrame, 
            "Crosslexic, a tool for solving crossword puzzles.\n"
          + "Copyright (c) 2010-2011, Arthur Gouros\n"
          + "All rights reserved.\n"
          + "\n"
          + "Redistribution and use in source and binary forms, with or without\n"
          + "modification, are permitted provided that the following conditions are met:\n"
          + "\n"
          + "- Redistributions of source code must retain the above copyright notice,\n"
          + "  this list of conditions and the following disclaimer.\n"
          + "- Redistributions in binary form must reproduce the above copyright notice,\n"
          + "  this list of conditions and the following disclaimer in the documentation\n"
          + "  and/or other materials provided with the distribution.\n"
          + "- Neither the name of Arthur Gouros nor the names of its contributors\n"
          + "  may be used to endorse or promote products derived from this software\n"
          + "  without specific prior written permission.\n"
          + "\n"
          + "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\"\n"
          + "AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE\n"
          + "IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE\n"
          + "ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE\n"
          + "LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n"
          + "CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF\n"
          + "SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS\n"
          + "INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN\n"
          + "CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)\n"
          + "ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE\n"
          + "POSSIBILITY OF SUCH DAMAGE.",
            "Crosslexic license", JOptionPane.PLAIN_MESSAGE);
        
        return;
	}
	

	protected static JLabel createLabel(ImageIcon icon, String text) {
	    JLabel label = new JLabel();
	    label.setIcon(icon);
	    label.setText(text);
	    
	    return label;
	}
		

	protected static class myListCellRenderer extends DefaultListCellRenderer {
		
		private static final long serialVersionUID = 1L;
	    
		public Component getListCellRendererComponent(JList list, Object value,
		         int index, boolean isSelected, boolean cellHasFocus) {

			setOpaque(true);
		    setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
		    setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
			
		    if (value instanceof JLabel) {
		        JLabel jl = (JLabel) value;
		        setText(jl.getText());
		        setIcon(jl.getIcon());
		    } 
		    else if (value instanceof String)
		        setText((String) value);
		    else
		        setText(value.toString());
		    
			return (JLabel) this;
		}
	}


    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        try {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Crosslexic crosslexic = new Crosslexic ();
                }
            });
        } catch (Exception e) {
            System.err.println("GUI didn't successfully complete");
        }
    }


	public static JComboBox getWordlengthComboBox() {
		return wordlengthComboBox;
	}


	public static void setWordlengthComboBox(JComboBox wordlengthComboBox) {
		Crosslexic.wordlengthComboBox = wordlengthComboBox;
	}


	public static int getWordlength() {
		return wordlength;
	}


	public static void setWordlength(int wordlength) {
		Crosslexic.wordlength = wordlength;
	}


	public static JButton getSearchButton() {
		return searchButton;
	}


	public static void setSearchButton(JButton searchButton) {
		Crosslexic.searchButton = searchButton;
	}


	public static JButton getClearButton() {
		return clearButton;
	}


	public static void setClearButton(JButton clearButton) {
		Crosslexic.clearButton = clearButton;
	}


	public static JFormattedTextField[] getLetterText() {
		return letterText;
	}


	public static void setLetterText(JFormattedTextField[] letterText) {
		Crosslexic.letterText = letterText;
	}


	/**
	 * @return the listModel
	 */
	public static DefaultListModel getListModel() {
		return listModel;
	}


	/**
	 * @return the menu_activated
	 */
	public static boolean isMenu_activated() {
		return menu_activated;
	}


	/**
	 * @return the key_code
	 */
	public static int getKey_code() {
		return key_code;
	}


	/**
	 * @return the progressBar
	 */
	public static JProgressBar getProgressBar() {
		return progressBar;
	}


	/**
	 * @return the searchThread
	 */
	public static Thread getSearchThread() {
		return searchThread;
	}


	/**
	 * @param searchThread the searchThread to set
	 */
	public static void setSearchThread(Thread searchThread) {
		Crosslexic.searchThread = searchThread;
	}

}
