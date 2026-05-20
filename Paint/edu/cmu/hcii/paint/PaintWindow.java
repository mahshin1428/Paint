package edu.cmu.hcii.paint;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class PaintWindow extends JFrame implements PaintObjectConstructorListener {

    private final PaintCanvas canvas;
    private final Actions actions;
    private final PaintObjectConstructor objectConstructor;

    
    public PaintWindow(int initialWidth, int initialHeight) {
        
        super("Paint");
     
        actions = new Actions(this);
        
        setResizable(true);
        
        setBackground(new Color(128, 10, 160));
        
        canvas = new PaintCanvas(initialWidth, initialHeight);

        JButton clearButton = new JButton(actions.clearAction);
        clearButton.setOpaque(false);
        JButton undoButton = new JButton(actions.undoAction);
        undoButton.setOpaque(false);

        JPanel clearUndoPanel = new JPanel();
        clearUndoPanel.setOpaque(false);
        clearUndoPanel.setLayout(new BoxLayout(clearUndoPanel, BoxLayout.Y_AXIS));
        clearUndoPanel.add(clearButton);
        clearUndoPanel.add(undoButton);
        
        JRadioButton pencilButton = new JRadioButton(actions.pencilAction);
        pencilButton.setOpaque(false);
        pencilButton.setSelected(true);
        JRadioButton eraserButton = new JRadioButton(actions.eraserAction);
        eraserButton.setOpaque(false);
        JRadioButton lineButton = new JRadioButton(actions.lineAction);
        lineButton.setOpaque(false);

        ButtonGroup toolButtonGroup = new ButtonGroup();
        toolButtonGroup.add(pencilButton);
        toolButtonGroup.add(eraserButton);
        toolButtonGroup.add(lineButton);
        
        JPanel toolPanel = new JPanel();
        toolPanel.setOpaque(false);
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        toolPanel.add(pencilButton);
        toolPanel.add(eraserButton);
        toolPanel.add(lineButton);
        
        JPanel rPanel = new JPanel(new FlowLayout());
        rPanel.setOpaque(false);
        rPanel.add(new JLabel("Red"));
        JSlider rSlider = new JSlider(0, 255, 0);
        rSlider.setOpaque(false);

        JPanel gPanel = new JPanel(new FlowLayout());
        gPanel.setOpaque(false);
        gPanel.add(new JLabel("Green"));
        JSlider gSlider = new JSlider(0, 255, 255);
        gSlider.setOpaque(false);

        JPanel bPanel = new JPanel(new FlowLayout());
        bPanel.setOpaque(false);
        bPanel.add(new JLabel("Blue"));
        JSlider bSlider = new JSlider(0, 255, 0);
        bSlider.setOpaque(false);

        JComponent currentColorComponent = new JComponent() {
            public void paintComponent(Graphics g) {

                Color oldColor = g.getColor();
                g.setColor(objectConstructor.getColor());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(oldColor);

            }
        };

        ChangeListener colorChangeListener = new ChangeListener() {

            public void stateChanged(ChangeEvent changeEvent) {

	        objectConstructor.setColor(new Color(rSlider.getValue(), gSlider.getValue(), bSlider.getValue()));
                currentColorComponent.repaint();

            }
        };

        rSlider.addChangeListener(colorChangeListener);
        rPanel.add(rSlider);

        gSlider.addChangeListener(colorChangeListener);
        gPanel.add(gSlider);

        bSlider.addChangeListener(colorChangeListener);
        bPanel.add(bSlider);
        
        JPanel colorPanel = new JPanel();
        colorPanel.setOpaque(false);
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        colorPanel.add(rPanel);
        colorPanel.add(gPanel);
        colorPanel.add(bPanel);
        currentColorComponent.setPreferredSize(new Dimension(100, 50));
        colorPanel.add(currentColorComponent);

        JPanel thicknessPanel = new JPanel(new FlowLayout());
        thicknessPanel.setOpaque(false);
        thicknessPanel.add(new JLabel("Thickness"));
        JSlider thicknessSlider = new JSlider(1, 50, 5);
        thicknessSlider.setOpaque(false);
        thicknessSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                objectConstructor.setThickness(thicknessSlider.getValue());
            }
        });
        thicknessPanel.add(thicknessSlider);

        JPanel controlPanel = new JPanel();
        GridBagLayout controlPanelGridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.weighty = 1;
        constraints.insets = new Insets(5, 5, 5, 5);
        controlPanelGridBag.setConstraints(toolPanel, constraints);        
        controlPanelGridBag.setConstraints(colorPanel, constraints);        
        controlPanelGridBag.setConstraints(thicknessPanel, constraints);
        controlPanelGridBag.setConstraints(clearUndoPanel, constraints);
        controlPanel.setLayout(controlPanelGridBag);
        controlPanel.setOpaque(false);
        controlPanel.add(toolPanel);
        controlPanel.add(colorPanel);
        controlPanel.add(thicknessPanel);
        controlPanel.add(clearUndoPanel);
        
        JScrollPane canvasPane = new JScrollPane(canvas);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(canvasPane, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.WEST);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
        
        
        objectConstructor = new PaintObjectConstructor(this);
        objectConstructor.setClass(PencilPaint.class);
        objectConstructor.setColor(new Color(0, 255, 0));
        objectConstructor.setThickness(5);        
        canvas.addMouseListener(objectConstructor);
        canvas.addMouseMotionListener(objectConstructor);
        
        pack();
        setVisible(true);
        
    }
    
    public void setPaintObjectClass(Class<? extends PaintObject> paintObjectClass) {

        objectConstructor.setClass(paintObjectClass);
                
    }

    public void undo() { 
        
        canvas.undo(); 
        if(canvas.sizeOfHistory() == 0) actions.undoAction.setEnabled(false);
    
    }
    
    public void clear() { 
        
        canvas.clear(); 
    
    }
    
    public void constructionBeginning(PaintObject temporaryObject) {
        
        canvas.setTemporaryObject(temporaryObject);   
        
    }
    
    public void constructionContinuing(PaintObject temporaryObject) {
        
        canvas.setTemporaryObject(temporaryObject);   
        
    }
    
    public void constructionComplete(PaintObject finalObject) {
        
        canvas.setTemporaryObject(null);   
        canvas.addPaintObject(finalObject);
        actions.undoAction.setEnabled(true);
        
    }
    
	public void hoveringOverConstructionArea(PaintObject hoverObject) {
		
		canvas.setHoveringObject(hoverObject);
		
	}
    
    
}
