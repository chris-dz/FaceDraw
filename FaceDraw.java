import java.awt.Graphics;
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
        int frameWidth = 1200;
        int frameHeight = 800;
        int margin = 30;
        List<Face> FaceList = new ArrayList<>();
        Random random = new Random();
        int count = random.nextInt(8) + 3;
        for (; count > 0; count --) {
            FaceList.add(new Face(frameWidth - margin, frameHeight - margin));
        }

        FaceFrame faceFrame = new FaceFrame(frameWidth, frameHeight, FaceList);
    }
}

class Face extends Oval {
    private int smiling;    // -1 frowning, 0 neutral, 1 smiling

    private Oval leftEye;
    private Oval rightEye;
    private Arc mouth;

    public Face() {
        // Unknown frame dimensions... Assuming 300 by 200:
        setRandomDimensions(300, 200);
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
        setEyesAndMouth(widthIn, heightIn);
    }

    private void setRandomDimensions(int frameWidthIn, int frameHeightIn) {
        // Use the least of the frame's dimensions, so that ovals don't have the tendency 
        // to stretch horizontally or vertically in correlation with on the screen/frame size
        int minFrameDimension = (Math.min(frameWidthIn, frameHeightIn));
        // Large faces tend to overlap a lot. Limit the max size to a fraction of the frame dimensions:
        minFrameDimension = (int) (minFrameDimension * 0.5);
        // Prevent the face from being too thin or too wide in relation to its height
        int minFaceDimension = (int) (minFrameDimension * 0.3);
        int width = getRandomValue(minFaceDimension, minFrameDimension);
        int height = getRandomValue(minFaceDimension, minFrameDimension);
        setWidth(width);
        setHeight(height);
        setPositionX(getRandomValue(0, frameWidthIn - width));
        setPositionY(getRandomValue(0, frameHeightIn - height));
        setSmiling(getRandomValue(0, 3) - 1);

        // Add eyes and mouth
        setEyesAndMouth(width, height);
    }

    private void setEyesAndMouth(int width, int height) {
        int x = getPositionX() + (width / 2 - 10 * (width / 60));
        int y = getPositionY() + (height / 2 - height / 6);
        int w = width / 10;
        int h = height / 4;

        leftEye = new Oval(x, y, w, h);

        x = getPositionX() + (width / 2 + 4 * (width / 60));
        rightEye = new Oval(x, y, w, h);

        x = getPositionX() + width / 4;
        y = getPositionY() + (int) (height * 0.7);
        w = (int) (width / 2);
        h = (int) (height * 0.2);
        mouth = new Arc(x, y, w, h, smiling);
    }

    private int getRandomValue(int min, int max) {
        if (min >= max || min < 0 || max < 0) {
            throw new IllegalArgumentException("The value of min must be less " +
                "than the value of max and both of them must be positive integers. " +
                "Only min may be 0");
        }
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawOval(getPositionX(), getPositionY(), getWidth(), getHeight());
        leftEye.paintComponent(g);
        rightEye.paintComponent(g);
        mouth.paintComponent(g);
    }

    @Override
    public String toString() {
        return(String.format("Face: x=%d, y=%d, width=%d, height=%d, smiling=%s",
            getPositionX(), getPositionY(), getWidth(), getHeight(),
            (smiling < 0 ? "Frowning" : (smiling > 0 ? "Smiling" : "Neutral"))));
    }

    public void setSmiling(int smilingIn) {
        smiling = smilingIn;
        if (mouth != null) {
            mouth.setSmiling(smilingIn);
        }
    }
    public int getSmiling() { return smiling; }
}

class Arc extends Oval {
    private int smiling = 0;

    public Arc(int positionXIn, int positionYIn, int widthIn, int heightIn, int smilingIn) {
        setPositionX(positionXIn);
        setPositionY(positionYIn);
        setWidth(widthIn);
        setHeight(heightIn);
        setSmiling(smilingIn);
    }

    @Override
    public void paintComponent(Graphics g) {
        // super.paintComponent(g);
        if (smiling < 0) {
            g.drawArc(getPositionX(), getPositionY() - getHeight(), getWidth(), getHeight(), 190, 160);
        } else if (smiling == 0) {
            g.drawLine(getPositionX(), getPositionY(), getPositionX() + getWidth(), getPositionY());
        } else {
            g.drawArc(getPositionX(), getPositionY(), getWidth(), getHeight(), 10, 160);
        }
    }

    public void setSmiling(int smilingIn) { smiling = smilingIn; }
    public int getSmiling() { return smiling; }
}

class Oval extends JComponent {
    private int positionX;
    private int positionY;
    private int height;
    private int width;

    public Oval() {

    }

    public Oval(int positionXIn, int positionYIn, int widthIn, int heightIn) {
        setPositionX(positionXIn);
        setPositionY(positionYIn);
        setWidth(widthIn);
        setHeight(heightIn);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawOval(getPositionX(), getPositionY(), getWidth(), getHeight());
    }

    public void setPositionX(int positionXIn) { positionX = positionXIn; }
    public int getPositionX() { return positionX; }
    public void setPositionY(int positionYIn) { positionY = positionYIn; }
    public int getPositionY() { return positionY; }
    public void setHeight(int heightIn) { height = heightIn; }
    public int getHeight() { return height; }
    public void setWidth(int widthIn) { width = widthIn; }
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
        getFaces().forEach(e -> { 
            e.paintComponent(g);
            System.out.println(e);
         });
    }
}

class FaceFrame extends JFrame {
    private JPanel facePanel;
    public FaceFrame(int widthIn, int heightIn, List<Face> FaceList) {
        super("FaceDraw");
        setSize(widthIn, heightIn);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        facePanel = new FacePanel(FaceList);
        setContentPane(facePanel);
        setVisible(true);
    }
}