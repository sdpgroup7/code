package uk.ac.ed.inf.sdp2012.group7.vision;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Plate{

	
	public Point[] getCorners(ArrayList<Point> points){
		Point centroid = getCentroid(points);
		Point furthest = new Point(0,0);
		Point opposite = new Point(0,0);
		Point adjacent = new Point(0,0);
		Point adjacent2 = new Point(0,0);
		double dist = 0;
		for(Point p : points){
			if(Point.distance(p.x, p.y, centroid.x, centroid.y) > dist){
				furthest = p;
				dist = Point.distance(p.x, p.y, centroid.x, centroid.y);
			}
		}
		dist = 0;
		for(Point p : points){
			if(Point.distance(p.x, p.y, furthest.x, furthest.y) > dist){
				opposite = p;
				dist = Point.distance(p.x, p.y, furthest.x, furthest.y);
			}
		}
		dist = 0;
		for(Point p : points){
			if(Line2D.ptLineDist(furthest.x, furthest.y, opposite.x, opposite.y, p.x, p.y) > dist){
				adjacent = p;
				dist = Line2D.ptLineDist(furthest.x, furthest.y, opposite.x, opposite.y, p.x, p.y);
			}
		}
		dist = 0;
		ArrayList<Point> outside = new ArrayList<Point>();
		for(Point p : points){
			if(!isPointInTriangle(furthest,opposite,adjacent,p)){
				outside.add(p);
			}
		}
		for(Point p : outside){
			if(Line2D.ptLineDist(furthest.x, furthest.y, opposite.x, opposite.y, p.x, p.y) > dist){
				adjacent2 = p;
				dist = Line2D.ptLineDist(furthest.x, furthest.y, opposite.x, opposite.y, p.x, p.y);
			}
		}
		return new Point[]{furthest,opposite,adjacent,adjacent2};
	}
	
	public boolean isPointInTriangle(Point a, Point b, Point c, Point p){
		Point v0 = new Point(c.x - a.x, c.y - a.y);
		Point v1 = new Point(b.x - a.x, b.y - a.y);
		Point v2 = new Point(p.x - a.x, p.y - a.y);
		
		int dot00 = dot(v0, v0);
		int dot01 = dot(v0, v1);
		int dot02 = dot(v0, v2);
		int dot11 = dot(v1, v1);
		int dot12 = dot(v1, v2);

		double invDenom = 1.0 / (double)(dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// Check if point is in triangle
		return (u >= -0.1) && (v >= -0.1) && (u + v < 1.1);

	}
	
	public int dot(Point a, Point b){
		return (a.x * b.x) + (a.y *b.y);
	}
	
	public Point getCentroid(ArrayList<Point> points){
		Point centroid = new Point(0,0);
		try{
			for(Point p : points){
				centroid.translate(p.x, p.y);
			}
			return new Point(centroid.x / points.size(), centroid.y / points.size());
		} catch (Exception e){
			return new Point(0,0);
		}
	}
	/**
	 * 
	 * @param a point p
	 * @param array of four points, forming a rectangle
	 * @return whether p is in the rectangle formed from the four points
	 */
	public boolean isInRectangle(Point p, Point[] points){
		if( p == new Point(0,0) ){
			return false;
		}
		
		boolean a; 
		boolean b; 
		
		a = isPointInTriangle(points[0], points[2], points[3], p);
		b = isPointInTriangle(points[1], points[2], points[3], p);
		
		return a || b;
	}
}
