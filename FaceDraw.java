import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This is a FaceDraw application for Object Oriented Programming class
 * Spring 2020, 1st half-semester
 * @author Krzysztof Dziedzic
 */
public class FaceDraw {
    public static void main(String[] args) {
        List<Face> FaceList = new ArrayList<>();
        Random random = new Random();
        int count = random.nextInt(8) + 3;
        for (; count > 0; count --) {
            FaceList.add(new Face());
        }

        FaceFrame faceFrame = new FaceFrame(FaceList);
    }
}

class Face extends Oval {
    private int smiling;    // -1 frowning, 0 neutral, 1 smiling
    private int minSize = 50;

    private Oval leftEye;
    private Oval rightEye;
    private JComponent mouth;

    public Face() {
        // Unknown frame dimensions... Assuming frame is half the screen height and width:
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        setRandomDimensions(bounds.width, bounds.height);
    }

    public Face(int frameWidthIn, int frameHeightIn) {
        this(frameWidthIn, frameHeightIn, 0, 0, 0, 0, 0);
        setRandomDimensions(frameWidthIn, frameHeightIn);
    }

    public Face(int frameWidthIn, int frameHeightIn, int positionXIn, int positionYIn, int widthIn, int heightIn, int smilingIn) {
        // frame dimensions are not used, as position and dimensions of the Face are supplied
        setPositionX(positionXIn);
        setPositionY(positionYIn);
        setWidth(widthIn);
        setHeight(heightIn);
        setSmiling(smilingIn);
    }

    private void setRandomDimensions(int frameWidthIn, int frameHeightIn) {
        int width = getRandomValue(minSize, frameWidthIn);
        int height = getRandomValue(minSize, frameHeightIn);
        setWidth(width);
        setHeight(height);
        setPositionX(getRandomValue(0, frameWidthIn - width));
        setPositionY(getRandomValue(0, frameHeightIn - height));
        setSmiling(getRandomValue(0, 3) - 1);
    }

    private int getRandomValue(int min, int max) {
        if (min >= max || min < 0 || max < 0) {
            throw new IllegalArgumentException("The value of min must be less " +
                "than the value of max and both of them must be positive integers");
        }
        Random random = new Random();
        int result = random.nextInt(max);
        while (result < min) {
            result = random.nextInt(max);
        }
        return result;
    }

    public void setSmiling(int smilingIn) { smiling = smilingIn; }
    public int getSmiling() { return smiling; }
}

class Oval extends JComponent {
    private int positionX;
    private int positionY;
    private int height;
    private int width;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawOval(positionX, positionY, width, height);
    }

    public void setPositionX(int x) { positionX = x; }
    public int getPositionX() { return positionX; }
    public void setPositionY(int y) { positionY = y; }
    public int getPositionY() { return positionY; }
    public void setHeight(int height) { this.height = height; }
    public int getHeight() { return height; }
    public void setWidth(int width) { this.width = width; }
    public int getWidth() { return width; }
}

class FacePanel extends JPanel {
    private List<Face> faces;
    public void setFaces(List<Face> faces) { this.faces = faces; }
    public List<Face> getFaces() {
        if (faces == null) {
            faces = new ArrayList<>();
        }
        return faces;
    }

    public FacePanel() {
        this(new ArrayList<Face>());
    }

    public FacePanel(List<Face> faces) {
        this.faces = faces;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        getFaces().forEach(e -> { e.paintComponent(g); });
    }
}

class FaceFrame extends JFrame {
    private JPanel facePanel;
    public FaceFrame(List<Face> faces) {
        super("FaceDraw");
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        setBounds(bounds);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        facePanel = new FacePanel(faces);
        setContentPane(facePanel);
        setVisible(true);
    }
}