/**
 * <pre>
 *  
 *    |_ Circle.java
 * 
 * </pre>
 * 
 * @author : khkim
 * @since : 2019. 2. 28. 오전 11:34:51
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

class CircleFrame extends JFrame{
	
	public static final int panelWidth = 900;
	public static final int panelHeight = 900;
	
	/**
	 * 반지름 길이를 입력 안받았을 경우 사용되는 기본생성자
	 */
	public CircleFrame(){
        this.setTitle("streami 과제1");
        this.setFocusable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CirclePanel panel = new CirclePanel();
        this.add(panel, BorderLayout.CENTER);
        this.setLocation(0, 0);
        this.setSize(panelWidth, panelHeight);
        this.setVisible(true);
    }
	
	/**
	 * 반지름 길이를 파라미터로 입력받았을 경우 사용되는 생성자 (Frame에서 Panel 생성시 파라미터로 radius 전달) 
	 */
    public CircleFrame(int radius){
    	this.setTitle("streami 과제1");
    	this.setFocusable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CirclePanel panel = new CirclePanel(radius);
        this.add(panel, BorderLayout.CENTER);
        this.setLocation(0, 0);
        this.setSize(panelWidth, panelHeight);
        this.setVisible(true);
    }
    
    class CirclePanel extends JPanel{
    	
    	private int radius;
    	
    	public CirclePanel() {
    		this.radius = 100;		// 기본값 100으로 설정
    	}
    	public CirclePanel(int radius) {
    		this.radius = radius;
    	}
    	
        public void paintComponent(Graphics g){
        	System.out.println("radius ==> " + radius);
        	int centerX = getWidth()/2;
        	int centerY = getHeight()/2;
        	
            super.paintComponent(g);
            g.setColor(Color.RED);	// 눈에띄는 빨간색으로 설정
            g.fillArc(centerX - radius/2, centerY - radius/2, radius, radius, 0, 360);	 // 속이 채워진 원
            
            /************************************************** 원 검증 로직 추가 시작 ************************************************/
            int repeats = 360;
            Graphics2D g2d = (Graphics2D)g;
            
            for(int i=0; i<repeats;i ++) {
            	
                g2d.setColor(Color.BLUE);	// 검증을 위한 사각형은 파란색으로 설정
            	int angle = 360 / repeats;
            	g2d.setColor(Color.BLUE);
            	Rectangle rect = new Rectangle(centerX - radius/2, centerY - radius/2, radius, radius);
            	g2d.rotate(angle, rect.x + rect.width/2, rect.y + rect.height/2);
            	g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
            	g2d.draw(rect);
            	
            }
            /************************************************** 원 검증 로직 추가 시작 ************************************************/
            
            
        }
    }
}

public class Circle {
	
	public static void draw() {
		 int radius = 0;
         Scanner scan = new Scanner(System.in);      // 문자 입력을 인자로 Scanner 생성
         System.out.print("반지름을 입력하세요:");
         try {
        	 
        	 radius = Integer.parseInt(scan.nextLine());            // 키보드 문자 입력
        	 // 원의 반지름 최대 크기를 400으로 제한
        	 if(radius > 400) {										// Panel 가로 세로 사이즈를 900으로 하고 원의 중심을 Panel의 450, 450에서 시작하도록 설정
        		 System.out.println("Panel의 크기는 가로,세로 크기는 900입니다. 원의 최대 반지름 크기는 400입니다.");
            	 draw();
        	 }else {
        		 new CircleFrame(radius);
        	 }
         }catch(Exception e) {
        	 System.out.println("숫자가 아니거나 Integer 최대값 "+ Integer.MAX_VALUE + "을 초과했습니다.");
        	 draw();
        	 
         }
    	
	}
	
    public static void main(String[] args) {
    	draw();
    }
}