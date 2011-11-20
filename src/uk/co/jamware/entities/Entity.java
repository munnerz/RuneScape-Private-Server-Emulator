package uk.co.jamware.entities;

import javax.vecmath.Point3d;

public abstract class Entity {
	private Point3d coordinates;
	
	public Entity() {
		this.coordinates = new Point3d();
	}
	
	public Entity(Point3d coordinates) {
		this.coordinates = coordinates;
	}
	
	public Point3d getCoordinates() {
		return this.coordinates;
	}
}
