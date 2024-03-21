import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

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
    };

    int opIndex;  //option index for
    int lastOp;
    float scale = 1f;
    float intensify = 1.5f;
    int shift = 0;
    private BufferedImage bi, biFiltered;   // the input image saved as bi;//
    int w, h;

    public Demo() {
        try {
            bi = ImageIO.read(new File("../images/mars.jpg"));

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



    void setOpIndex(int i) {
        lastOp = opIndex;
        opIndex = i;
    }

    void setScale(float i) {
        if (i > 2f)
        {
            scale = 2;
        }
        else if(i < 0f )
        {
            scale = 0;
        }
        else
        {
            scale = i;
        }
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
                if(y == 0 && x == 0)
                {
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


    //************************************
    //  You need to register your functioin here
    //************************************
    public void filterImage() {

//        if (opIndex == lastOp) {
//            return;
//        }

        switch (opIndex) {
            case 0: biFiltered = bi; /* original */
                return;
            case 1: biFiltered = ImageNegative(bi); /* Image Negative */
                return;
            case 2: 
                JFrame f = new JFrame("Input");
                JPanel panel = new JPanel();
                f.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {}
                });
                JTextField intInput = new JTextField(5);
                intInput.setActionCommand("SetInput");
                intInput.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    JTextField tf = (JTextField) e.getSource();
                    try{
                        intensify = Float.parseFloat(tf.getText());
                        biFiltered = resizeImage(bi); /* Resize Image */
                        repaint();
                        f.dispose();
                    }
                    catch (Exception ex) {
                        System.out.println("Something went wrong.");
                    }
                }});
                panel.add(new JLabel("Input:"));
                panel.add(intInput);
                f.add("North", panel);
                f.pack();
                f.setVisible(true);
                return;
            case 3: biFiltered = PixelVShift(bi); /* Pixel Value Shift */
                return;
            case 4: biFiltered = PixelVShift(bi); /* Pixel Value Shift and Resize */
                biFiltered = resizeImage(biFiltered); /* Resize Image */
                return;
            //************************************
            // case x:
            //      biFiltered = function(bi); /* coment */
            //      return;
            //************************************

        }

    }



    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Undo")
        {
            opIndex = lastOp;
            filterImage();
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
        Demo processedImg = new Demo();
        Demo originalImg = new Demo();
        imagePanel.add(originalImg);
        imagePanel.add(processedImg);
        f.add("Center", imagePanel);
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
        panel.add(undoButton);
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
    }
}