/*
    Copyright (C) 2020  Krzysztof Dziedzic

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This is a FaceDraw application for Object Oriented Programming class
 * Spring 2020, 1st half-semester
 * 
 * This software is freely distributable under version 3 of the GNU General Public License
 * The full text of the License should be distributed with this software
 * in a file named LICENSE, accompanying this software.
 * 
 * @author Krzysztof Dziedzic
 */
public class FaceDraw implements Runnable {
    private static Random random;
    private static FaceFrame faceFrame;
    private static FacePanel panel;

    public static void main(String[] args) {
        random = new Random();
        faceFrame = new FaceFrame();

        // Unique feature:
        // At random intervals between 1 and 3 seconds a random face may change its mood.
        // If it changes its mood to the same mood it's already in, no changes will be visible on the screen.
        Thread updateMood = new Thread(new FaceDraw());
        updateMood.start();
    }

    @Override
    public void run() {
        while(true) {
            int millis = random.nextInt(3) + 1;
            millis *= 1000;
            try {
                System.out.println("Sleeping " + millis);
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                return;
            }
            if (panel == null) {
                panel = (FacePanel) faceFrame.getFacePanel();
            }
            int index = random.nextInt(panel.getFaces().size());
            int smiling = random.nextInt(3) - 1;
            panel.getFaces().get(index).setSmiling(smiling);
            faceFrame.repaint();
        }
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
        setBounds(positionXIn, positionYIn, widthIn, heightIn);
        setSmiling(smilingIn);
        setEyesAndMouth(widthIn, heightIn);
    }

    private void setRandomDimensions(int frameWidthIn, int frameHeightIn) {
        // Use the least of the frame's dimensions, so that ovals don't have the tendency 
        // to stretch horizontally or vertically in correlation with the screen/frame size
        int minFrameDimension = (Math.min(frameWidthIn, frameHeightIn));
        // Large faces tend to overlap a lot. Limit the max size to a fraction of the frame dimensions:
        minFrameDimension = (int) (minFrameDimension * 0.5);
        // Prevent the face from being too thin or too wide in relation to its height
        int minFaceDimension = (int) (minFrameDimension * 0.3);
        int width = getRandomValue(minFaceDimension, minFrameDimension);
        int height = getRandomValue(minFaceDimension, minFrameDimension);
        setBounds(getRandomValue(0, frameWidthIn - width), getRandomValue(0, frameHeightIn - height), width, height);
        setSmiling(getRandomValue(0, 3) - 1);

        // Add eyes and mouth
        setEyesAndMouth(width, height);
    }

    private void setEyesAndMouth(int width, int height) {
        int x = getX() + (width / 2 - 10 * (width / 60));
        int y = getY() + (height / 2 - height / 6);
        int w = width / 10;
        int h = height / 4;

        leftEye = new Oval(x, y, w, h);

        x = getX() + (width / 2 + 4 * (width / 60));
        rightEye = new Oval(x, y, w, h);

        x = getX() + width / 4;
        y = getY() + (int) (height * 0.7);
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
        leftEye.paintComponent(g);
        rightEye.paintComponent(g);
        mouth.paintComponent(g);
        System.out.println(toString());
    }

    @Override
    public String toString() {
        return(String.format("Face: x=%d, y=%d, width=%d, height=%d, smiling=%s",
            getX(), getY(), getWidth(), getHeight(),
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

class Arc extends JComponent {
    private int smiling = 0;

    public Arc(int positionXIn, int positionYIn, int widthIn, int heightIn, int smilingIn) {
        setBounds(positionXIn, positionYIn, widthIn, heightIn);
        setSmiling(smilingIn);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (smiling < 0) {
            g.drawArc(getX(), getY() - getHeight(), getWidth(), getHeight(), 190, 160);
        } else if (smiling == 0) {
            g.drawLine(getX(), getY(), getX() + getWidth(), getY());
        } else {
            g.drawArc(getX(), getY(), getWidth(), getHeight(), 10, 160);
        }
    }

    public void setSmiling(int smilingIn) {
        if (smiling != smilingIn) {
            smiling = smilingIn;
        }
    }
    public int getSmiling() { return smiling; }
}

class Oval extends JComponent {

    public void setX(int x) { setLocation(x, getY()); }
    public void setY(int y) { setLocation(getX(), y); }
    public void setWidth(int width) { setSize(width, getHeight()); }
    public void setHeight(int height) { setSize(getWidth(), height); }

    public Oval() {

    }

    public Oval(int positionXIn, int positionYIn, int widthIn, int heightIn) {
        setBounds(positionXIn, positionYIn, widthIn, heightIn);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawOval(getX(), getY(), getWidth(), getHeight());
    }
}

class FacePanel extends JPanel {
    private int margin = 30;
    private List<Face> faces;
    public void setFaces(List<Face> faces) { this.faces = faces; }
    public List<Face> getFaces() {
        if (faces == null) {
            faces = Collections.synchronizedList(new ArrayList<>());
        }
        return faces;
    }

    public FacePanel() {
        this(Collections.synchronizedList(new ArrayList<>()));
        initializePanel();
    }

    public FacePanel(JFrame parentFrame) {
        this(Collections.synchronizedList(new ArrayList<>()));
        Rectangle bounds = parentFrame.getBounds();
        bounds.grow(margin * -1, margin * -1);
        setBounds(bounds);
        initializePanel();
    }

    public FacePanel(List<Face> faces) {
        setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getDefaultConfiguration().getBounds());
        this.faces = faces;
    }

    private void initializePanel() {
        Random random = new Random();
        int count = random.nextInt(8) + 3;
        for (; count > 0; count --) {
            faces.add(new Face(getWidth(), getHeight()));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        getFaces().forEach(e -> { 
            e.paintComponent(g);
         });
    }
}

class FaceFrame extends JFrame {
    private JPanel facePanel;

    public JPanel getFacePanel() { return facePanel; }

    public FaceFrame() {
        super("FaceDraw");
        setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getDefaultScreenDevice().getDefaultConfiguration().getBounds());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        facePanel = new FacePanel(this);
        setContentPane(facePanel);
        setVisible(true);
    }

    public FaceFrame(int widthIn, int heightIn, List<Face> FaceList) {
        super("FaceDraw");
        setSize(widthIn, heightIn);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        facePanel = new FacePanel(FaceList);
        setContentPane(facePanel);
        setVisible(true);
    }
}