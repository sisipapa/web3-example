import java.awt.*;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

public class Shapes extends JPanel
{
	private double angle;
	private int type;
	private int radius;
	private int repeats;

	public Shapes(int t, int r, int z)
	{
	   type = t;
	   radius = r;
	   repeats = z;      
	   angle = 360 / repeats;
	}

	public void paintComponent( Graphics g )
	{
	  super.paintComponent( g );
	  Graphics2D g2d = (Graphics2D)g;
	
	  g.setColor(Color.red);
	
	  int centerX = getWidth()/2;
	  int centerY = getHeight()/2;
	
	  double curAngle = 0;
	
	  for(int i=0; i<=repeats; i+=1)
	  {
	      g.setColor(Color.blue);
	
	      Rectangle tangle = new Rectangle(0, 0, radius, radius);
	      g2d.rotate(Math.toRadians(curAngle));
	      g2d.translate(centerX, centerY);
	      g2d.draw(tangle);
	      curAngle += angle;
	  }
	}
}