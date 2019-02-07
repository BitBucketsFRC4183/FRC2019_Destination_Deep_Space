package frc.robot.utils.autotuner;

public class DFT_DataWindow extends DataWindow {
    private Complex amplitudes[];
    private Complex[] units;



    public DFT_DataWindow(int length) {
        super(length);



        amplitudes = new Complex[length];
        units = new Complex[length];
        
        for (int k = 0; k < length; k++) {
        	amplitudes[k] = new Complex(0, 0);
        	units[k]      = Complex.unit(2 * Math.PI * k / length);
        }
    }



    public void reset() {
        super.reset();



        for (int k = 0; k < size(); k++) {
            amplitudes[k] = new Complex(0, 0);
        }
    }



    public void add(double n) {
        for (int k = 0; k < size(); k++) {
            amplitudes[k] =
                units[k]
                .multiply(
                    amplitudes[k].add(
                        (n - get(0)) / size() // next index to be replaced is next --> get(0)
                    )
                );
        }



        super.add(n);
    }





    // DFT utilities

    /** Return complex amplitude of k-th frequency */
    public Complex getAmplitude(int k) {
        return amplitudes[k];
    }

    /** Get k-th frequency in 1/(units of data separation interval) */
    public double getFrequency(int k) {
        return ((double) k) / size();
    }
}