package ballcover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import coreset.Coreset;

import model.Point;
import model.Util;

public class BlurredBallCover {
	public HashSet<Point> union_coreset;
	public double[] center;
	public double radius;

	public double time_elapsed = 0.0;

	private double eps;
	private LinkedList<BlurredBall> blurred_cover;
	
	public BlurredBallCover(List<Point> initPointSet, double eps, boolean append_mode) {
		this.eps = eps;
		this.union_coreset = new HashSet<>();
		this.blurred_cover = new LinkedList<>();
		this.center = new double[Util.d];
		this.radius = 0.0;
		
		long t1 = System.nanoTime();
		BlurredBall initBall = new BlurredBall(0, initPointSet);
		this.union_coreset.addAll(initBall.ball_coreset);
		this.blurred_cover.addFirst(initBall);
				
//		System.out.println(this.blurred_cover.getFirst().ball_radius + "," + this.union_coreset.size());
		long t2 = System.nanoTime();
		this.time_elapsed += (t2 - t1) / 1e9;
	}
	
	public BlurredBallCover(List<Point> pointSet, double eps) {
		this.eps = eps;
		this.union_coreset = new HashSet<>();
		this.blurred_cover = new LinkedList<>();
		this.center = new double[Util.d];
		this.radius = 0.0;
		
		long t1 = System.nanoTime();
		int batch_id = 0;
		for (batch_id = 0; batch_id < pointSet.size() / Util.BATCH_SIZE; batch_id++) {
			List<Point> next_batch = pointSet.subList(batch_id * Util.BATCH_SIZE, (batch_id + 1) * Util.BATCH_SIZE);
//			System.out.println(next_batch.get(0).idx);
			
			if (this.blurred_cover.isEmpty()) {
				BlurredBall initBall = new BlurredBall(0, next_batch);
				this.union_coreset.addAll(initBall.ball_coreset);
				this.blurred_cover.addFirst(initBall);
				
//				System.out.println(this.blurred_cover.getFirst().ball_radius + "," + this.union_coreset.size());
			} else {
				append(next_batch);
			}
		}
		
		if (batch_id * Util.BATCH_SIZE < pointSet.size()) {
			List<Point> next_batch = pointSet.subList(batch_id * Util.BATCH_SIZE, pointSet.size());
//			System.out.println(next_batch.get(0).idx);
			append(next_batch);
		}
		long t2 = System.nanoTime();
		this.time_elapsed = (t2 - t1) / 1e9;
		
		Coreset coreset = new Coreset(new ArrayList<>(this.union_coreset), 1e-6);
		this.center = coreset.center;
		this.radius = coreset.radius;
	}
	
	public void append(List<Point> pointSet) {
//		System.out.println(pointSet.get(0).idx);
		long t1 = System.nanoTime();
		boolean need_update = false;
		for (Point p : pointSet) {
			boolean point_in_ballcover = false;
			Iterator<BlurredBall> iter = this.blurred_cover.iterator();
			while (iter.hasNext()) {
				BlurredBall cur_ball = iter.next();
				if (Math.sqrt(Util.dist2(p.data,cur_ball.ball_center)) <= (1.0 + this.eps) * cur_ball.ball_radius) {
					point_in_ballcover = true;
					break;
				}
			}
			if (! point_in_ballcover) {
				need_update = true;
				break;
			}
		}
		
		if (need_update) {
			HashSet<Point> candidate = new HashSet<>();
			candidate.addAll(this.union_coreset);
			candidate.addAll(pointSet);
			int next_ball_id = this.blurred_cover.getFirst().ball_id + 1;
			BlurredBall nextBall = new BlurredBall(next_ball_id, new ArrayList<>(candidate));
			
			this.union_coreset.addAll(nextBall.ball_coreset);
			this.blurred_cover.addFirst(nextBall);
			
//			System.out.println(this.blurred_cover.getFirst().ball_radius + "," + this.union_coreset.size());
		}
		long t2 = System.nanoTime();
		this.time_elapsed += (t2 - t1) / 1e9;
	}
	
	public void approxMEB() {
		Coreset coreset = new Coreset(new ArrayList<>(this.union_coreset), 1e-6);
		this.center = coreset.center;
		this.radius = coreset.radius;
	}
	
	public void validate(List<Point> pointSet) {
		double max_sq_dist = 0.0;
		for (Point point : pointSet) {
			double sq_dist = Util.dist2(center, point.data);
			if (sq_dist > max_sq_dist) {
				max_sq_dist = sq_dist;
			}
		}
		double exp_radius = Math.sqrt(max_sq_dist);
		System.out.println("Actual Radius " + exp_radius);
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("center ");
		for (int i = 0; i < Util.d - 1; i++) {
			builder.append(center[i]).append(" ");
		}
		builder.append(center[Util.d - 1]).append("\n");
		builder.append("radius ").append(radius).append("\n");
		builder.append("time ").append(time_elapsed).append("s\n");
		return builder.toString();
	}

	public void output() {
		StringBuilder builder = new StringBuilder();
		builder.append("radius=").append(this.radius).append("\n");
		builder.append("squared radius=").append(this.radius * this.radius).append("\n");
		System.out.print(builder.toString());
	}
	
	class BlurredBall {
		int ball_id;
		double[] ball_center;
		double ball_radius;
		ArrayList<Point> ball_coreset;
		
		BlurredBall(int id, List<Point> pointSet) {
			this.ball_id = id;
			
			Coreset coreset = new Coreset(pointSet, eps / 3.0);
			this.ball_center = coreset.center;
			this.ball_radius = coreset.radius;
			this.ball_coreset = coreset.core_points;
		}
	}
}
