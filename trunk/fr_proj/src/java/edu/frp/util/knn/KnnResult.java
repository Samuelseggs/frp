package edu.frp.util.knn;

/**
 * Result of a distance calculus in KNN algorithm
 * @see java.lang.Comparable
 * @author Saulo (scsm@ecomp.poli.br)
 */
public class KnnResult implements Comparable<KnnResult> {

	private String name;
	
	private double distance;
	
	/**
	 * Class constructor
	 * @param name the name of the compared class
	 * @param distance the distance to the compared class
	 */
	public KnnResult(String name, double distance) {
		this.name = name;
		this.distance = distance;
	}
	
	@Override
	public int compareTo(KnnResult o) {
		if (this.distance < o.distance)
			return -1;
		else if (this.distance > o.distance) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
