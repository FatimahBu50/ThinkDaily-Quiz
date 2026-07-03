package ThinkDaily;

public class LevelUtils {

    public static final int POINTS_PER_LEVEL = 100;

    public static int computeLevelFromPoints(int points) {
        if (points < 0) points = 0;
        return (points / POINTS_PER_LEVEL) + 1;
    }

    public static int pointsIntoLevel(int points) {
        if (points < 0) points = 0;
        return points % POINTS_PER_LEVEL;
    }

    public static int progressPercent(int points) {
        int into = pointsIntoLevel(points);
        return (int) Math.round((into * 100.0) / POINTS_PER_LEVEL);
    }
}
