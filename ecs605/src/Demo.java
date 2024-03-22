import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
import java.util.Random;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.util.Collections;

public class Demo extends Component implements ActionListener {

    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************

    String descs[] = {
            "Original",
            "Negative",
            "Resize",
            "Shift",
            "Resize and Shift",
            "AND",
            "OR",
            "XOR",
            "ADD",
            "MULT",
            "SUB",
            "DIV",
            "Bit Plane slice",
            "log",
            "pow",
            "avrage",
            "waigthed avrage",
            "4-neighbour",
            "8-neighbour",
            "4-neighbour Enhancement",
            "8-neighbour Enhancement",
            "salt and peper",
            "gliter",
            "min",
            "max",
            "mid",
    };
    Demo original = this;
    int opIndex;  //option index for
    int lastOp;
    float scale = 1f;
    float intensify = 1.5f;
    int shift = 0;
    private BufferedImage bi, biFiltered, biRoi;   // the input image saved as bi;//
    int w, h;

    public Demo() {
        try {
            bi = ImageIO.read(new File("../images/fa.png"));

            w = bi.getWidth(null);
            h = bi.getHeight(null);
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }

    public Demo(Demo original) {
        this.original = original;
        try {
            bi = ImageIO.read(new File("../images/fa.png"));

            w = bi.getWidth(null);
            h = bi.getHeight(null);
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }


    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp","gif","jpeg","jpg","png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    public void loadImage() {
        JFileChooser j = new JFileChooser();
        j.showOpenDialog(null);
        try {
            bi = ImageIO.read(j.getSelectedFile());
            w = bi.getWidth(null);
            h = bi.getHeight(null);
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                biFiltered = bi = bi2;
            }
            if (original != this)
            {
                original.bi = bi;
                original.w = w;
                original.h = h;
                original.biFiltered = biFiltered;
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }

    void setOpIndex(int i) {
        lastOp = opIndex;
        opIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();
        g.drawImage(biFiltered, 0, 0,Math.round(w*scale),Math.round(h*scale), null);
    }


    //************************************
    //  Convert the Buffered Image to Array
    //************************************
    private static int[][][] convertToArray(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x,y);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                result[x][y][0]=a;
                result[x][y][1]=r;
                result[x][y][2]=g;
                result[x][y][3]=b;
            }
        }
        return result;
    }

    //************************************
    //  Convert the  Array to BufferedImage
    //************************************
    public BufferedImage convertToBimage(int[][][] TmpArray){

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value

                int p = (a<<24) | (r<<16) | (g<<8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }


    //************************************
    //  Example:  Image Negative
    //************************************
    public BufferedImage ImageNegative(BufferedImage timg){
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = 255-ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255-ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255-ImageArray[x][y][3];  //b
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }


    //************************************
    //  Your turn now:  Add more function below
    //************************************
    public BufferedImage resizeImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] ImageArray = convertToArray(image);          //  Convert the image to array

