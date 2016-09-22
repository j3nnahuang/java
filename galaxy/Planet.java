public class Planet {
	public double xxPos; // Current x position
	public double yyPos; // Current y position
	public double xxVel; // Current velocity in x plane
	public double yyVel; // Current velocity in y plane
	public double mass; // Mass of planet
	public String imgFileName; // Name of image in *images* directory
	private double g = 6.67e-11;

	public Planet(double xP, double yP, double xV,
    			  double yV, double m, String img) {
		xxPos = xP;
		yyPos = yP;
		xxVel = xV;
		yyVel = yV;
		mass = m;
		imgFileName = img;
	}

	/* Takes in a planet p and copies its attributes */
	public Planet(Planet p) {
		xxPos = p.xxPos;
		yyPos = p.yyPos;
		xxVel = p.xxVel;
		yyVel = p.yyVel;
		mass = p.mass;
		imgFileName = p.imgFileName;
	}

	/* Calculates the radius between planets a and b with center a */
	public double calcDistance(Planet b) {
		double dx = b.xxPos - xxPos;
		double dy = b.yyPos - yyPos;
		double radius = Math.sqrt((dx * dx) + (dy * dy));
		return radius;
	}

	/* Calculates the force in both x,y plane relative to a */
	public double calcForceExertedBy(Planet b) {
		double radius = calcDistance(b);
		double force = (g * mass * b.mass) / (radius * radius);
		return force; // Is this line efficient?
	}

	/* Calculates the force in the x direction exerted on itself by planet other */
	public double calcForceExertedByX(Planet other) {
		double dx = other.xxPos - xxPos;
		double radius = calcDistance(other);
		double force = calcForceExertedBy(other);
		double xForce = force * dx / radius;
		return xForce;
	}

	/* Calculates the force in the y direction exerted on itself by planet other */
	public double calcForceExertedByY(Planet other) {
		double dy = other.yyPos - yyPos;
		double radius = calcDistance(other);
		double force = calcForceExertedBy(other);
		double yForce = force * dy / radius;
		return yForce;
	}

	public double calcNetForceExertedByX(Planet[] planets) {
		int i = 0;
		double xNetForce = 0;
		while (i < planets.length) {
			if (this.equals(planets[i]) == false) {
				xNetForce += calcForceExertedByX(planets[i]);
			}
			i += 1;
		}
		return xNetForce;
	}

	public double calcNetForceExertedByY(Planet[] planets) {
		int i = 0;
		double yNetForce = 0;
		while (i < planets.length) {
			if (this.equals(planets[i]) == false) {
				yNetForce += calcForceExertedByY(planets[i]);
			}
			i += 1;
		}
		return yNetForce;
	}

	public void update(double dt, double fX, double fY) {
		double xAcc = fX / mass;
		double yAcc = fY / mass;
		xxVel = xxVel + (dt * xAcc);
		yyVel = yyVel + (dt * yAcc);
		xxPos = xxPos + (dt * xxVel);
		yyPos = yyPos + (dt * yyVel);
	}

	public void draw() {
		String img_name = "images/" + imgFileName;
		StdDraw.picture(xxPos, yyPos, img_name);
	}
}