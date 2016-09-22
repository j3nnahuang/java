public class NBody {
	public static double readRadius(String universe) {
		In file = new In(universe);
		int num_planets = file.readInt();
		double radius = file.readDouble();
		return radius;
	}

	public static Planet[] readPlanets(String universe) {
		In file = new In(universe);
		int num_planets = file.readInt();
		Planet[] planet_array = new Planet[num_planets];
		double radius = file.readDouble();
		Planet curr_planet;
		for (int i = 0; i < num_planets; i ++) {
			double xpos = file.readDouble();
			double ypos = file.readDouble();
			double xvel = file.readDouble();
			double yvel = file.readDouble();
			double m = file.readDouble();
			String name = file.readString();
			curr_planet = new Planet(xpos, ypos, xvel, yvel, m, name);
			planet_array[i] = curr_planet;
		}
		return planet_array;
	} 

	public static void main(String[] args) {
		double T = Double.valueOf(args[0]);
		double dt = Double.valueOf(args[1]);
		String filename = args[2];
		double radius = readRadius(filename);
		Planet[] planets = readPlanets(filename);
		StdDraw.setScale(-radius, radius);
		StdDraw.clear();
		StdDraw.picture(0,0, "./images/starfield.jpg");
		for (int i = 0; i < planets.length; i ++) {
			planets[i].draw();
		}
		for (double time = 0; time < T; time += dt) {
			double[] xForces = new double[planets.length];
			double[] yForces = new double[planets.length];
			for (int i = 0; i < planets.length; i ++) {
				xForces[i] = planets[i].calcNetForceExertedByX(planets);
				yForces[i] = planets[i].calcNetForceExertedByY(planets);
			}
			for (int i = 0; i < planets.length; i++) {
				planets[i].update(dt, xForces[i], yForces[i]);
			}
			StdDraw.setScale(-radius, radius);
			StdDraw.clear();
			StdDraw.picture(0,0, "./images/starfield.jpg");
			for (int i = 0; i < planets.length; i ++) {
				planets[i].draw();
			}
			StdDraw.show(10);
		}
		StdOut.printf("%d\n", planets.length);
		StdOut.printf("%.2e\n", radius);
		for (int i = 0; i < planets.length; i++) {
			StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
   				planets[i].xxPos, planets[i].yyPos, planets[i].xxVel, planets[i].yyVel, planets[i].mass, planets[i].imgFileName);	
		}		
	}
}