        // Image resize Operation:
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][1] = Math.round(ImageArray[x][y][1] * intensify);  //r
                if (ImageArray[x][y][1] <0){
                    ImageArray[x][y][1] = 0;
                }
                else if(ImageArray[x][y][1] > 255){
                    ImageArray[x][y][1]=255;
                }
                ImageArray[x][y][2] = Math.round(ImageArray[x][y][2] * intensify);  //g
                if (ImageArray[x][y][2] <0){
                    ImageArray[x][y][2] = 0;
                }
                else if(ImageArray[x][y][2] > 255){
                    ImageArray[x][y][2]=255;
                }
                ImageArray[x][y][3] = Math.round(ImageArray[x][y][3] * intensify);  //b
                if (ImageArray[x][y][3] <0){
                    ImageArray[x][y][3] = 0;
                }
                else if(ImageArray[x][y][3] > 255){
                    ImageArray[x][y][3]=255;
                }
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage PixelVShift(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] ImageArray = convertToArray(image);          //  Convert the image to array
        int rmin = (ImageArray[0][0][1] + shift); 
        int rmax = rmin;
        int gmin = (ImageArray[0][0][2] + shift); 
        int gmax = gmin;
        int bmin = (ImageArray[0][0][3] + shift); 
        int bmax = bmin;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                if(y == 0 && x == 0) {
                    continue;
                }
                ImageArray[x][y][1] = ImageArray[x][y][1] + shift;  //r
                if (ImageArray[x][y][1] < 0){
                    ImageArray[x][y][1] = 0;
                }
                else if(ImageArray[x][y][1] > 255){
                    ImageArray[x][y][1] = 255;
                }

                if (ImageArray[x][y][1] < rmin){
                    rmin = ImageArray[x][y][1];
                }
                else if(ImageArray[x][y][1] > rmax){
                    rmax = ImageArray[x][y][1];
                }
                ImageArray[x][y][2] = ImageArray[x][y][2] + shift;  //g
                
                if (ImageArray[x][y][2] < 0){
                    ImageArray[x][y][2] = 0;
                }
                else if(ImageArray[x][y][2] > 255){
                    ImageArray[x][y][2] = 255;
                }

                if (ImageArray[x][y][2] < gmin){
                    gmin = ImageArray[x][y][2];
                }
                else if(ImageArray[x][y][2] > gmax){
                    gmax = ImageArray[x][y][2];
                }
                ImageArray[x][y][3] = ImageArray[x][y][3] + shift;  //b
                
                if (ImageArray[x][y][3] < 0){
                    ImageArray[x][y][3] = 0;
                }
                else if(ImageArray[x][y][3] > 255){
                    ImageArray[x][y][3] = 255;
                }

                if (ImageArray[x][y][3] < bmin){
                    bmin = ImageArray[x][y][3];
                }
                else if(ImageArray[x][y][3] > bmax){
                    bmax = ImageArray[x][y][3];
                }
            }
        }
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray[x][y][3]=255*((ImageArray[x][y][3]-bmin)/(bmax-bmin));                
                ImageArray[x][y][2]=255*((ImageArray[x][y][2]-gmin)/(gmax-gmin));
                ImageArray[x][y][1]=255*((ImageArray[x][y][1]-rmin)/(rmax-rmin));
            }
        }
        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage AddNoise(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Random random = new Random();
        int randomVal;
        int[][][] ImageArray = convertToArray(image);          //  Convert the image to array
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){

                randomVal = random.nextInt(255);
                ImageArray[x][y][1] = ImageArray[x][y][1] + randomVal;  //r
                if (ImageArray[x][y][1] < 0){
                    ImageArray[x][y][1] = 0;
                }
                else if(ImageArray[x][y][1] > 255){
                    ImageArray[x][y][1] = 255;
                }
                randomVal = random.nextInt(255);

                ImageArray[x][y][2] = ImageArray[x][y][2] + randomVal;  //g
                
                if (ImageArray[x][y][2] < 0){
                    ImageArray[x][y][2] = 0;
                }
                else if(ImageArray[x][y][2] > 255){
                    ImageArray[x][y][2] = 255;
                }
                randomVal = random.nextInt(255);

                ImageArray[x][y][3] = ImageArray[x][y][3] + randomVal;  //b
                
                if (ImageArray[x][y][3] < 0){
                    ImageArray[x][y][3] = 0;
                }
                else if(ImageArray[x][y][3] > 255){
                    ImageArray[x][y][3] = 255;
                }
            }
        }
        return convertToBimage(ImageArray);
    }

    public void input(int index , BufferedImage image) {
        JFrame f = new JFrame("Input");
        JTextField intInput = new JTextField(5);
        JPanel panel = new JPanel();
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {return;}
        });
        intInput.setActionCommand("SetInput");
        intInput.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
            JTextField tf = (JTextField) e.getSource();
            try{
                switch (index) {
                    case 1:
                        intensify = Float.parseFloat(tf.getText());
                        if (intensify > 2f){intensify = 2f;}
                        else if (intensify < 0f){intensify = 0f;}
                        biFiltered = resizeImage(image); /* Resize Image */
                        repaint();
                        f.dispose();
                        return;
                    case 2:
                        shift = Integer.parseInt(tf.getText());
                        biFiltered = PixelVShift(image); /* Pixel Value Shift */
                        repaint();
                        f.dispose();
                        return;
                    case 3:
                        int plane = Integer.parseInt(tf.getText());
                        if (plane > 7) {plane = 7;}
                        else if (plane < 0) {plane = 0;}
                        biFiltered = bitPlaneSlice(plane , image); /* Pixel Value Shift */
                        repaint();
                        f.dispose();
                        return;
                    case 4:
                        double power = Float.parseFloat(tf.getText());
                        if (power > 25) {power = 25;}
                        else if (power < 0.01) {power = 0.01;}
                        biFiltered = powF(power , image); /* Pixel Value Shift */
                        repaint();
                        f.dispose();
                        return;
                }
            }
            catch (Exception ex) {
                System.out.println("Something went wrong.");
            }
        }});
        panel.add(new JLabel("Input:"));
        panel.add(intInput);
        f.add("North", panel);
        f.setVisible(true);
    }

    public BufferedImage addOprand(BufferedImage image1 ,BufferedImage image2 ) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        int[][][] ImageArray1 = convertToArray(image1);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image2);         //  Convert the image to array
        
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1 [x][y][1] += ImageArray2 [x][y][1] * ImageArray2 [x][y][0] / 255;
                if (ImageArray1 [x][y][1] > 255) { ImageArray1 [x][y][1] = 255; }
                ImageArray1 [x][y][2] += ImageArray2 [x][y][2] * ImageArray2 [x][y][0] / 255;
                if (ImageArray1 [x][y][2] > 255) { ImageArray1 [x][y][2] = 255; }
                ImageArray1 [x][y][3] += ImageArray2 [x][y][3] * ImageArray2 [x][y][0] / 255;
                if (ImageArray1 [x][y][3] > 255) { ImageArray1 [x][y][3] = 255; }
            }
        }
        return convertToBimage(ImageArray1);  // Convert the array to BufferedImage
    }

    public BufferedImage multOprand(BufferedImage image1 ,BufferedImage image2 ) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        int[][][] ImageArray1 = convertToArray(image1);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image2);         //  Convert the image to array
        
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1 [x][y][1] = ImageArray1 [x][y][1] * ImageArray2 [x][y][1] * (ImageArray2 [x][y][0] / 255);
                if (ImageArray1 [x][y][1] > 255) { ImageArray1 [x][y][1] = 255; }
                ImageArray1 [x][y][2] = ImageArray1 [x][y][2] * ImageArray2 [x][y][2] * (ImageArray2 [x][y][0] / 255);
                if (ImageArray1 [x][y][2] > 255) { ImageArray1 [x][y][2] = 255; }
                ImageArray1 [x][y][3] = ImageArray1 [x][y][3] * ImageArray2 [x][y][3] * (ImageArray2 [x][y][0] / 255);
                if (ImageArray1 [x][y][3] > 255) { ImageArray1 [x][y][3] = 255; }
            }
        }
        return convertToBimage(ImageArray1);  // Convert the array to BufferedImage
    }

    public BufferedImage subOprand(BufferedImage image1 ,BufferedImage image2 ) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        int[][][] ImageArray1 = convertToArray(image1);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image2);         //  Convert the image to array
        
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray1 [x][y][1] = ImageArray1 [x][y][1] - ImageArray2 [x][y][1] * (ImageArray2 [x][y][0] / 255);
                if (ImageArray1 [x][y][1] < 0) { ImageArray1 [x][y][1] = 0; }
                ImageArray1 [x][y][2] = ImageArray1 [x][y][2] - ImageArray2 [x][y][2] * (ImageArray2 [x][y][0] / 255);
                if (ImageArray1 [x][y][2] < 0) { ImageArray1 [x][y][2] = 0; }
                ImageArray1 [x][y][3] = ImageArray1 [x][y][3] - ImageArray2 [x][y][3] * (ImageArray2 [x][y][0] / 255);
                if (ImageArray1 [x][y][3] < 0) { ImageArray1 [x][y][3] = 0; }
            }
        }
        return convertToBimage(ImageArray1);  // Convert the array to BufferedImage
    }

    public BufferedImage divOprand(BufferedImage image1 ,BufferedImage image2 ) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        int[][][] ImageArray1 = convertToArray(image1);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image2);         //  Convert the image to array
        int temp;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                temp = (ImageArray2 [x][y][1] * (ImageArray2 [x][y][0] / 255));
                if (temp == 0){ temp = 1;}
                ImageArray1 [x][y][1] = ImageArray1 [x][y][1] / temp ;
                temp = (ImageArray2 [x][y][2] * (ImageArray2 [x][y][0] / 255));
                if (temp == 0){ temp = 1;}
                ImageArray1 [x][y][2] = ImageArray1 [x][y][2] / temp ;
                temp = (ImageArray2 [x][y][3] * (ImageArray2 [x][y][0] / 255));
                if (temp == 0){ temp = 1;}
                ImageArray1 [x][y][3] = ImageArray1 [x][y][3] / temp ;
            }
        }
        return convertToBimage(ImageArray1);  // Convert the array to BufferedImage
    }

    public BufferedImage andOprand(BufferedImage image1 ,BufferedImage image2 ) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        int[][][] ImageArray1 = convertToArray(image1);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image2);         //  Convert the image to array
        
        int temp;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][1] > w && ImageArray2[x][y][1] > w ){
                        temp += w;
                    }
                    if (ImageArray1[x][y][1] > w ){
                        ImageArray1[x][y][1] -= w;
                    }
                    if (ImageArray2[x][y][1] > w ){
                        ImageArray2[x][y][1] -= w;
                    }
                }
                ImageArray1[x][y][1] = temp;
                
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][2] > w && ImageArray2[x][y][1] > w ){
                        temp += w;
                    }
                    if (ImageArray1[x][y][2] > w ){
                        ImageArray1[x][y][2] -= w;
                    }
                    if (ImageArray2[x][y][2] > w ){
                        ImageArray2[x][y][2] -= w;
                    }
                }
                ImageArray1[x][y][2] = temp;
                
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][3] > w && ImageArray2[x][y][1] > w ){
                        temp += w;
                    }
                    if (ImageArray1[x][y][3] > w ){
                        ImageArray1[x][y][3] -= w;
                    }
                    if (ImageArray2[x][y][3] > w ){
                        ImageArray2[x][y][3] -= w;
                    }
                }
                ImageArray1[x][y][3] = temp;
            }
        }
        return convertToBimage(ImageArray1);  // Convert the array to BufferedImage
    }

    public BufferedImage orOprand(BufferedImage image1 ,BufferedImage image2 ) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        int[][][] ImageArray1 = convertToArray(image1);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image2);         //  Convert the image to array
        
        int temp;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][1] > w || ImageArray2[x][y][1] > w ){
                        temp += w;
                    }
                    if (ImageArray1[x][y][1] > w ){
                        ImageArray1[x][y][1] -= w;
                    }
                    if (ImageArray2[x][y][1] > w ){
                        ImageArray2[x][y][1] -= w;
                    }
                }
                ImageArray1[x][y][1] = temp;
                
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][2] > w || ImageArray2[x][y][1] > w ){
                        temp += w;
                    }
                    if (ImageArray1[x][y][2] > w ){
                        ImageArray1[x][y][2] -= w;
                    }
                    if (ImageArray2[x][y][2] > w ){
                        ImageArray2[x][y][2] -= w;
                    }
                }
                ImageArray1[x][y][2] = temp;
                
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][3] > w || ImageArray2[x][y][1] > w ){
                        temp += w;
                    }
                    if (ImageArray1[x][y][3] > w ){
                        ImageArray1[x][y][3] -= w;
                    }
                    if (ImageArray2[x][y][3] > w ){
                        ImageArray2[x][y][3] -= w;
                    }
                }
                ImageArray1[x][y][3] = temp;
            }
        }
        return convertToBimage(ImageArray1);  // Convert the array to BufferedImage
    }

    public BufferedImage xorOprand(BufferedImage image1 ,BufferedImage image2 ) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        int[][][] ImageArray1 = convertToArray(image1);          //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image2);         //  Convert the image to array
        
        int temp;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][1] > w ){
                        ImageArray1[x][y][1] -= w;
                        if (ImageArray2[x][y][1] < w ){
                            temp += w;
                        }
                        else {
                            ImageArray2[x][y][1] -= w;
                        }
                    }
                    if (ImageArray2[x][y][1] > w ){
                        ImageArray2[x][y][1] -= w;
                        if (ImageArray1[x][y][1] < w ){
                            temp += w;
                        }
                    }
                }
                ImageArray1[x][y][1] = temp;
                
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][2] > w ){
                        ImageArray1[x][y][2] -= w;
                        if (ImageArray2[x][y][2] < w ){
                            temp += w;
                        }
                        else {
                            ImageArray2[x][y][2] -= w;
                        }
                    }
                    if (ImageArray2[x][y][2] > w ){
                        ImageArray2[x][y][2] -= w;
                        if (ImageArray1[x][y][2] < w ){
                            temp += w;
                        }
                    }
                }
                ImageArray1[x][y][2] = temp;
                
                temp = 0;
                for (int w = 128; w > 0 ; w = w / 2) {
                    if (ImageArray1[x][y][3] > w ){
                        ImageArray1[x][y][3] -= w;
                        if (ImageArray2[x][y][3] < w ){
                            temp += w;
                        }
                        else {
                            ImageArray2[x][y][3] -= w;
                        }
                    }
                    if (ImageArray2[x][y][3] > w ){
                        ImageArray2[x][y][3] -= w;
                        if (ImageArray1[x][y][3] < w ){
                            temp += w;
                        }
                    }
                }
                ImageArray1[x][y][3] = temp;
            }
        }
        return convertToBimage(ImageArray1);  // Convert the array to BufferedImage
    }

    public BufferedImage roiOprand(int index ,BufferedImage image1 ,BufferedImage image2 ) {
        int width = image1.getWidth();
        int height = image1.getHeight();

        int[][][] ImageArray2 = convertToArray(image2);         //  Convert the image to array
        
        int temp;
        for(int y=0; y<height; y++){
            for(int x =0; x<width; x++){
                ImageArray2[x][y][0] = Math.round((ImageArray2[x][y][1] + ImageArray2[x][y][2] + ImageArray2[x][y][3]) / 3);
                ImageArray2[x][y][1] = ImageArray2[x][y][0];
                ImageArray2[x][y][2] = ImageArray2[x][y][0];
                ImageArray2[x][y][3] = ImageArray2[x][y][0];
            }
        }
        biRoi = convertToBimage(ImageArray2);


        return andOprand(image1,biRoi);  // Convert the array to BufferedImage
    }

    public void optand(int index , BufferedImage image) {
        JFileChooser j = new JFileChooser();
        j.showOpenDialog(null);
        try {
            BufferedImage bi1 = ImageIO.read(j.getSelectedFile());
            int w1 = bi1.getWidth(null);
            int h1 = bi1.getHeight(null);
            if (bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2 = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi1, 0, 0, null);
                bi1 = bi2;
            }
            switch (index) {
                case 1: biFiltered = andOprand(image , bi1);
                    return ;
                case 2: biFiltered = orOprand(image , bi1);
                    return ;
                case 3: biFiltered = xorOprand(image , bi1);
                    return ;
                case 4: biFiltered = addOprand(image , bi1);
                    return ;
                case 5: biFiltered = multOprand(image , bi1);
                    return ;
                case 6: biFiltered = subOprand(image , bi1);
                    return ;
                case 7: biFiltered = divOprand(image , bi1);
                    return ;
            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            System.out.println("Image could not be read");

            System.exit(1);
        }
    }

    public BufferedImage logF(BufferedImage image ) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] ImageArray1 = convertToArray(image);         //  Convert the image to array
        int [] LUT = new int[256] ;
        for(int k=0; k<=255; k++){
            LUT[k] = (int)(Math.log(1+k)*255/Math.log(256));
        }

        // To find the bit plane k of an image, k can be 0,1,2,…,7
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray1[x][y][1] = LUT[ImageArray1[x][y][1]];      
                ImageArray1[x][y][2] = LUT[ImageArray1[x][y][2]];      
                ImageArray1[x][y][3] = LUT[ImageArray1[x][y][3]];                
            }
        }
        return convertToBimage(ImageArray1);
    }

    public BufferedImage powF(double p ,BufferedImage image ) {
        int width = image.getWidth();
        int height = image.getHeight();

        int [] LUT = new int[256] ;
        int[][][] ImageArray1 = convertToArray(image);         //  Convert the image to array
        for(int k=0; k<=255; k++){
            LUT[k] = (int)(Math.pow(255,1-p)*Math.pow(k,p));
        }

        // To find the bit plane k of an image, k can be 0,1,2,…,7
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray1[x][y][1] = LUT[ImageArray1[x][y][1]];      
                ImageArray1[x][y][2] = LUT[ImageArray1[x][y][2]];      
                ImageArray1[x][y][3] = LUT[ImageArray1[x][y][3]];                
            }
        }
        return convertToBimage(ImageArray1);
    }

    public BufferedImage bitPlaneSlice(int plane ,BufferedImage image ) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] ImageArray1 = convertToArray(image);         //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image);         //  Convert the image to array
        
        // To find the bit plane k of an image, k can be 0,1,2,…,7
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                ImageArray2[x][y][1] = 0;
                for (int w = 7; w >= 0 ; w --) {
                    if(w == plane && ImageArray1[x][y][1] - (1 << w) >= 0) {
                        ImageArray2[x][y][1] = 255;
                        break;
                    }
                    if (ImageArray1[x][y][1] > (1 << w)) {
                        ImageArray1[x][y][1] -= (1 << w);
                    }
                }
                ImageArray2[x][y][2] = 0;
                for (int w = 7; w >= 0 ; w --) {
                    if(w == plane && ImageArray1[x][y][2] - (1 << w) >= 0) {
                        ImageArray2[x][y][2] = 255;
                        break;
                    }
                    if (ImageArray1[x][y][2] > (1 << w) ){
                        ImageArray1[x][y][2] -= (1 << w);
                    }
                }
                ImageArray2[x][y][3] = 0;
                for (int w = 7; w >= 0 ; w --) {
                    if(w == plane && ImageArray1[x][y][3] - (1 << w) >= 0) {
                        ImageArray2[x][y][3] = 255;
                        break;
                    }
                    if (ImageArray1[x][y][3] > (1 << w) ){
                        ImageArray1[x][y][3] -= (1 << w);
                    }
                }
                
            }
        }
        return convertToBimage(ImageArray2);
    }

    public void TestHisogram( BufferedImage image ) {
        // For this example, I just randomised some data, you would
        // Need to load it yourself...
        int width = image.getWidth();
        int height = image.getHeight();
        
        int HistgramR[] = new int[256];
        int HistgramG[] = new int[256];
        int HistgramB[] = new int[256];
        int HistgramC[] = new int[256];     // chrome as in black and white
        int[][][] ImageArray = convertToArray(image);         //  Convert the image to array
        for(int k=0; k<256; k++){ // Initialisation
            HistgramR[k] = 0;
            HistgramG[k] = 0;
            HistgramB[k] = 0;
            HistgramC[k] = 0;
        }
        for(int y=0; y<height; y++){ // bin histograms
            for(int x=0; x<width; x++){
                HistgramR[ImageArray[x][y][1]]++;
                HistgramG[ImageArray[x][y][2]]++;
                HistgramB[ImageArray[x][y][3]]++;
                if (ImageArray[x][y][1] == ImageArray[x][y][2] && ImageArray[x][y][2] == ImageArray[x][y][3]){
                    HistgramC[ImageArray[x][y][3]]++;
                    }
            }
        }
        Map<Integer, Integer> mapHistoryR = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryG = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryB = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryC = new TreeMap<Integer, Integer>();
        for(int k=0; k<256; k++){ // Initialisation
            mapHistoryR.put(k, HistgramR[k]);
            mapHistoryG.put(k, HistgramG[k]);
            mapHistoryB.put(k, HistgramB[k]);
            mapHistoryC.put(k, HistgramC[k]);
        }
        JFrame frame = new JFrame("Test");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {}
        });
        frame.setLayout(new BorderLayout());
        JPanel plane = new  JPanel();
        plane.add(new Graph(mapHistoryR));
        plane.add(new Graph(mapHistoryG));
        plane.add(new Graph(mapHistoryB));
        plane.add(new Graph(mapHistoryC));
        frame.add(plane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void TestHisogram2( BufferedImage image ) {
        // For this example, I just randomised some data, you would
        // Need to load it yourself...
        int width = image.getWidth();
        int height = image.getHeight();
        
        int HistgramR[] = new int[256];
        int HistgramG[] = new int[256];
        int HistgramB[] = new int[256];
        int HistgramC[] = new int[256];     // chrome as in black and white
        int[][][] ImageArray = convertToArray(image);         //  Convert the image to array
        for(int k=0; k<256; k++){ // Initialisation
            HistgramR[k] = 0;
            HistgramG[k] = 0;
            HistgramB[k] = 0;
            HistgramC[k] = 0;
        }
        for(int y=0; y<height; y++){ // bin histograms
            for(int x=0; x<width; x++){
                HistgramR[ImageArray[x][y][1]]++;
                HistgramG[ImageArray[x][y][2]]++;
                HistgramB[ImageArray[x][y][3]]++;
                if (ImageArray[x][y][1] == ImageArray[x][y][2] && ImageArray[x][y][2] == ImageArray[x][y][3]){
                    HistgramC[ImageArray[x][y][3]]++;
                    }
            }
        }
        Map<Integer, Integer> mapHistoryR = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryG = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryB = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryC = new TreeMap<Integer, Integer>();
        
        double nHistgramR[] = new double[256];
        double nHistgramG[] = new double[256];
        double nHistgramB[] = new double[256];
        double nHistgramC[] = new double[256];
        for(int k=0; k<256; k++){ // Normalisation
            nHistgramR[k] = HistgramR[k]/height/width * 100; // r
            nHistgramG[k] = HistgramG[k]/height/width * 100; // g
            nHistgramB[k] = HistgramB[k]/height/width * 100; // b
            nHistgramC[k] = HistgramC[k]/height/width * 100; // b
        }
        for(int k=0; k<256; k++){ // Initialisation
            mapHistoryR.put(k,  (int)Math.round(nHistgramR[k]));
            mapHistoryG.put(k,  (int)Math.round(nHistgramG[k]));
            mapHistoryB.put(k,  (int)Math.round(nHistgramB[k]));
            mapHistoryC.put(k,  (int)Math.round(nHistgramC[k]));
        }
        JFrame frame = new JFrame("Test");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {}
        });
        frame.setLayout(new BorderLayout());
        JPanel plane = new  JPanel();
        plane.add(new Graph(mapHistoryR));
        plane.add(new Graph(mapHistoryG));
        plane.add(new Graph(mapHistoryB));
        plane.add(new Graph(mapHistoryC));
        frame.add(plane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void TestHisogram3( BufferedImage image ) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        int HistgramR[] = new int[256];
        int HistgramG[] = new int[256];
        int HistgramB[] = new int[256];
        int HistgramC[] = new int[256];     // chrome as in black and white
        int[][][] ImageArray = convertToArray(image);         //  Convert the image to array
        for(int k=0; k<256; k++){ // Initialisation
            HistgramR[k] = 0;
            HistgramG[k] = 0;
            HistgramB[k] = 0;
            HistgramC[k] = 0;
        }
        for(int y=0; y<height; y++){ // bin histograms
            for(int x=0; x<width; x++){
                HistgramR[ImageArray[x][y][1]]++;
                HistgramG[ImageArray[x][y][2]]++;
                HistgramB[ImageArray[x][y][3]]++;
                if (ImageArray[x][y][1] == ImageArray[x][y][2] && ImageArray[x][y][2] == ImageArray[x][y][3]){
                    HistgramC[ImageArray[x][y][3]]++;
                    }
            }
        }
        Map<Integer, Integer> mapHistoryR = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryG = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryB = new TreeMap<Integer, Integer>();
        Map<Integer, Integer> mapHistoryC = new TreeMap<Integer, Integer>();

        double nHistgramR[] = new double[256];
        double nHistgramG[] = new double[256];
        double nHistgramB[] = new double[256];
        double nHistgramC[] = new double[256];
        for(int k=0; k<256; k++){ // Normalisation
            nHistgramR[k] = HistgramR[k]/height/width * 100; // r
            nHistgramG[k] = HistgramG[k]/height/width * 100; // g
            nHistgramB[k] = HistgramB[k]/height/width * 100; // b
            nHistgramC[k] = HistgramC[k]/height/width * 100; // b
        }
        int lastR = 0;
        int lastG = 0;
        int lastB = 0;
        int lastC = 0;
        for(int k=0; k<256; k++){ // Initialisation
            mapHistoryR.put(k, Math.round((HistgramR[k] + lastR) / height / width ));
            lastR += HistgramR[k];
            HistgramR[k] = lastR;
            mapHistoryG.put(k, Math.round((HistgramG[k] + lastG) / height / width ));
            lastG += HistgramG[k];
            HistgramG[k] = lastG;
            mapHistoryB.put(k, Math.round((HistgramB[k] + lastB) / height / width ));
            lastB += HistgramB[k];
            HistgramB[k] = lastB;
            mapHistoryC.put(k, Math.round((HistgramC[k] + lastC) / height / width ));
            lastC += HistgramC[k];
            HistgramC[k] = lastC;
        }
        JFrame frame = new JFrame("Test");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {}
        });
        frame.setLayout(new BorderLayout());
        JPanel plane = new  JPanel();
        plane.add(new Graph(mapHistoryR));
        plane.add(new Graph(mapHistoryG));
        plane.add(new Graph(mapHistoryB));
        plane.add(new Graph(mapHistoryC));
        frame.add(plane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    //************************************
    //  You need to register your functioin here
    //************************************

    public BufferedImage avrageF(int c, int m, int a, int d, BufferedImage image ) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] ImageArray1 = convertToArray(image);         //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image);         //  Convert the image to array

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                if (x - 1 < 0) {
                    if (y - 1 < 0) {
                        ImageArray2[x][y][1] =  (m * ImageArray1[x][y][1] + 
                                                        a * ImageArray1[x][y+1][1] + 
                                                        a * ImageArray1[x+1][y][1] + 
                                                        c * ImageArray1[x+1][y+1][1])/d;

                        ImageArray2[x][y][2] = (m * ImageArray1[x][y][2] + 
                                                        a * ImageArray1[x][y+1][2] + 
                                                        a * ImageArray1[x+1][y][2] + 
                                                        ImageArray1[x+1][y+1][2]) / d;

                        ImageArray2[x][y][3] = (m * ImageArray1[x][y][3] + 
                                                        a * ImageArray1[x][y+1][3] + 
                                                        a * ImageArray1[x+1][y][3] + 
                                                        c * ImageArray1[x+1][y+1][3]) / d;
                    }
                    else if (y + 1 >= height) {
                        ImageArray2[x][y][1] = (m * ImageArray1[x][y][1] + 
                                                        a * ImageArray1[x][y-1][1] + 
                                                        a * ImageArray1[x+1][y][1] + 
                                                        c * ImageArray1[x+1][y-1][1]) / d;

                        ImageArray2[x][y][2] = (m * ImageArray1[x][y][2] + 
                                                        a * ImageArray1[x][y-1][2] + 
                                                        a * ImageArray1[x+1][y][2] + 
                                                        c * ImageArray1[x+1][y-1][2]) / d;

                        ImageArray2[x][y][3] = (m * ImageArray1[x][y][3] + 
                                                        a * ImageArray1[x][y-1][3] + 
                                                        a * ImageArray1[x+1][y][3] + 
                                                        c * ImageArray1[x+1][y-1][3]) / d;
                    }
                    else {
                        ImageArray2[x][y][1] = (a * ImageArray1[x][y-1][1] + 
                                                        m * ImageArray1[x][y][1] + 
                                                        a * ImageArray1[x][y+1][1] + 
                                                        c * ImageArray1[x+1][y-1][1] + 
                                                        a * ImageArray1[x+1][y][1] + 
                                                        c * ImageArray1[x+1][y+1][1]) / d;

                        ImageArray2[x][y][2] = (a * ImageArray1[x][y-1][2] + 
                                                        m * ImageArray1[x][y][2] + 
                                                        a * ImageArray1[x][y+1][2] + 
                                                        c * ImageArray1[x+1][y-1][2] + 
                                                        a * ImageArray1[x+1][y][2] + 
                                                        c * ImageArray1[x+1][y+1][2]) / d;

                        ImageArray2[x][y][3] = (2 * ImageArray1[x][y-1][3] + 
                                                        m * ImageArray1[x][y][3] + 
                                                        a * ImageArray1[x][y+1][3] + 
                                                        c * ImageArray1[x+1][y-1][3] + 
                                                        a * ImageArray1[x+1][y][3] + 
                                                        c * ImageArray1[x+1][y+1][3]) / d;
                    }
                }
                else if (y - 1 < 0) {
                    if (x + 1 >= width) {
                        ImageArray2[x][y][1] = (a * ImageArray1[x-1][y][1] + 
                                                        c * ImageArray1[x-1][y+1][1] + 
                                                        m * ImageArray1[x][y][1] + 
                                                        a * ImageArray1[x][y+1][1]) / d;

                        ImageArray2[x][y][2] = (m * ImageArray1[x][y][2] + 
                                                        a * ImageArray1[x][y+1][2] + 
                                                        a * ImageArray1[x-1][y][2] + 
                                                        c * ImageArray1[x-1][y+1][2]) / d;

                        ImageArray2[x][y][3] = (m * ImageArray1[x][y][3] + 
                                                        a * ImageArray1[x][y+1][3] + 
                                                        a * ImageArray1[x-1][y][3] + 
                                                        c * ImageArray1[x-1][y+1][3]) / d;
                    }
                    else{
                        ImageArray2[x][y][1] = (a * ImageArray1[x-1][y][1] + 
                                                        c * ImageArray1[x-1][y+1][1] + 
                                                        m * ImageArray1[x][y][1] + 
                                                        a * ImageArray1[x][y+1][1] + 
                                                        a * ImageArray1[x+1][y][1] + 
                                                        c * ImageArray1[x+1][y+1][1]) / d;
                                                        
                        ImageArray2[x][y][2] = (a * ImageArray1[x-1][y][2] + 
                                                        c * ImageArray1[x-1][y+1][2] + 
                                                        m * ImageArray1[x][y][2] + 
                                                        a * ImageArray1[x][y+1][2] + 
                                                        a * ImageArray1[x+1][y][2] + 
                                                        c * ImageArray1[x+1][y+1][2]) / d;
                                                        
                        ImageArray2[x][y][3] = (a * ImageArray1[x-1][y][3] + 
                                                        c * ImageArray1[x-1][y+1][3] + 
                                                        m * ImageArray1[x][y][3] + 
                                                        a * ImageArray1[x][y+1][3] + 
                                                        a * ImageArray1[x+1][y][3] + 
                                                        c * ImageArray1[x+1][y+1][3]) / d;
                    }
                }
                else if (x + 1 >= width) {
                    if (y + 1 >= height) {
                        ImageArray2[x][y][1] = (c * ImageArray1[x-1][y-1][1] + 
                                                        a * ImageArray1[x-1][y][1] + 
                                                        a * ImageArray1[x][y-1][1] + 
                                                        m * ImageArray1[x][y][1]);

                        ImageArray2[x][y][2] = (m * ImageArray1[x][y][2] + 
                                                        a * ImageArray1[x][y-1][2] + 
                                                        a * ImageArray1[x-1][y][2] + 
                                                        c * ImageArray1[x-1][y-1][2]) / d;

                        ImageArray2[x][y][3] = (m * ImageArray1[x][y][3] + 
                                                        a * ImageArray1[x][y-1][3] + 
                                                        a * ImageArray1[x-1][y][3] + 
                                                        c * ImageArray1[x-1][y-1][3]) / d;
                    }
                    else {
                        ImageArray2[x][y][1] = (a * ImageArray1[x-1][y][1] + 
                                                        c * ImageArray1[x-1][y+1][1] + 
                                                        m * ImageArray1[x][y][1] + 
                                                        a * ImageArray1[x][y+1][1] ) / d;
                                                        
                        ImageArray2[x][y][2] = (a * ImageArray1[x-1][y][2] + 
                                                        c * ImageArray1[x-1][y+1][2] + 
                                                        4 * ImageArray1[x][y][2] + 
                                                        a * ImageArray1[x][y+1][2] ) / d;
                                                        
                        ImageArray2[x][y][3] = (a * ImageArray1[x-1][y][3] + 
                                                        c * ImageArray1[x-1][y+1][3] + 
                                                        4 * ImageArray1[x][y][3] + 
                                                        a * ImageArray1[x][y+1][3] ) / d;
                    }
                }
                else if (y + 1 >= height) {
                    ImageArray2[x][y][1] = (a * ImageArray1[x-1][y][1] + 
                                                    c * ImageArray1[x-1][y-1][1] + 
                                                    m * ImageArray1[x][y][1] + 
                                                    a * ImageArray1[x][y-1][1] + 
                                                    a * ImageArray1[x+1][y][1] + 
                                                    c * ImageArray1[x+1][y-1][1]  ) / d;
                                                    
                    ImageArray2[x][y][2] = (a * ImageArray1[x-1][y][2] + 
                                                    c * ImageArray1[x-1][y-1][2] + 
                                                    m * ImageArray1[x][y][2] + 
                                                    a * ImageArray1[x][y-1][2] + 
                                                    a * ImageArray1[x+1][y][2] + 
                                                    c * ImageArray1[x+1][y-1][2] ) / d;
                                                    
                    ImageArray2[x][y][3] = (a * ImageArray1[x-1][y][3] + 
                                                    c * ImageArray1[x-1][y-1][3] + 
                                                    4 * ImageArray1[x][y][3] + 
                                                    a * ImageArray1[x][y-1][3] + 
                                                    a * ImageArray1[x+1][y][3] + 
                                                    c * ImageArray1[x+1][y-1][3] ) / d;

                }
                else {
                    ImageArray2[x][y][1] = (c * ImageArray1[x-1][y-1][1] + 
                                                    a * ImageArray1[x-1][y][1] + 
                                                    c * ImageArray1[x-1][y+1][1] + 
                                                    a * ImageArray1[x][y-1][1] + 
                                                    m * ImageArray1[x][y][1] + 
                                                    a * ImageArray1[x][y+1][1] + 
                                                    c * ImageArray1[x+1][y-1][1] + 
                                                    a * ImageArray1[x+1][y][1] + 
                                                    c * ImageArray1[x+1][y+1][1]) / d;

                    ImageArray2[x][y][2] = (c * ImageArray1[x-1][y-1][2] + 
                                                    a * ImageArray1[x-1][y][2] + 
                                                    c * ImageArray1[x-1][y+1][2] + 
                                                    a * ImageArray1[x][y-1][2] + 
                                                    m * ImageArray1[x][y][2] + 
                                                    a * ImageArray1[x][y+1][2] + 
                                                    c * ImageArray1[x+1][y-1][2] + 
                                                    a * ImageArray1[x+1][y][2] + 
                                                    c * ImageArray1[x+1][y+1][2]) / d;

                    ImageArray2[x][y][3] = (c * ImageArray1[x-1][y-1][3] + 
                                                    a * ImageArray1[x-1][y][3] + 
                                                    c * ImageArray1[x-1][y+1][3] + 
                                                    a * ImageArray1[x][y-1][3] + 
                                                    m * ImageArray1[x][y][3] + 
                                                    a * ImageArray1[x][y+1][3] + 
                                                    c * ImageArray1[x+1][y-1][3] + 
                                                    a * ImageArray1[x+1][y][3] + 
                                                    c * ImageArray1[x+1][y+1][3]) / d;
                }
            }
        }
        float temp = intensify;
        intensify = 1f;
        BufferedImage result = resizeImage(convertToBimage(ImageArray2));
        intensify = temp;
        return result;
    }

    public BufferedImage min3(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        int[][][] ImageArray1 = convertToArray(image);         //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image);         //  Convert the image to array
        for(int y=0; y<height; y++){ // bin histograms
            for(int x=0; x<width; x++){
                if(x - 1 < 0 || x + 1 >= width || y -1 < 0 || y + 1 >= height){
                    ImageArray2[x][y][1] = 0;
                    ImageArray2[x][y][2] = 0;
                    ImageArray2[x][y][3] = 0;
                }
                else{
                    for(int i = 1; i < 4; i++) {
                        int min = 255;
                        for(int j = -1; j < 2; j++) {
                            for(int k = -1; j < 2; j++) {
                                if (ImageArray1[x+j][y+k][i] < min){
                                    min = ImageArray1[x+j][y+k][i];
                                }
                            }
                        }
                        ImageArray2 [x][y][i] = min;
                    }
                }
            }
        }
        return convertToBimage(ImageArray2);
    }

    public BufferedImage max3(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        int[][][] ImageArray1 = convertToArray(image);         //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(image);         //  Convert the image to array
        for(int y=0; y<height; y++){ // bin histograms
            for(int x=0; x<width; x++){
                if(x - 1 < 0 || x + 1 >= width || y -1 < 0 || y + 1 >= height){
                    ImageArray2[x][y][1] = 0;
                    ImageArray2[x][y][2] = 0;
                    ImageArray2[x][y][3] = 0;
                }
                else{
                    for(int i = 1; i < 4; i++) {
                        int max = 0;
                        for(int j = -1; j < 2; j++) {
                            for(int k = -1; j < 2; j++) {
                                if(x + j < 0 || x + j >= width || y - k < 0 || y + k >= height){
                                    continue;
                                }
                                if (ImageArray1[x+j][y+k][i] > max){
                                    max = ImageArray1[x+j][y+k][i];
                                }
                            }
                        }
                        ImageArray2 [x][y][i] = max;
                    }
                }
            }
        }
        return convertToBimage(ImageArray2);
    }

    public BufferedImage mid3(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        int[][][] ImageArray1 = convertToArray(min3(image));         //  Convert the image to array
        int[][][] ImageArray2 = convertToArray(max3(image));         //  Convert the image to array
        for(int y=0; y<height; y++){ // bin histograms
            for(int x=0; x<width; x++){
                ImageArray1[x][y][1] = (ImageArray1[x][y][1] + ImageArray2[x][y][1])/2;
                ImageArray1[x][y][2] = (ImageArray1[x][y][2] + ImageArray2[x][y][2])/2;
                ImageArray1[x][y][3] = (ImageArray1[x][y][3] + ImageArray2[x][y][3])/2;
            }
        }
        return convertToBimage(ImageArray1);
    }

    public BufferedImage gliter(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        int[][][] ImageArray = convertToArray(image);         //  Convert the image to array
        
        Random random = new Random();
        int randomVal;
        for(int y=0; y<height; y++){ // bin histograms
            for(int x=0; x<width; x++){
                randomVal = random.nextInt(256);
                if (randomVal % 255 == 0) {
                    ImageArray[x][y][1] = randomVal;}
                randomVal = random.nextInt(256);
                if (randomVal % 255 == 0) {ImageArray[x][y][2] = randomVal;}
                randomVal = random.nextInt(256);
                if (randomVal % 255 == 0) {ImageArray[x][y][3] = randomVal;}
            }
        }
        return convertToBimage(ImageArray);
    }

    public BufferedImage saltAndPepper(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        int[][][] ImageArray = convertToArray(image);         //  Convert the image to array
        
        Random random = new Random();
        int randomVal;
        for(int y=0; y<height; y++){ // bin histograms
            for(int x=0; x<width; x++){
                randomVal = random.nextInt(256);
                if (randomVal % 255 == 0) {
                    ImageArray[x][y][1] = randomVal;}
                if (randomVal % 255 == 0) {ImageArray[x][y][2] = randomVal;}
                if (randomVal % 255 == 0) {ImageArray[x][y][3] = randomVal;}
            }
        }
        return convertToBimage(ImageArray);
    }

    public void filterImage() {

        if (opIndex == lastOp) {
           return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
                return;
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
                return;
            case 2: input(1, bi); /* Resize Image */
                return;
            case 3: input(2 , bi); /* Pixel Value Shift */
                return;
            case 4: 
                biFiltered = AddNoise(bi);
                JFrame f = new JFrame("Input");
                JTextField intInput = new JTextField(5);
                JTextField intInput2 = new JTextField(5);
                JPanel panel = new JPanel();
                f.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {return;}
                });
                Button doneButton = new Button("Done");
                doneButton.setActionCommand("Done");
                doneButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    try{
                        intensify = Float.parseFloat(intInput.getText());
                        if (intensify > 2f){intensify = 2f;}
                        else if (intensify < 0f){intensify = 0f;}
                        biFiltered = resizeImage(biFiltered); /* Resize Image */
                        shift = Integer.parseInt(intInput2.getText());
                        biFiltered = PixelVShift(biFiltered); /* Resize Image */
                        repaint();
                        f.dispose();
                    }
                    catch (Exception ex) {
                        System.out.println("Something went wrong.");
                    }
                }});
                panel.add(new JLabel("Input resize:"));
                panel.add(intInput);
                panel.add(new JLabel("Input shift:"));
                panel.add(intInput2);
                panel.add(doneButton);
                f.add("North", panel);
                f.setVisible(true);
                return;
            case 5: optand(1 , bi); /* AND */
                return;
            case 6: optand(2 , bi); /* OR */
                return;
            case 7: optand(3 , bi); /* XOR */
                return;
            case 8: optand(4 , bi); /* ADD */
                return;
            case 9: optand(5 , bi); /* MULT */
                return;
            case 10: optand(6 , bi); /* SUB */
                return;
            case 11: optand(7 , bi); /* DIV */
                return;
            case 12: input(3 , bi); /* Bit Plane Slice */
                return;
            case 13: biFiltered = logF(bi); /* Bit Plane Slice */
                return;
            case 14: input(4 , bi); /* Bit Plane Slice */
                return;
            case 15: biFiltered = avrageF(1,1,1,9,bi); /* Bit Plane Slice */
                return;
            case 16: biFiltered = avrageF(1,4,2,16,bi); /* Bit Plane Slice */
                return;
            case 17: biFiltered = avrageF(0,4,-1,1,bi); /* Bit Plane Slice */
                return;
            case 18: biFiltered = avrageF(-1,8,-1,1,bi); /* Bit Plane Slice */
                return;
            case 19: biFiltered = avrageF(0,5,-1,1,bi); /* Bit Plane Slice */
                return;
            case 20: biFiltered = avrageF(-1,9,-1,1,bi); /* Bit Plane Slice */
                return;
            case 21: biFiltered = saltAndPepper(bi); /* Bit Plane Slice */
                return;
            case 22: biFiltered = gliter(bi); /* Bit Plane Slice */
                return;
            case 23: biFiltered = min3(bi); /* Bit Plane Slice */
                return;
            case 24: biFiltered = max3(bi); /* Bit Plane Slice */
                return;
            case 25: biFiltered = mid3(bi); /* Bit Plane Slice */
                return;
            //************************************
            // case x:
            //      biFiltered = function(bi); /* coment */
            //      return;
            //************************************

        }

    }



    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Load")
        {
            loadImage();
            repaint();
        }
        if (e.getActionCommand() == "Hist")
        {
            TestHisogram(biFiltered);
        }
        if (e.getActionCommand() == "Hist2")
        {
            TestHisogram2(biFiltered);
        }
        if (e.getActionCommand() == "Hist3")
        {
            TestHisogram3(biFiltered);
        }
        else if (e.getActionCommand() == "Undo")
        {
            opIndex = lastOp;
            repaint();
        }
        else {
            JComboBox cb = (JComboBox) e.getSource();
            if (cb.getActionCommand().equals("SetFilter")) {
                setOpIndex(cb.getSelectedIndex());
                repaint();
            } else if (cb.getActionCommand().equals("Formats")) {
                String format = (String) cb.getSelectedItem();
                File saveFile = new File("savedimage." + format);
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(saveFile);
                int rval = chooser.showSaveDialog(cb);
                if (rval == JFileChooser.APPROVE_OPTION) {
                    saveFile = chooser.getSelectedFile();
                    try {
                        ImageIO.write(biFiltered, format, saveFile);
                    } catch (IOException ex) {}
                }
            }
        }
    };

    public static void main(String s[]) {
        JFrame f = new JFrame("Image Processing Demo");
        JPanel imagePanel = new JPanel();
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        Demo originalImg = new Demo();
        Demo processedImg = new Demo(originalImg);
        imagePanel.add(originalImg);
        imagePanel.add(processedImg);
        f.add("Center", imagePanel);
        Button loadButton = new Button("Load");
        loadButton.setActionCommand("Load");
        loadButton.addActionListener(processedImg);
        Button infoButton = new Button("Info");
        infoButton.setActionCommand("Hist");
        infoButton.addActionListener(processedImg);
        Button infoButton2 = new Button("Normalized Info");
        infoButton2.setActionCommand("Hist2");
        infoButton2.addActionListener(processedImg);
        Button infoButton3 = new Button("Equalized Info");
        infoButton3.setActionCommand("Hist3");
        infoButton3.addActionListener(processedImg);
        Button undoButton = new Button("Undo");
        undoButton.setActionCommand("Undo");
        undoButton.addActionListener(processedImg);
        JComboBox choices = new JComboBox(processedImg.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(processedImg);
        JComboBox formats = new JComboBox(processedImg.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(processedImg);
        JPanel panel = new JPanel();
        panel.add(loadButton);
        panel.add(infoButton);
        panel.add(infoButton2);
        panel.add(infoButton3);
        panel.add(undoButton);
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }

    protected class Graph extends JPanel {

        protected static final int MIN_BAR_WIDTH = 4;
        private Map<Integer, Integer> mapHistory;

        public Graph(Map<Integer, Integer> mapHistory) {
            this.mapHistory = mapHistory;
            int width = (mapHistory.size() * MIN_BAR_WIDTH) + 11;
            Dimension minSize = new Dimension(width, 128);
            Dimension prefSize = new Dimension(width, 256);
            setMinimumSize(minSize);
            setPreferredSize(prefSize);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (mapHistory != null) {
                int xOffset = 5;
                int yOffset = 5;
                int width = getWidth() - 1 - (xOffset * 2);
                int height = getHeight() - 1 - (yOffset * 2);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(xOffset, yOffset, width, height);
                int barWidth = Math.max(MIN_BAR_WIDTH,
                        (int) Math.floor((float) width
                        / (float) mapHistory.size()));
                int maxValue = 0;
                for (Integer key : mapHistory.keySet()) {
                    int value = mapHistory.get(key);
                    maxValue = Math.max(maxValue, value);
                }
                int xPos = xOffset;
                for (Integer key : mapHistory.keySet()) {
                    int value = mapHistory.get(key);
                    int barHeight = Math.round(((float) value
                            / (float) maxValue) * height);
                    g2d.setColor(new Color(key, key, key));
                    int yPos = height + yOffset - barHeight;
        //Rectangle bar = new Rectangle(xPos, yPos, barWidth, barHeight);
                    Rectangle2D bar = new Rectangle2D.Float(
                            xPos, yPos, barWidth, barHeight);
                    g2d.fill(bar);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.draw(bar);
                    xPos += barWidth;
                }
                g2d.dispose();
            }
        }
    }

}