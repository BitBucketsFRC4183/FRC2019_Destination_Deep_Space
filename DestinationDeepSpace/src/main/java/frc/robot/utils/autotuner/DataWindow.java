package frc.robot.utils.autotuner;

/** Unordered data window */
public class DataWindow {
    private double[] data;
    private final int LENGTH;

    private int next; // next index to replace
    private boolean filled;
    private double sum;

    private boolean setExtremum;
    private double min;
    private double max;




    public DataWindow(int length) {
        LENGTH = length;
        data   = new double[LENGTH];



        reset();
    }





    public void reset() {
        next   = 0;
        filled = false;
        sum    = 0;

        setExtremum = false;

        for (int k = 0; k < data.length; k++) {
            data[k] = 0;
        }
    }



    public void add(double n) {
        if (setExtremum == false) {
            min = n;
            max = n;

            setExtremum = true;
        }

        // 2 7 3 4 5
        //   ^
        //   6
        // max of new set is 6, but max of (max = 7, 6) is 7
        // the problem is that you could be replacing a global extremum
        // if you are replacing the extremum, you will want to take max to be max of (2nd max, new value)
        if (max != data[next]) {
            max = Math.max(max, n);
        } else {
            max = n;

            for (int i = 0; i < LENGTH; i++) {
                if (i != next) {
                    if (data[i] > max) {
                        max = data[i];
                    }
                }
            }
        }

        if (min != data[next]) {
            min = Math.min(min, n);
        } else {
            min = n;

            for (int i = 0; i < LENGTH; i++) {
                if (i != next) {
                    if (data[i] < min) {
                        min = data[i];
                    }
                }
            }
        }



        sum -= data[next];
        sum += n;

        data[next] = n;
        next++;

        if (next == LENGTH) {
            filled = true; // whole window is filled with data
            next = 0; // next data additional will replace the first data entry
        }
    }



    // do we have enough data?
    public boolean isFilled() {
        return filled;
    }

    public double average() {
        return sum / LENGTH;
    }



    // used for determining whether it is safe to use the window for measurements
    public double maxDif() {
        return max - min;
    }



    public double get(int i) {
    	// change how data is accessed to make it look like you push back
    	// old data to make space for new data
    	return data[(next + i) % LENGTH];
    }

    public int size() {
        return LENGTH;
    }





    @Override
    public String toString() {
        String ret = "";

        for (int i = 0; i < LENGTH; i++) {
            ret += "\t" + data[i] + "\n";
        }

        return ret;
    }
}