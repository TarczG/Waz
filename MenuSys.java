import java.awt.*;
import java.awt.event.*;
/** klasa dodajaca do aplikacji menusystemowe*/
public class MenuSys extends Frame implements ActionListener {
    public MenuSys() {
        super("System menu");

/** dodanie obiektu SnakeCanvas */
        SnakeCanvas c= new SnakeCanvas();
        // c.setPreferredSize(new Dimension(640,400));
        c.setVisible(true);
        c.setFocusable(true);
        this.add(c);
        TextField instruction = new TextField("Press SPACE to pause the game, or  press G to hide the grid");
        instruction.setEditable(false);
        this.add(instruction,"South");
        this.setVisible(true);
        this.setSize(new Dimension(SnakeCanvas.BoxWidth*SnakeCanvas.GridWidth+10,SnakeCanvas.BoxHeight*SnakeCanvas.GridHeight+100));

/** dodanie obslugi zdarzen okna */
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
        createMenu();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
    /** metoda tworzaca menu systemowe */
    private void createMenu() {
        MenuBar mb = new MenuBar();
        setMenuBar(mb);


        Menu help = new Menu("About");
        mb.add(help);
        MenuItem about = new MenuItem("About...");
        about.addActionListener(this);
        help.add(about);
    }
    /** metoda obslugujaca zdarzenia menu */
    public void actionPerformed (ActionEvent event)
    {

        if (event.getActionCommand().startsWith("About..."))
        {
            new AboutDialog(this);
        }
    }
    /** klasa otwierajaca nowe okno informacji o programie */
    public class AboutDialog extends Dialog
    {

        AboutDialog (Frame frame)
        {
            super (frame, "About...",true);
            addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent event) {
                    AboutDialog.this.dispose();
                }
            });

            Panel center = new Panel ( new GridLayout(3,1));
            center.add(new Label("***",Label.CENTER));
            center.add(new Label("Snake verion 2.0" ,Label.CENTER));
            center.add(new Label("***",Label.CENTER));
            add (center, "Center");
            Button ok = new Button("Ok");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    AboutDialog.this.dispose();
                }
            });
            Panel okPanel = new Panel();
            okPanel.add(ok);
            add(okPanel,"South");

            pack();
            setLocationRelativeTo(frame);
            setVisible(true);

        }
    }
}
